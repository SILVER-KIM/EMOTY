package com.example.diary.item;

import android.graphics.drawable.Drawable;

public class MyItem {

    private String id;
    private String type;
    private Drawable icon;
    private String title;
    private String openDay;
    private String closeDay;
    private String dDay;
    private String gps;
    private double latitude;
    private double longitude;

    private boolean state = false;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean getState(){
        return state;
    }

    public void setState(boolean change){
        this.state = change;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setGps(String gps){
        this.gps = gps;
    }

    public String getGps(){
        return gps;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpenDay() {
        return openDay;
    }

    public void setOpenDay(String openDay) {
        this.openDay = openDay;
    }

    public String getCloseDay() {
        return closeDay;
    }

    public void setCloseDay(String closeDay) {
        this.closeDay = closeDay;
    }

    public String getdDay() {
        return dDay;
    }

    public void setdDay(String dDay) {
        this.dDay = dDay;
    }
}