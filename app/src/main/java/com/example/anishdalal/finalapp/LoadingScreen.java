package com.example.anishdalal.finalapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Typeface tfM = Typeface.createFromAsset(getAssets(), "Raleway/Raleway-Medium.ttf");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#181818")));
        //getSupportActionBar().setTitle("");
        mAuth = FirebaseAuth.getInstance();

        Button button1 = (Button) findViewById(R.id.submit);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                //Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        button1.setTypeface(tfM);

        Button button2 = (Button) findViewById(R.id.newuser);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(view.getContext(), CreateAccount.class);
                view.getContext().startActivity(Intent);}
        });

        button2.setTypeface(tfM);

    }
}
