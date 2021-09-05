package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class SelectTimeCapsuleActivity extends Activity {

    ImageButton text_TC;
    ImageButton voice_TC;

    String id;
    String location;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_time_capsule);

        Intent select = getIntent();
        id = select.getStringExtra("id");
        location = select.getStringExtra("GPS");
        latitude = select.getDoubleExtra("latitude", 0);
        longitude = select.getDoubleExtra("longitude", 0);

        text_TC = (ImageButton)findViewById(R.id.text_TC);
        voice_TC = (ImageButton)findViewById(R.id.voice_TC);

        text_TC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent text = new Intent(getApplicationContext(), WriteTimeCapsuleActivity.class);
                text.putExtra("id", id);
                text.putExtra("GPS", location);
                text.putExtra("latitude", latitude);  // 위도
                text.putExtra("longitude", longitude);   //경도
                startActivity(text);
                finish();
            }
        });

        voice_TC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voice = new Intent(getApplicationContext(), RecordingActivity.class);
                voice.putExtra("id", id);
                voice.putExtra("GPS", location);
                voice.putExtra("latitude", latitude);  // 위도
                voice.putExtra("longitude", longitude);   //경도
                startActivity(voice);
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
