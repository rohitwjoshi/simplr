package com.example.anishdalal.finalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by haroon on 4/14/17.
 */

public class TaskListAdapter extends ArrayAdapter<TaskObj> {

private ArrayList<TaskObj> dataSet;
        Context mContext;

// View lookup cache
private static class ViewHolder {
    TextView titleView;
    TextView descriptionView;
    TextView priceView;
    TextView locationView;
}

    public TaskListAdapter(ArrayList<TaskObj> data, Context context) {
        super(context, R.layout.mytask_list_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TaskObj s = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mytask_list_item, parent, false);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.taskList_title);
            viewHolder.descriptionView = (TextView) convertView.findViewById(R.id.taskList_description);
            viewHolder.priceView = (TextView) convertView.findViewById(R.id.taskList_price);
            viewHolder.locationView = (TextView) convertView.findViewById(R.id.taskList_location);

            viewHolder.titleView.setTypeface(MainActivity.tfM);
            viewHolder.descriptionView.setTypeface(MainActivity.tfR);
            viewHolder.priceView.setTypeface(MainActivity.tfM);
            viewHolder.priceView.setTypeface(MainActivity.tfR);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.titleView.setText(s.getTitle());
        viewHolder.descriptionView.setText(s.getDescription());
        viewHolder.priceView.setText(String.format("$%.2f", s.getPrice()));
        viewHolder.locationView.setText(s.getDistance());
        // Return the completed view to render on screen
        return convertView;
    }
}

