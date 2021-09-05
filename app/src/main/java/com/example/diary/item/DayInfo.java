package com.example.diary.item;

import android.util.Log;

/**
 * 하루의 날짜정보를 저장하는 클래스
 */
public class DayInfo
{
    private String year;
    private String date;
    private String month;
    private boolean inMonth;
    private String polarity;
    private String myDay;
    private String state;
    /**
     * 날짜를 반환한다.
     *
     * @return date 날짜
     */
    public String getDay()
    {
        return date;
    }

    /**
     * 날짜를 저장한다.
     *
     * @param date 날짜
     */
    public void setDay(String date)
    {
        this.date = date;
    }

    /**
     * 이번달의 날짜인지 정보를 반환한다.
     *
     * @return inMonth(true/false)
     */
    public boolean isInMonth()
    {
        return inMonth;
    }

    /**
     * 이번달의 날짜인지 정보를 저장한다.
     *
     * @param inMonth(true/false)
     */
    public void setInMonth(boolean inMonth)
    {
        this.inMonth = inMonth;
    }


    public String getState()
    {
        return state;
    }

    public void setState(String state){
        Log.e("들어옴", state);
        this.state = state;
    }

    public String getMonth() { return month; }

    public void setMonth(String month) { this.month = month; }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }

    public String getPolarity() { return polarity; }

    public void setPolarity(String polarity) { this.polarity = polarity; }

    public String getMyDay() { return myDay; }

    public void setMyDay(String myDay) { this.myDay = myDay; }

}