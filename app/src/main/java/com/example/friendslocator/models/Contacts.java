package com.example.friendslocator.models;

public class Contacts {
    private String name="";
    private String imageURL="";
    private String email="";
    private String phone ="";
    private String status="";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    private double lng;
    private  double lat;

    public Contacts(String name, String imageURL, String email, String phone,String status) {
        this.name = name;
        this.imageURL = imageURL;
        this.email = email;
        this.phone = phone;
    }
    public Contacts(String name, String imageURL, String email, String phone,double  lng,double lat,String status) {
        this.name = name;
        this.imageURL = imageURL;
        this.email = email;
        this.phone = phone;
        this.lng = lng;
        this.lat = lat;
        this.status = status;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageuri) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
