package com.example.anishdalal.finalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.anishdalal.finalapp.MyBroadcastedTasks.TAG2;
import static com.example.anishdalal.finalapp.Profile.pref_filename;

public class CreateAccount extends AppCompatActivity {

    DatabaseReference mDatabase;
    EditText email, password, name, description, paymentEmail;
    Button create;
    SharedPreferences pref;
    FirebaseAuth mAuth;
    ArrayList<String> users;
    ValueEventListener v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        paymentEmail = (EditText) findViewById(R.id.payment_email);
        mAuth = FirebaseAuth.getInstance();
        //getSupportActionBar().setTitle("Create Account");

        create = (Button) findViewById(R.id.submit);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView email = (AutoCompleteTextView) findViewById(R.id.email);
                EditText password = (EditText) findViewById(R.id.password);
                if (!email.getText().toString().contains("@jhu.edu")) {
                    email.setError("Please enter a valid @jhu.edu email");

                } else if (password.getText().toString().length() < 4) {
                    password.setError("Please enter a valid password over 4 characters");
                } else {
                    tryCreateAccount(view);
                }
            }});
        }

    private void tryCreateAccount(View v) {
        // Check for a valid password, if the user entered one.
        boolean cancel = false;
        View focusView = null;
        Log.d(TAG2, "creating account");
        String nm = name.getText().toString();
        String pEmail = paymentEmail.getText().toString();
        if (nm.equals("")) {
            name.setError("Please enter your name");
            focusView = name;
            cancel = true;
        }
        if (!isPasswordValid(password.getText().toString())) {
            password.setError("Please enter a valid password over 4 characters");
            focusView = password;
            cancel = true;
        }
        // Check for a valid email address.
        if (email.getText().toString().equals("")) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(email.getText().toString())) {
            email.setError("Please enter a valid @jhu.edu email");
            focusView = email;
            cancel = true;
        }
        else if (!pEmail.contains("@") || !pEmail.contains(".")) {
            paymentEmail.setError("Please enter a valid email");
            focusView = paymentEmail;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),
                    password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG2, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String email = user.getEmail();
                                String username = email.substring(0, email.indexOf("@"));
                                pref = getSharedPreferences(pref_filename, 0);
                                SharedPreferences.Editor edit = pref.edit();
                                Map<String, Object> updates = new HashMap<>();
                                User toCreate = new User(username, email,
                                        name.getText().toString(),
                                        description.getText().toString(),
                                        paymentEmail.getText().toString());
                                String id = mDatabase.child("username").push().getKey();
                                updates.put("/username/" + username, toCreate.toMap());
                                mDatabase.updateChildren(updates);
                                edit.putString("username", username);
                                edit.putString("email", email);
                                edit.putString("name", name.getText().toString());
                                edit.putString("description", description.getText().toString());
                                edit.putString("paymentEmail", paymentEmail.getText().toString());
                                edit.apply();
                                launchMainActivity();

                                Toast.makeText(getApplicationContext(), "Created Account!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG2, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void launchMainActivity() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@jhu.edu");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(v);
    }
}
