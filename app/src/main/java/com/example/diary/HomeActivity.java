package com.example.diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Debug;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HomeActivity extends Activity {
    private FirebaseAuth mAuth;

    String id;
    String pw;

    FirebaseDatabase db;
    DatabaseReference myDB;

    Intent intent;
    Intent intent2;
    Intent intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();

        LinearLayout background = findViewById(R.id.back);
        Button login = (Button)findViewById(R.id.login);
        Button join = (Button)findViewById(R.id.join);

        SharedPreferences loginINFO = getSharedPreferences("loginINFO", MODE_PRIVATE);
        id = loginINFO.getString("ID", "");
        pw = loginINFO.getString("Password", "");

        if(!id.equals("") && !pw.equals("")) {
            checkDB(id, pw);
        }

        intent = new Intent(getApplicationContext(), JoinActivity.class);
        intent2 = new Intent(getApplicationContext(), LoginActivity.class);
        intent3 = new Intent(getApplicationContext(), MainActivity.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent2);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        getSigneture(getApplicationContext());
    }

    public static String getSigneture(Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for(int i = 0; i < packageInfo.signatures.length; i++){
                Signature signature = packageInfo.signatures[i];
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    System.out.println("으악" + Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }

        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    public void checkDB(String id, final String pw){
        int index = id.indexOf("@");
        final String idName = id.substring(0, index);
        myDB.child("User").child(idName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String password = dataSnapshot.child("password").getValue().toString().trim();
                if(password.equals(pw)) {
                    intent3.putExtra("id", idName);
                    startActivity(intent3);
                    Toast.makeText(getApplicationContext(), "자동로그인 되었습니다:)", 0).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
