package com.example.diary;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlTask extends AsyncTask<String, Void, String> {

    String apiKEY = "1158060063519353442";
    String line, result;

    @Override
    protected String doInBackground(String... strings) {
        try {
            String diary_emotion = "http://api.adams.ai/datamixiApi/tms?key=" + apiKEY + "&query="
                    + strings[0] + "&lang=kor&analysis=om";
            URL url = new URL(diary_emotion);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.setRequestMethod("GET");

            if (urlconnection.getResponseCode() == urlconnection.HTTP_OK) {
                InputStreamReader input = new InputStreamReader(urlconnection.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(input);
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
                Log.i("result : ", result);
            } else {
                Log.i("통신 결과 : ", urlconnection.getResponseCode() + "에러");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}

