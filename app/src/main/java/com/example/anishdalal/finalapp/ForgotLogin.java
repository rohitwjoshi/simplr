package com.example.anishdalal.finalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ForgotLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);
        getSupportActionBar().setTitle("Forgot Password");

        Button button1 = (Button) findViewById(R.id.submit);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ForgotLogin.this, "Password emailed!", Toast.LENGTH_SHORT).show();
                Intent Intent = new Intent(view.getContext(), LoginActivity.class);
                view.getContext().startActivity(Intent);}
        });
    }
}
