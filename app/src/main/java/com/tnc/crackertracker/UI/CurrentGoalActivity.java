package com.tnc.crackertracker.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.tnc.crackertracker.R;

public class CurrentGoalActivity extends AppCompatActivity {
    ImageView bulking,cutting,maintaining,health;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_current_goal);

        bulking = findViewById(R.id.bulking);
        cutting = findViewById(R.id.cutting);
        maintaining = findViewById(R.id.maintaining);
        health = findViewById(R.id.health);

        // Set click listeners
        bulking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomeScreen();
            }
        });
        cutting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomeScreen();
            }
        });

        maintaining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomeScreen();
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomeScreen();
            }
        });
    }
    private void navigateToHomeScreen() {
        // Navigate to HomeScreenActivity
        Intent intent = new Intent(CurrentGoalActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity after navigation
    }
}