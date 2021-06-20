package com.refuem.planbucket.Models;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private String group;
    private String username;
    private String messageType;
    private String message;
    private String date;


    public MessageModel() {
    }

    public MessageModel(String group,String username, String messageType, String message, String date) {
        this.group = group;
        this.username = username;
        this.messageType = messageType;
        this.message = message;
        this.date = date;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
