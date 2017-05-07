package com.example.anishdalal.finalapp;

import android.location.Location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haroon on 4/14/17.
 */

public class TaskObj implements Serializable {

    private String title;
    private double price;
    private String description;
    private String username;
    private double task_lat;
    private double task_longi;
    private double curr_lat;
    private double curr_long;
    private String accepted_by;
    private String paymentEmail;

    public TaskObj(String t, double p, String des, double task_lat, double task_longi,
                   double curr_lat, double curr_longi) {
        this.title = t;
        this.price = p;
        this.description = des;
        this.task_lat = task_lat;
        this.task_longi = task_longi;
        this.curr_lat = curr_lat;
        this.curr_long = curr_longi;
    }

    public TaskObj() {}

    public String getTitle() {
        return this.title;
    }

    public double getPrice() {
        return this.price;
    }

    public String getUsername() {return this.username;}

    public String getDescription() { return this.description;}

    public String getAccepted_by() { return this.accepted_by; }

    public double getTask_lat() {
        return this.task_lat;
    }

    public double getTask_longi() { return this.task_longi; }

    public double getCurr_lat() { return this.curr_lat; }

    public double getCurr_long() { return this.curr_long; }

    public void setBroadcaster(String s) {this.username = s;}

    public void setTitle(String s) {this.title = s;}

    public void setDescription(String s) {this.description = s;}

    public void setAccepted_by(String username) {this.accepted_by = username;}

    public void setPrice(Double p) {this.price = p;}

    public void setUsername(String s) {this.username = s;}

    public void setTask_lat(Double d) {this.task_lat = d;}

    public void setTask_longi(Double d) {this.task_longi = d;}

    public void setCurr_lat(Double d) {this.curr_lat = d;}

    public void setCurr_long(Double l) {this.curr_long = l;}

    public double getLongitude() {
        return this.task_longi;
    }

    public String getPaymentEmail() { return this.paymentEmail; }

    public void setPaymentEmail(String s) { this.paymentEmail = s; }

    public String getDistance() {
        float[] f = new float[1];
        Location.distanceBetween(curr_lat, curr_long, task_lat, task_longi, f);
        return String.format("%.2fm", f[0]*0.000621371);
    }

    public Map<String, Object> taskmap(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", this.username);
        hashMap.put("accepted_by", this.accepted_by);
        hashMap.put("title", this.title);
        hashMap.put("price", this.price);
        hashMap.put("description", this.description);
        hashMap.put("task_lat", this.task_lat);
        hashMap.put("task_longi", this.task_longi);
        hashMap.put("curr_lat", this.curr_lat);
        hashMap.put("curr_long", this.curr_long);
        hashMap.put("paymentEmail", this.paymentEmail);

        return hashMap;
    }

    public String toString() {
        return String.format("%s - %.2f", title, price);
    }

    public boolean equals(Object o) {
        if (o instanceof TaskObj) {
            TaskObj t = (TaskObj) o;
            return (this.title.equals(t.getTitle())
                    && this.price == t.getPrice()
                    && this.username.equals(t.getUsername())
                    && this.description.equals(t.getDescription()));
        }
        return false;
    }
}
