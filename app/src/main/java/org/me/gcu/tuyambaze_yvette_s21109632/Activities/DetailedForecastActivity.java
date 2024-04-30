package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.me.gcu.tuyambaze_yvette_s21109632.Domains.WeatherForecast;
import org.me.gcu.tuyambaze_yvette_s21109632.R;

public class DetailedForecastActivity extends AppCompatActivity {

    private TextView tvDay, tvDate, tvTemperature, tvWeatherCondition, tvVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);

        tvDay = findViewById(R.id.tv_day);
        tvDate = findViewById(R.id.tv_date);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvWeatherCondition = findViewById(R.id.tv_weather_condition);
        tvVisibility = findViewById(R.id.tv_visibility);

        WeatherForecast forecast = (WeatherForecast) getIntent().getSerializableExtra("forecast");
        if (forecast != null) {
            displayForecastDetails(forecast);
        }
    }

    private void displayForecastDetails(WeatherForecast forecast) {
        tvDay.setText(forecast.getDay());
        tvDate.setText(forecast.getDate());
        tvTemperature.setText(String.format("%s / %s", forecast.getMinTemperature(), forecast.getMaxTemperature()));
        tvWeatherCondition.setText(forecast.getWeatherCondition());
        tvVisibility.setText(forecast.getVisibility());
    }
}