package com.example.diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerActivity extends Activity {
    TextView date;
    DatePicker datePicker;
    Calendar now;
    ImageButton lock;
    Intent intent;
    String present_date;
    String lock_date;
    String lockDATE;
    SimpleDateFormat mFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_date_picker);

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        date = (TextView)findViewById(R.id.openDate);
        now = Calendar.getInstance();
        lock = (ImageButton)findViewById(R.id.lock);

        intent = new Intent();

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat(" yyyy - MM - dd");
                lock_date = format.format(now.getTime());

                intent.putExtra("date", lock_date);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        long nowDay = System.currentTimeMillis();
        Date nowDate = new Date(nowDay);
        mFormat = new SimpleDateFormat("yyyy - MM - dd");
        present_date = mFormat.format(nowDate);

        //현재 날짜 표시
        date.setText(present_date);

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //최소 날짜 설정
                //view.setMinDate(System.currentTimeMillis()+(24 * 60 * 60 * 1000));

                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, monthOfYear);
                now.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                date.setText(year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth + "일");
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