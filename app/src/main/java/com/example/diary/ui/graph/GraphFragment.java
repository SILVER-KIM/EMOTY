package com.example.diary.ui.graph;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary.DiaryActivity;
import com.example.diary.R;
import com.example.diary.TimeCasuleActivity;
import com.example.diary.adapter.DiaryAdapter;
import com.example.diary.adapter.SearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    EditText search_text;
    ImageButton search_btn;
    ListView search_listView;

    public SearchAdapter searchAdapter;

    String id;

    FirebaseDatabase db;
    DatabaseReference myRF;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                ViewModelProviders.of(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);

        // DB 정의 초기화 해주기
        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        // 사용자 ID 받아오기
        id = ((DiaryActivity)getActivity()).userid;

        TextView title = (TextView)((DiaryActivity)getActivity()).findViewById(R.id.toolbar_title);
        title.setText("Search");

        search_text = (EditText)root.findViewById(R.id.search_text);
        search_btn = (ImageButton)root.findViewById(R.id.search_btn);
        search_listView = (ListView)root.findViewById(R.id.searchlistview);

        searchAdapter = new SearchAdapter();
        search_listView.setAdapter(searchAdapter);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = search_text.getText().toString();
                search_text(text);
            }
        });

        search_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String text = search_text.getText().toString();
                search_text(text);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return root;
    }

    public void search_text(final String text){
        searchAdapter.clear();
        myRF.child("User").child(id).child("Diary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String title = postSnapshot.getKey();
                    String date = postSnapshot.child("Date").getValue().toString();
                    String textd = postSnapshot.child("Text").getValue().toString();
                    int emotion = Integer.parseInt(postSnapshot.child("Emotion").getValue().toString());
                    if(title.contains(text)) {
                        if(emotion >= 1)
                            searchAdapter.addItem(date, title, ContextCompat.getDrawable(getContext(), R.drawable.happyemoty), textd, emotion);
                        else if(emotion < 0)
                            searchAdapter.addItem(date, title, ContextCompat.getDrawable(getContext(), R.drawable.bujungemoty), textd, emotion);
                    }
                    else if(textd.contains(text)){
                        if(emotion >= 1)
                            searchAdapter.addItem(date, title, ContextCompat.getDrawable(getContext(), R.drawable.happyemoty), textd, emotion);
                        else if(emotion < 0)
                            searchAdapter.addItem(date, title, ContextCompat.getDrawable(getContext(), R.drawable.bujungemoty), textd, emotion);
                    }
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
