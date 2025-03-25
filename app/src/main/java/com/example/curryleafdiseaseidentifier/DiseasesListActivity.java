package com.example.curryleafdiseaseidentifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DiseasesListActivity extends AppCompatActivity {

    private RecyclerView diseasesRecyclerView;
    private List<DiseaseModel> diseaseList;
    private DiseaseAdapter diseaseAdapter;
    private TextView txtHomeMain, txtDiseasesMain, txtProfileMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases_list);

        diseasesRecyclerView = findViewById(R.id.diseasesRecyclerView);
        diseasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Navigation Bar Buttons
        txtHomeMain = findViewById(R.id.txtHomeMain);
        txtDiseasesMain = findViewById(R.id.txtDiseasesMain);
        txtProfileMain = findViewById(R.id.txtProfileMain);

        // Populate disease list with images and data
        diseaseList = new ArrayList<>();
        diseaseList.add(new DiseaseModel("Fungal Leaf Spot Disease",
                "Small, dark spots on leaves with yellow halos. Leaves turn yellow and fall prematurely.",
                "Caused by Cercospora, Alternaria, Colletotrichum. High humidity promotes growth.",
                "Use disease-resistant varieties, apply copper-based fungicides, remove infected leaves.",
                R.drawable.fungal_leaf_spot));  // Add disease image

        diseaseList.add(new DiseaseModel("Powdery Mildew",
                "White or grayish powdery growth on leaves, stems, and flowers. Leaves may curl or turn yellow.",
                "Caused by Erysiphe and Podosphaera. Thrives in warm, dry conditions.",
                "Ensure airflow, use sulfur sprays, remove infected leaves, apply neem oil.",
                R.drawable.powdery_mildew));

        diseaseList.add(new DiseaseModel("Tortoise Beetle Infestation",
                "Leaves have holes, skeletonized appearance. Metallic-colored beetles found on leaves.",
                "Caused by Chelymorpha cassidea beetles. Favorable in warm conditions.",
                "Handpick beetles, use neem oil, introduce natural predators, rotate crops.",
                R.drawable.tortoise_beetle));


        // Set up the RecyclerView adapter with the listener
        diseaseAdapter = new DiseaseAdapter(this, diseaseList, new DiseaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DiseaseModel disease) {
                // Create an intent to open DiseaseDetailsActivity
                Intent intent = new Intent(DiseasesListActivity.this, DiseaseDetailsActivity.class);

                // Pass disease data to DiseaseDetailsActivity
                intent.putExtra("diseaseName", disease.getName());
                intent.putExtra("diseaseSymptoms", disease.getSymptoms());
                intent.putExtra("diseaseCauses", disease.getCauses());
                intent.putExtra("diseaseTreatment", disease.getTreatment());
                intent.putExtra("diseaseImage", disease.getImageResourceId());

                // Start DiseaseDetailsActivity
                startActivity(intent);
            }
        });
        diseasesRecyclerView.setAdapter(diseaseAdapter);

        // Home Button: Reload ImageActivity (Home Page)
        txtHomeMain.setOnClickListener(v -> {
            Intent intent = new Intent(DiseasesListActivity.this, ImageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Prevents stacking activities
            startActivity(intent);
        });

        // Diseases Button: Redirect to Diseases List Page
        txtDiseasesMain.setOnClickListener(v -> {
            Intent intent = new Intent(DiseasesListActivity.this, DiseasesListActivity.class);
            startActivity(intent);
        });

        // Profile Button: Redirect to Profile Page
        txtProfileMain.setOnClickListener(v -> {
            Intent intent = new Intent(DiseasesListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
