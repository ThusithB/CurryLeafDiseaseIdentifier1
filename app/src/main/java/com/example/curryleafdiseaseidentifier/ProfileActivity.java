package com.example.curryleafdiseaseidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername;
    Button editProfile, logoutButton;

    private TextView txtHomeMain, txtDiseasesMain, txtProfileMain;

    // Firebase Database reference
    DatabaseReference reference;
    String usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase database reference
        reference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Views
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        editProfile = findViewById(R.id.editProfileButton);  // The button to navigate to EditProfileActivity
        logoutButton = findViewById(R.id.logoutButton);  // The new logout button

        // Retrieve username passed from LoginActivity or previous activity
        Intent receivedIntent = getIntent();
        usernameUser = receivedIntent.getStringExtra("username");

        // If username is null, try to get it from Firebase Auth
        if (usernameUser == null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                String userEmail = mAuth.getCurrentUser().getEmail();
                fetchUserDataByEmail(userEmail);
            } else {
                // If user is not logged in, redirect to login screen
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } else {
            // Fetch user data from Firebase using username
            showUserData();
        }

        // Set the click listener to open EditProfileActivity
        editProfile.setOnClickListener(view -> passUserDataToEditProfile());

        // Set the click listener to handle logout
        logoutButton.setOnClickListener(v -> logout());

        // Navigation Bar Buttons
        txtHomeMain = findViewById(R.id.txtHomeMain);
        txtDiseasesMain = findViewById(R.id.txtDiseasesMain);
        txtProfileMain = findViewById(R.id.txtProfileMain);
        // Home Button: Reload ImageActivity (Home Page)
        txtHomeMain.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ProfileActivity.this, ImageActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Prevents stacking activities
            startActivity(homeIntent);
        });

        // Diseases Button: Redirect to Diseases List Page
        txtDiseasesMain.setOnClickListener(v -> {
            Intent diseasesIntent = new Intent(ProfileActivity.this, DiseasesListActivity.class);
            startActivity(diseasesIntent);
        });

        // Profile Button: Redirect to Profile Page
        txtProfileMain.setOnClickListener(v -> {
            Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
    }

    // Fetch user data by email (fallback method)
    private void fetchUserDataByEmail(String userEmail) {
        profileEmail.setText(userEmail);

        // Find the user in Firebase Database by email
        Query query = reference.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Loop through results (should be only one)
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String username = userSnapshot.child("username").getValue(String.class);

                        // Set the retrieved values to the TextViews
                        profileName.setText(name);
                        profileUsername.setText(username);

                        // Store username for later use
                        usernameUser = username;
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show the user data in the profile
    public void showUserData() {
        // Query the database for the user by their username
        Query query = reference.orderByChild("username").equalTo(usernameUser);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Loop through results (should be only one)
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String username = userSnapshot.child("username").getValue(String.class);

                        // Set the fetched data to the profile TextViews
                        profileName.setText(name);
                        profileEmail.setText(email);
                        profileUsername.setText(username);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(ProfileActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Pass user data to EditProfileActivity
    private void passUserDataToEditProfile() {
        // Pass username to EditProfileActivity
        Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        editProfileIntent.putExtra("username", usernameUser);  // Pass username to EditProfileActivity
        startActivity(editProfileIntent);
    }

    // Handle logout functionality
    public void logout() {
        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Show a Toast to notify the user
        Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();  // Close the current activity so the user can't go back to ProfileActivity
    }
}