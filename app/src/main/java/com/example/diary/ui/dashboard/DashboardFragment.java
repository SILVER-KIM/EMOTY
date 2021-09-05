package com.example.diary.ui.dashboard;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.adapter.MyAdapter;
import com.example.diary.R;
import com.example.diary.TimeCasuleActivity;
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

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    // ListView 선언, Adapter 선언
    ListView mListView;
    MyAdapter mMyAdapter;

    // Calendar 선언
    Calendar openCal;
    Calendar todayCal;
    Date openDate;
    Date today;

    String title;
    // 로그인한 사용자 ID
    String id;

    // ImageBTN 선언
    ImageButton select;

    // Firebase 애들 선언
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView title = (TextView)((TimeCasuleActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Close");

        // TimeCapsuleActivity에 정의해둔 사용자 id값 가져오기
        id = ((TimeCasuleActivity)getActivity()).id;

        select = (ImageButton)((TimeCasuleActivity)getActivity()).findViewById(R.id.select);
        select.setVisibility(View.INVISIBLE);

        // Firebase DB, Storage 초기화
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        // ListView 선언, 초기화해주고 만들어둔 Adapter 연결해주기
        mListView = (ListView)root.findViewById(R.id.closelistview);
        mMyAdapter = new MyAdapter();
        mListView.setAdapter(mMyAdapter);

        checkDB();
        mMyAdapter.notifyDataSetChanged();
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

    public void refreshFG(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
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
                    if(state.equals("lock")) {
                        mMyAdapter.addItem("Record", id,ContextCompat.getDrawable(getContext(), R.drawable.voice), title, closeDay, openDay, dDay, gps, latitude, longitude);
                        mMyAdapter.notifyDataSetChanged();
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
                    if(state.equals("lock")) {
                        mMyAdapter.addItem("Text", id,ContextCompat.getDrawable(getContext(), R.drawable.text), title, closeDay, openDay, dDay, gps, latitude, longitude);
                        mMyAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}