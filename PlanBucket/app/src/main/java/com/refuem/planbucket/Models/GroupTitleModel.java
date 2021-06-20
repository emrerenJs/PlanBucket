package com.refuem.planbucket.Models;

import java.io.Serializable;

public class GroupTitleModel implements Serializable {

    private String groupTitle;

    public GroupTitleModel() {
    }

    public GroupTitleModel(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }
}
