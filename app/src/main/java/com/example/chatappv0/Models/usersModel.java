package com.example.chatappv0.Models;

public class usersModel {
    public String username;
    public String status;
    public String userid;
    public String email;
    public String country;
    public String language;
    public String gender;
    public String profession;
    public String imageurl;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public usersModel() {
    }

    public usersModel(String username, String s, String userId,String email,String imageurl) {
        this.username = username;
        this.status = s;
        this.userid=userId;
        this.email=email;
        this.imageurl=imageurl;
    }



}
