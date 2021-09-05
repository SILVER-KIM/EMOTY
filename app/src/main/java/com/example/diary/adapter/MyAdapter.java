package com.example.diary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diary.item.MyItem;
import com.example.diary.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter{
    Context context;

    TextView dDay;
    TextView just;
    ImageButton openBTN;

    boolean state;
    FirebaseDatabase db;
    DatabaseReference myDB;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<MyItem> mItems = new ArrayList<>();

    public void removeItem(int position){
        mItems.remove(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        state = false;

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_close, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView type = (ImageView) convertView.findViewById(R.id.type) ;
        TextView title = (TextView) convertView.findViewById(R.id.title) ;
        TextView closeDay = (TextView) convertView.findViewById(R.id.closeDay) ;
        TextView openDay = (TextView) convertView.findViewById(R.id.openDay) ;
        dDay = (TextView) convertView.findViewById(R.id.Dday) ;
        just = (TextView)convertView.findViewById(R.id.j);
        openBTN = (ImageButton)convertView.findViewById(R.id.unlock);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final MyItem myItem = getItem(position);

        openBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = ((Activity) context).getIntent();
                String location = my.getStringExtra("GPS");
                //double latitude2 = my.getDoubleExtra("latitude", 0);
                //double longitude2 = my.getDoubleExtra("longitude", 0);

                String type = myItem.getType();
                String title = myItem.getTitle();
                String id = myItem.getId();
                String gps = myItem.getGps();

                myDB.child("User").child(id).child("TimeCapsule").child(type).child(title).child("state").setValue("unlock");
                removeItem(position);
                notifyDataSetChanged();
            }
        });

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        type.setImageDrawable(myItem.getIcon());
        title.setText(myItem.getTitle());
        closeDay.setText(myItem.getCloseDay());
        openDay.setText(myItem.getOpenDay());
        dDay.setText(myItem.getdDay());

        if(Integer.parseInt(myItem.getdDay()) <= 0){
            dDay.setVisibility(View.INVISIBLE);
            just.setVisibility(View.INVISIBLE);
            openBTN.setVisibility(View.VISIBLE);
        }
        else {
            dDay.setVisibility(View.VISIBLE);
            just.setVisibility(View.VISIBLE);
            openBTN.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String type, String id, Drawable image, String title, String closeDay, String openDay, String dDay, String gps, double latitude, double longitude) {

        MyItem mItem = new MyItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setType(type);
        mItem.setId(id);
        mItem.setIcon(image);
        mItem.setTitle(title);
        mItem.setCloseDay(closeDay);
        mItem.setOpenDay(openDay);
        mItem.setdDay(dDay);
        mItem.setGps(gps);
        mItem.setLatitude(latitude);
        mItem.setLongitude(longitude);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }

}