package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Check if dark mode is enabled and set the appropriate theme
        boolean isDarkModeOn = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Temperature Unit
        RadioGroup temperatureUnitRadioGroup = view.findViewById(R.id.temperatureUnitRadioGroup);
        String temperatureUnit = sharedPreferences.getString("temperature_unit", "celsius");
        switch (temperatureUnit) {
            case "celsius":
                temperatureUnitRadioGroup.check(R.id.celsiusRadioButton);
                break;
            case "fahrenheit":
                temperatureUnitRadioGroup.check(R.id.fahrenheitRadioButton);
                break;
            case "system":
                temperatureUnitRadioGroup.check(R.id.systemRadioButton);
                break;
        }
        temperatureUnitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedUnit = "";
                if (checkedId == R.id.celsiusRadioButton) {
                    selectedUnit = "celsius";
                } else if (checkedId == R.id.fahrenheitRadioButton) {
                    selectedUnit = "fahrenheit";
                } else if (checkedId == R.id.systemRadioButton) {
                    selectedUnit = "system";
                }
                sharedPreferences.edit().putString("temperature_unit", selectedUnit).apply();
            }
        });

        // Other settings configurations...

        // Dark Mode Switch
        Switch darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(isDarkModeOn);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        return view;
    }
}
