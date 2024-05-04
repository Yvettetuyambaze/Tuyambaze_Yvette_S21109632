package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.me.gcu.tuyambaze_yvette_s21109632.Domains.WeatherForecast;
import org.me.gcu.tuyambaze_yvette_s21109632.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CompareLocationsActivity extends AppCompatActivity {

    private Spinner firstLocationSpinner, secondLocationSpinner;
    private TextView firstLocationName, firstLocationWeatherCondition, firstLocationTemperature, firstLocationWindSpeed, firstLocationHumidity, firstLocationVisibility, firstLocationPressure, firstLocationUvRisk, firstLocationPollution, firstLocationSunrise, firstLocationSunset;
    private TextView secondLocationName, secondLocationWeatherCondition, secondLocationTemperature, secondLocationWindSpeed, secondLocationHumidity, secondLocationVisibility, secondLocationPressure, secondLocationUvRisk, secondLocationPollution, secondLocationSunrise, secondLocationSunset;

    private int firstLocationId, secondLocationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_locations);
        Button backButton = findViewById(R.id.backButton);

        firstLocationSpinner = findViewById(R.id.firstLocationSpinner);
        secondLocationSpinner = findViewById(R.id.secondLocationSpinner);

        firstLocationName = findViewById(R.id.firstLocationName);
        firstLocationWeatherCondition = findViewById(R.id.firstLocationWeatherCondition);
        firstLocationTemperature = findViewById(R.id.firstLocationTemperature);
        firstLocationWindSpeed = findViewById(R.id.firstLocationWindSpeed);
        firstLocationHumidity = findViewById(R.id.firstLocationHumidity);
        firstLocationVisibility = findViewById(R.id.firstLocationVisibility);
        firstLocationPressure = findViewById(R.id.firstLocationPressure);
        firstLocationUvRisk = findViewById(R.id.firstLocationUvRisk);
        firstLocationPollution = findViewById(R.id.firstLocationPollution);
        firstLocationSunrise = findViewById(R.id.firstLocationSunrise);
        firstLocationSunset = findViewById(R.id.firstLocationSunset);

        secondLocationName = findViewById(R.id.secondLocationName);
        secondLocationWeatherCondition = findViewById(R.id.secondLocationWeatherCondition);
        secondLocationTemperature = findViewById(R.id.secondLocationTemperature);
        secondLocationWindSpeed = findViewById(R.id.secondLocationWindSpeed);
        secondLocationHumidity = findViewById(R.id.secondLocationHumidity);
        secondLocationVisibility = findViewById(R.id.secondLocationVisibility);
        secondLocationPressure = findViewById(R.id.secondLocationPressure);
        secondLocationUvRisk = findViewById(R.id.secondLocationUvRisk);
        secondLocationPollution = findViewById(R.id.secondLocationPollution);
        secondLocationSunrise = findViewById(R.id.secondLocationSunrise);
        secondLocationSunset = findViewById(R.id.secondLocationSunset);

        // Set up the spinner adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstLocationSpinner.setAdapter(adapter);
        secondLocationSpinner.setAdapter(adapter);

        // Set up spinner item selection listeners
        firstLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                firstLocationId = getLocationIdFromName(selectedLocation);
                fetchWeatherData(firstLocationId, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        secondLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                secondLocationId = getLocationIdFromName(selectedLocation);
                fetchWeatherData(secondLocationId, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity (homeScreen)
            }
        });

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

    private void fetchWeatherData(int locationId, boolean isFirstLocation) {
        new WeatherDataTask(isFirstLocation).execute(locationId);
    }

    private class WeatherDataTask extends AsyncTask<Integer, Void, List<WeatherForecast>> {
        private final boolean isFirstLocation;

        WeatherDataTask(boolean isFirstLocation) {
            this.isFirstLocation = isFirstLocation;
        }

        @Override
        protected List<WeatherForecast> doInBackground(Integer... locationIds) {
            int locationId = locationIds[0];
            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId);
                InputStream inputStream = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                return parseWeatherData(parser);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<WeatherForecast> weatherList) {
            if (weatherList != null && !weatherList.isEmpty()) {
                if (isFirstLocation) {
                    updateFirstLocationViews(weatherList.get(0));
                } else {
                    updateSecondLocationViews(weatherList.get(0));
                }
            }
        }

        private List<WeatherForecast> parseWeatherData(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<WeatherForecast> weatherList = new ArrayList<>();
            int eventType = parser.getEventType();
            WeatherForecast currentForecast = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if ("channel".equals(tagName)) {
                            // Do nothing
                        } else if ("item".equals(tagName)) {
                            currentForecast = new WeatherForecast();
                        } else if ("title".equals(tagName)) {
                            if (currentForecast != null) {
                                currentForecast.setDay(parser.nextText());
                            }
                        } else if ("description".equals(tagName)) {
                            if (currentForecast != null) {
                                String description = parser.nextText();
                                parseWeatherDetails(currentForecast, description);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(parser.getName())) {
                            if (currentForecast != null) {
                                weatherList.add(currentForecast);
                            }
                        }
                        break;
                    default:
                        // Do nothing
                }
                eventType = parser.next();
            }

            return weatherList;
        }

        private void parseWeatherDetails(WeatherForecast forecast, String description) {
            String[] parts = description.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("Maximum Temperature:")) {
                    forecast.setMaxTemperature(part.replace("Maximum Temperature:", "").trim());
                } else if (part.startsWith("Minimum Temperature:")) {
                    forecast.setMinTemperature(part.replace("Minimum Temperature:", "").trim());
                } else if (part.startsWith("Wind Direction:")) {
                    forecast.setWindDirection(part.replace("Wind Direction:", "").trim());
                } else if (part.startsWith("Wind Speed:")) {
                    forecast.setWindSpeed(part.replace("Wind Speed:", "").trim());
                } else if (part.startsWith("Visibility:")) {
                    forecast.setVisibility(part.replace("Visibility:", "").trim());
                } else if (part.startsWith("Pressure:")) {
                    forecast.setPressure(part.replace("Pressure:", "").trim());
                } else if (part.startsWith("Humidity:")) {
                    forecast.setHumidity(part.replace("Humidity:", "").trim());
                } else if (part.startsWith("UV Risk:")) {
                    forecast.setUvRisk(part.replace("UV Risk:", "").trim());
                } else if (part.startsWith("Pollution:")) {
                    forecast.setPollution(part.replace("Pollution:", "").trim());
                } else if (part.startsWith("Sunrise:")) {
                    forecast.setSunrise(part.replace("Sunrise:", "").trim());
                } else if (part.startsWith("Sunset:")) {
                    forecast.setSunset(part.replace("Sunset:", "").trim());
                }
            }
        }

        private void updateFirstLocationViews(WeatherForecast forecast) {
            firstLocationName.setText(forecast.getDay());
            firstLocationWeatherCondition.setText(forecast.getWeatherCondition());
            firstLocationTemperature.setText(String.format("Temperature: %s°C / %s°C", forecast.getMinTemperature(), forecast.getMaxTemperature()));
            firstLocationWindSpeed.setText(String.format("Wind: %s %s", forecast.getWindDirection(), forecast.getWindSpeed()));
            firstLocationHumidity.setText(String.format("Humidity: %s%%", forecast.getHumidity()));
            firstLocationVisibility.setText(String.format("Visibility: %s", forecast.getVisibility()));
            firstLocationPressure.setText(String.format("Pressure: %s mb", forecast.getPressure()));
            firstLocationUvRisk.setText(String.format("UV Risk: %s", forecast.getUvRisk()));
            firstLocationPollution.setText(String.format("Pollution: %s", forecast.getPollution()));
            firstLocationSunrise.setText(String.format("Sunrise: %s", forecast.getSunrise()));
            firstLocationSunset.setText(String.format("Sunset: %s", forecast.getSunset()));


        }
        private void updateSecondLocationViews(WeatherForecast forecast) {
            secondLocationName.setText(forecast.getDay());
            secondLocationWeatherCondition.setText(forecast.getWeatherCondition());
            secondLocationTemperature.setText(String.format("Temperature: %s°C / %s°C", forecast.getMinTemperature(), forecast.getMaxTemperature()));
            secondLocationWindSpeed.setText(String.format("Wind: %s %s", forecast.getWindDirection(), forecast.getWindSpeed()));
            secondLocationHumidity.setText(String.format("Humidity: %s%%", forecast.getHumidity()));
            secondLocationVisibility.setText(String.format("Visibility: %s", forecast.getVisibility()));
            secondLocationPressure.setText(String.format("Pressure: %s mb", forecast.getPressure()));
            secondLocationUvRisk.setText(String.format("UV Risk: %s", forecast.getUvRisk()));
            secondLocationPollution.setText(String.format("Pollution: %s", forecast.getPollution()));
            secondLocationSunrise.setText(String.format("Sunrise: %s", forecast.getSunrise()));
            secondLocationSunset.setText(String.format("Sunset: %s", forecast.getSunset()));
        }
    }
}