package com.example.curryleafdiseaseidentifier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DiseaseDetailsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageView diseaseImage;  // ImageView for displaying the disease image
    private TextView diseaseName, diseaseSymptoms, diseaseCauses, diseaseTreatment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        diseaseName = findViewById(R.id.diseaseName);
        diseaseSymptoms = findViewById(R.id.diseaseSymptoms);
        diseaseCauses = findViewById(R.id.diseaseCauses);
        diseaseTreatment = findViewById(R.id.diseaseTreatment);
        diseaseImage = findViewById(R.id.diseaseImage);  // Initialize ImageView

        // Set the click listener for the back button
        backButton.setOnClickListener(v -> {
            // Navigate back to the Diseases List Activity
            Intent intent = new Intent(DiseaseDetailsActivity.this, DiseasesListActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity so that the user can't go back to it using the back button
        });

        // Get data from the intent
        String name = getIntent().getStringExtra("diseaseName");
        String symptoms = getIntent().getStringExtra("diseaseSymptoms");
        String causes = getIntent().getStringExtra("diseaseCauses");
        String treatment = getIntent().getStringExtra("diseaseTreatment");
        int imageResId = getIntent().getIntExtra("diseaseImage", R.drawable.fungal_leaf_spot);  // Default image if no image is passed

        // Set data to TextViews
        diseaseName.setText(name);
        diseaseSymptoms.setText("Symptoms: " + symptoms);
        diseaseCauses.setText("Causes: " + causes);
        diseaseTreatment.setText("Prevention/Treatment: " + treatment);

        // Set the image dynamically
        diseaseImage.setImageResource(imageResId);
    }
}
