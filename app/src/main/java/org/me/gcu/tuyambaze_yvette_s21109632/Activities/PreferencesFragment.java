package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PreferencesFragment extends PreferenceFragmentCompat {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Set up the update interval preference
        Preference updateIntervalPreference = findPreference("update_interval");
        updateIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Update the periodic work request with the new interval
                String updateIntervalString = (String) newValue;
                long updateIntervalMillis = Long.parseLong(updateIntervalString) * 60 * 60 * 1000;
                WorkManager.getInstance(getActivity()).cancelUniqueWork("WeatherDataUpdate");
                schedulePeriodicDataUpdates(updateIntervalMillis);
                return true;
            }
        });

        // Set up the selected location preference
        Preference selectedLocationPreference = findPreference("selected_location");
        String selectedLocation = sharedPreferences.getString("selected_location", "");
        selectedLocationPreference.setSummary(selectedLocation);

        // Set up the last update preference
        Preference lastUpdatePreference = findPreference("last_update");
        long lastUpdateTimestamp = sharedPreferences.getLong("last_update_timestamp", 0);
        String lastUpdateText = formatTimestamp(lastUpdateTimestamp);
        lastUpdatePreference.setSummary(lastUpdateText);
    }

    private void schedulePeriodicDataUpdates(long updateIntervalMillis) {
        PeriodicWorkRequest periodicUpdateRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, updateIntervalMillis, TimeUnit.MILLISECONDS)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .build();

        WorkManager.getInstance(getActivity()).enqueueUniquePeriodicWork(
                "WeatherDataUpdate",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicUpdateRequest
        );
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) {
            return "Never";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}