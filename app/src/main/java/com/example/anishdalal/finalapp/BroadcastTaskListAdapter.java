package com.example.anishdalal.finalapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by haroon on 4/14/17.
 */

public class BroadcastTaskListAdapter extends ArrayAdapter<TaskObj> {

    private ArrayList<TaskObj> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView titleView;
        TextView descriptionView;
        TextView priceView;
        TextView locationView;
        ImageView indicatorView;
        TextView acceptedByView;
    }

    public BroadcastTaskListAdapter(ArrayList<TaskObj> data, Context context) {
        super(context, R.layout.broadcast_tasks_list_row, data);
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
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.broadcast_taskList_title);
            viewHolder.descriptionView = (TextView) convertView.findViewById(R.id.broadcast_taskList_description);
            viewHolder.priceView = (TextView) convertView.findViewById(R.id.broadcast_taskList_price);
            viewHolder.locationView = (TextView) convertView.findViewById(R.id.broadcast_taskList_location);
            viewHolder.indicatorView = (ImageView) convertView.findViewById(R.id.broadcast_tasklist_indicator);
            viewHolder.acceptedByView = (TextView) convertView.findViewById(R.id.broadcast_tasklist_accebted_by);

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
        if (s.getAccepted_by().equals("")) {
            viewHolder.indicatorView.setBackgroundResource(R.drawable.red_indicator);
            viewHolder.acceptedByView.setText("Pending");
            viewHolder.acceptedByView.setTextColor(Color.parseColor(Integer.toHexString(R.color.red)));
        }
        else {
            viewHolder.indicatorView.setBackgroundResource(R.drawable.green_indicator);
            viewHolder.acceptedByView.setText(s.getAccepted_by());
            viewHolder.acceptedByView.setTextColor(Color.parseColor(Integer.toHexString(R.color.green)));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}


