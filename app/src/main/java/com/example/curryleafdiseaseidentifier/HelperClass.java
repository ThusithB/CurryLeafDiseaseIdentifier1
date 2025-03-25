package com.example.curryleafdiseaseidentifier;

public class HelperClass {

    String name, email, username, password;
    String profileImageUrl; // Add this field for storing image URL

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Update constructor to include profileImageUrl
    public HelperClass(String name, String email, String username, String password, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
    }

    // Also keep the original constructor for backward compatibility
    public HelperClass(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImageUrl = ""; // Default empty string
    }

    public HelperClass() {
    }
}