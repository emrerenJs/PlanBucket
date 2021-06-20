package com.refuem.planbucket.Models;

import java.io.Serializable;

public class SendMessageModel implements Serializable {
    private String messageType;
    private String message;
    private String username;
    private String token;
    private String title;

    public SendMessageModel() {
    }

    public SendMessageModel(String messageType, String message, String username, String token, String title) {
        this.messageType = messageType;
        this.message = message;
        this.username = username;
        this.token = token;
        this.title = title;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
