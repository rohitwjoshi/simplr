package com.example.anishdalal.finalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditTask extends CustomFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private DatabaseReference mDatabase;
    private static final String TAG = "MA_LOG";
    private String key;
    private SupportPlaceAutocompleteFragment acomplete;

    // TODO: Rename and change types of parameters
    private TaskObj curr;

    public EditTask() {
        // Required empty public constructor
    }

    public static EditTask newInstance(TaskObj t) {
        EditTask fragment = new EditTask();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, t);
        fragment.setArguments(args);
        return fragment;
    }

    public static EditTask newInstance(TaskObj t, String key) {
        EditTask fragment = new EditTask();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, t);
        args.putString("key", key);
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
            key = getArguments().getString("key");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "inflating view");
        View v = inflater.inflate(R.layout.fragment_edit_task, container, false);
        Log.d(TAG, "getting views");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final EditText title = (EditText) v.findViewById(R.id.edit_taskname);
        final EditText price = (EditText) v.findViewById(R.id.edit_price);
        final EditText details = (EditText) v.findViewById(R.id.edit_taskdetails);
        acomplete = new SupportPlaceAutocompleteFragment();
        acomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng location = place.getLatLng();
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
        ft.replace(R.id.edit_fragment_container, acomplete);
        ft.commit();
        title.setText(curr.getTitle());
        details.setText(curr.getDescription());
        price.setText(String.format("%.2f",curr.getPrice()));
        Log.d(TAG, "setting views");
        Button save = (Button) v.findViewById(R.id.edit_updateJob);
        Button cancel = (Button) v.findViewById(R.id.edit_cancel);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = title.getText().toString();
                double nPrice = Double.parseDouble(price.getText().toString());
                String nDescription = details.getText().toString();
                Map<String, Object> hashMap = curr.taskmap();
                hashMap.put("title", nTitle);
                hashMap.put("price", nPrice);
                hashMap.put("description", nDescription);
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("/jobs/"+key, hashMap);
                mDatabase.updateChildren(m);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(),
                        "Lesson updated!! (or it will be once we implement the back end)",
                        Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_jobview, JobDetailsFragment.newInstance(curr))
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }


}
