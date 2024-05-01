package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.me.gcu.tuyambaze_yvette_s21109632.Domains.WeatherForecast;
import org.me.gcu.tuyambaze_yvette_s21109632.R;

public class DetailedForecastActivity extends AppCompatActivity {

    private TextView tvDay, tvDate, tvTemperature, tvWeatherCondition, tvVisibility;
    private TextView tvWindDirection, tvWindSpeed, tvHumidity, tvUvRisk, tvPollution, tvSunrise, tvSunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);
        Button backButton = findViewById(R.id.backButton);

        tvDay = findViewById(R.id.tv_day);
        tvDate = findViewById(R.id.tv_date);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvWeatherCondition = findViewById(R.id.tv_weather_condition);
        tvVisibility = findViewById(R.id.tv_visibility);
        tvWindDirection = findViewById(R.id.tv_wind_direction);
        tvWindSpeed = findViewById(R.id.tv_wind_speed);
        tvHumidity = findViewById(R.id.tv_humidity);
        tvUvRisk = findViewById(R.id.tv_uv_risk);
        tvPollution = findViewById(R.id.tv_pollution);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);

        WeatherForecast forecast = (WeatherForecast) getIntent().getSerializableExtra("forecast");
        if (forecast != null) {
            displayForecastDetails(forecast);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity (LandingScreen)
            }
        });
    }

    private void displayForecastDetails(WeatherForecast forecast) {
        tvDay.setText(forecast.getDay());
        tvDate.setText(forecast.getDate());
        tvTemperature.setText(String.format("%s / %s", forecast.getMinTemperature(), forecast.getMaxTemperature()));
        tvWeatherCondition.setText(forecast.getWeatherCondition());
        tvVisibility.setText(forecast.getVisibility());
        tvWindDirection.setText(forecast.getWindDirection());
        tvWindSpeed.setText(forecast.getWindSpeed());
        tvHumidity.setText(forecast.getHumidity());
        tvUvRisk.setText(forecast.getUvRisk());
        tvPollution.setText(forecast.getPollution());
        tvSunrise.setText(forecast.getSunrise());
        tvSunset.setText(forecast.getSunset());
    }
}