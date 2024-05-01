package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.tuyambaze_yvette_s21109632.R;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private TextView locationNameTextView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;
    private TextView windTextView;
    private TextView humidityTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;
    private Button nextBtn;
    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private TextView adviceTextView;
    private ImageView adviceIconImageView;

    private AutoCompleteTextView locationSearchView;
    private ArrayAdapter<String> locationAdapter;
    private List<String> locationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        locationNameTextView = view.findViewById(R.id.locationName);

        weatherDescriptionTextView = view.findViewById(R.id.weatherDescription);
        temperatureTextView = view.findViewById(R.id.temperature);
        windTextView = view.findViewById(R.id.wind);
        humidityTextView = view.findViewById(R.id.humidity);
        visibilityTextView = view.findViewById(R.id.visibility);
        pressureTextView = view.findViewById(R.id.pressure);
        nextBtn = view.findViewById(R.id.nextBtn);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeek);
        dateTextView = view.findViewById(R.id.date);
        adviceTextView = view.findViewById(R.id.adviceTextView);
        adviceIconImageView = view.findViewById(R.id.adviceIconImageView);

        recyclerView = view.findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        locationSearchView = view.findViewById(R.id.locationSearchView);
        locationList = new ArrayList<>();
        populateLocationList();

        locationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, locationList);
        locationSearchView.setAdapter(locationAdapter);



        // Save the selected location to SharedPreferences
        locationSearchView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLocation = (String) parent.getItemAtPosition(position);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_location", selectedLocation);
            editor.apply();
            // ...
        });



        locationSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        locationSearchView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLocation = (String) parent.getItemAtPosition(position);
            int locationId = getLocationIdFromName(selectedLocation);
            if (locationId != -1) {
                fetchWeatherData(locationId);
                fetchCurrentWeather(locationId);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLocation = locationSearchView.getText().toString();
                int locationId = getLocationIdFromName(selectedLocation);
                if (locationId != -1) {
                    Intent intent = new Intent(getActivity(), FutureActivity.class);
                    intent.putExtra("locationId", locationId);
                    startActivity(intent);
                } else {
                    showInvalidLocationDialog();
                }
            }
        });

        // Check internet connectivity
        if (!isInternetConnected()) {
            showNoInternetDialog();
        }

        return view;
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click if needed
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Cancel button click if needed
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void populateLocationList() {
        locationList.add("Glasgow");
        locationList.add("London");
        locationList.add("New York");
        locationList.add("Oman");
        locationList.add("Mauritius");
        locationList.add("Bangladesh");
    }

    private int getLocationIdFromName(String locationName) {
        switch (locationName.toLowerCase()) {
            case "glasgow":
                return 2648579;
            case "london":
                return 2643743;
            case "new york":
                return 5128581;
            case "oman":
                return 287286;
            case "mauritius":
                return 934154;
            case "bangladesh":
                return 1185241;
            default:
                showInvalidLocationDialog();
                return -1;
        }
    }

    private void showInvalidLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Invalid Location")
                .setMessage("The entered location is not available. Please try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchWeatherData(int locationId) { new WeatherDataTask().execute(locationId);}

    private class WeatherDataTask extends AsyncTask<Integer, Void, List<Weather>> {
        @Override
        protected List<Weather> doInBackground(Integer... locationIds) {
            int locationId = locationIds[0];
            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId);
                InputStream inputStream = url.openConnection().getInputStream();
                return XMLPullPerserHandlerWeather.parseWeatherData(inputStream);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Weather> weatherList) {
            if (weatherList != null && !weatherList.isEmpty()) {
                Weather weather = weatherList.get(0);

                // Update the weather description TextView
                weatherDescriptionTextView.setText(weather.getWeatherCondition());

                // Update the advice TextView and icon based on the weather condition
                String weatherCondition = weather.getWeatherCondition().toLowerCase();
                Advice advice = getAdvice(weatherCondition);
                adviceTextView.setText(advice.getMessage());
                adviceIconImageView.setImageResource(advice.getIconResId());

                // Update the current location TextView
                String selectedLocation = locationSearchView.getText().toString();
                locationNameTextView.setText(selectedLocation);
            }
        }

        private Advice getAdvice(String weatherCondition) {
            if (weatherCondition.contains("sunny")) {
                return new Advice("Embrace the sunshine! Apply sunscreen, wear a hat, and stay cool.", R.drawable.sunny);
            } else if (weatherCondition.contains("rain") || weatherCondition.contains("showers")) {
                return new Advice("Prepare for the rain. Carry an umbrella and wear waterproof gear.", R.drawable.rain);
            } else if (weatherCondition.contains("cloud") || weatherCondition.contains("overcast")) {
                return new Advice("It's a cloudy day. Enjoy the pleasant weather and plan indoor activities.", R.drawable.cloudy);
            } else if (weatherCondition.contains("snow")) {
                return new Advice("Bundle up and stay warm! Wear layers and be cautious on slippery surfaces.", R.drawable.snow_thunder);
            } else {
                return new Advice("Have a fantastic day! Stay prepared for any weather surprises.", R.drawable.cloudy_sunny);
            }
        }
    }

    private void fetchCurrentWeather(int locationId) {
        new CurrentWeatherTask().execute(locationId);
    }

    private class CurrentWeatherTask extends AsyncTask<Integer, Void, CurrentWeather> {
        @Override
        protected CurrentWeather doInBackground(Integer... locationIds) {
            int locationId = locationIds[0];
            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationId);
                InputStream inputStream = url.openConnection().getInputStream();
                CurrentWeatherXmlParser parser = new CurrentWeatherXmlParser();
                return parser.parse(inputStream);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CurrentWeather currentWeather) {
            if (currentWeather != null) {
                // Update UI with the current weather data
                temperatureTextView.setText(currentWeather.getTemperature());
                windTextView.setText(currentWeather.getWindDirection() + ", " + currentWeather.getWindSpeed());
                humidityTextView.setText(currentWeather.getHumidity());
                pressureTextView.setText(currentWeather.getPressure());
                visibilityTextView.setText(currentWeather.getVisibility());

                // Update the current location TextView
                String selectedLocation = locationSearchView.getText().toString();
                locationNameTextView.setText(selectedLocation);
                String dayOfWeek = currentWeather.getDayOfWeek();
                String formattedDate = currentWeather.getFormattedDate();

                dayOfWeekTextView.setText(dayOfWeek);
                dateTextView.setText(formattedDate);
            }
        }
    }

    private static class Advice {
        private String message;
        private int iconResId;

        public Advice(String message, int iconResId) {
            this.message = message;
            this.iconResId = iconResId;
        }

        public String getMessage() {
            return message;
        }

        public int getIconResId() {
            return iconResId;
        }
    }
}