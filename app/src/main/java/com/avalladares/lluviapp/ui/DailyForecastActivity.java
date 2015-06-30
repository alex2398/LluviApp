package com.avalladares.lluviapp.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.adapters.DayAdapter;
import com.avalladares.lluviapp.weather.Day;

import java.util.Arrays;


// Extendemos la clase a ListActivity para poder incluir nuestro custom list item
public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aquí es donde indicamos el activity que vamos a rellenar
        setContentView(R.layout.activity_daily_forecast);


        // Datos que recibimos de MainActivity en el intent

        Intent intent = getIntent();
        RelativeLayout mBackground = (RelativeLayout) findViewById(R.id.backgroundList);
        TextView mCity = (TextView) findViewById(R.id.locationTextView);

        // Background
        int bg = intent.getIntExtra("background", 0);

        Drawable draw = getResources().getDrawable(bg);
        mBackground.setBackground(draw);

        // Nombre de la ciudad
        String city = intent.getStringExtra("city");
        mCity.setText(city);

        // Array de días con los datos de cada día
        // Con el método getParecelableArrayExtra recibimos el extra en el intent de tipo parcelable
        // con el tag "DAILY_FORECAST"

        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);

        // Guardamos el array recibido en una variable mDays
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        // Creamos un adaptador al que le pasamos el contexto y el array de días (mDays) que hemos
        // recibido en el intent
        DayAdapter adapter = new DayAdapter(this, mDays);

        // Creamos una lista a partir del adaptador dentro de nuestro activity.
        // De este modo, hemos seleccionado el activity a rellenar (activity_daily_forecast), donde
        // hemos creado un listView con id android:id/List (requerido), y lo hemos asociado con el
        // array que le hemos pasado al adaptador, que a su vez rellena el daily_list_item con el array de dias
        // obtenido de la anterior actividad (...uf!)

        setListAdapter(adapter);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax() + "";

        String message = String.format("El %s la maxima sera de %s \n y estara %s", dayOfTheWeek, highTemp, conditions);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }
}
