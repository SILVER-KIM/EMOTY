package com.example.diary.ui.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.textclassifier.TextClassificationContext;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.diary.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowTextActivity extends Activity {

    String id;
    String title;
    String type;
    String photoURI;

    EditText s_titleText;       // 타임캡슐 제목
    EditText s_editText;        // 타임캡슐 내용
    TextView s_Present;         // 타임캡슐 현재 날짜
    TextView s_GpsPlace;        // 타임캡슐 묻은 위치
    ImageView s_picture;        // 타임캡슐에 넣은 사진

    // Firebase 애들 선언
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myDB;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy - MM - dd");
    String present_day = mFormat.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_text);

        // intent로 데이터베이스 경로에 필요한 id, type, title값을 받아와서 초기화해준다.
        Intent text = getIntent();
        id = text.getStringExtra("id");
        title = text.getStringExtra("title");
        type = text.getStringExtra("type");

        // layout 요소들 초기화 해주기
        s_titleText = (EditText)findViewById(R.id.s_titleText);
        s_editText = (EditText)findViewById(R.id.s_editText);
        s_Present = (TextView)findViewById(R.id.s_Present);
        s_GpsPlace = (TextView)findViewById(R.id.s_GpsPlace);
        s_picture = (ImageView)findViewById(R.id.s_picture);

        // Firebase DB, Storage 초기화
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        checkDB();
    }

    public void checkDB(){
        myDB.child("User").child(id).child("TimeCapsule").child(type).child(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s_titleText.setText(title);
                s_editText.setText(dataSnapshot.child("word").getValue().toString().trim());
                s_Present.setText(present_day);
                s_GpsPlace.setText(dataSnapshot.child("GPS").getValue().toString().trim());
                if(dataSnapshot.hasChild("photo")) {
                    photoURI = dataSnapshot.child("photo").getValue().toString().trim();
                    Glide.with(getApplicationContext())
                            .load(photoURI)
                            .into(s_picture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
