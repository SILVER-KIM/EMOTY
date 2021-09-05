package com.example.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.adapter.ExListAdapter;
import com.example.diary.item.MyGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class DiaryActivity extends AppCompatActivity implements View.OnClickListener{

    public String userid;
    private DrawerLayout mDrawerLayout;
    ImageButton drawerBTN;
    String name;
    private DatabaseReference DB;

    private View header;
    private ExpandableListView friend_list;
    ArrayList<MyGroup> friend;
    ExListAdapter adapter_expandable;

    TextView profile_name;
    ImageButton profile_emoty;
    FloatingActionButton friend_btn;
    FloatingActionButton add_friend;
    FloatingActionButton message;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close;

    // 로그아웃에 사용하는 파이어베이스 인증 선언
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기

        // 인증 초기화
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        DB = FirebaseDatabase.getInstance().getReference();

        friend_btn = (FloatingActionButton)findViewById(R.id.friend_btn);
        add_friend = (FloatingActionButton)findViewById(R.id.fab1);
        message = (FloatingActionButton)findViewById(R.id.fab2);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        friend_btn.setOnClickListener(this);
        add_friend.setOnClickListener(this);
        message.setOnClickListener(this);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.container) ;
        drawerBTN = (ImageButton)findViewById(R.id.drawer_BTN);
        drawerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Drawer 화면의 사용자 닉네임 수정을 위해 초기화, 선언
                profile_name = (TextView)findViewById(R.id.profile_name);
                profile_emoty = (ImageButton)findViewById(R.id.profile_emoty);
                mDrawerLayout.openDrawer(GravityCompat.START);
                checkNAME(userid);

                profile_emoty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent changeEmoty = new Intent(getApplicationContext(), ChangeEmotyActivity.class);
                        startActivityForResult(changeEmoty, 1004);
                    }
                });
            }
        });

        NavigationView navigationView = (NavigationView)findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.account){
                    Toast.makeText(getApplicationContext(), title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.setting){
                    Toast.makeText(getApplicationContext(), title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.logout){
                    Intent logout = new Intent(getApplicationContext(), HomeActivity.class);
                    SharedPreferences loginINFO = getSharedPreferences("loginINFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginINFO.edit();
                    editor.putString("ID", "");
                    editor.putString("Password", "");
                    editor.commit();
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(),  "로그아웃 성!공!", Toast.LENGTH_SHORT).show();
                    startActivity(logout);
                    finish();
                }

                return true;
            }
        });

        friend = new ArrayList<MyGroup>();
        friend_list = (ExpandableListView)findViewById(R.id.friend_list);
        MyGroup temp = new MyGroup("친구");
        temp.child.add("이단비");
        temp.child.add("김문영");
        friend.add(temp);

        adapter_expandable = new ExListAdapter(getApplicationContext(),
                R.layout.group_parent, R.layout.group_child, friend);
        friend_list.setAdapter(adapter_expandable);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_diary, R.id.navigation_calendar, R.id.navigation_graph)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.friend_btn:
                anim();
                Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab1:
                anim();
                Intent add = new Intent(getApplicationContext(), SearchUserActivity.class);
                add.putExtra("myID", userid);
                startActivity(add);
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    public void anim() {

        if (isFabOpen) {
            add_friend.startAnimation(fab_close);
            message.startAnimation(fab_close);
            add_friend.setClickable(false);
            message.setClickable(false);
            isFabOpen = false;
        } else {
            add_friend.startAnimation(fab_open);
            message.startAnimation(fab_open);
            add_friend.setClickable(true);
            message.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String value = data.getStringExtra("result");
        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                switch (value){
                    case "happy":
                        profile_emoty.setImageResource(R.drawable.smilemoty);
                        break;
                    case "sad":
                        profile_emoty.setImageResource(R.drawable.sademoty);
                        break;
                    case "angry":
                        profile_emoty.setImageResource(R.drawable.angryemoty);
                        break;
                    case "just":
                        profile_emoty.setImageResource(R.drawable.emoty);
                        break;
                }
            }
        }
    }

    public void checkNAME(final String id){
        DB.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.getKey().equals(id)){
                        name = postSnapshot.child("name").getValue().toString().trim();
                        profile_name.setText(name);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(getApplicationContext(), MainActivity.class);
        back.putExtra("id", userid);
        startActivity(back);
        finish();
    }

}
