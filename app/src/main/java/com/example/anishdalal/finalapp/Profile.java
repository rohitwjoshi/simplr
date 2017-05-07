package com.example.anishdalal.finalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anishdalal on 4/14/17.
 */

public class Profile extends CustomFragment {

    public static final int GALLERY_INTENT_CALLED = 1;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 2;
    public static final int KITKAT_VALUE = 1002;
    Intent intent;
    int takeFlags;
    SharedPreferences pref;
    public static final String pref_filename = "ProfilePreferences";
    ImageView profile_pic;
    Bitmap bitmap;
    String sTargetUri;
    Uri targetUri;

    public UpdateClickListener listener;

    View view;

    String name;
    String user_name;
    String email;
    String descrip;
    String paymentEmail;

    public interface UpdateClickListener {
        public void onUpdateClick(String name, String user, String email);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile, container, false);

        profile_pic = (ImageView) view.findViewById(R.id.profPic);
        final EditText et_name = (EditText) view.findViewById(R.id.et_name);
        final TextView et_user = (TextView) view.findViewById(R.id.et_username);
        final TextView et_email = (TextView) view.findViewById(R.id.et_email);
        final EditText et_description  = (EditText) view.findViewById(R.id.et_description);
        final EditText et_paymentEmail = (EditText) view.findViewById(R.id.et_payment_email);

        et_name.setTypeface(MainActivity.tfR);
        et_user.setTypeface(MainActivity.tfR);
        et_email.setTypeface(MainActivity.tfR);
        et_description.setTypeface(MainActivity.tfR);
        et_paymentEmail.setTypeface(MainActivity.tfR);

        Button btUpdate = (Button) view.findViewById(R.id.bt_update);

        //profile_pic.setImageBitmap();

        pref = getActivity().getSharedPreferences(pref_filename, 0);
        if(!(pref == null))
        {
            sTargetUri = pref.getString("target_uri", "default");
            name = pref.getString("name", "John Doe");
            user_name = pref.getString("username", "John Doe");
            email = pref.getString("email", "John Doe@gmail.com");
            descrip = pref.getString("description", "default");
            paymentEmail = pref.getString("paymentEmail", "ex@ex.com");

        }
        if(sTargetUri.equals("default"))
        {
            profile_pic.setImageResource(R.drawable.ic_menu_camera);
        }
        else
        {
            targetUri = Uri.parse(sTargetUri);

            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                profile_pic.setImageBitmap(bitmap);
            } catch (FileNotFoundException | SecurityException e){
                profile_pic.setImageResource(R.drawable.ic_android_black_24dp);
            }
        }

        et_name.setText(name);
        et_user.setText(user_name);
        et_email.setText(email);
        et_description.setText(descrip);
        et_paymentEmail.setText(paymentEmail);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*

                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, 1);
                */

                if (Build.VERSION.SDK_INT < 19){
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_INTENT_CALLED);
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                }
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = et_name.getText().toString();
                user_name = et_user.getText().toString();
                email = et_email.getText().toString();
                descrip = et_description.getText().toString();

                showDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                Uri originalUri = null;
                if (Build.VERSION.SDK_INT < 19) {
                    originalUri = data.getData();
                } else {
                    originalUri = data.getData();
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try {
                        getActivity().getContentResolver().takePersistableUriPermission(originalUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    catch (SecurityException e){
                        e.printStackTrace();
                    }
                }
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(originalUri));
                    sTargetUri = originalUri.toString();
                    profile_pic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (UpdateClickListener) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");
    }


    public void onPositiveClick() {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("name", name);
        edit.putString("email", email);
        edit.putString("username", user_name);
        edit.putString("target_uri", sTargetUri);
        edit.putString("description", descrip);
        edit.putString("paymentEmail", paymentEmail);
        User u = new User(user_name, email, name, descrip, paymentEmail);
        Map<String, Object> updates = new HashMap<>();
        updates.put("/username/" + user_name, u.toMap());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.updateChildren(updates);
        edit.apply();
    }

    public void onNegativeClick() {

    }

    public void showDialog()
    {
        DialogFragment fragment = new AreyouSureDialog(this);
        fragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
}
