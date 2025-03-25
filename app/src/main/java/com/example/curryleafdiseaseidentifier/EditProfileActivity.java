package com.example.curryleafdiseaseidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText editName, editEmail;
    private Button saveButton, changeProfileImageButton;
    private ImageView editProfileImage;
    private ProgressBar progressBar;
    private String usernameUser, currentName, currentEmail;
    private DatabaseReference reference;

    // Firebase Storage reference
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase instances
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Views
        editName = findViewById(R.id.editName);
        progressBar = findViewById(R.id.progressBar); // Make sure to add this ProgressBar to your layout
        if (progressBar == null) {
            // Create a progress bar programmatically if it doesn't exist in layout
            progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setVisibility(View.GONE);
        }

        // Check if editEmail exists in the layout
        try {
            editEmail = findViewById(R.id.editEmail);
        } catch (Exception e) {
            Log.e(TAG, "EditEmail view not found: " + e.getMessage());
            // If editEmail doesn't exist in the layout, we'll handle this later
        }
        saveButton = findViewById(R.id.saveButton);
        changeProfileImageButton = findViewById(R.id.btnChangeProfileImage);
        editProfileImage = findViewById(R.id.editProfileImage);

        // Get username from intent
        usernameUser = getIntent().getStringExtra("username");
        Log.d(TAG, "Received username: " + (usernameUser != null ? usernameUser : "null"));

        // If username is not provided through intent, try to get it from Firebase Auth
        if (usernameUser == null || usernameUser.isEmpty()) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                String userEmail = mAuth.getCurrentUser().getEmail();
                fetchUserDataByEmail(userEmail);
            } else {
                Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // Load current user data using the provided username
            loadUserData();
        }

        // Save button click listener
        saveButton.setOnClickListener(view -> updateUserProfile());

        // Change Profile Image button click listener
        changeProfileImageButton.setOnClickListener(view -> openImagePicker());
    }

    // Fetch user data by email (fallback method if username is not provided)
    private void fetchUserDataByEmail(String userEmail) {
        Log.d(TAG, "Fetching user data by email: " + userEmail);

        // Find the user in Firebase Database by email
        Query query = reference.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Loop through results (should be only one)
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        currentName = userSnapshot.child("name").getValue(String.class);
                        currentEmail = userEmail;
                        usernameUser = userSnapshot.child("username").getValue(String.class);

                        // Set the current values to edit fields
                        editName.setText(currentName);
                        if (editEmail != null) {
                            editEmail.setText(currentEmail);
                        }

                        // Check if profile image URL exists and load it
                        if (userSnapshot.hasChild("profileImageUrl")) {
                            String imageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);
                            // Load image with Glide
                            loadProfileImage(imageUrl);
                        }

                        Log.d(TAG, "User data loaded by email: " + usernameUser);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(EditProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Load user data from Firebase
    private void loadUserData() {
        Log.d(TAG, "Loading user data for username: " + usernameUser);
        Query query = reference.orderByChild("username").equalTo(usernameUser);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        currentName = userSnapshot.child("name").getValue(String.class);
                        currentEmail = userSnapshot.child("email").getValue(String.class);

                        // Set the current values to edit fields
                        editName.setText(currentName);
                        if (editEmail != null) {
                            editEmail.setText(currentEmail);
                        }

                        // Check if profile image URL exists and load it
                        if (userSnapshot.hasChild("profileImageUrl")) {
                            String imageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);
                            // Load image with Glide
                            loadProfileImage(imageUrl);
                        }

                        Log.d(TAG, "User data loaded: " + currentName);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(EditProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to load profile image using Glide
    private void loadProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Glide.with(EditProfileActivity.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.personlogo)  // Add a default placeholder image resource
                        .error(R.drawable.personlogoerror)       // Add an error image resource
                        .into(editProfileImage);
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile image: " + e.getMessage());
            }
        }
    }

    // Update the user profile
    private void updateUserProfile() {
        if (usernameUser == null || usernameUser.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Error: Username not available", Toast.LENGTH_SHORT).show();
            return;
        }

        final String newName = editName.getText().toString().trim();

        // Create final variable for email
        final String newEmail;
        // Get new email if the editEmail field exists
        if (editEmail != null) {
            newEmail = editEmail.getText().toString().trim();
        } else {
            newEmail = currentEmail; // Default to current email
        }

        boolean hasChanges = false;

        // Check if name is changed
        if (!newName.equals(currentName)) {
            hasChanges = true;
        }

        // Check if email is changed (only if editEmail exists)
        if (editEmail != null && !newEmail.equals(currentEmail)) {
            hasChanges = true;
        }

        final boolean finalHasChanges = hasChanges;

        if (finalHasChanges) {
            // Find the user by username
            Query query = reference.orderByChild("username").equalTo(usernameUser);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String userKey = userSnapshot.getKey();

                            // Update the name and email
                            reference.child(userKey).child("name").setValue(newName);
                            if (editEmail != null) {
                                reference.child(userKey).child("email").setValue(newEmail);
                            }

                            // Update current values
                            currentName = newName;
                            if (editEmail != null) {
                                currentEmail = newEmail;
                            }

                            // Upload profile image if changed
                            if (editProfileImage.getDrawable() instanceof BitmapDrawable) {
                                uploadProfileImage(userKey);
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                // Return to profile page with updated data
                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                intent.putExtra("username", usernameUser);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Update profile error: " + error.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no profile data changed, check if image changed
            if (editProfileImage.getDrawable() instanceof BitmapDrawable) {
                // Find the user key first
                Query query = reference.orderByChild("username").equalTo(usernameUser);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userKey = userSnapshot.getKey();
                                uploadProfileImage(userKey);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Find user error: " + error.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Failed to find user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(EditProfileActivity.this, "No changes detected", Toast.LENGTH_SHORT).show();

                // Return to profile page anyway
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("username", usernameUser);
                startActivity(intent);
                finish();
            }
        }
    }

    // Open Image Picker (Gallery)
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                editProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error loading image: " + e.getMessage());
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Upload Profile Image to Firebase Storage
    private void uploadProfileImage(String userKey) {
        if (usernameUser == null || usernameUser.isEmpty()) {
            Log.e(TAG, "Username is null or empty");
            Toast.makeText(this, "Error: Username not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        try {
            // Get the bitmap from ImageView
            BitmapDrawable drawable = (BitmapDrawable) editProfileImage.getDrawable();
            if (drawable == null) {
                Log.e(TAG, "Drawable is null");
                Toast.makeText(this, "Error: No image selected", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = drawable.getBitmap();

            // Log the upload attempt
            Log.d(TAG, "Attempting to upload image for: " + usernameUser);

            // Convert bitmap to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] data = baos.toByteArray();

            // Create a unique filename using UUID to avoid conflicts
            String uniqueId = UUID.randomUUID().toString();
            String imagePath = "profile_images/" + usernameUser + "_" + uniqueId + ".jpg";
            Log.d(TAG, "Storage path: " + imagePath);

            // Create a new storage reference for this specific upload
            StorageReference profileImageRef = storage.getReference().child(imagePath);

            // Upload the image
            UploadTask uploadTask = profileImageRef.putBytes(data);
            uploadTask
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "Upload progress: " + progress + "%");
                        if (progressBar != null) {
                            progressBar.setProgress((int) progress);
                        }
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Upload successful");

                        // Get the download URL
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Log.d(TAG, "Download URL: " + downloadUrl);

                            // Hide progress
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }

                            // Save the image URL in the database
                            reference.child(userKey).child("profileImageUrl").setValue(downloadUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Profile image URL saved to database");
                                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                        // Return to profile page
                                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                        intent.putExtra("username", usernameUser);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to update profile image URL: " + e.getMessage());
                                        Toast.makeText(EditProfileActivity.this, "Failed to update profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            // Hide progress
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(EditProfileActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Hide progress
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e(TAG, "Failed to upload image: " + e.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            // Hide progress
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Log.e(TAG, "Error processing image: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}