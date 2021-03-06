package com.avalladares.lluviapp.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by avalladares on 26/06/2015.
 */

//Implementamos la clase para que sea parcelable (empaquetar datos)
public class Day implements Parcelable {
    // Implementamos un objeto Creator
    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {

            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
    private double mTime;
    private String mSummary;
    private String mIcon;
    private double mTemperatureMax;
    private String mTimezone;
    public Day() {

    }

    // Método para leer los valores (tienen que ir en el mismo orden que el anterior)(desempaquetar)
    private Day(Parcel in) {
        mTimezone = in.readString();
        mTime = in.readDouble();
        mSummary = in.readString();
        mTemperatureMax = in.readDouble();
        mIcon = in.readString();

    }

    public double getTime() {
        return mTime;
    }

    public void setTime(double time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getTemperatureMax() {

        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {

        mTemperatureMax = temperatureMax;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    // Métodos de la clase parcelable, que nos permite pasar arrays entre actividades en el intent

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    // Método para escribir los valores en el destino (empaquetar)

    public String getDayOfTheWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimezone));
        Date datetime = new Date((long) mTime * 1000);

        return formatter.format(datetime);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTimezone);
        dest.writeDouble(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperatureMax);
        dest.writeString(mIcon);

    }
}
