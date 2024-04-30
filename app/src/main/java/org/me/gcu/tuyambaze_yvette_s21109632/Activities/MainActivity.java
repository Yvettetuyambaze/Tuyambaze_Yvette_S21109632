package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    MapFragment locationFragment = new MapFragment();
    AboutFragment searchFragment = new AboutFragment();
    NotificationFragment notificationFragment = new NotificationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable the app bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";

                if (item.getItemId() == R.id.home) {
                    selectedFragment = homeFragment;
                    title = "Home";
                } else if (item.getItemId() == R.id.location) {
                    selectedFragment = locationFragment;
                    title = "Location";
                } else if (item.getItemId() == R.id.about) {
                    selectedFragment = searchFragment;
                    title = "About";
                } else if (item.getItemId() == R.id.notification) {
                    selectedFragment = notificationFragment;
                    title = "Notification";
                } else if (item.getItemId() == R.id.settings) {
                    selectedFragment = settingsFragment;
                    title = "Settings";
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item1) {
            // Handle menu item 1 click
            return true;
        } else if (id == R.id.menu_item2) {
            // Handle menu item 2 click
            return true;
        } else if (id == R.id.menu_item3) {
            // Handle menu item 3 click
            return true;
        } else if (id == R.id.menu_item4) {
            // Handle menu item 4 click
            return true;
        } else if (id == R.id.menu_item5) {
            // Handle menu item 5 click
            return true;
        } else if (id == R.id.menu_item6) {
            // Handle menu item 6 click
            return true;
        } else if (id == R.id.menu_item7) {
            // Handle menu item 7 click
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Weather> fetchWeatherData() {
        // Implement your code to fetch weather data from an API or database
        // Return the weather data as a List<Weather>
        // For demonstration purposes, let's create a sample weatherDataList
        List<Weather> weatherDataList = new ArrayList<>();
        // Add sample weather data to the list
        weatherDataList.add(createWeather("New York", "40.7128 -74.0060", "Sunny", "25°C", "30°C", "10%", "5 m/s"));
        weatherDataList.add(createWeather("London", "51.5074 -0.1278", "Cloudy", "18°C", "22°C", "60%", "3 m/s"));
        weatherDataList.add(createWeather("Paris", "48.8566 2.3522", "Rainy", "15°C", "18°C", "80%", "2 m/s"));
        return weatherDataList;
    }

    private Weather createWeather(String title, String geoRssPoint, String forecastWeatherType,
                                  String minTemperature, String maxTemperature, String humidity, String wind) {
        Weather weather = new Weather();
        weather.setTitle(title);
        weather.setGeoRssPoint(geoRssPoint);
        weather.setForecastWeatherType(forecastWeatherType);
        weather.setMinTemperature(minTemperature);
        weather.setMaxTemperature(maxTemperature);
        weather.setHumidity(humidity);
        weather.setWind(wind);
        return weather;
    }
}