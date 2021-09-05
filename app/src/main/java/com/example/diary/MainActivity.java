package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        ImageView diary = findViewById(R.id.diary);
        ImageView timecapsule = findViewById(R.id.timecapsule);

        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), DiaryActivity.class);
                intent1.putExtra("id", id);
                startActivity(intent1);
                finish();
            }
        });

        timecapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), TimeCasuleActivity.class);
                intent2.putExtra("id", id);
                startActivity(intent2);
                finish();
            }
        });
    }
}
