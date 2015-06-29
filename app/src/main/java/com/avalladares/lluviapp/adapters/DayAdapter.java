package com.avalladares.lluviapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.weather.Day;

/**
 * Created by Alex on 26/06/2015.
 */
public class DayAdapter extends BaseAdapter {

    // Adaptador que convierte un objeto a una vista
    // En este caso convertimos un array de objetas Day a vistas
    // Es necesario un constructor al que se le pasa el contexto
    // y el objeto origen.

    private Context mContext;
    private Day[] mDays;

    // Constructor

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    // Hay que rellenar este método con el tamaño del objeto, en
    // este caso al ser un array, su propiedad length.

    @Override
    public int getCount() {
        return mDays.length;
    }



    // También hay que rellenar este método, que retorna la posición de un item
    // en el array.
    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // we aren't going to use this. Tag items for easy reference
    }


    // El método más importante es getView, que es el que obtiene la vista a partir
    // del objeto
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // El convertView es el elemento que usamos para almacenar la vista, y
        // lo vamos reciclando conforme se va usando

        if (convertView == null) {

            // Si el convertView está vacío lo llenamos con un LayoutInflater, pasandole el contexto
            // y a continuacion al metodo inflate le pasamos el layout que queremos rellenar, en este
            // caso el list item customizado que hemos creado

            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);

             // Creamos un nuevo ViewHolder (contenedor) y le asignamos los elementos de la vista, así lo tenemos
            // relleno con los datos

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtenemos el objeto con la posición del array, el método getView al parecer
        // recorre todos los elementos usando el método getItem y getView
        Day day = mDays[position];

        // Asignamos los valores a las propiedades del objeto creado

        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText((int) Math.round(day.getTemperatureMax()) + "");
        // Para la posición 0 ponemos "Today" en lugar del nombre del día
        if (position==0){
            holder.dayLabel.setText(R.string.Today);
        } else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }

        // Devolvemos el conjunto de list items
        return convertView;
    }

    // La clase ViewHolder la creamos para almacenar lo que queremos introducir en la vista
    // Es como la plantilla que iremos rellenando para cada objeto
    private static class ViewHolder {
        ImageView iconImageView; // public by default
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
