package com.refuem.planbucket.Models;

import java.io.Serializable;

public class ResponseModel implements Serializable {
    private String head;
    private String body;
    private int status;

    public ResponseModel() {
    }

    public ResponseModel(String head, String body, int status) {
        this.head = head;
        this.body = body;
        this.status = status;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
