package com.avalladares.lluviapp;

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

    public String getFormattedTime () {
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

    public int getBgId() {
        int bgId=R.drawable.sunny_no_clouds;

        switch (mIcon) {
            case "clear-day":
                bgId = R.drawable.sunny_no_clouds;
                break;
            case "clear-night":
                bgId = R.drawable.clear_night;
                break;
            case "rain":
                bgId = R.drawable.rainy;
                break;
            case "snow":
                bgId = R.drawable.snow;
                break;
            case "sleet":
                bgId = R.drawable.sleet;
                break;
            case "wind":
                bgId = R.drawable.windy;
                break;
            case "fog":
                bgId = R.drawable.fog;
                break;
            case "cloudy":
                bgId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                bgId = R.drawable.sunny_cloudy;
                break;
            case "partly-cloudy-night":
                bgId = R.drawable.cloudy_night;
                break;
        }

        return bgId;

    }

    public int getIconId() {
        int iconId = R.mipmap.clear_day;

        switch (mIcon) {
            case "clear-day":
                iconId = R.mipmap.clear_day;
                break;
            case "clear-night":
                iconId = R.mipmap.clear_night;
                break;
            case "rain":
                iconId = R.mipmap.rain;
                break;
            case "snow":
                iconId = R.mipmap.snow;
                break;
            case "sleet":
                iconId = R.mipmap.sleet;
                break;
            case "wind":
                iconId = R.mipmap.wind;
                break;
            case "fog":
                iconId = R.mipmap.fog;
                break;
            case "cloudy":
                iconId = R.mipmap.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.mipmap.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.mipmap.cloudy_night;
                break;
        }

        return iconId;

    }
}
