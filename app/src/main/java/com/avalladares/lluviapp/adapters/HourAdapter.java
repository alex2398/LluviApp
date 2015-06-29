package com.avalladares.lluviapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.weather.Hour;

/**
 * Created by avalladares on 29/06/2015.
 */

// En este caso, usaremos un adaptador RecyclerView que funciona mejor con listas de items grandes

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Hour[] mHours;
    public HourAdapter (Hour[] hours) {
        mHours = hours;
    }
    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false);
        HourViewHolder viewHolder = new HourViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    // Creamos manualmente la clase para el ViewHolder
    public class HourViewHolder extends RecyclerView.ViewHolder {

        // Creamos las variables miembro
        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        // Constructor de la clase
        public HourViewHolder(View itemView) {
            super(itemView);
            // OJO, usamos findViewById en lugar de findItemById

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryTextView);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

        }

        // Con este metodo enlazamos las vistas a los datos que queremos mostrar
        public void bindHour(Hour hour) {

            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature() + "");
            mIconImageView.setImageResource(hour.getIconId());

        }
    }
}
