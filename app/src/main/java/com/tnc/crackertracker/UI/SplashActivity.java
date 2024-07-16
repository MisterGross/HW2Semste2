package com.tnc.crackertracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.tnc.crackertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 4000; // 2 seconds delay
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        // Delay navigation using a Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateBasedOnAuthState();
            }
        }, SPLASH_DELAY);


    }
    private void navigateBasedOnAuthState() {
        // Check if user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to home screen
            navigateToHomeActivity();
        } else {
            // User is not logged in, navigate to login screen
            navigateToLoginActivity();
        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, CurrentGoalActivity.class); // Replace with your home screen activity
        startActivity(intent);
        finish(); // Close SplashActivity to prevent returning to it when back button is pressed from HomeActivity
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class); // Replace with your login screen activity
        startActivity(intent);
        finish(); // Close SplashActivity to prevent returning to it when back button is pressed from LoginActivity
    }
}