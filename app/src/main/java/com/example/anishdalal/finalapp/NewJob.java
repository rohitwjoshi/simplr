package com.example.anishdalal.finalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by anishdalal on 4/14/17.
 */

public class NewJob extends CustomFragment {
    private DatabaseReference mdatabase;
    final String TAG = "MA_LOG";
    String name;
    String dets;
    String price;
    String address;
    LatLng location;
    public static String Nj_Pref_FILE = "njPrefFile";
    private SupportPlaceAutocompleteFragment acomplete;

    //public BroadcastButtonListener listener;

    /*
    public interface BroadcastButtonListener{
        public void onbroadcastClick();
        public void onClearClick();
    }
    */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_job, container, false);
        TextView tv_something = (TextView) view.findViewById(R.id.newJobDescription);
        final EditText et_task_name = (EditText) view.findViewById(R.id.et_taskname);
        final EditText et_task_details = (EditText) view.findViewById(R.id.et_taskdetails);
        final EditText et_price = (EditText) view.findViewById(R.id.et_price);
        Button broadcast = (Button) view.findViewById(R.id.bt_broadcastJob);
        Button clear = (Button) view.findViewById(R.id.bt_clear);

        et_task_name.setTypeface(MainActivity.tfR);
        et_task_details.setTypeface(MainActivity.tfR);
        et_price.setTypeface(MainActivity.tfR);

        tv_something.setTypeface(MainActivity.tfM);

        acomplete = new SupportPlaceAutocompleteFragment();
        acomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                location = place.getLatLng();
                Log.d(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
            }
        });
        //acomplete.setHint("TaskObj Address");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, acomplete);
        ft.commit();
        /*final PlaceAutocompleteFragment acomplete =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
*/
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_task_name.getText().toString();
                dets = et_task_details.getText().toString();
                price = et_price.getText().toString();
                address = ((EditText)acomplete.getView().
                        findViewById(R.id.place_autocomplete_search_input)).getText().toString();
                showDialog();
                /*
                if(validate(name, dets, price))
                {
                    //listener.onbroadcastClick();
                    showDialog();

                }
                */
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_price.setText(" ");
                et_task_name.setText(" ");
                et_task_details.setText(" ");
            }
        });

        return view;
    }
/*
    public void onDestroyView() {
        Log.d(TAG, "on destroy");
        super.onDestroyView();
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.place_autocomplete_fragment);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
*/
    public void showDialog()
    {
        DialogFragment fragment = new AreyouSureDialog(this);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onPositiveClick() {
        /*
        SharedPreferences pref = getActivity().getSharedPreferences(Nj_Pref_FILE, 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("task_name", name);
        edit.putString("task_details", dets);
        edit.putString("task_price", price);
        edit.putString("task_addr", address);
        edit.apply();
        */

        double curr_lat = 0;
        double curr_long = 0;
        if (MainActivity.mLocationListen.canGetLocation()) {
            curr_lat = MainActivity.mLocationListen.getLatitude();
            curr_long = MainActivity.mLocationListen.getLongitude();
        }
        Log.d(TAG, "Lat: " + curr_lat);
        Log.d(TAG, "Long: " + curr_long);
        mdatabase = FirebaseDatabase.getInstance().getReference();
        TaskObj t = new TaskObj(name, Double.parseDouble(price), dets, location.latitude, location.longitude, curr_lat, curr_long);
        SharedPreferences pref = getActivity().getSharedPreferences(Profile.pref_filename, 0);
        String name = pref.getString("username", "default");
        t.setUsername(name);
        String jobID = mdatabase.child("jobs").push().getKey();
        Map<String, Object> updates = new HashMap<>();
        t.setAccepted_by("");
        t.setPaymentEmail("");
        updates.put("/jobs/" + jobID, t.taskmap());
        //updates.put("/jobs/" + jobID + "accepted_job", "");
        mdatabase.child("/users/" + name + "/broadcasted/").push().setValue(jobID);
        //updates.put("/users/" + name + "/broadcasted/", jobID);
        mdatabase.updateChildren(updates);
        //mdatabase.child("jobs").child(jobID).setValue(t);
    }

    @Override
    public void onNegativeClick() {
        super.onNegativeClick();
    }

    public boolean validate(String name, String task_details, String price)
    {
        if(name.equals("") || task_details.equals(""))
        {
            return false;
        }
        try {
            float temp = Float.valueOf(price);
        }catch(Exception e)
        {

        }
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Job");

    }
}
