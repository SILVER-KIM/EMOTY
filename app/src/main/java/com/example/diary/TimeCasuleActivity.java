package com.example.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import org.w3c.dom.Text;

public class TimeCasuleActivity extends AppCompatActivity {
    // 로그아웃에 사용하는 파이어베이스 인증 선언
    FirebaseAuth mAuth;

    public String id;
    private DrawerLayout mDrawerLayout;
    ImageButton drawerBTN;
    String name;
    private DatabaseReference DB;

    TextView profile_name;
    ImageButton profile_emoty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_casule);

        // 인증 초기화
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        DB = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기

        mDrawerLayout = (DrawerLayout)findViewById(R.id.container) ;
        drawerBTN = (ImageButton)findViewById(R.id.drawer_BTN);
        drawerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Drawer 화면의 사용자 닉네임 수정을 위해 초기화, 선언
                profile_name = (TextView)findViewById(R.id.profile_name);
                profile_emoty = (ImageButton)findViewById(R.id.profile_emoty);
                mDrawerLayout.openDrawer(GravityCompat.START);
                checkNAME(id);

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
                menuItem.setChecked(false);
                return true;
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
        back.putExtra("id", id);
        startActivity(back);
        finish();
    }

}
