package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.me.gcu.tuyambaze_yvette_s21109632.Adapters.WeatherForecastAdapter;
import org.me.gcu.tuyambaze_yvette_s21109632.Domains.WeatherForecast;
import org.me.gcu.tuyambaze_yvette_s21109632.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FutureActivity extends AppCompatActivity implements WeatherForecastAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WeatherForecastAdapter adapter;
    private List<WeatherForecast> weatherForecastList;
    private int locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        // Retrieve the location ID from the intent extras
        Intent intent = getIntent();
        locationId = intent.getIntExtra("locationId", 0);

        recyclerView = findViewById(R.id.weather_recycler_view);
        weatherForecastList = new ArrayList<>();
        adapter = new WeatherForecastAdapter(weatherForecastList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchWeatherForecastData();
    }

    private void fetchWeatherForecastData() {
        new WeatherForecastTask().execute();
    }

    private class WeatherForecastTask extends AsyncTask<Void, Void, List<WeatherForecast>> {
        @Override
        protected List<WeatherForecast> doInBackground(Void... voids) {
            try {
                // Use the location ID to construct the URL
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
        protected void onPostExecute(List<WeatherForecast> weatherForecasts) {
            if (weatherForecasts != null && !weatherForecasts.isEmpty()) {
                weatherForecastList.clear();
                weatherForecastList.addAll(weatherForecasts);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private List<WeatherForecast> parseWeatherData(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<WeatherForecast> weatherForecastList = new ArrayList<>();
        int eventType = parser.getEventType();
        WeatherForecast currentForecast = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if ("item".equals(tagName)) {
                        currentForecast = new WeatherForecast();
                    } else if ("title".equals(tagName)) {
                        String title = parser.nextText();
                        String[] parts = title.split(":");
                        if (parts.length >= 2) {
                            String day = parts[0].trim();
                            String weatherCondition = parts[1].trim();
                            currentForecast.setDay(day);
                            currentForecast.setWeatherCondition(weatherCondition);
                        }
                    } else if ("description".equals(tagName)) {
                        String description = parser.nextText();
                        String[] parts = description.split(",");
                        for (String part : parts) {
                            part = part.trim();
                            if (part.startsWith("Maximum Temperature:")) {
                                currentForecast.setMaxTemperature(part.replace("Maximum Temperature:", "").trim());
                            } else if (part.startsWith("Minimum Temperature:")) {
                                currentForecast.setMinTemperature(part.replace("Minimum Temperature:", "").trim());
                            } else if (part.startsWith("Visibility:")) {
                                currentForecast.setVisibility(part.replace("Visibility:", "").trim());
                            }
                        }
                    } else if ("pubDate".equals(tagName)) {
                        String pubDate = parser.nextText();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        try {
                            Date date = inputFormat.parse(pubDate);
                            if (currentForecast != null) {
                                currentForecast.setDate(outputFormat.format(date));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if ("item".equals(tagName)) {
                        if (currentForecast != null) {
                            weatherForecastList.add(currentForecast);
                        }
                    }
                    break;
                default:
                    // Do nothing
            }
            eventType = parser.next();
        }

        return weatherForecastList;
    }

    @Override
    public void onItemClick(WeatherForecast forecast) {
        Intent intent = new Intent(FutureActivity.this, DetailedForecastActivity.class);
        intent.putExtra("forecast", forecast);
        startActivity(intent);
    }
}