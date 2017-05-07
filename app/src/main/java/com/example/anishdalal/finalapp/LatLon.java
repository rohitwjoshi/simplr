package com.example.anishdalal.finalapp;

import java.util.HashMap;

/**
 * Created by anishdalal on 4/29/17.
 */

public class LatLon
{
    private double latitude;
    private double longitude;

    public LatLon(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {return this.latitude;}
    public double getLongitude() {return this.longitude;}

    public void setLongitude(double longi) { this.longitude = longi; }
    public void setLatitude( double latitude ) { this.latitude = latitude;}

    public HashMap toMap()
    {
        HashMap retMap = new HashMap();
        retMap.put("latitude", this.latitude);
        retMap.put("longitude", this.longitude);
        return retMap;
    }
}
