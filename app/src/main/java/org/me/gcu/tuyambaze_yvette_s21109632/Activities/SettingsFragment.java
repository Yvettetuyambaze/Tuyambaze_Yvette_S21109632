package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends Fragment {

    private RadioGroup temperatureUnitRadioGroup;
    private Spinner windSpeedUnitSpinner, precipitationUnitSpinner, pressureUnitSpinner, distanceUnitSpinner;
    private Switch darkModeSwitch;
    private Button restoreDefaultsButton;
    private SharedPreferences sharedPreferences;

    private static final int DEFAULT_START_HOUR = 8;
    private static final int DEFAULT_END_HOUR = 20;

    private WorkManager workManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        temperatureUnitRadioGroup = view.findViewById(R.id.temperatureUnitRadioGroup);
        windSpeedUnitSpinner = view.findViewById(R.id.windSpeedUnitSpinner);
        precipitationUnitSpinner = view.findViewById(R.id.precipitationUnitSpinner);
        pressureUnitSpinner = view.findViewById(R.id.pressureUnitSpinner);
        distanceUnitSpinner = view.findViewById(R.id.distanceUnitSpinner);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        restoreDefaultsButton = view.findViewById(R.id.restoreDefaultsButton);

        workManager = WorkManager.getInstance(requireContext());
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        setupSpinners();
        loadPreferences();

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);
        applyTheme(isDarkMode);

        int startHour = sharedPreferences.getInt("start_hour", DEFAULT_START_HOUR);
        int endHour = sharedPreferences.getInt("end_hour", DEFAULT_END_HOUR);
        scheduleDataUpdates(startHour, endHour);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme(isChecked);
        });

        restoreDefaultsButton.setOnClickListener(v -> {
            // Restore default preferences
            sharedPreferences.edit()
                    .putBoolean("dark_mode", false)
                    .putInt("start_hour", DEFAULT_START_HOUR)
                    .putInt("end_hour", DEFAULT_END_HOUR)
                    .apply();

            // Update UI with default values
            darkModeSwitch.setChecked(false);
            applyTheme(false);
            scheduleDataUpdates(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
        });

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> windSpeedAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.wind_speed_units, android.R.layout.simple_spinner_item);
        windSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windSpeedUnitSpinner.setAdapter(windSpeedAdapter);

        ArrayAdapter<CharSequence> precipitationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.precipitation_units, android.R.layout.simple_spinner_item);
        precipitationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        precipitationUnitSpinner.setAdapter(precipitationAdapter);

        ArrayAdapter<CharSequence> pressureAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.pressure_units, android.R.layout.simple_spinner_item);
        pressureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pressureUnitSpinner.setAdapter(pressureAdapter);

        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.distance_units, android.R.layout.simple_spinner_item);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceUnitSpinner.setAdapter(distanceAdapter);
    }

    private void loadPreferences() {
        // Load preferences from shared preferences or default values
        // and set the UI elements accordingly

        // Example: Load the selected temperature unit from SharedPreferences
        int temperatureUnitValue = sharedPreferences.getInt("temperature_unit", 0); // 0 for Celsius, 1 for Fahrenheit, 2 for System Setting
        temperatureUnitRadioGroup.check(temperatureUnitRadioGroup.getChildAt(temperatureUnitValue).getId());
    }

    private void applyTheme(boolean isDarkMode) {
       // int themeResId = isDarkMode ? R.style.Theme_Tuyambaze_Yvette_S21109632_Dark : R.style.Theme_Tuyambaze_Yvette_S21109632_Light;
        //requireActivity().setTheme(themeResId);
        requireActivity().recreate();
    }

    private void scheduleDataUpdates(int startHour, int endHour) {
        workManager.cancelAllWork();

        PeriodicWorkRequest dataUpdateRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, 1, TimeUnit.HOURS)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .build();

        workManager.enqueueUniquePeriodicWork(
                "data_update_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                dataUpdateRequest);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);

//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresDeviceIdle(false)
//                .setRequiresBatteryNotLow(true)
//                .setRequiresStorageNotLow(true)
//                .setTimeWindow(
//                        startCalendar.getTimeInMillis(),
//                        endCalendar.getTimeInMillis())
//                .build();
//
//        workManager.updateConstraints(dataUpdateRequest.getWorkSpec(), constraints);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        // Save preferences when the fragment is paused
//        savePreferences();
//    }

//    private void savePreferences() {
//        // Save the selected preferences to SharedPreferences
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Example: Save the selected temperature unit
//        int selectedTemperatureUnitId = temperatureUnitRadioGroup.getCheckedRadioButtonId();
//        int temperatureUnitValue;
//        switch (selectedTemperatureUnitId) {
//            case R.id.celsiusRadioButton:
//                temperatureUnitValue = 0; // Celsius
//                break;
//            case R.id.fahrenheitRadioButton:
//                temperatureUnitValue = 1; // Fahrenheit
//                break;
//            case R.id.systemRadioButton:
//                temperatureUnitValue = 2; // System Setting
//                break;
//            default:
//                temperatureUnitValue = 0; // Default to Celsius
//        }
//        editor.putInt("temperature_unit", temperatureUnitValue);
//
//        // Save other preferences here...
//
//        editor.apply();
//    }
}