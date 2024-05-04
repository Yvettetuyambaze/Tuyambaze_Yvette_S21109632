package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.me.gcu.tuyambaze_yvette_s21109632.R;

public class LandingScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        // Find the button and progress bar by their IDs
        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button demoButton = findViewById(R.id.howToUseAppButton);
        progressBar = findViewById(R.id.progressBar);

        // Show the progress bar and start the animation
        progressBar.setVisibility(View.VISIBLE);
        startProgressBarAnimation();

        // Set an OnClickListener for the "Get Started" button
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnectivityAndNavigate();
            }
        });

        // Set an OnClickListener for the "How to Use App" button
        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingScreen.this, DemoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkInternetConnectivityAndNavigate() {
        if (isInternetConnected()) {
            // Create an intent to navigate to the MainActivity
            Intent intent = new Intent(LandingScreen.this, MainActivity.class);
            startActivity(intent); // Start the MainActivity
            finish(); // Finish the LandingScreen activity
        } else {
            showNoInternetDialog();
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LandingScreen.this);
        builder.setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Check internet connectivity again when the "Retry" button is clicked
                        checkInternetConnectivityAndNavigate();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }

    private void startProgressBarAnimation() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progressBar.getProgress() + 10);
                if (progressBar.getProgress() >= 100) {
                    progressBar.setProgress(0);
                }
                handler.postDelayed(runnable, 100);
            }
        };
        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}