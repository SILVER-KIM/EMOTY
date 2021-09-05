package com.example.diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.diary.ui.diary.DiaryFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class WriteDiaryActivity extends Activity {

    EditText d_titleText;
    EditText d_editText;

    TextView d_text;
    TextView d_present_day;
    TextView explain;
    TextView anal_text;
    ImageButton anal_emoty;

    ImageButton analysis;

    Dialog saveDialog;

    int polarCount = 0;
    int scoreCount = 0;

    // ArrayList를 사용해서 각 문장에대한 값들을 저장한다.
    ArrayList<String> polarity_list = new ArrayList<String>();
    ArrayList<String> score_list = new ArrayList<String>();
    ArrayList<String> sentiword_list = new ArrayList<String>();

    FirebaseDatabase db;
    DatabaseReference myRF;

    String id, pw;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일");
    String present_day = mFormat.format(date);

    SimpleDateFormat aFormat = new SimpleDateFormat("yyyy-M-dd");
    String today = aFormat.format(date);

    String text;
    String setText = "";
    String result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_diary);

        db = FirebaseDatabase.getInstance();
        myRF = db.getReference();

        SharedPreferences loginINFO = getSharedPreferences("loginINFO", MODE_PRIVATE);
        id = loginINFO.getString("ID", "");
        pw = loginINFO.getString("Password", "");

        d_titleText = (EditText)findViewById(R.id.d_titleText);
        d_editText = (EditText)findViewById(R.id.d_editText);
        d_text = (TextView)findViewById(R.id.d_text);
        d_present_day = (TextView)findViewById(R.id.d_present_day);
        analysis = (ImageButton)findViewById(R.id.analysis);
        explain = (TextView)findViewById(R.id.explain);
        anal_text = (TextView)findViewById(R.id.anal_text);
        anal_emoty = (ImageButton) findViewById(R.id.anal_emoty);

        String content = d_text.getText().toString();
        SpannableString spannableString = new SpannableString(content);

        d_present_day.setText(present_day);

        saveDialog = new Dialog(this);

        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!d_editText.getText().toString().equals("") && !d_titleText.getText().toString().equals("")){
                    text = d_editText.getText().toString();
                    try {
                        result = new UrlTask().execute(text).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    urlJsonParser(result);

                }
                else if(d_editText.getText().toString().equals("") && !d_titleText.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "일기를 작성해주세요.", 0).show();
                else if(!d_editText.getText().toString().equals("") && d_titleText.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "일기 제목을 작성해주세요.", 0).show();
                else
                    Toast.makeText(getApplicationContext(), "일기 제목과 내용을 작성해주세요.", 0).show();
            }
        });

        anal_emoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(polarCount > 0)
                    ShowDialog("긍정");
                else if(polarCount < 0)
                    ShowDialog("부정");
                else
                    ShowDialog("중립");
            }
        });

    }
    private void ShowDialog(String emotion)
    {
        saveDialog.setContentView(R.layout.custom_save);

        ImageButton save_cancle = (ImageButton)saveDialog.findViewById(R.id.save_cancle);
        ImageView save_image = (ImageView)saveDialog.findViewById(R.id.save_image);
        Button save_diary = (Button)saveDialog.findViewById(R.id.save_diary);

        if(emotion.equals("긍정"))
            save_image.setImageResource(R.drawable.happyemoty);
        else if(emotion.equals("부정"))
            save_image.setImageResource(R.drawable.bujungemoty);
        else
            save_image.setImageResource(R.drawable.question);

        save_cancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveDialog.dismiss();
            }
        });

        save_diary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int index = id.indexOf("@");
                final String idName = id.substring(0, index);
                // 일기 내용 데이터베이스에 저장
                myRF.child("User").child(idName).child("Diary").child(d_titleText.getText().toString().trim()).child("Text").setValue(d_editText.getText().toString());
                myRF.child("User").child(idName).child("Diary").child(d_titleText.getText().toString().trim()).child("Date").setValue(present_day);
                myRF.child("User").child(idName).child("Diary").child(d_titleText.getText().toString().trim()).child("Day").setValue(today);
                myRF.child("User").child(idName).child("Diary").child(d_titleText.getText().toString().trim()).child("Emotion").setValue(polarCount);
                myRF.child("User").child(idName).child("Diary").child(d_titleText.getText().toString().trim()).child("Score").setValue(scoreCount);
                saveDialog.dismiss();
                finish();
            }
        });

        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.show();
    }

    // 텍스트 하이라이트
    public void highlight () {
        String content = text;
        SpannableString spannableString = new SpannableString(content);

        for(int i = 0; i < sentiword_list.size(); i++) {
            String word = sentiword_list.get(i);
            int start = content.indexOf(word);
            int end = start + word.length();
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#FFD8D8")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            d_text.setText(spannableString);
        }
    }

    public void settingText(String[] array, int count){
        for(int i = 0; i < array.length; i++){
            if(i == 0)
                polarity_list.add(array[i]);
            else if(i == 1)
                score_list.add(array[i]);
            else
                sentiword_list.add(array[i]);
        }
        checkEmotion();
    }

    public void checkEmotion(){
        for(int i = 0; i < polarity_list.size(); i++){
            polarCount += Integer.parseInt(polarity_list.get(i));
            scoreCount += Integer.parseInt(score_list.get(i));
            Toast.makeText(getApplicationContext(), sentiword_list.get(i), 0).show();
        }

        analysis.setVisibility(View.INVISIBLE);
        explain.setVisibility(View.INVISIBLE);
        anal_text.setVisibility(View.VISIBLE);
        anal_emoty.setVisibility(View.VISIBLE);

        if(polarCount > 0){
            anal_text.setText("긍정");
            anal_emoty.setImageResource(R.drawable.happyemoty);
        }
        else if(polarCount == 0){
            anal_text.setText("중립");
            anal_emoty.setImageResource(R.drawable.question);
        }
        else if(polarCount < 0) {
            anal_text.setText("부정");
            anal_emoty.setImageResource(R.drawable.bujungemoty);
        }
    }

    public String[] urlJsonParser(String jsonString) {
        String polarity = null;
        String score = null;
        String sentiword = null;

        String[] arraysum = new String[3];
        try {
            JSONObject return_object = new JSONObject(jsonString).getJSONObject("return_object");
            String sentence = return_object.getString("sentence");
            JSONArray  j_sa = new JSONArray(sentence);

            for (int i = 0; i < j_sa.length(); i++) {
                HashMap map = new HashMap<>();
                JSONObject jObject = j_sa.getJSONObject(i);

                if(jObject.has("sa")) {

                    String sa = jObject.getString("sa");
                    JSONObject sa_json = new JSONObject(sa);

                    polarity = sa_json.getString("polarity");
                    score = sa_json.getString("score");
                    sentiword = sa_json.getString("sentiword");

                    arraysum[0] = polarity;
                    arraysum[1] = score;
                    arraysum[2] = sentiword;

                    if (sentiword.length() >= 3)
                        settingText(arraysum, i);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arraysum;
    }
}
