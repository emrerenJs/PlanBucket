package com.refuem.planbucket.Models;

import java.io.Serializable;

public class GroupTitleRequestModel implements Serializable {
    private String title;
    private String token;

    public GroupTitleRequestModel() {
    }

    public GroupTitleRequestModel(String title, String token) {
        this.title = title;
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
