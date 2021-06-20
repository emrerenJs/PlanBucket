package com.refuem.planbucket.Models;

import java.io.Serializable;

public class NotificationInviteModel implements Serializable {

    private String group;
    private String header;
    private String message;

    public NotificationInviteModel() {
    }

    public NotificationInviteModel(String group, String header, String message) {
        this.group = group;
        this.header = header;
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
