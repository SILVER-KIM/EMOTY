package com.example.diary;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {
    FirebaseAuth mAuth;
    EditText id;
    EditText pw;
    CheckBox auto_Login;

    String idTXT;
    String pwTXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        id = (EditText)findViewById(R.id.id);
        pw = (EditText)findViewById(R.id.pw);
        Button loginBTN = (Button)findViewById(R.id.loginBTN);
        Button findPW = (Button)findViewById(R.id.findPW);
        ImageButton closeBTN = (ImageButton)findViewById(R.id.closeBTN);
        ImageButton showBTN = (ImageButton)findViewById(R.id.pwshow);
        auto_Login = (CheckBox)findViewById(R.id.auto_Login);

        pw.setTransformationMethod(PasswordTransformationMethod.getInstance());               // 비밀번호 숨기기

        showBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        pw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP   :
                        pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });


        findPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTXT = id.getText().toString().trim();
                if (!idTXT.isEmpty()) {
                    mAuth.getInstance().sendPasswordResetEmail(idTXT).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(getApplicationContext(), "비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "비밀번호 변경 메일을 전송하지못했습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(), "재설정할 이메일 주소를 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        });
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTXT = id.getText().toString().trim();
                pwTXT = pw.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(idTXT, pwTXT).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(auto_Login.isChecked()){
                                SharedPreferences loginINFO = getSharedPreferences("loginINFO", MODE_PRIVATE);
                                SharedPreferences.Editor editor = loginINFO.edit();
                                editor.putString("ID", idTXT);
                                editor.putString("Password", pwTXT);
                                editor.commit();
                            }
                            int index = idTXT.indexOf("@");
                            String idName = idTXT.substring(0, index);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("id", idName);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
