package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowDiaryActivity extends Activity {

    String title;
    String date;
    String text;
    int emotion;

    TextView show_day;
    TextView show_titleText;
    TextView show_text;
    TextView show_emoty_text;
    ImageView show_emoty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        // intent로 데이터베이스 경로에 필요한 id, type, title값을 받아와서 초기화해준다.
        Intent diary = getIntent();
        title = diary.getStringExtra("title");
        text = diary.getStringExtra("text");
        date = diary.getStringExtra("date");
        emotion = diary.getIntExtra("emotion", 0);

        show_day = (TextView)findViewById(R.id.show_day);
        show_titleText = (TextView)findViewById(R.id.show_titleText);
        show_text = (TextView)findViewById(R.id.show_text);
        show_emoty_text = (TextView)findViewById(R.id.show_emoty_text);
        show_emoty = (ImageView)findViewById(R.id.show_emoty);

        show_day.setText(date);
        show_titleText.setText(title);
        show_text.setText(text);

        if(emotion >= 1) {
            show_emoty.setImageResource(R.drawable.happyemoty);
            show_emoty_text.setText("긍정");
        }
        else if(emotion < 0) {
            show_emoty.setImageResource(R.drawable.bujungemoty);
            show_emoty_text.setText("부정");
        }

    }
}
