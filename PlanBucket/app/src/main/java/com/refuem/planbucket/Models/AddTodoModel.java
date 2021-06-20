package com.refuem.planbucket.Models;

import java.io.Serializable;

public class AddTodoModel implements Serializable {

    private String title;
    private String username;
    private String todoTitle;
    private String todoBody;
    private String token;

    public AddTodoModel() {
    }

    public AddTodoModel(String title, String username, String todoTitle, String todoBody, String token) {
        this.title = title;
        this.username = username;
        this.todoTitle = todoTitle;
        this.todoBody = todoBody;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
