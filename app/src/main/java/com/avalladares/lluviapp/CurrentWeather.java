package com.avalladares.lluviapp;

import com.avalladares.lluviapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Alex on 19/06/2015.
 */
public class CurrentWeather {
    private String mIcon;
    private long mTime;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private double mTemperature;
    private String mTimeZone;

    public String getBgColor() {
        return mBgColor;
    }

    String mBgColor;
    int mBgPicture;

    public int getBgPicture() {
        return mBgPicture;
    }

    public void setBgPicture(int bgPicture) {
        mBgPicture = bgPicture;
    }




    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        int iconId = R.mipmap.clear_day;



        if (mIcon.equals("clear-day")) {
            iconId = R.mipmap.clear_day;
            mBgPicture=R.drawable.sunny_no_clouds;
            mBgColor = "#800081ff";
        } else if (mIcon.equals("clear-night")) {
            iconId = R.mipmap.clear_night;
            mBgPicture=R.drawable.clear_night;
            mBgColor = "#f1022d53";
        } else if (mIcon.equals("rain")) {
            iconId = R.mipmap.rain;
            mBgPicture=R.drawable.rainy;
            mBgColor = "40288DAB";
        } else if (mIcon.equals("snow")) {
            iconId = R.mipmap.snow;
            mBgPicture=R.drawable.snow;
            mBgColor = "#406482ab";
        } else if (mIcon.equals("sleet")) {
            iconId = R.mipmap.sleet;
            mBgPicture=R.drawable.sleet;
            mBgColor = "#406482ab";
        } else if (mIcon.equals("wind")) {
            iconId = R.mipmap.wind;
            mBgPicture=R.drawable.windy;
            mBgColor = "#406482ab";
        } else if (mIcon.equals("fog")) {
            iconId = R.mipmap.fog;
            mBgPicture=R.drawable.fog;
            mBgColor = "#406482ab";
        } else if (mIcon.equals("cloudy")) {
            iconId = R.mipmap.cloudy;
            mBgPicture=R.drawable.cloudy;
            mBgColor = "#632D3F54";
        } else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.mipmap.partly_cloudy;
            mBgPicture=R.drawable.sunny_cloudy;
            mBgColor = "#406482ab";
        } else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.mipmap.cloudy_night;
            mBgPicture=R.drawable.cloudy_night;
            mBgColor = "CD273345";
        }

        return iconId;

    }


    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFormattedDate (){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(mTime * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public String getFormattedTime (){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(mTime * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public int getHumidity() {
        return (int) Math.round(mHumidity * 100);
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        return (int) Math.round(mPrecipChance * 100);
    }

    public void setPrecipChance(double precipiChance) {
        mPrecipChance = precipiChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }
}
