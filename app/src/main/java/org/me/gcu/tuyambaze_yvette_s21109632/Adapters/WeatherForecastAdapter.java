package org.me.gcu.tuyambaze_yvette_s21109632.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.me.gcu.tuyambaze_yvette_s21109632.Domains.WeatherForecast;
import org.me.gcu.tuyambaze_yvette_s21109632.R;
import java.util.List;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.WeatherForecastViewHolder> {
    private List<WeatherForecast> weatherForecastList;
    private OnItemClickListener listener;

    public WeatherForecastAdapter(List<WeatherForecast> weatherForecastList, OnItemClickListener listener) {
        this.weatherForecastList = weatherForecastList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeatherForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);
        return new WeatherForecastViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastViewHolder holder, int position) {
        WeatherForecast forecast = weatherForecastList.get(position);
        holder.bind(forecast);
    }

    @Override
    public int getItemCount() {
        return weatherForecastList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(WeatherForecast forecast);
    }

    static class WeatherForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDay, tvDate, tvTemperature, tvWeatherCondition, tvVisibility;
        private OnItemClickListener listener;
        private WeatherForecast forecast;

        public WeatherForecastViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvWeatherCondition = itemView.findViewById(R.id.tv_weather_condition);
            tvVisibility = itemView.findViewById(R.id.tv_visibility);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(WeatherForecast forecast) {
            this.forecast = forecast;
            tvDay.setText(forecast.getDay());
            tvDate.setText(forecast.getDate());
            tvTemperature.setText(String.format("%s / %s", forecast.getMinTemperature(), forecast.getMaxTemperature()));
            tvWeatherCondition.setText(forecast.getWeatherCondition());
            tvVisibility.setText(forecast.getVisibility());
        }

        @Override
        public void onClick(View v) {
            if (listener != null && forecast != null) {
                listener.onItemClick(forecast);
            }
        }
    }
}