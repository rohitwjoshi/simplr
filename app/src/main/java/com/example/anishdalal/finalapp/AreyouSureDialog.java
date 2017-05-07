package com.example.anishdalal.finalapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by anishdalal on 4/14/17.
 */

public class AreyouSureDialog extends DialogFragment {

    CustomFragment fragment;

    public ConfirmationListener listener;
    public interface ConfirmationListener {
        void onPositiveClick(CustomFragment fragment);
        void onNegativeClick(DialogFragment dialogFragment);
    }

    public AreyouSureDialog(CustomFragment fragment)
    {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.confirmation_dialog, null))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onPositiveClick(fragment);
                        Toast.makeText(getActivity(),"Successfully completed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onNegativeClick(AreyouSureDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ConfirmationListener) context;
    }
}
