package com.example.diary.item;

import android.graphics.drawable.Drawable;

public class SearchItem {

    private String date;
    private String title;
    private String text;
    private int emotion;
    private Drawable emoty;

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

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
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
