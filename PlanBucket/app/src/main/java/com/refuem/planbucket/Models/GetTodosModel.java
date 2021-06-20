package com.refuem.planbucket.Models;

import java.io.Serializable;

public class GetTodosModel implements Serializable {

    private String todoTitle;
    private String todoBody;
    private String username;
    private boolean isFinished;

    public GetTodosModel() {
    }

    public GetTodosModel(String todoTitle, String todoBody, String username, boolean isFinished) {
        this.todoTitle = todoTitle;
        this.todoBody = todoBody;
        this.username = username;
        this.isFinished = isFinished;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getTodoBody() {
        return todoBody;
    }

    public void setTodoBody(String todoBody) {
        this.todoBody = todoBody;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
