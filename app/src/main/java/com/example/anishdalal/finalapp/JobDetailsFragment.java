package com.example.anishdalal.finalapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

public class JobDetailsFragment extends CustomFragment implements
        OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TaskObj";
    private static final String TAG = "MA_LOG";
    private DatabaseReference mDatabase;
    private Location loc;
    private double latitude;
    private double longitude;
    MapView mapView;
    private GoogleMap googleMap;
    private Marker curr_mark;

    View v;

    // TODO: Rename and change types of parameters
    private TaskObj curr;

    public JobDetailsFragment() {
        // Required empty public constructor
    }

    public static JobDetailsFragment newInstance(TaskObj t) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, t);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        if (getArguments() != null) {
            Log.d(TAG, "getting arg");
            curr = (TaskObj) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Job Description");
        // Inflate the layout for this fragment
        Log.d(TAG, "creating view");
        v = inflater.inflate(R.layout.fragment_job_details, container, false);
        TextView titleView = (TextView) v.findViewById(R.id.jd_title);
        TextView priceView = (TextView) v.findViewById(R.id.jd_price);
        TextView descrView = (TextView) v.findViewById(R.id.jd_description);
        //TextView locationView = (TextView) v.findViewById(R.id.jd_location);
        Log.d(TAG, "found all basic views");
        mapView = (MapView) v.findViewById(R.id.mapView);
        Log.d(TAG, "found mapview");
        titleView.setText(curr.getTitle());
        priceView.setText(String.format("$%.2f", curr.getPrice()));
        descrView.setText(curr.getDescription());
        latitude = curr.getTask_lat();
        longitude = curr.getTask_longi();
        titleView.setTypeface(MainActivity.tfM);
        priceView.setTypeface(MainActivity.tfR);
        descrView.setTypeface(MainActivity.tfR);
        return v;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) v.findViewById(R.id.mapView);
        if (mapView != null) {
            // Initialise the MapView
            mapView.onCreate(null);
            mapView.onResume();
            // Set the map ready callback to receive the GoogleMap object
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        try {
            googleMap.setMyLocationEnabled(true);
            Log.d(TAG, "lat" + latitude);
            Log.d(TAG, "long" + longitude);
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("TaskObj Location");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            // adding marker
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
        catch(SecurityException e) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

}
