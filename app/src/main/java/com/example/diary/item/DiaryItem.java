package com.example.diary.item;

import android.graphics.drawable.Drawable;

public class DiaryItem {

    private String day;         // 요일
    private String date;        // 일자
    private String today;
    private String title;
    private Drawable emoty;
    private int emotion;
    private String text;

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public int getEmotion(){
        return emotion;
    }

    public void setEmotion(int emotion){
        this.emotion = emotion;
    }

    public String getDay(){
        return day;
    }

    public void setDay(String day){
        this.day = day;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }
    public String getToday(){
        return today;
    }

    public void setToday(String today){
        this.today = today;
    }
    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Drawable getEmoty()
    {
        return emoty;
    }

    public void setEmoty(Drawable emoty)
    {
        this.emoty = emoty;
    }
}
