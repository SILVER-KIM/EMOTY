package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingActivity extends Activity {

    ImageButton lock;
    TextView today;
    TextView lock_date;
    TextView mul;
    ImageButton dateBTN;
    String btnState;
    String lockDATE;
    String getTime;
    String recordURI;

    ImageButton play;
    ImageButton stop;
    ImageButton save;
    ImageButton record;
    ImageView tape;
    EditText title;

    Boolean play_status = false;
    Boolean stop_status = false;
    Boolean save_status = false;
    Boolean record_status = false;

    UploadTask uploadTask;
    String fileTitle;
    MediaRecorder recorder;
    String filename;
    MediaPlayer player;
    File sdcard;
    File file;
    String id;
    String gps;
    double latitude;
    double longitude;

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myRF;

    Date mDate;
    SimpleDateFormat simpleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        long now = System.currentTimeMillis();
        mDate = new Date(now);
        simpleDate = new SimpleDateFormat("yyyy - MM - dd");
        getTime = simpleDate.format(mDate);

        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        Intent voice = getIntent();
        id = voice.getStringExtra("id");
        gps = voice.getStringExtra("GPS");
        latitude = voice.getDoubleExtra("latitude", 0);
        longitude = voice.getDoubleExtra("longitude", 0);

        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        lock = (ImageButton)findViewById(R.id.lockBTN);
        dateBTN = (ImageButton)findViewById(R.id.changeDate);
        today = (TextView)findViewById(R.id.today_Date);
        lock_date = (TextView)findViewById(R.id.lock_Date);
        mul = (TextView)findViewById(R.id.mul);
        btnState = "date";

        play = (ImageButton)findViewById(R.id.play);
        stop = (ImageButton)findViewById(R.id.stop);
        save = (ImageButton)findViewById(R.id.save);
        record = (ImageButton)findViewById(R.id.record);
        tape = (ImageView)findViewById(R.id.tape);
        title = (EditText)findViewById(R.id.title);

        permissionCheck();

        sdcard = Environment.getExternalStorageDirectory();

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnState.equals("date")){
                    Intent datePicker = new Intent(getApplicationContext(), DatePickerActivity.class);
                    startActivityForResult(datePicker, 1004);
                    btnState = "lock";
                    lock.setImageResource(R.drawable.heartlock);
                    /*
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,30,0,0);  // ??????, ???, ?????????, ?????? ???????????????.
                    lock.setLayoutParams(params);
                     */
                }
                else if(btnState.equals("lock")){
                    saveDB();
                    finish();
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(RecordingActivity.this).load(R.raw.record).into(tape);
                playAudio();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(RecordingActivity.this).onStop();
                stopAudio();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecording();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ?????? ????????? ????????? ???  false??? ?????? ??????
                if(record_status == false) {
                    if (checkTitle() == true) {
                        record.setImageResource(R.drawable.record);
                        record_status = true;
                        file = new File(sdcard, fileTitle);
                        filename = file.getAbsolutePath();
                        Glide.with(RecordingActivity.this).load(R.raw.record).into(tape);
                        recordAudio();
                    }
                }
                else if(record_status){
                    record.setImageResource(R.drawable.unrecord);
                    Glide.with(RecordingActivity.this).onStop();
                    stopRecording();
                }
            }
        });

    }

    private boolean checkTitle(){
        if(!title.getText().toString().isEmpty()){
            fileTitle = title.getText().toString() + ".mp4";
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(), "??????????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void saveRecording(){
        Uri file = Uri.fromFile(new File(filename));
        uploadTask = storageRef.child("record/"+fileTitle).putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Storage ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "?????? ?????? ????????????? ", Toast.LENGTH_SHORT).show();
                getURI();
                File check = new File(filename);
                check.delete();
            }
        });
    }

    public void saveDB(){
        int index = fileTitle.indexOf(".");
        String name = fileTitle.substring(0, index);

        // ???????????? ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("Name").setValue(fileTitle);
        // ??????????????? ?????? ?????? ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("Date").setValue(getTime);
        // ??????????????? ??? ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("OpenDate").setValue(lockDATE);
        // ?????? ?????? uri
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("Uri").setValue(recordURI);
        // GPS
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("GPS").setValue(gps);
        // ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("state").setValue("lock");
        // ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("latitude").setValue(latitude);
        // ??????
        myRF.child("User").child(id).child("TimeCapsule").child("Record").child(name).child("longitude").setValue(longitude);
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void playAudio() {
        try {
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(filename);
            player.prepare();
            player.start();

            Toast.makeText(getApplicationContext(), "?????? ?????? ????????????? ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (player != null) {
            player.pause();

            Toast.makeText(getApplicationContext(), "???????????? ????????????? ", Toast.LENGTH_SHORT).show();
        }
    }


    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void recordAudio() {
        recorder = new MediaRecorder();

        /* ????????? ???????????? ????????? ??????.
         * ????????? : ??? ????????? ????????? ????????????, ????????? ????????? ????????? ?????? ???????????? ???
         * ?????? 15????????? ????????? ?????? 8K(8000?????????) ????????? ???????????? ?????????
         * ????????? ????????? ?????????, ????????? ????????? ?????? */
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // ???????????? ?????? ???????????? ?????? ?????????
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // ?????? ?????? ??????
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setOutputFile(filename);

        try {
            recorder.prepare();
            recorder.start();

            Toast.makeText(getApplicationContext(), "?????? ?????? ????????????? ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "?????????.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                lockDATE = data.getStringExtra("date");

                today.setText(getTime);
                lock_date.setText(lockDATE);
                mul.setText("~");
                dateBTN.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getURI(){
        StorageReference uriSTR = storage.getReferenceFromUrl("gs://diary-731c2.appspot.com").child("record").child(fileTitle);
        uriSTR.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                recordURI = String.valueOf(uri);
            }
        });
    }
}
