package com.example.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.R;
import com.example.diary.item.openItem;
import com.example.diary.ui.show.ShowRecordActivity;
import com.example.diary.ui.show.ShowTextActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class openMyAdapter extends BaseAdapter {

    TextView dDay;
    TextView just;
    ImageButton seeBTN;
    // boolean accept = false;

    public LocationManager lm;
    public String currentLocation;

    double longitude;
    double longitude_ok;
    double latitude_ok;
    double latitude;
    double altitude;
    String provider;
    Context context;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<openItem> oItems = new ArrayList<>();

    @Override
    public int getCount() {
        return oItems.size();
    }

    @Override
    public openItem getItem(int position) {
        return oItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        context = parent.getContext();
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        getLocation();


        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_open, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView open_type = (ImageView) convertView.findViewById(R.id.open_type) ;
        TextView open_title = (TextView) convertView.findViewById(R.id.open_title) ;
        TextView open_closeDay = (TextView) convertView.findViewById(R.id.open_closeDay) ;
        TextView open_openDay = (TextView) convertView.findViewById(R.id.open_openDay) ;
        TextView open_Gps = (TextView) convertView.findViewById(R.id.open_Gps) ;
        seeBTN = (ImageButton)convertView.findViewById(R.id.see);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final openItem openItem = getItem(position);

        /* 버튼을 클릭했을때 보여주는 액티비티를 띄우고 안에 해당하는 값들을 업로드한다. */
        seeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (openItem.getGPS().equals(currentLocation.trim())) {
                        if (openItem.getType().equals("Text")) {
                            Intent text = new Intent(context, ShowTextActivity.class);
                            text.putExtra("id", openItem.getId());
                            text.putExtra("type", openItem.getType());
                            text.putExtra("title", openItem.getTitle());
                            context.startActivity(text);
                        } else if (openItem.getType().equals("Record")) {
                            Intent record = new Intent(context, ShowRecordActivity.class);
                            record.putExtra("id", openItem.getId());
                            record.putExtra("type", openItem.getType());
                            record.putExtra("title", openItem.getTitle());
                            context.startActivity(record);
                        }
                    } else
                        Toast.makeText(context, "타임캡슐을 묻은 위치와 현재 위치가 다릅니다.", 0).show();
                }catch(NullPointerException e){
                    Toast.makeText(context, "잠시만 기다려 주세요.", 0).show();
                }
            }
        });

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        open_type.setImageDrawable(openItem.getIcon());
        open_title.setText(openItem.getTitle());
        open_closeDay.setText(openItem.getCloseDay());
        open_openDay.setText(openItem.getOpenDay());
        open_Gps.setText(openItem.getGPS());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String id,  String type, Drawable image, String title, String closeDay, String openDay, String gps, double latitude, double longitude) {

        openItem openItem = new openItem();

        /* MyItem에 아이템을 setting한다. */
        openItem.setId(id);
        openItem.setType(type);
        openItem.setIcon(image);
        openItem.setTitle(title);
        openItem.setCloseDay(closeDay);
        openItem.setOpenDay(openDay);
        openItem.setGPS(gps);
        openItem.setLatitude(latitude);
        openItem.setLongitude(longitude);

        /* mItems에 MyItem을 추가한다. */
        oItems.add(openItem);

    }

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
            currentLocation = getCompleteAddressString(context, latitude, longitude);
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


    public  String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
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

}
