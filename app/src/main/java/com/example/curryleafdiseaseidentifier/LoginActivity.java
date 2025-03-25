package com.example.curryleafdiseaseidentifier;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupRedirectText, forgotPassword;
    ImageView showPasswordIcon;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, navigate to the home page (ImageActivity)
            Intent intent = new Intent(LoginActivity.this, ImageActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity
        }

        // Initialize views
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        showPasswordIcon = findViewById(R.id.showPasswordIcon);

        // Make "Sign Up" part bold dynamically
        String text = "Not yet Registered? Sign Up";
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("Sign Up");
        int end = start + "Sign Up".length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
        signupRedirectText.setText(spannableString);

        // Set listeners for buttons
        loginButton.setOnClickListener(v -> loginUser());
        signupRedirectText.setOnClickListener(v -> navigateToSignUp());
        forgotPassword.setOnClickListener(v -> resetPassword());
        showPasswordIcon.setOnClickListener(v -> togglePasswordVisibility());
    }

    // Login with Firebase Authentication
    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Navigate to Home page
                        Intent intent = new Intent(LoginActivity.this, ImageActivity.class);
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    } else {
                        // Firebase error message
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Navigate to SignUp page
    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    // Reset password logic
    private void resetPassword() {
        String email = loginEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Toggle password visibility
    private void togglePasswordVisibility() {
        if (loginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show password
            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPasswordIcon.setImageResource(R.drawable.baseline_visibility_off_24); // Change to "hide" icon
        } else {
            // Hide password
            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPasswordIcon.setImageResource(R.drawable.baseline_visibility_24); // Change to "show" icon
        }
    }
}
