package com.example.diary.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.R;
import com.example.diary.SelectTimeCapsuleActivity;
import com.example.diary.TimeCasuleActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    String provider;
    double longitude;
    double longitude_ok;
    double latitude_ok;
    double latitude;
    double altitude;
    private HomeViewModel homeViewModel;
    MapView mapView;
    ImageButton tcBTN;
    LocationManager lm;
    String address;
    String id;
    public String currentLocation;
    MapPOIItem   customMarker;
    MapPoint mapPoint;

    // Calendar 선언
    Calendar openCal;
    Calendar todayCal;
    Date openDate;
    Date today;

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> dDAY = new ArrayList<String>();
    int count = 0;

    FirebaseDatabase db;
    DatabaseReference myDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayout map_view = (LinearLayout)root.findViewById(R.id.map_view);

        mapView = new MapView(getActivity());
        mapView.setZoomLevel(13, true);
        map_view.addView(mapView);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        count = 0;

        id = ((TimeCasuleActivity)getActivity()).id;
        tcBTN = (ImageButton)((TimeCasuleActivity)getActivity()).findViewById(R.id.select);
        tcBTN.setVisibility(View.VISIBLE);

        TextView title = (TextView)((TimeCasuleActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Map");

        tcBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectTimeCapsuleActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("GPS", currentLocation);
                intent.putExtra("latitude", latitude_ok);  // 위도
                intent.putExtra("longitude", longitude_ok);   //경도
                startActivity(intent);
            }
        });

        checkDB();

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        return root;
    }



    public int dDAY(String openDay){
        SimpleDateFormat change = new SimpleDateFormat("yyyy - MM - dd");
        String now = change.format(System.currentTimeMillis());
        try {
            today = change.parse(now);
            openDate = change.parse(openDay);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        todayCal = Calendar.getInstance();
        openCal = Calendar.getInstance();

        todayCal.setTime(today);
        openCal.setTime(openDate);

        //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
        long current = todayCal.getTimeInMillis()/86400000;
        long open = openCal.getTimeInMillis()/86400000;
        long count = open - current; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
        return (int)count;
    }

    public void checkDB(){
        myDB.child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if(postSnapshot.getKey().equals("TimeCapsule")){
                        // TimeCapsule이 음성 녹음일 때
                        if(postSnapshot.hasChild("Record")){
                            checkRecord();
                        }
                        // TimeCapsule이 글 형식일 때
                        if(postSnapshot.hasChild("Text")){
                            checkText();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkRecord(){
        myDB.child("User").child(id).child("TimeCapsule").child("Record").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String title =postSnapshot.getKey();
                    String state = postSnapshot.child("state").getValue().toString().trim();
                    if(state.equals("lock")) {
                        String dDay = String.valueOf(dDAY(postSnapshot.child("OpenDate").getValue().toString().trim()));
                        double latitude = Double.parseDouble(postSnapshot.child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(postSnapshot.child("longitude").getValue().toString());
                        setMarker(latitude, longitude, title, dDay);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkText(){
        myDB.child("User").child(id).child("TimeCapsule").child("Text").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String title =postSnapshot.getKey();
                    String state = postSnapshot.child("state").getValue().toString().trim();
                    if(state.equals("lock")) {
                        String dDay = String.valueOf(dDAY(postSnapshot.child("OpenDate").getValue().toString().trim()));
                        double latitude = Double.parseDouble(postSnapshot.child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(postSnapshot.child("longitude").getValue().toString());
                        setMarker(latitude, longitude, title, dDay);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setMarker(double point_la, double point_lo, String title, String dDay){
        Bitmap size =  BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        size = Bitmap.createScaledBitmap(size,60,60, true);
        customMarker = new MapPOIItem();
        mapPoint = MapPoint.mapPointWithGeoCoord(point_la, point_lo);
        customMarker.setItemName(title);
        customMarker.setTag(count);
        name.add(title);
        dDAY.add(dDay);
        customMarker.setMapPoint(mapPoint);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageBitmap(size); // 마커 이미지.
        customMarker.setCustomImageAutoscale(true); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mapView.addPOIItem(customMarker);
        count++;
        mapView.selectPOIItem(customMarker, true);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            altitude = location.getAltitude();   //고도
            provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            longitude_ok = longitude;
            latitude_ok = latitude;
            currentLocation = getCompleteAddressString(getActivity(), latitude, longitude);
//            txtCurrentMoney.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
//                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

            lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.

        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };



    public void getLocation() {

        try {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

            //txtCurrentPositionInfo.setText("위치정보 미수신중");
            //lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.

        } catch (SecurityException ex) {

        }

    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");


                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }

        // "대한민국 " 글자 지워버림
        strAdd = strAdd.substring(5);

        return strAdd;
    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;
        ImageView box;
        TextView f;
        TextView ppDate;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            box = (ImageView) mCalloutBalloon.findViewById(R.id.box);
            f = (TextView) mCalloutBalloon.findViewById(R.id.f);
            ppDate = (TextView) mCalloutBalloon.findViewById(R.id.ppdate);

            int num = poiItem.getTag();

            f.setText(name.get(num));
            if(Integer.parseInt(dDAY.get(num)) <= 0){
                ppDate.setText("D-Day");
            }
            else
                ppDate.setText("D-" + "" + dDAY.get(num));

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }


}