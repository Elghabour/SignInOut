package com.example.signuplogintask;


import com.google.firebase.firestore.Exclude;

public class User {
    private String user_id;
    private String first_name;
    private String last_name;
    private String gender;
    private String birth_date;
    private String country;
    private String phone;
    private String bio;
    private String profile_picture_url;
    private float rating = 0.0f;

    public User(String first_name, String last_name,
                String gender, String birth_date,
                String country, String phone,
                String bio, String profile_picture, float rating) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.birth_date = birth_date;
        this.country = country;
        this.phone = phone;
        this.bio = bio;
        this.profile_picture_url = profile_picture;
        this.rating = rating;
    }

    public User() {
        //required
    }

    @Exclude
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getprofile_picture_url() {
        return profile_picture_url;
    }

    public void setprofile_picture_url(String profile_picture) {
        this.profile_picture_url = profile_picture;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
