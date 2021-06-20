package com.refuem.planbucket.Models;

import java.io.Serializable;

public class ConnectChatModel implements Serializable {
    private String username;
    private String title;
    private String token;
    private boolean isNotificationClient;

    public ConnectChatModel() {
    }

    public ConnectChatModel(String username, String title, String token, boolean isNotificationClient) {
        this.username = username;
        this.title = title;
        this.token = token;
        this.isNotificationClient = isNotificationClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean getIsNotificationClient() {
        return isNotificationClient;
    }

    public void setIsNotificationClient(boolean notificationClient) {
        isNotificationClient = notificationClient;
    }
}
