package com.example.anishdalal.finalapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBroadcastedTasks extends CustomFragment {

    private DatabaseReference mDatabase;

    static final String TAG = "MA_LOG";
    static final String TAG2 = "UIMA_LOG";
    private ArrayAdapter<TaskObj> adapter;
    private ListView lv;
    private ArrayList<TaskObj> taskObjs;
    private int position;
    TaskItemClickListener listener;
    public ArrayList<String> jobIDs;
    public String un;
    public interface TaskItemClickListener {
        public void TaskClick(Fragment fragment, String s);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        taskObjs = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> acceptedBy = new ArrayList<>();
        //Query broadcastedJobsQuery = mDatabase.child("jobs").orderByKey();
        un = getContext().getSharedPreferences(Profile.pref_filename, 0).getString("username", "default");

        final ArrayList<String> jobIDs = new ArrayList<>();
        /*
        mDatabase.child("users").child(un).child("broadcasted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG2, "MyBroadcastedTasks - users - onDataChanged");
                Log.d(TAG2, "Initial ids: \n" + jobIDs.toString());
                jobIDs.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getting child");
                    String jobID = child.getValue(String.class);
                    if(!(jobIDs.contains(jobID)))
                        jobIDs.add(jobID);
                }
                Log.d(TAG2, "Final ids: \n" + jobIDs.toString());
                adapter.notifyDataSetChanged();
                lv.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG2, "MyBroadcastedTasks - jobs - onDataChanged");
                Log.d(TAG2, "Initial taskObjs: \n" + taskObjs.toString());
                taskObjs.clear();
                double lat = MainActivity.mLocationListen.getLatitude();
                double longi = MainActivity.mLocationListen.getLongitude();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    TaskObj potential = child.getValue(TaskObj.class);
                    if (potential.getUsername().equals(un)) {
                        potential.setCurr_lat(lat);
                        potential.setCurr_long(longi);
                        taskObjs.add(potential);
                        acceptedBy.add(potential.getAccepted_by());
                        jobIDs.add(child.getKey());
                    }
                }
                /*
                ArrayList<String> iterate = (ArrayList<String>) jobIDs.clone();
                for(String key: iterate)
                {
                    TaskObj task = dataSnapshot.child(key).getValue(TaskObj.class);
                    if(task == null)
                    {
                        jobIDs.remove(key);
                    } else {
                        taskObjs.add(task);
                        acceptedBy.add(task.getAccepted_by());
                    }
                }*/
                Log.d(TAG2, "Final taskObjs: \n" + taskObjs.toString());
                adapter.notifyDataSetChanged();
                lv.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, taskObjs.toString());
        //adapter = new BroadcastTaskListAdapter(taskObjs, getContext());
        adapter = new TaskListAdapter(taskObjs, getContext());
        lv = (ListView) v.findViewById(R.id.lv_my_tasks);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), JobViewActivity.class);
                intent.putExtra("TaskObj", taskObjs.get(position));
                intent.putExtra("Type", "broadcasted");
                intent.putExtra("Accepted By", acceptedBy.get(position));
                intent.putExtra("key", jobIDs.get(position));
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

    /**
     * Creates a context menu (edit, delete).
     * @param menu the context menu
     * @param v the view
     * @param menuInfo the menu information
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()== R.id.lv_my_tasks) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.task_select_menu, menu);
        }
    }

    /**
     * Handles behavior when a menu item is selected.
     * @param item the menu item
     * @return true if the selection was valid, false if not
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = info.id;
        this.position = info.position;
        switch(item.getItemId()) {
            case R.id.action_edit: // open an edit fragment
                Fragment frag = EditTask.newInstance(taskObjs.get(position));
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, frag)
                        .addToBackStack("content_main")
                        .commit();
                return true;
            case R.id.action_delete: // open a confirmation dialog
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                build.setTitle("Delete lesson?")
                        .setMessage("Are you sure you want to delete this lesson?");
                // delete element if "yes" button is clicked
                build.setPositiveButton("Delete Lesson", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        taskObjs.remove(position);
                        adapter.notifyDataSetChanged();
                        lv.invalidateViews();
                        Toast.makeText(getContext(),
                                "TaskObj deleted!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                // do nothing if "no" button is clicked
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
                // show the dialog
                AlertDialog dialog = build.create();
                dialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
