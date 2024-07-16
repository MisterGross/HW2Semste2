package com.tnc.crackertracker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.tnc.crackertracker.R;
import com.tnc.crackertracker.Model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnc.crackertracker.UI.LoginActivity;

public class ProfileFragment extends Fragment {
    private DatabaseReference databaseReference;
    private TextView usernameTextView;
    private Button logout;
    private ShapeableImageView profileImageView;
    private User currentUser; // Declare currentUser as a member variable

    private boolean isUserDataLoaded = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTextView = view.findViewById(R.id.user_name);
        profileImageView = view.findViewById(R.id.userprofile);
        logout = view.findViewById(R.id.logout_btn);

        // Set onClickListener for logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Sign out user

                // Navigate to login screen, assuming LoginActivity is your login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isUserDataLoaded) {
            fetchUserData();
        } else {
            displayUserData();
        }
    }

    private void fetchUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Fetch the user data from Firebase Database
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            currentUser = user; // Store the fetched user data
                            isUserDataLoaded = true; // Mark data as loaded
                            displayUserData(); // Display the fetched data in UI
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors here.
                }
            });
        }
    }

    private void displayUserData() {
        if (currentUser != null) {
            // Set username
            if (usernameTextView != null) {
                usernameTextView.setText(currentUser.getUsername());
            }
            // Load profile image using Glide or any other image loading library
            if (getContext() != null && profileImageView != null) {
                Glide.with(getContext())
                        .load(currentUser.getProfileImageUrl())
                        .into(profileImageView);
            }
        }
    }
}
