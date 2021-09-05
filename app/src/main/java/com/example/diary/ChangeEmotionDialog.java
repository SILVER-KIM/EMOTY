package com.example.diary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

public class ChangeEmotionDialog extends Dialog {

    ImageButton happy;
    ImageButton question;
    ImageButton bujung;

    private View.OnClickListener happyListener;
    private View.OnClickListener questionListener;
    private View.OnClickListener bujungListener;

    public ChangeEmotionDialog(@NonNull Context context, View.OnClickListener happyL, View.OnClickListener quesL, View.OnClickListener bujungL) {
        super(context);
        this.happyListener = happyL;
        this.questionListener = quesL;
        this.bujungListener = bujungL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_diary_emotion);


        happy = (ImageButton)findViewById(R.id.happy);
        question = (ImageButton)findViewById(R.id.question);
        bujung = (ImageButton)findViewById(R.id.bujung);

        happy.setOnClickListener(happyListener);
        question.setOnClickListener(questionListener);
        bujung.setOnClickListener(bujungListener);
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
