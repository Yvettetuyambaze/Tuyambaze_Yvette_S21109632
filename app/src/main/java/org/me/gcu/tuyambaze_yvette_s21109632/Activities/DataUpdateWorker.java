package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.me.gcu.tuyambaze_yvette_s21109632.R;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class DataUpdateWorker extends Worker {

    public DataUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Perform the data update here
        updateWeatherData();

        // Show a notification to the user
        showUpdateNotification();

        return Result.success();
    }

    private void updateWeatherData() {
        // Get the selected location from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLocation = sharedPreferences.getString("selected_location", "");

        // Fetch the weather data for the selected location
        if (!selectedLocation.isEmpty()) {
            int locationId = getLocationIdFromName(selectedLocation);
            if (locationId != -1) {
                fetchWeatherData(locationId);
                fetchCurrentWeather(locationId);
            }
        }
    }

    private int getLocationIdFromName(String locationName) {
        switch (locationName) {
            case "Glasgow":
                return 2648579;
            case "London":
                return 2643743;
            case "New York":
                return 5128581;
            case "Oman":
                return 287286;
            case "Mauritius":
                return 934154;
            case "Bangladesh":
                return 1185241;
            default:
                return -1;
        }
    }

    private void fetchWeatherData(int locationId) {
        try {
            URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId);
            InputStream inputStream = url.openConnection().getInputStream();
            List<Weather> weatherList = XMLPullPerserHandlerWeather.parseWeatherData(inputStream);
            // Process the weather data as needed
            // ...
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void fetchCurrentWeather(int locationId) {
        try {
            URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationId);
            InputStream inputStream = url.openConnection().getInputStream();
            CurrentWeatherXmlParser parser = new CurrentWeatherXmlParser();
            CurrentWeather currentWeather = parser.parse(inputStream);
            // Process the current weather data as needed
            // ...
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void showUpdateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "weather_channel")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Weather Data Updated")
                .setContentText("The weather data has been successfully updated.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}