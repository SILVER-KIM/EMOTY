package com.example.diary;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinActivity extends Activity {
    // Firebase DB연결
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRF = db.getReference();

    // Auth를 사용하기위한 인스턴스 선언
    private FirebaseAuth mAuth;

    EditText password;
    EditText email;
    EditText checkpassword;
    EditText name;

    String mail, pw, chpw, nm; // 메일, 비밀번호, 비번확인, 이름 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // Auth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // xml view 초기화
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        checkpassword = (EditText)findViewById(R.id.checkpassword);
        name = (EditText)findViewById(R.id.name);

        ImageButton pwShow = (ImageButton)findViewById(R.id.pwshow);
        ImageButton chpwShow = (ImageButton)findViewById(R.id.chpwshow);

        Button joinBTN = (Button)findViewById(R.id.joinBTN);

        // 비밀번호 보이기, 숨기기 기능 구현
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());               // 비밀번호 숨기기
        checkpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        pwShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP   :
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });

        chpwShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        checkpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP   :
                        checkpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });

        joinBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString().trim();
                pw = password.getText().toString().trim();
                chpw = checkpassword.getText().toString().trim();
                nm = name.getText().toString().trim();

                if(pw.equals(chpw) && !nm.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(mail, pw).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(JoinActivity.this, "비밀번호를 다시 입력해주세요(6자리 이상).", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(JoinActivity.this, "email 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(JoinActivity.this, "이미존재하는 email 입니다.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(JoinActivity.this, "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "회원가입 성공:-)", Toast.LENGTH_SHORT).show();
                                int index = mail.indexOf("@");
                                String id = mail.substring(0, index);
                                myRF.child("User").child(id).child("name").setValue(nm);
                                myRF.child("User").child(id).child("password").setValue(pw);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else if(!pw.equals(chpw) && !nm.isEmpty() || !pw.equals(chpw) && nm.isEmpty())
                    Toast.makeText(getApplicationContext(), "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show();
                else if(pw.equals(chpw) && nm.isEmpty())
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
