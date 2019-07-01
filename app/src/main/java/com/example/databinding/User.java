package com.example.databinding;

public class User {
    private String firstName;
    private String lastName;
    private  String imageurl;

    public User(String firstName, String lastName,String imageurl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageurl=imageurl;
    }

    public String getImageurl() {
        return this.imageurl;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
}
