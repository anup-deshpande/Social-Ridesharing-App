package com.example.inclass01_advancemad;

import java.io.Serializable;

public class User implements Serializable {

    public String userId;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String city;
    public String gender;
    public String imageUrl;

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", city='" + city + '\'' +
                ", gender='" + gender + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
