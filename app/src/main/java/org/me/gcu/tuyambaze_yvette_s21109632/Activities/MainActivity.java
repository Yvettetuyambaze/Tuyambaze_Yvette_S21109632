package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    MapFragment locationFragment = new MapFragment();
    AboutFragment searchFragment = new AboutFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    PreferencesFragment preferencesFragment = new PreferencesFragment();

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

        // Schedule periodic data updates
        schedulePeriodicDataUpdates();

        // Create the notification channel
        createNotificationChannel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item2 || id == R.id.menu_item4 || id == R.id.menu_item5 || id == R.id.menu_item6 || id == R.id.menu_item7) {
            // Navigate to the settings fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, settingsFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.menu_item3) {
            // Navigate to the preferences fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, preferencesFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void schedulePeriodicDataUpdates() {
        // Get the update interval from SharedPreferences (default is 12 hours)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String updateIntervalString = sharedPreferences.getString("update_interval", "12");
        long updateIntervalMillis = Long.parseLong(updateIntervalString) * 60 * 60 * 1000;

        PeriodicWorkRequest periodicUpdateRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, updateIntervalMillis, TimeUnit.MILLISECONDS)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "WeatherDataUpdate",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicUpdateRequest
        );
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weather Channel";
            String description = "Channel for weather notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("weather_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}