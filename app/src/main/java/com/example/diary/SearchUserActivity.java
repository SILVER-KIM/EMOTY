package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SearchUserActivity extends AppCompatActivity {

    EditText userID;
    ImageButton search_BTN;
    TextView search_Name;
    LinearLayout search_view;
    ImageButton addBTN;

    boolean add_state;

    private boolean state = false;

    FirebaseDatabase db;
    DatabaseReference myRF;

    // 검색한 유저 INFO
    String userName;
    String name;
    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        Intent intent = getIntent();
        myID = intent.getStringExtra("myID");

        userID = (EditText)findViewById(R.id.search_id);
        search_BTN = (ImageButton)findViewById(R.id.search_user_btn);
        search_Name = (TextView)findViewById(R.id.search_user_name);
        search_view = (LinearLayout)findViewById(R.id.search_view);
        addBTN = (ImageButton)findViewById(R.id.addBTN);

        // DB 선언
        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(myID, name);
            }
        });

        search_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userID.equals("")) {
                    name = userID.getText().toString();
                    if(userINFO(name) == true) {
                        search_view.setVisibility(View.VISIBLE);
                        state = false;
                    }
                    else
                        Toast.makeText(getApplicationContext(), "없는 아이디 입니다.", 0).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", 0).show();
            }
        });
    }

    public boolean userINFO(final String id){
        myRF.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.getKey().equals(id)){
                        userName = postSnapshot.child("name").getValue().toString().trim();
                        search_Name.setText(userName);
                        state = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return state;
    }

    public void requetFriend(String FriendID, String myID) {
        myRF.child("Requests").child(FriendID).child("Sender").setValue(myID);
        Toast.makeText(getApplicationContext(), "친구신청 완료", Toast.LENGTH_SHORT).show();
    }


    public void addFriend(final String myID, final String friendID){
        myRF.child("Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getKey().equals(friendID)) {
                            if (postSnapshot.hasChild(myID))
                                Toast.makeText(getApplicationContext(), "이미 친구신청을 한 상태입니다.", 0).show();
                            else
                                myRF.child("Request").child(friendID).child("Sender").push().setValue(myID);
                        } else {
                            requetFriend(friendID, myID);
                            Toast.makeText(getApplicationContext(), "여기", 0).show();
                        }
                    }
                }
                else
                    requetFriend(friendID, myID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}