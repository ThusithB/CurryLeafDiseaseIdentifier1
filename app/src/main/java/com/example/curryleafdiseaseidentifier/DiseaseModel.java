package com.example.curryleafdiseaseidentifier;

public class DiseaseModel {
    private String name;
    private String symptoms;
    private String causes;
    private String treatment;
    private int imageResourceId; // Stores image resource ID

    public DiseaseModel(String name, String symptoms, String causes, String treatment, int imageResourceId) {
        this.name = name;
        this.symptoms = symptoms;
        this.causes = causes;
        this.treatment = treatment;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getCauses() {
        return causes;
    }

    public String getTreatment() {
        return treatment;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
