package com.example.anishdalal.finalapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haroon on 4/30/17.
 */

public class User {

    private String username;
    private String email;
    private String name;
    private String description;
    private String paypalEmail;

    public User() {

    }

    public User(String username, String email, String name, String description, String paypalEmail) {
        this.username = username;
        this.email = email;
        this.paypalEmail = paypalEmail;
        this.name = name;
        this.description = description;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPaypalEmail() {return this.paypalEmail; }

    public void setPaypalEmail(String n) { this.paypalEmail = n; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", this.username);
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("paypalEmail", this.paypalEmail);
        return map;
    }

    public boolean equals(Object o) {
        if (o instanceof User) {
            User u = (User) o;
            return (this.username.equals(u.getUsername()));
        }
        return false;
    }
}
