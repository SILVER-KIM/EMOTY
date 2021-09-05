package com.example.diary.item;

import android.graphics.drawable.Drawable;

public class openItem {

    private String id;
    private String type;
    private Drawable open_icon;
    private String open_title;
    private String open_openDay;
    private String open_closeDay;
    private String gps;
    private double latitude;
    private double longitude;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public String getGPS(){
        return gps;
    }

    public void setGPS(String gps){
        this.gps = gps;
    }


    public void setType(String type){
        this.type = type;
    }

    public Drawable getIcon() {
        return open_icon;
    }

    public void setIcon(Drawable icon) {
        this.open_icon = icon;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getTitle() {
        return open_title;
    }

    public void setTitle(String title) {
        this.open_title = title;
    }

    public String getOpenDay() {
        return open_openDay;
    }

    public void setOpenDay(String openDay) {
        this.open_openDay = openDay;
    }

    public String getCloseDay() {
        return open_closeDay;
    }

    public void setCloseDay(String closeDay) {
        this.open_closeDay = closeDay;
    }

}
