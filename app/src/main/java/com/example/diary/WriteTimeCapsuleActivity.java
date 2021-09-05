package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteTimeCapsuleActivity extends Activity {

    private static final int PICK_FROM_ALBUM = 1;

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myRF;
    UploadTask uploadTask;

    EditText title;
    EditText word;
    String btnState;
    ImageView photo;
    ImageButton gallery;
    ImageButton settingBTN;
    TextView today;
    TextView lockDay;
    TextView GpsPlace;
    String lockDATE;

    double longitude;
    double latitude;

    String fileTitle;
    String fileText;
    String id;
    String gps;
    String photoUri;

    private Uri photoURI;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy - MM - dd");
    String present_day = mFormat.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_time_capsule);

        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        Intent text = getIntent();
        id = text.getStringExtra("id");
        gps = text.getStringExtra("GPS");
        latitude = text.getDoubleExtra("latitude", 0);
        longitude = text.getDoubleExtra("longitude", 0);
        Toast.makeText(getApplicationContext(), latitude+"/"+longitude, 0).show();

        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        photo=(ImageView)findViewById(R.id.picture);
        gallery=(ImageButton)findViewById(R.id.gallery);
        settingBTN=(ImageButton)findViewById(R.id.settingBTN);
        today=(TextView)findViewById(R.id.Present);
        lockDay=(TextView)findViewById(R.id.lock_Day);
        GpsPlace=(TextView)findViewById(R.id.GpsPlace);
        title=(EditText)findViewById(R.id.titleText);
        word=(EditText)findViewById(R.id.editText);
        btnState = "date";

        today.setText(present_day);
        GpsPlace.setText(gps);

        //권한 체크
        permissionCheck();

        // 갤러리 버튼 클릭
        gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!checkTitle()){
                    Toast.makeText(getApplicationContext(), "타임캡슐 제목을 입력해주세요.", 0).show();
                }
                else if(checkTitle()) {
                    Intent goGallery = new Intent(Intent.ACTION_PICK);
                    goGallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    goGallery.setType("image/*");
                    startActivityForResult(goGallery, PICK_FROM_ALBUM);
                }
            }
        });

        // 달력 버튼 클릭해서 저장할 날짜 설정
        settingBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btnState.equals("date")) {
                    Intent datePicker = new Intent(getApplicationContext(), DatePickerActivity.class);
                    startActivityForResult(datePicker, 1004);
                    btnState = "lock";
                    settingBTN.setImageResource(R.drawable.blackheartlock);
                } else if (btnState.equals("lock")) {
                    if(checkTitle()==true && checkText()==true){
                        saveDB();
                        finish();
                    }
                }
            }
        });
    }

    // 제목 확인
    private boolean checkTitle() {
        if (!title.getText().toString().isEmpty()) {
            fileTitle = title.getText().toString();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "타임캡슐의 이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 쓴 글 확인
    private boolean checkText() {
        if (!word.getText().toString().isEmpty()) {
            fileText = word.getText().toString();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "타임캡슐의 내용을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 타임캡슐 저장
    public void saveDB() {
        // 타임캡슐 제목
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("Name").setValue(fileTitle);
        // 타임캡슐을 묻는 현재 날짜
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("Date").setValue(present_day);
        // 타임캡슐을 열 날짜
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("OpenDate").setValue(lockDATE);
        // GPS
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("GPS").setValue(gps);
        // 타임캡슐 글
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("word").setValue(fileText);
        // 타임캡슐 사진
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("photo").setValue(photoUri);
        // 상태
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("state").setValue("lock");
        // 위도
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("latitude").setValue(latitude);
        // 경도
        myRF.child("User").child(id).child("TimeCapsule").child("Text").child(fileTitle).child("longitude").setValue(longitude);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 잠기는 날짜 선택
            case 1004: {
                if (resultCode == RESULT_OK) {
                    lockDATE = data.getStringExtra("date");

                    today.setText(present_day);
                    lockDay.setText(lockDATE);
                }
            }

            // 갤러리에서 사진진
            case PICK_FROM_ALBUM: {
                try {
                    photoURI = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoURI);
                    photo.setImageBitmap(bitmap);
                    goToStorage(photoURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // 사진을 스토리지에 저장하는 함수
    public void goToStorage(Uri photoURI){
        Uri file = null;
        file = photoURI;
        uploadTask = storageRef.child("text/"+fileTitle).putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.v("알림", "사진 업로드 실패");
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getURI();
                Toast.makeText(getApplicationContext(), "사진 저장 성공!", 0).show();
            }
        });
    }

    // 스토리지 uri 가져오기
    public void getURI(){
        StorageReference timecapsule = storage.getReferenceFromUrl("gs://diary-731c2.appspot.com").child("text").child(fileTitle);
        timecapsule.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                photoUri = String.valueOf(uri);
            }
        });
    }

    //권한 체크
    private void permissionCheck () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
    }

}