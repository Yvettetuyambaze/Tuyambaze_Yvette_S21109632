package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

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
}