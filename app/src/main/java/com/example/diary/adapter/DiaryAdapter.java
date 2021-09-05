package com.example.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.TotalCaptureResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.diary.ChangeEmotionDialog;
import com.example.diary.ShowDiaryActivity;
import com.example.diary.item.DiaryItem;
import com.example.diary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiaryAdapter extends BaseAdapter{
    Context context;

    FirebaseDatabase db;
    DatabaseReference myDB;
    ChangeEmotionDialog customDialog;
    String id;
    String title;

    public DiaryAdapter(String id){
        this.id = id;
    }
    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<DiaryItem> dItems = new ArrayList<>();

    // 다이얼로그 클릭_리스너 선언
    private View.OnClickListener happyL = new View.OnClickListener() {
        public void onClick(View v)
        {
            updateDiary(title, 1);
            customDialog.dismiss();
        }
    };
    private View.OnClickListener quesL = new View.OnClickListener() {
        public void onClick(View v)
        {
            updateDiary(title, 0);
            customDialog.dismiss();
        }
    };
    private View.OnClickListener bujungL = new View.OnClickListener() {
        public void onClick(View v)
        {
            updateDiary(title, -1);
            customDialog.dismiss();
        }
    };

    public void removeItem(int position){
        dItems.remove(position);
    }

    @Override
    public int getCount() {
        return dItems.size();
    }

    @Override
    public DiaryItem getItem(int position) {
        return dItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        // Drawable 선언
        Resources res = context.getResources();
        Drawable happy = ResourcesCompat.getDrawable(res, R.drawable.happyemoty, null);
        Drawable ques = ResourcesCompat.getDrawable(res, R.drawable.question, null);
        Drawable bujung = ResourcesCompat.getDrawable(res, R.drawable.bujungemoty,null);

        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_diary, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView diary_day = (TextView) convertView.findViewById(R.id.diary_day) ;
        TextView diary_date = (TextView) convertView.findViewById(R.id.diary_date) ;
        TextView diary_today = (TextView) convertView.findViewById(R.id.diary_today) ;
        TextView diary_title = (TextView) convertView.findViewById(R.id.diary_title) ;
        ImageButton diary_emotion = (ImageButton)convertView.findViewById(R.id.diary_emotion);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final DiaryItem myItem = getItem(position);

        diary_emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diary = new Intent(context, ShowDiaryActivity.class);
                diary.putExtra("title", myItem.getTitle());
                diary.putExtra("date", myItem.getToday());
                diary.putExtra("text", myItem.getText());
                diary.putExtra("emotion", myItem.getEmotion());
                context.startActivity(diary);
            }
        });

        diary_emotion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*
                Intent intent = new Intent(context, ChangeEmotionActivity.class);
                ((Activity)context).startActivityForResult(intent, 1004);
                 */
                title = myItem.getTitle();
                customDialog = new ChangeEmotionDialog(context, happyL, quesL, bujungL);
                // 커스텀 다이얼로그를 호출한다.
                customDialog.show();
                return false;
            }

        });


       /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        diary_emotion.setImageDrawable(myItem.getEmoty());
        diary_day.setText(myItem.getDay());
        diary_date.setText(myItem.getDate());
        diary_today.setText(myItem.getToday());
        diary_title.setText(myItem.getTitle());

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String day, String date, String today, String title, Drawable emoty, int emotion, String text) {

        DiaryItem dItem = new DiaryItem();

        /* MyItem에 아이템을 setting한다. */
        dItem.setDay(day);
        dItem.setDate(date);
        dItem.setToday(today);
        dItem.setTitle(title);
        dItem.setEmoty(emoty);
        dItem.setEmotion(emotion);
        dItem.setText(text);

        /* mItems에 MyItem을 추가한다. */
        dItems.add(dItem);
    }

    public void updateDiary(final String diaryNAME, final int polarCount){
        myDB.child("User").child(id).child("Diary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // postSnapshot -> 일기 제목
                    String title = postSnapshot.getKey();
                    if(title.equals(diaryNAME)){
                        Toast.makeText(context, "으악!" + polarCount, 0).show();
                        myDB.child("User").child(id).child("Diary").child(title).child("Emotion").setValue(polarCount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}