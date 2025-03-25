package com.example.curryleafdiseaseidentifier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int STORAGE_PERMISSION_REQUEST = 101;

    private ImageView imageView;
    private Button btnCamera, btnGallery, btnPredict;
    private String currentPhotoPath;
    private TextView txtHomeMain, txtDiseasesMain, txtProfileMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnPredict = findViewById(R.id.btnPredict);

        // Navigation Bar Buttons
        txtHomeMain = findViewById(R.id.txtHomeMain);
        txtDiseasesMain = findViewById(R.id.txtDiseasesMain);
        txtProfileMain = findViewById(R.id.txtProfileMain);

        // Set the click listener for the camera button
        btnCamera.setOnClickListener(v -> openCamera());

        // Set the click listener for the gallery button
        btnGallery.setOnClickListener(v -> openGallery());

        // Predict Button: Just show a toast message
        btnPredict.setOnClickListener(v -> {
            if (imageView.getDrawable() == null) {  // No image uploaded
                Toast.makeText(ImageActivity.this, "Please upload an image!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ImageActivity.this, "Prediction feature is currently disabled!", Toast.LENGTH_SHORT).show();
            }
        });

        // Home Button: Reload ImageActivity (Home Page)
        txtHomeMain.setOnClickListener(v -> {
            Intent intent = new Intent(ImageActivity.this, ImageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Prevents stacking activities
            startActivity(intent);
        });

        // Diseases Button: Redirect to Diseases List Page
        txtDiseasesMain.setOnClickListener(v -> {
            Toast.makeText(ImageActivity.this, "Diseases Section Clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ImageActivity.this, DiseasesListActivity.class);
            startActivity(intent);
        });

        // Profile Button: Redirect to Profile Page
        txtProfileMain.setOnClickListener(v -> {
            Intent intent = new Intent(ImageActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    // Method to open camera
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            dispatchTakePictureIntent();
        }
    }

    // Method to open gallery
    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            selectImageFromGallery();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            } else {
                selectImageFromGallery();
            }
        }
    }

    // Take picture using the camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("ImageActivity", "Error creating image file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.curryleafdiseaseidentifier.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    // Select image from gallery
    private void selectImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File file = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(file));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
            }
        }
    }

    // Create image file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
