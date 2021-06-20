package com.refuem.planbucket.Models;

import java.io.Serializable;

public class GroupInfoModel implements Serializable {

    private String title;

    public GroupInfoModel() {
    }

    public GroupInfoModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
