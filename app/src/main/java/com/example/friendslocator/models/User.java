package com.example.friendslocator.models;

public class User {
    private String email="";
    private String name="";
    private String password="";
    private String phone="";
    private String imageuri="";
    User(){

    }
    public User(String email, String name, String password, String phone) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }
    public User(String email, String name, String password, String phone,String imageuri) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.imageuri = imageuri;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getImageURI() {
        return imageuri;
    }

    public void setImageURI(String imageuri) {
        this.imageuri = imageuri;
    }
}
