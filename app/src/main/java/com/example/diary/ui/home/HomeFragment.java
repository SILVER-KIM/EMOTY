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

    // Calendar ??????
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
                intent.putExtra("latitude", latitude_ok);  // ??????
                intent.putExtra("longitude", longitude_ok);   //??????
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

        //->(24 * 60 * 60 * 1000) 24?????? 60??? 60??? * (ms???->??? ?????? 1000)
        long current = todayCal.getTimeInMillis()/86400000;
        long open = openCal.getTimeInMillis()/86400000;
        long count = open - current; // ?????? ???????????? dday ????????? ????????? ?????????.
        return (int)count;
    }

    public void checkDB(){
        myDB.child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if(postSnapshot.getKey().equals("TimeCapsule")){
                        // TimeCapsule??? ?????? ????????? ???
                        if(postSnapshot.hasChild("Record")){
                            checkRecord();
                        }
                        // TimeCapsule??? ??? ????????? ???
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
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // ??????????????? ????????? ????????? ??????.
        customMarker.setCustomImageBitmap(size); // ?????? ?????????.
        customMarker.setCustomImageAutoscale(true); // hdpi, xhdpi ??? ??????????????? ???????????? ???????????? ????????? ?????? ?????? ?????????????????? ????????? ????????? ??????.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // ?????? ???????????? ????????? ?????? ??????(???????????????) ?????? - ?????? ????????? ?????? ?????? ?????? x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) ???.
        mapView.addPOIItem(customMarker);
        count++;
        mapView.selectPOIItem(customMarker, true);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //????????? ???????????? ???????????? ???????????? ????????????.
            //?????? Location ????????? ???????????? ?????? ?????? ????????? ????????? ??????.
            longitude = location.getLongitude(); //??????
            latitude = location.getLatitude();   //??????
            altitude = location.getAltitude();   //??????
            provider = location.getProvider();   //???????????????
            //Gps ?????????????????? ?????? ????????????. ??????????????? ??????.
            //Network ?????????????????? ?????? ????????????
            //Network ????????? Gps??? ?????? ???????????? ?????? ????????????.

            longitude_ok = longitude;
            latitude_ok = latitude;
            currentLocation = getCompleteAddressString(getActivity(), latitude, longitude);
//            txtCurrentMoney.setText("???????????? : " + provider + "\n?????? : " + longitude + "\n?????? : " + latitude
//                    + "\n?????? : " + altitude + "\n????????? : "  + accuracy);

            lm.removeUpdates(mLocationListener);  //  ?????????????????? ????????? ??????????????? ???????????? ??????.

        }

        public void onProviderDisabled(String provider) {
            // Disabled???
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled???
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // ?????????
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };



    public void getLocation() {

        try {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // ????????? ???????????????
                    100, // ??????????????? ?????? ???????????? (miliSecond)
                    1, // ??????????????? ?????? ???????????? (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // ????????? ???????????????
                    100, // ??????????????? ?????? ???????????? (miliSecond)
                    1, // ??????????????? ?????? ???????????? (m)
                    mLocationListener);

            //txtCurrentPositionInfo.setText("???????????? ????????????");
            //lm.removeUpdates(mLocationListener);  //  ?????????????????? ????????? ??????????????? ???????????? ??????.

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

        // "???????????? " ?????? ????????????
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