package com.tnc.crackertracker.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.tnc.crackertracker.Fragment.LogFragment;
import com.tnc.crackertracker.Fragment.MealsFragment;
import com.tnc.crackertracker.Fragment.ProfileFragment;
import com.tnc.crackertracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.menu_bar);
        // Load LogFragment directly in onCreate
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LogFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_log) {
                    fragment = new LogFragment();
                } else if (itemId == R.id.navigation_profile) {
                    fragment = new ProfileFragment();
                } else if (itemId == R.id.navigation_meals) {
                    fragment = new MealsFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
                return true;
            }
        });
    }

}