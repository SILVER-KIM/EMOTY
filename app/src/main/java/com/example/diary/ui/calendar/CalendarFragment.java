package com.example.diary.ui.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.LuhnChecksumValidator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.DiaryActivity;
import com.example.diary.R;
import com.example.diary.adapter.CalendarAdapter;
import com.example.diary.item.DayInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    ArrayList<String> dayNum = new ArrayList<>();
    ArrayList<Integer> dayEmotion = new ArrayList<>();

    private CalendarViewModel calendarViewModel;
    GridView monthView;
    CalendarAdapter calendarAdapter;

    public static int SUNDAY        = 1;
    public static int MONDAY        = 2;
    public static int TUESDAY       = 3;
    public static int WEDNSESDAY    = 4;
    public static int THURSDAY      = 5;
    public static int FRIDAY        = 6;
    public static int SATURDAY      = 7;

    private TextView mTvCalendarTitle;

    private ArrayList<DayInfo> mDayList;


    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase db;
    DatabaseReference myDB;

    Calendar mLastMonthCalendar;
    Calendar mThisMonthCalendar;
    Calendar mNextMonthCalendar;

    ImageButton bLastMonth;
    ImageButton bNextMonth;

    String id;
    String state;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        LinearLayout header_linear = (LinearLayout)root.findViewById(R.id.header_linear);
        TextView title = (TextView)((DiaryActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Calendar");

        // 사용자 ID 받아오기
        id = ((DiaryActivity)getActivity()).userid;

        // 파이어베이스 초기화
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();
        storage = FirebaseStorage.getInstance("gs://diary-731c2.appspot.com");
        storageRef = storage.getReference();

        checkDB();

        bLastMonth = (ImageButton)root.findViewById(R.id.calendar_prev_button);
        bNextMonth = (ImageButton) root.findViewById(R.id.calendar_next_button);

        mTvCalendarTitle = (TextView)root.findViewById(R.id.calendar_date_display);

        monthView = (GridView)root.findViewById(R.id.calendar_grid);

        bLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThisMonthCalendar = getLastMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
            }
        });

        bNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThisMonthCalendar = getNextMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
            }
        });

        //monthView.setOnItemClickListener();
        mDayList = new ArrayList<DayInfo>();
        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        initCalendarAdapter();
        // 이번달 의 캘린더 인스턴스를 생성한다.
        mThisMonthCalendar = Calendar.getInstance();
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mThisMonthCalendar);
    }

    /**
     * 달력을 셋팅한다.
     *
     * @param calendar 달력에 보여지는 이번달의 Calendar 객체
     */
    private void getCalendar(Calendar calendar)
    {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        mDayList.clear();

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);
        Log.e("지난달 마지막일", calendar.get(Calendar.DAY_OF_MONTH)+"");

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);
        Log.e("이번달 시작일", calendar.get(Calendar.DAY_OF_MONTH)+"");

        if(dayOfMonth == SUNDAY)
        {
            dayOfMonth += 7;
        }

        lastMonthStartDay -= (dayOfMonth-1)-1;


        // 캘린더 타이틀(년월 표시)을 세팅한다.
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");

        DayInfo day;

        Log.e("DayOfMOnth", dayOfMonth+"");

        for(int i=0; i<dayOfMonth-1; i++)
        {
            final int date = lastMonthStartDay+i;
            day = new DayInfo();
            day.setDay(Integer.toString(date));
            day.setMonth(Integer.toString(mThisMonthCalendar.get(Calendar.MONTH)));
            day.setYear(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)));
            day.setInMonth(false);
            day.setMyDay(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)) + "-" + Integer.toString(mThisMonthCalendar.get(Calendar.MONTH))
                    + "-" + Integer.toString(date));
            for(int j = 0; j < dayNum.size(); j++){
                if(dayNum.get(j).equals(String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)) + "-"
                        + String.valueOf(mThisMonthCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(date))) {
                    if (dayEmotion.get(j) == 1)
                        day.setState("happy");
                    else
                        day.setState("bujung");
                }
            }
            mDayList.add(day);
        }
        for(int i=1; i <= thisMonthLastDay; i++)
        {
            final int count = i;
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setMonth(Integer.toString(mThisMonthCalendar.get(Calendar.MONTH) + 1));
            day.setYear(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)));
            day.setInMonth(true);
            day.setMyDay(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)) + "-" + Integer.toString(mThisMonthCalendar.get(Calendar.MONTH) + 1)
                    + "-" + Integer.toString(i));
            for(int j = 0; j < dayNum.size(); j++){
                if(dayNum.get(j).equals(String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)) + "-"
                        + String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(i))) {
                    if (dayEmotion.get(j) == 1)
                        day.setState("happy");
                    else
                        day.setState("bujung");
                }
            }
            mDayList.add(day);
        }
        for(int i=1; i<42-(thisMonthLastDay+dayOfMonth-1)+1; i++)
        {
            final int count = i;
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setMonth(Integer.toString(mThisMonthCalendar.get(Calendar.MONTH) + 2));
            day.setYear(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)));
            day.setInMonth(false);
            day.setMyDay(Integer.toString(mThisMonthCalendar.get(Calendar.YEAR)) + "-" + Integer.toString(mThisMonthCalendar.get(Calendar.MONTH) + 2)
                    + "-" + Integer.toString(i));
            for(int j = 0; j < dayNum.size(); j++){
                if(dayNum.get(j).equals(String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)) + "-"
                        + String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 2) + "-" + String.valueOf(i))) {
                    if (dayEmotion.get(j) == 1)
                        day.setState("happy");
                    else
                        day.setState("bujung");
                }
            }
            mDayList.add(day);
        }
        initCalendarAdapter();
    }

    public void checkDB(){
        myDB.child("User").child(id).child("Diary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    state = "";
                    String title = postSnapshot.getKey();
                    String Date = postSnapshot.child("Day").getValue().toString().trim();
                    dayNum.add(Date);
                    int  Emotion = Integer.parseInt(postSnapshot.child("Emotion").getValue().toString());
                    dayEmotion.add(Emotion);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /**
     * 지난달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return LastMonthCalendar
     */
    private Calendar getLastMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    /**
     * 다음달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return NextMonthCalendar
     */
    private Calendar getNextMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
    {

    }

    private void initCalendarAdapter()
    {
        calendarAdapter = new CalendarAdapter(getActivity(), R.layout.day, mDayList);
        monthView.setAdapter(calendarAdapter);
    }

}