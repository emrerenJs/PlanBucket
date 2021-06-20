package com.refuem.planbucket.Models;

import com.refuem.planbucket.EditProfileActivity;

import java.io.Serializable;

public class EditProfileModel implements Serializable {
    private String job;
    private String gender;
    private String biography;
    private String firstname;
    private String lastname;
    private String photo;
    private String username;
    private String token;

    public EditProfileModel() {
    }

    public EditProfileModel(String job, String gender, String biography, String firstname, String lastname, String photo, String username, String token) {
        this.job = job;
        this.gender = gender;
        this.biography = biography;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photo = photo;
        this.username = username;
        this.token = token;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
