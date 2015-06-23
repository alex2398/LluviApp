package com.avalladares.lluviapp;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
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
    private int bgId;


    // Member variable (properties about the object)

    // Backgrounds arrays

    public int[] mclear_day = {
            R.drawable.sunny1,
            R.drawable.sunny2,
            R.drawable.sunny3,
            R.drawable.sunny4,
            R.drawable.sunny5
    };

    public int[] mclear_night = {
            R.drawable.clear_night1,
            R.drawable.clear_night2,
            R.drawable.clear_night3,
            R.drawable.clear_night4,
            R.drawable.clear_night5,
    };


    public int[] mrain = {
            R.drawable.rain1,
            R.drawable.rain2,
            R.drawable.rain3,
            R.drawable.rain4,
            R.drawable.rain5,
    };

    public int[] mcloudy = {
            R.drawable.cloudy1,
            R.drawable.cloudy2,
            R.drawable.cloudy3,
            R.drawable.cloudy4,
            R.drawable.cloudy5
    };

    public int[] msnowy = {
            R.drawable.snowy1,
            R.drawable.snowy2,
            R.drawable.snowy3,
            R.drawable.snowy4,
            R.drawable.snowy5
    };

    public int[] mfog = {
            R.drawable.fog1,
            R.drawable.fog2,
            R.drawable.fog3,
            R.drawable.fog4,
            R.drawable.fog5
    };

    public int[] msleet = {
            R.drawable.sleet1,
            R.drawable.sleet2,
            R.drawable.sleet3,
            R.drawable.sleet4,
            R.drawable.sleet5
    };

    public int[] mwind = {
            R.drawable.wind1,
            R.drawable.wind2,
            R.drawable.wind3,
            R.drawable.wind4,
            R.drawable.wind5
    };

    public int[] mcloudy_day = {
            R.drawable.cloudy_day1,
            R.drawable.cloudy_day2,
            R.drawable.cloudy_day3,
            R.drawable.cloudy_day4,
            R.drawable.cloudy_day5
    };

    public int[] mcloudy_night = {
            R.drawable.cloudy_night1,
            R.drawable.cloudy_night2,
            R.drawable.cloudy_night3,
            R.drawable.cloudy_night4,
            R.drawable.cloudy_night5
    };


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

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(mTime * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public String getFormattedTime() {
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

        Random randomgenerator = new Random();
        int random = randomgenerator.nextInt(5);

        switch (mIcon) {
            case "clear-day":
                bgId = mclear_day[random];
                break;
            case "clear-night":
                bgId = mclear_night[random];
                break;
            case "rain":
                bgId = mrain[random];
                break;
            case "snow":
                bgId = msnowy[random];
                break;
            case "sleet":
                bgId = msleet[random];
                break;
            case "wind":
                bgId = mwind[random];
                break;
            case "fog":
                bgId = mfog[random];
                break;
            case "cloudy":
                bgId = mcloudy[random];
                break;
            case "partly-cloudy-day":
                bgId = mcloudy_day[random];
                break;
            case "partly-cloudy-night":
                bgId = mcloudy_night[random];
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

