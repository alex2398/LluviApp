package com.avalladares.lluviapp.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.adapters.DayAdapter;
import com.avalladares.lluviapp.weather.Day;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        RelativeLayout mBackground = (RelativeLayout) findViewById(R.id.backgroundList);


        int bg = intent.getIntExtra("background", 0);
        Drawable draw = getResources().getDrawable(bg);
        mBackground.setBackground(draw);



        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);


    }
}