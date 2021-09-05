package com.example.diary;

import android.content.Context;

public class Emotion {
    String text;
    String result;
    public Emotion(String text, Context context) {
        this.text = text;
        /*
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(R.layout.fragment_home, null);
        EditText editText = (EditText) myView.findViewById(R.id.diary_text);

        editText.setText(result);
         */

    }
    public String sendResult(String text){
        String urlstr = "http://api.adams.ai/datamixiApi/omAnalysis?key=1158060063519353442&query="
                + text
                + "&type=1";

        return(urlstr);

        /*BufferedReader br = null;
        try{
            String urlstr = "http://api.adams.ai/datamixiApi/omAnalysis??key=1158060063519353442&query="
                    + text
                    + "&type=1";
            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8"));
            String line;
            while((line = br.readLine()) != null) {
                result = result + line + "\n";
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return(result);
         */
    }
}
