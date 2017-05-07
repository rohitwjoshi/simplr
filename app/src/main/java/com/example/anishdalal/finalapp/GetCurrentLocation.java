package com.example.anishdalal.finalapp;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.anishdalal.finalapp.MyBroadcastedTasks.TAG2;

/**
* Created by some guy on StackOverflow on 4/26/17.
*/


public class GetCurrentLocation implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "location-updates-sample";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 0;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final String LOCATION_KEY = "location-key";
    private final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context mContext;
    private getLocation mGetCurrentLocation;

    public GetCurrentLocation(Context context) {
        Log.d(TAG2, "Instantiating GetCurrentLocation object");
        mContext = context;
        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        Log.d(TAG2, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    public interface getLocation{
        public void onLocationChanged(Location location);
    }

    public void startGettingLocation(getLocation location) {
        mGetCurrentLocation = location;
        connect();
    }

    public void stopGettingLocation() {
        stopLocationUpdates();
        disconnect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException s){

            }
        }
    }
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }



    private void connect() {
        mGoogleApiClient.connect();
    }

    private void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG2, "Connected to GoogleApiClient");
        startLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        mGetCurrentLocation.onLocationChanged(location);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}

