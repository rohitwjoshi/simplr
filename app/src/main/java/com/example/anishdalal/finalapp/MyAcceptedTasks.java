package com.example.anishdalal.finalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.anishdalal.finalapp.MyBroadcastedTasks.TAG2;

public class MyAcceptedTasks extends CustomFragment {

    private DatabaseReference mDatabase;
    static final String TAG = "MA_LOG";
    private ArrayAdapter<TaskObj> adapter;
    private ListView lv;
    private ArrayList<TaskObj> taskObjs;
    private int position;
    String un;
    GoogleApiClient.Builder mgapiClient;
    TaskItemClickListener listener;
    public interface TaskItemClickListener {
        public void TaskClick();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_accepted_tasks, container, false);
        taskObjs = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Query broadcastedJobsQuery = mDatabase.child("jobs").orderByKey();
        un = getContext().getSharedPreferences(Profile.pref_filename, 0).getString("username", "default");
        final ArrayList<String> jobIDs = new ArrayList<>();
        /*
        mDatabase.child("users").child(un).child("accepted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG2, "MyAcceptedTasks - users - onDataChanged");
                Log.d(TAG2, "Initial ids: \n" + jobIDs.toString());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getting child");
                    String jobID = child.getValue(String.class);
                    if(!(jobIDs.contains(jobID)))
                        jobIDs.add(jobID);
                }
                Log.d(TAG2, "Final ids: \n" + jobIDs.toString());

                //adapter.notifyDataSetChanged();
                //lv.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cancel");
            }
        });*/

        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG2, "MyAcceptedTasks - jobs - onDataChanged");
                Log.d(TAG2, "Initial taskObjs: \n" + taskObjs.toString());
                Log.d(TAG2, un);
                double lat = MainActivity.mLocationListen.getLatitude();
                double longi = MainActivity.mLocationListen.getLongitude();
                //taskObjs.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    TaskObj potential = child.getValue(TaskObj.class);
                    Log.d(TAG2, "TaskObj: " + potential);
                    if (potential.getAccepted_by().equals(un)) {
                        potential.setCurr_lat(lat);
                        potential.setCurr_long(longi);
                        taskObjs.add(potential);
                        jobIDs.add(child.getKey());
                    }
                }
                /*
                for(String key: jobIDs)
                {
                    taskObjs.add(dataSnapshot.child(key).getValue(TaskObj.class));
                }
                */
                Log.d(TAG2, "Final taskObjs: \n" + jobIDs.toString());
                adapter.notifyDataSetChanged();
                lv.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new TaskListAdapter(taskObjs, getContext());
        lv = (ListView) v.findViewById(R.id.lv_my_acc_tasks);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), JobViewActivity.class);
                intent.putExtra("TaskObj", taskObjs.get(position));
                intent.putExtra("key", jobIDs.get(position));
                intent.putExtra("Type", "accepted");
                startActivity(intent);
            }
        });
        //registerForContextMenu(lv);

        return v;

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TaskItemClickListener) context;
    }
}
