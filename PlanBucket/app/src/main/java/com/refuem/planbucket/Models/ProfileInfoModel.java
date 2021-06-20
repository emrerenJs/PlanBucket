package com.refuem.planbucket.Models;

import java.io.Serializable;

public class ProfileInfoModel implements Serializable {
    private String job;
    private String gender;
    private String biography;
    private String firstname;
    private String lastname;
    private String photo;

    public ProfileInfoModel() {
    }

    public ProfileInfoModel(String job, String gender, String biography, String firstname, String lastname, String photo) {
        this.job = job;
        this.gender = gender;
        this.biography = biography;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photo = photo;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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
}
