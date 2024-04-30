package org.me.gcu.tuyambaze_yvette_s21109632.Activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.tuyambaze_yvette_s21109632.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Map<String, LatLng> locationCoordinates;
    private Map<String, Integer> locationIds;
    private Button zoomInButton;
    private Button zoomOutButton;
    private AutoCompleteTextView searchView;
    private List<String> locationSuggestions;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        zoomInButton = view.findViewById(R.id.btn_zoom_in);
        zoomOutButton = view.findViewById(R.id.btn_zoom_out);

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });

        searchView = view.findViewById(R.id.search_view);
        locationSuggestions = new ArrayList<>();
        // Add location suggestions to the list
        locationSuggestions.add("Glasgow");
        locationSuggestions.add("London");
        locationSuggestions.add("NewYork");
        locationSuggestions.add("Oman");
        locationSuggestions.add("Mauritius");
        locationSuggestions.add("Bangladesh");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, locationSuggestions);
        searchView.setAdapter(adapter);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = (String) parent.getItemAtPosition(position);
                searchLocation(selectedLocation);
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        searchView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            String query = textView.getText().toString().trim();
            if (!query.isEmpty()) {
                searchLocation(query);
            }
            return true;
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Initialize the location coordinates and IDs
        initializeLocationCoordinates();
        initializeLocationIds();

        // Add markers for each location
        addLocationMarkers();

        // Set up custom info window adapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set up marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Move the camera to the clicked marker's location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10f));

                // Fetch weather data for the clicked location
                String locationName = marker.getTitle();
                int locationId = locationIds.get(locationName);
                fetchWeatherData(locationId, marker);

                return false;
            }
        });
    }

    private void initializeLocationCoordinates() {
        locationCoordinates = new HashMap<>();
        locationCoordinates.put("Glasgow", new LatLng(55.8642, -4.2518));
        locationCoordinates.put("London", new LatLng(51.5074, -0.1278));
        locationCoordinates.put("NewYork", new LatLng(40.7128, -74.0060));
        locationCoordinates.put("Oman", new LatLng(21.4735, 55.9754));
        locationCoordinates.put("Mauritius", new LatLng(-20.3484, 57.5522));
        locationCoordinates.put("Bangladesh", new LatLng(23.6850, 90.3563));
    }

    private void initializeLocationIds() {
        locationIds = new HashMap<>();
        locationIds.put("Glasgow", 2648579);
        locationIds.put("London", 2643743);
        locationIds.put("NewYork", 5128581);
        locationIds.put("Oman", 287286);
        locationIds.put("Mauritius", 934154);
        locationIds.put("Bangladesh", 1185241);
    }

    private void addLocationMarkers() {
        // Iterate through the location coordinates and add markers to the map
        for (Map.Entry<String, LatLng> entry : locationCoordinates.entrySet()) {
            String locationName = entry.getKey();
            LatLng latLng = entry.getValue();

            // Create marker options
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(locationName);

            // Add marker to the map
            mMap.addMarker(markerOptions);
        }

        // Move the camera to the first marker
        if (!locationCoordinates.isEmpty()) {
            LatLng firstLocation = locationCoordinates.values().iterator().next();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 5f));
        }
    }

    private void zoomIn() {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    private void zoomOut() {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
            } else {
                showLocationNotFoundDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showLocationNotFoundDialog();
        }
    }

    private void showLocationNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Location Not Found")
                .setMessage("The entered location could not be found. Please try again.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void fetchWeatherData(int locationId, Marker marker) {
        String apiUrl = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
        new WeatherDataTask(marker).execute(apiUrl);
    }

    private class WeatherDataTask extends AsyncTask<String, Void, Map<String, String>> {

        private Marker marker;

        public WeatherDataTask(Marker marker) {
            this.marker = marker;
        }

        @Override
        protected Map<String, String> doInBackground(String... urls) {
            String apiUrl = urls[0];
            Map<String, String> weatherData = new HashMap<>();

            try {
                URL url = new URL(apiUrl);
                InputStream inputStream = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                boolean isItem = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            isItem = true;
                        } else if (isItem) {
                            if (tagName.equalsIgnoreCase("title")) {
                                weatherData.put("Day", parser.nextText());
                            } else if (tagName.equalsIgnoreCase("pubDate")) {
                                weatherData.put("Date", parser.nextText());
                            } else if (tagName.equalsIgnoreCase("description")) {
                                String description = parser.nextText();
                                String[] lines = description.split("\\n");
                                for (String line : lines) {
                                    if (line.contains("Maximum Temperature:")) {
                                        weatherData.put("MaxTemperature", line.split(":")[1].trim());
                                    } else if (line.contains("Minimum Temperature:")) {
                                        weatherData.put("MinTemperature", line.split(":")[1].trim());
                                    } else if (line.contains("Visibility:")) {
                                        weatherData.put("Visibility", line.split(":")[1].trim());
                                    }
                                }
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            isItem = false;
                            break;
                        }
                    }
                    eventType = parser.next();
                }
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(Map<String, String> weatherData) {
            marker.setTag(weatherData);
            marker.showInfoWindow();
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View infoWindowView;

        public CustomInfoWindowAdapter() {
            infoWindowView = getLayoutInflater().inflate(R.layout.marker_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Map<String, String> weatherData = (Map<String, String>) marker.getTag();

            if (weatherData != null) {
                TextView dateTextView = infoWindowView.findViewById(R.id.date);
                TextView dayTextView = infoWindowView.findViewById(R.id.day);
                TextView visibilityTextView = infoWindowView.findViewById(R.id.visibility);

                dateTextView.setText(Html.fromHtml("<b>Date:</b> " + weatherData.get("Date")));
                dayTextView.setText(Html.fromHtml("<b>Weather Condition:</b> " + weatherData.get("Day")));

            }

            return infoWindowView;
        }
    }
}