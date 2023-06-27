package com.example.autologoutapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {
    private Handler logoutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reset logout timer on user activity
        resetLogoutTimer();

        // Add your logic and functionality for the main screen
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resetLogoutTimer(); // Reset logout timer on user touch event
        return super.onTouchEvent(event);
    }

    private void resetLogoutTimer() {
        if (logoutHandler != null) {
            logoutHandler.removeCallbacks(logoutRunnable);
        }

        logoutHandler = new Handler();
        logoutHandler.postDelayed(logoutRunnable, 60 * 1000); // 1 minutes timeout
    }

    private Runnable logoutRunnable = new Runnable() {
        @Override
        public void run() {
            // Implement logout logic and navigate back to the login screen
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    };
}
