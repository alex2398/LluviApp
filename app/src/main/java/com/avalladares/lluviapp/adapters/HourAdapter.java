package com.avalladares.lluviapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.weather.Hour;

/**
 * Created by avalladares on 29/06/2015.
 */

// En este caso, usaremos un adaptador RecyclerView que funciona mejor con listas de items grandes

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Context mContext;
    private Hour[] mHours;
    private int lastPosition = -1;


    public HourAdapter(Context context, Hour[] hours) {
        mContext = context;
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
    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemView.setOnClickListener(this);

        }

        // Con este metodo enlazamos las vistas a los datos que queremos mostrar
        public void bindHour(Hour hour) {

            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature() + "º");
            mIconImageView.setImageResource(hour.getIconId());

        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String conditions = mSummaryLabel.getText().toString();
            String message = String.format("El %s la maxima sera de %s\n y estara %s", time, temperature, conditions);

            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }


    }
}
