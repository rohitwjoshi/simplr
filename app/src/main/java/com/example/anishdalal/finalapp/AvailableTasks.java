package com.example.anishdalal.finalapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static com.example.anishdalal.finalapp.MyBroadcastedTasks.TAG2;

public class AvailableTasks extends CustomFragment {

    static final String TAG = "MA_LOG";
    private DatabaseReference mDatabase;
    private ArrayAdapter<TaskObj> adapter;
    private ListView lv;
    private ArrayList<TaskObj> taskObjs;
    private ArrayList<String> keys;
    private int position;
    TaskItemClickListener listener;
    public interface TaskItemClickListener {
        public void TaskClick();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_available_tasks, container, false);
        taskObjs = new ArrayList<>();
        keys = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG2, "AvailableTasks - jobs - onDataChanged");
                Log.d(TAG2, "Initial taskObjs: " + taskObjs.toString());
                Log.d(TAG2, "Initial ids: " + keys.toString());
                taskObjs.clear();
                keys.clear();
                double lat = MainActivity.mLocationListen.getLatitude();
                double longi = MainActivity.mLocationListen.getLongitude();
                for(DataSnapshot child: dataSnapshot.getChildren())  {
                    TaskObj taskObj = child.getValue(TaskObj.class);
                    if (taskObj.getAccepted_by().equals("")) {
                        taskObj.setCurr_lat(lat);
                        taskObj.setCurr_long(longi);
                        taskObjs.add(taskObj);
                        keys.add(child.getKey());
                    }
                }
                Log.d(TAG2, "Final taskObjs: " + taskObjs.toString());
                Log.d(TAG2, "Final ids: " + keys.toString());
                adapter.notifyDataSetChanged();
                lv.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new TaskListAdapter(taskObjs, getContext());
        lv = (ListView) v.findViewById(R.id.lv_available_tasks);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), JobViewActivity.class);
                intent.putExtra("TaskObj", taskObjs.get(position));
                intent.putExtra("Type", "available");
                intent.putExtra("key", keys.get(position));
                startActivity(intent);
            }
        });
        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TaskItemClickListener) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Available Tasks");
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
        if (v.getId()== R.id.lv_available_tasks) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_available_tasks, menu);
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
        if (id == R.id.jd_action_accept) {
            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setTitle("Accept TaskObj?")
                    .setMessage("Are you sure you want to accept this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    taskObjs.remove(position);
                    adapter.notifyDataSetChanged();
                    lv.invalidateViews();
                    Toast.makeText(getContext(),
                            "Accepted TaskObj!",
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
        }
        else if (id == R.id.lv_action_decline) {
            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setTitle("Decline TaskObj?")
                    .setMessage("Are you sure you want to decline this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    taskObjs.remove(position);
                    adapter.notifyDataSetChanged();
                    lv.invalidateViews();
                    Toast.makeText(getContext(),
                            "Declined TaskObj!",
                            Toast.LENGTH_SHORT).show();
                }
            });
            // do nothing if "no" button is clicked
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });
            // show the dialog
            AlertDialog dialog = build.create();
            dialog.show();
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }
}
