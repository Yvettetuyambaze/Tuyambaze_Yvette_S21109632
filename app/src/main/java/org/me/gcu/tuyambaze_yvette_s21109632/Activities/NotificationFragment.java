package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

public class NotificationFragment extends Fragment {

    private TextView notificationTitleTextView;
    private TextView notificationDescriptionTextView;
    private Switch severeWeatherSwitch;
    private Switch nextHourPrecipitationSwitch;
    private TextView associateNotificationTextView;
    private TextView yourLocationsTextView;
    private TextView locationCupertino;
    private TextView locationNewYork;
    private TextView locationMumbai;
    private TextView notSupportedTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationTitleTextView = view.findViewById(R.id.notificationTitleTextView);
        notificationDescriptionTextView = view.findViewById(R.id.notificationDescriptionTextView);
        severeWeatherSwitch = view.findViewById(R.id.severeWeatherSwitch);
        nextHourPrecipitationSwitch = view.findViewById(R.id.nextHourPrecipitationSwitch);
        associateNotificationTextView = view.findViewById(R.id.associateNotificationTextView);
        yourLocationsTextView = view.findViewById(R.id.yourLocationsTextView);
        locationCupertino = view.findViewById(R.id.locationCupertino);
        locationNewYork = view.findViewById(R.id.locationNewYork);
        locationMumbai = view.findViewById(R.id.locationNewYork);
        notSupportedTextView = view.findViewById(R.id.notSupportedTextView);

        // Set up switch listeners or any other logic

        return view;
    }
}