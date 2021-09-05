package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class ChangeEmotyActivity extends Activity {

    ImageButton happyEmoty;
    ImageButton sadEmoty;
    ImageButton angryEmoty;
    ImageButton justEmoty;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_emoty);

        intent = new Intent();

        happyEmoty = (ImageButton)findViewById(R.id.happyEmoty);
        sadEmoty = (ImageButton)findViewById(R.id.sadEmoty);
        angryEmoty = (ImageButton)findViewById(R.id.angryEmoty);
        justEmoty = (ImageButton)findViewById(R.id.emoty);

        happyEmoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result", "happy");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        sadEmoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result", "sad");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        angryEmoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result", "angry");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        justEmoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result", "just");
                setResult(RESULT_OK, intent);
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