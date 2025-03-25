package com.example.curryleafdiseaseidentifier;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;  // FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // If user is logged in, navigate to ImageActivity (Home Page)
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so the user can't go back to it
        }

        // If the user is not logged in, show the login button
        Button btnNextToLogin = findViewById(R.id.btnNextToLogin);

        // Set click listener for the login button
        btnNextToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close MainActivity
            }
        });
    }
}
