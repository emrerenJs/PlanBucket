package com.refuem.planbucket.Models;

import java.io.Serializable;

public class FinishTodoModel implements Serializable {

    private String title;
    private String todoTitle;
    private String token;
    private boolean finishResult;

    public FinishTodoModel() {
    }

    public FinishTodoModel(String title, String todoTitle, String token, boolean finishResult) {
        this.title = title;
        this.todoTitle = todoTitle;
        this.token = token;
        this.finishResult = finishResult;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFinishResult() {
        return finishResult;
    }

    public void setFinishResult(boolean finishResult) {
        this.finishResult = finishResult;
    }
}
