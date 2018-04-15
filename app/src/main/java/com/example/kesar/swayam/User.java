package com.example.kesar.swayam;

/**
 * Created by kesar on 2/16/2018.
 */

public class User {

    private String name;
    private String email;
    private String phone;
    private String password;
    private String thumb_image;
    private String image;

    private String emergency_contact_one;
    private String emergency_contact_two;

    public User() {

    }

    public User(String name, String email, String phone, String password, String image, String thumb_image, String emergency_contact_one, String emergency_contact_two) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.thumb_image = thumb_image;
        this.emergency_contact_one = emergency_contact_one;
        this.emergency_contact_two = emergency_contact_two;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getEmergency_contact_one() {
        return emergency_contact_one;
    }

    public void setEmergency_contact_one(String emergency_contact_one) {
        this.emergency_contact_one = emergency_contact_one;
    }

    public String getEmergency_contact_two() {
        return emergency_contact_two;
    }

    public void setEmergency_contact_two(String emergency_contact_two) {
        this.emergency_contact_two = emergency_contact_two;
    }

}
