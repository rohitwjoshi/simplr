package com.example.anishdalal.finalapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalResultDelegate;

import java.util.HashMap;
import java.util.Map;

import static com.example.anishdalal.finalapp.MyBroadcastedTasks.TAG2;

public class JobViewActivity extends AppCompatActivity
{

    private DatabaseReference mDatabase;
    TaskObj curr;
    String key;

    String aby;
    LocationRequest mLocationRequest;
    private GetCurrentLocation mCurrentLocation;
    String type;
  //  Tracking listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        curr = (TaskObj) i.getSerializableExtra("TaskObj");
        aby = i.getStringExtra("Accepted By");
        type = i.getStringExtra("Type");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_jobview, JobDetailsFragment.newInstance(curr));
        transaction.commit();


    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (type.equals("available"))
        {
            getMenuInflater().inflate(R.menu.menu_available_tasks, menu);
            key = getIntent().getStringExtra("key");
            return true;
        } else if(type.equals("broadcasted") && !aby.equals(""))
        {
            getMenuInflater().inflate(R.menu.menu_broadcast, menu);
            key = getIntent().getStringExtra("key");
            return true;
        }else if(type.equals("broadcasted"))
        {
            getMenuInflater().inflate(R.menu.jd_menu, menu);
            key = getIntent().getStringExtra("key");
            return true;
        } else if (type.equals("accepted"))
        {
            getMenuInflater().inflate(R.menu.menu_accepted, menu);
            key = getIntent().getStringExtra("key");
            return true;
        }
        getMenuInflater().inflate(R.menu.jd_menu, menu);
        return true;
    }

    public void buildClient()
    {
        /*
        if(gaClient == null)
        {
            gaClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }
        */
    }


    public void startUploadingCoordinates()
    {
        mCurrentLocation = new GetCurrentLocation(this);
        final String user = getSharedPreferences(Profile.pref_filename, 0).getString("username", "def");
        mCurrentLocation.startGettingLocation(new GetCurrentLocation.getLocation() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                HashMap tomap = new HashMap();
                tomap.put("latitude", lat);
                tomap.put("longitude", longi);
                LatLon latlon = new LatLon(lat, longi);
                mDatabase.child("location").child(user).setValue(latlon);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CustomFragment frag;
        //noinspection SimplifiableIfStatement
        if (id == R.id.jd_action_accept) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Complete Task?")
                    .setMessage("Are you sure you want to mark this task as complete?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final String user_name = getSharedPreferences(Profile.pref_filename, 0).getString("username", "default");
                    ChildEventListener removeListen = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getKey().equals(key)) {
                                TaskObj toRemove = dataSnapshot.getValue(TaskObj.class);
                                String paymentEmail = toRemove.getPaymentEmail();
                                double price = toRemove.getPrice();
                                Log.d(TAG2, toRemove.toString());
                                Log.d(TAG2, paymentEmail);
                                Log.d(TAG2, "" + price);
                                pay(paymentEmail, price);
                                mDatabase.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mDatabase.child("jobs").addChildEventListener(removeListen);
                    mDatabase.child("jobs").child(key).removeValue();
                    mDatabase.child("users").child(user_name).child("broadcasted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child: dataSnapshot.getChildren())
                            {
                                if(child.getValue(String.class).equals(key))
                                {
                                    mDatabase.child("users").child(user_name).child("broadcasted")
                                            .child(child.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Toast.makeText(getApplicationContext(), "Task Completed!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            });
            build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            build.show();
            return true;
        }
        else if (id == R.id.jd_action_edit) {
            frag = EditTask.newInstance(this.curr, this.key);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_jobview, frag)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        else if (id == R.id.jd_action_delete) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Delete Task?")
                    .setMessage("Are you sure you want to delete this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final String un = getSharedPreferences(Profile.pref_filename, 0).getString("username", "default");
                    mDatabase.child("users").child(un).child("broadcasted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot jobToDelete: dataSnapshot.getChildren())
                            {
                                if(key.equals(jobToDelete.getValue(String.class)))
                                {
                                    mDatabase.child("users").child(un).child("broadcasted").child(jobToDelete.getKey())
                                            .removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mDatabase.child("jobs").child(key).removeValue();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("no", true);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Deleted Task!", Toast.LENGTH_LONG).show();
                }
            });
            build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            build.show();
            return true;
        }
        else if (id == R.id.action_accept) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Accept Task?")
                    .setMessage("Are you sure you want to accept this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String un = getSharedPreferences(Profile.pref_filename, 0).getString("username", "default");
                    String pe = getSharedPreferences(Profile.pref_filename, 0).getString("paymentEmail", "ex@ex.com");
                    String nkey = mDatabase.child("users").child(un).child("accepted").push().getKey();
                    mDatabase.child("users").child(un).child("accepted").child(nkey).setValue(key);
                    mDatabase.child("jobs").child(key).child("accepted_by").setValue(un);
                    mDatabase.child("jobs").child(key).child("paymentEmail").setValue(pe);
                    LocationListen listen = new LocationListen(getApplicationContext());
                    /*
                    mCurrentLocation = new GetCurrentLocation(getApplicationContext());
                    mCurrentLocation.startGettingLocation(new GetCurrentLocation.getLocation() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG2, "changing location");
                            Log.d(TAG2, location.toString());
                        }

                    });
                    startUploadingCoordinates();
                    */
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("mytasks", true);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Accepted Task!", Toast.LENGTH_LONG).show();
                }
            });

            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            build.show();
            return true;
        }
        else if (id == R.id.jd_action_decline) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Decline Task?")
                    .setMessage("Are you sure you want to decline this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("mytasks", true);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Declined TaskObj!", Toast.LENGTH_LONG).show();
                }
            });
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            build.show();
            return true;
        }
        else if (id == R.id.lv_action_decline) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Decline Task?")
                    .setMessage("Are you sure you want to relinquish this task?");
            // delete element if "yes" button is clicked
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final String un = getSharedPreferences(Profile.pref_filename, 0).getString("username", "");
                    mDatabase.child("jobs").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            TaskObj jobToRelinquish = dataSnapshot.getValue(TaskObj.class);
                            jobToRelinquish.setAccepted_by("");
                            jobToRelinquish.setPaymentEmail("");
                            Map updates = new HashMap();
                            updates.put("/jobs/"+key, jobToRelinquish.taskmap());
                            mDatabase.updateChildren(updates);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mDatabase.child("users").child(un).child("accepted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot acceptJobID: dataSnapshot.getChildren())
                            {
                                if(acceptJobID.getValue(String.class).equals(key))

                                    mDatabase.child("users").child(un).child("accepted").child(
                                            acceptJobID.getKey()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("mytasks", true);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Released task!", Toast.LENGTH_LONG).show();
                }
            });
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            build.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pay(String paymentEmail, double price) {
        PayPal ppObj = PayPal.initWithAppID(this.getBaseContext(),
                "APP-80W284485P519543T", PayPal.ENV_SANDBOX);
        PayPalPayment newPayment = new PayPalPayment();
        newPayment.setSubtotal(new java.math.BigDecimal(price));
        newPayment.setRecipient(paymentEmail);
        newPayment.setCurrencyType("USD");
        newPayment.setMerchantName(getSharedPreferences(Profile.pref_filename, 0)
                .getString("username", "default"));
        Intent paypalIntent = PayPal.getInstance().checkout(newPayment, getApplicationContext());
        startActivityForResult(paypalIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PayPalResultDelegate p = new PayPalResultDelegate() {
            @Override
            public void onPaymentSucceeded(String s, String s1) {
                Toast.makeText(getApplicationContext(),
                        "Payment successful",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPaymentFailed(String s, String s1, String s2, String s3, String s4) {
                Toast.makeText(getApplicationContext(),
                        "Payment failed",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPaymentCanceled(String s) {
                Toast.makeText(getApplicationContext(),
                        "Payment canceled",
                        Toast.LENGTH_LONG).show();
            }
        };
    }
}
