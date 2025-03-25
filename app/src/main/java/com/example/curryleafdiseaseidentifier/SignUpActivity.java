package com.example.curryleafdiseaseidentifier;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    ImageView showPasswordIcon;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        showPasswordIcon = findViewById(R.id.showPasswordIcon);

        // Make "Login" part bold in loginRedirectText
        String text = "Already an User? Login";
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("Login");
        int end = start + "Login".length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
        loginRedirectText.setText(spannableString);

        // Set Click Listeners
        signupButton.setOnClickListener(v -> signupUser());
        loginRedirectText.setOnClickListener(v -> navigateToLogin());
        showPasswordIcon.setOnClickListener(v -> togglePasswordVisibility());
    }

    // Sign-up logic using Firebase Authentication
    private void signupUser() {
        String name = signupName.getText().toString();
        String email = signupEmail.getText().toString();
        String username = signupUsername.getText().toString();
        String password = signupPassword.getText().toString();

        // Validate user inputs
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || name.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication: Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // After successful sign-up, store user data in Firebase Realtime Database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("users");

                        HelperClass helperClass = new HelperClass(name, email, username, password);
                        reference.child(username).setValue(helperClass);

                        Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to Login page
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign-up fails
                        Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Navigate to Login page
    private void navigateToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Toggle password visibility
    private void togglePasswordVisibility() {
        if (signupPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show password
            signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPasswordIcon.setImageResource(R.drawable.baseline_visibility_off_24); // Change to "hide" icon
        } else {
            // Hide password
            signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPasswordIcon.setImageResource(R.drawable.baseline_visibility_24); // Change to "show" icon
        }
    }
}
