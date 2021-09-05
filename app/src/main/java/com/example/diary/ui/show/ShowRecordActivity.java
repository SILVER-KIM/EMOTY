package com.example.diary.ui.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diary.R;
import com.example.diary.RecordingActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowRecordActivity extends Activity {

    // Firebase 애들 선언
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myDB;

    String filePath;

    String id;
    String title;
    String type;
    String voiceURI;

    EditText s_title;
    ImageButton s_play;
    ImageButton s_stop;
    ImageView s_tape;

    MediaPlayer player;
    File sdcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        sdcard = Environment.getExternalStorageDirectory();

        // intent로 데이터베이스 경로에 필요한 id, type, title값을 받아와서 초기화해준다.
        Intent voice = getIntent();
        id = voice.getStringExtra("id");
        title = voice.getStringExtra("title");
        type = voice.getStringExtra("type");

        // Firebase DB, Storage 초기화
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        // layout요소 초기화
        s_title = (EditText)findViewById(R.id.s_title);
        s_play = (ImageButton)findViewById(R.id.s_play);
        s_stop = (ImageButton)findViewById(R.id.s_stop);
        s_tape = (ImageView)findViewById(R.id.s_tape);

        permissionCheck();
        checkDB();

        s_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(ShowRecordActivity.this).load(R.raw.record).into(s_tape);
                playAudio();
            }
        });

        s_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(ShowRecordActivity.this).onStop();
                stopAudio();
            }
        });

    }


    public void checkDB(){
        myDB.child("User").child(id).child("TimeCapsule").child(type).child(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title = dataSnapshot.getKey().toString().trim();
                s_title.setText(dataSnapshot.getKey().toString().trim());
                if(dataSnapshot.hasChild("Uri")) {
                    voiceURI = dataSnapshot.child("Uri").getValue().toString().trim();
                    downloadManager(voiceURI, title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadManager(String url, String filename) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download");
        request.setTitle(filename);
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename +".mp4");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void playAudio() {
        try {
            closePlayer();

            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"  +  title + ".mp4";
            player = new MediaPlayer();
            player.setDataSource(filePath);
            player.prepare();
            player.start();

            Toast.makeText(getApplicationContext(), "재생 시작 ʕ•ﻌ•ʔ ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (player != null) {
            player.pause();

            Toast.makeText(getApplicationContext(), "일시정지 ʕ•ﻌ•ʔ ", Toast.LENGTH_SHORT).show();
        }
    }


    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}
