package com.iamdsr.travel.models;

import java.util.Objects;

public class UserModel {
    private String id, full_name, username, email, full_name_lowercase;

    public UserModel(){

    }

    public UserModel(String id, String full_name, String username, String email, String full_name_lowercase) {
        this.id = id;
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.full_name_lowercase = full_name_lowercase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name_lowercase() {
        return full_name_lowercase;
    }

    public void setFull_name_lowercase(String full_name_lowercase) {
        this.full_name_lowercase = full_name_lowercase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return id.equals(userModel.id) && full_name.equals(userModel.full_name) && username.equals(userModel.username) && email.equals(userModel.email) && full_name_lowercase.equals(userModel.full_name_lowercase);
    }

}
