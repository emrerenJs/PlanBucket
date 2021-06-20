package com.refuem.planbucket.Models;

import java.io.Serializable;

public class SendInviteModel implements Serializable {
    private String title;
    private String username;
    private String token;

    public SendInviteModel() {
    }

    public SendInviteModel(String title, String username, String token) {
        this.title = title;
        this.username = username;
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
