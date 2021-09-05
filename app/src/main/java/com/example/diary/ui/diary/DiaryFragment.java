package com.example.diary.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.DiaryActivity;
import com.example.diary.Emotion;
import com.example.diary.R;
import com.example.diary.SelectTimeCapsuleActivity;
import com.example.diary.TimeCasuleActivity;
import com.example.diary.WriteDiaryActivity;
import com.example.diary.adapter.DiaryAdapter;
import com.example.diary.adapter.openMyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class DiaryFragment extends Fragment {
    private DiaryViewModel diaryViewModel;

    ImageButton wBTN;
    ListView myListView;
    public DiaryAdapter diaryAdapter;

    String id;

    FirebaseDatabase db;
    DatabaseReference myRF;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final Context context = getContext();

        diaryViewModel =
                ViewModelProviders.of(this).get(DiaryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_diary, container, false);

        // DB 정의 초기화 해주기
        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        // 사용자 ID 받아오기
        id = ((DiaryActivity)getActivity()).userid;

        wBTN = (ImageButton)((DiaryActivity)getActivity()).findViewById(R.id.write);
        wBTN.setVisibility(View.VISIBLE);

        TextView title = (TextView)((DiaryActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Diary");

        myListView = (ListView)root.findViewById(R.id.diarylistview);
        diaryAdapter = new DiaryAdapter(id);
        myListView.setAdapter(diaryAdapter);

        wBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteDiaryActivity.class);
                startActivity(intent);
            }
        });

        addDiary();

        diaryAdapter.notifyDataSetChanged();

        return root;
    }

    public void addDiary(){
        myRF.child("User").child(id).child("Diary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // postSnapshot -> 일기 제목
                    String title = postSnapshot.getKey();
                    String write_day = postSnapshot.child("Date").getValue().toString().trim();
                    String polarity = postSnapshot.child("Emotion").getValue().toString();
                    String text = postSnapshot.child("Text").getValue().toString();
                    int emotion = Integer.parseInt(polarity);

                    String day = write_day.substring(10, 12);
                    String date = write_day.substring(14, 15);
                    String today = write_day.substring(0, 14);
                    if(Integer.parseInt(polarity) > 0)
                        diaryAdapter.addItem(day, date, today, title, ContextCompat.getDrawable(getContext(), R.drawable.happyemoty), emotion, text);
                    else if(Integer.parseInt(polarity) == 0)
                        diaryAdapter.addItem(day, date, today, title, ContextCompat.getDrawable(getContext(), R.drawable.question), emotion, text);
                    else if(Integer.parseInt(polarity) < 0)
                        diaryAdapter.addItem(day, date, today, title, ContextCompat.getDrawable(getContext(), R.drawable.bujungemoty), emotion, text);

                    diaryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
