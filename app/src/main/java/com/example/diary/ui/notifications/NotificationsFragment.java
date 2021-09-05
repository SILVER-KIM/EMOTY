package com.example.diary.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.R;
import com.example.diary.TimeCasuleActivity;
import com.example.diary.adapter.openMyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationsFragment extends Fragment {

    // ListView 선언, Adapter 선언
    ListView oListView;
    public openMyAdapter oMyAdapter;

    // Calendar 선언
    Calendar openCal;
    Calendar todayCal;
    Date openDate;
    Date today;

    String title;
    // 로그인한 사용자 ID
    String id;

    // Firebase 애들 선언
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myDB;

    ImageButton select;
    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // TimeCapsuleActivity에 정의해둔 사용자 id값 가져오기
        id = ((TimeCasuleActivity)getActivity()).id;

        select = (ImageButton)((TimeCasuleActivity)getActivity()).findViewById(R.id.select);
        select.setVisibility(View.INVISIBLE);

        TextView title = (TextView)((TimeCasuleActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Open");

        // Firebase DB, Storage 초기화
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        // ListView 선언, 초기화해주고 만들어둔 Adapter 연결해주기
        oListView = (ListView)root.findViewById(R.id.openlistview);
        oMyAdapter = new openMyAdapter();
        oListView.setAdapter(oMyAdapter);

        checkDB();
        oMyAdapter.notifyDataSetChanged();
        return root;
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
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    title = postSnapshot.getKey();
                    String closeDay = postSnapshot.child("Date").getValue().toString().trim();
                    String openDay = postSnapshot.child("OpenDate").getValue().toString().trim();
                    int dDAY = dDAY(openDay);
                    String dDay = String.valueOf(dDAY(openDay));
                    String state = postSnapshot.child("state").getValue().toString().trim();
                    String gps = postSnapshot.child("GPS").getValue().toString().trim();
                    double latitude = Double.parseDouble(postSnapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(postSnapshot.child("longitude").getValue().toString());
                    if(state.equals("unlock")) {
                        oMyAdapter.addItem(id, "Record",  ContextCompat.getDrawable(getContext(), R.drawable.voice), title, closeDay, openDay, gps, latitude, longitude);
                        oMyAdapter.notifyDataSetChanged();
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
                    title = postSnapshot.getKey();
                    String closeDay = postSnapshot.child("Date").getValue().toString().trim();
                    String openDay = postSnapshot.child("OpenDate").getValue().toString().trim();
                    int dDAY = dDAY(openDay);
                    String dDay = String.valueOf(dDAY(openDay));
                    String state = postSnapshot.child("state").getValue().toString().trim();
                    String gps = postSnapshot.child("GPS").getValue().toString().trim();
                    double latitude = Double.parseDouble(postSnapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(postSnapshot.child("longitude").getValue().toString());
                    if(state.equals("unlock")) {
                        oMyAdapter.addItem(id, "Text", ContextCompat.getDrawable(getContext(), R.drawable.text), title, closeDay, openDay, gps, latitude, longitude);
                        oMyAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
}
