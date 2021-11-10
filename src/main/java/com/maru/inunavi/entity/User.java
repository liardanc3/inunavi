package com.maru.inunavi.entity;

public class User {
    private String id;
    private String password;
    private String name;
    private String email;
    private String class_list;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClass_list() {
        return class_list;
    }

    public void setClass_list(String class_list) {
        this.class_list = class_list;
    }
}
