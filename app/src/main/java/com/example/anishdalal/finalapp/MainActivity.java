package com.example.anishdalal.finalapp;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MyBroadcastedTasks.TaskItemClickListener,
        MyAcceptedTasks.TaskItemClickListener,
        AvailableTasks.TaskItemClickListener,
        Profile.UpdateClickListener,
        AreyouSureDialog.ConfirmationListener {

    SharedPreferences pref;
    View header;

    public static Typeface tfM; //Typeface.createFromAsset(getAssets())
    public static Typeface tfR;
    protected static LocationListen mLocationListen;
    private DatabaseReference mDatabase;
    public ArrayList<String> t;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tfM = Typeface.createFromAsset(getAssets(), "Raleway/Raleway-Medium.ttf");
        tfR = Typeface.createFromAsset(getAssets(), "Raleway/Raleway-Regular.ttf");
        toolbar.hideOverflowMenu();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        t = new ArrayList<>();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));
        mLocationListen = new LocationListen(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setCustomView(R.layout.actionbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                header = navigationView.getHeaderView(0);

                TextView nav_header_tv = (TextView) header.findViewById(R.id.tv_name);

                nav_header_tv.setTypeface(tfM);

                //ImageView prof_pic = (ImageView) header.findViewById(R.id.profPic);
                CircleImageView prof_pic = (CircleImageView) header.findViewById(R.id.profPic);

                pref = getSharedPreferences(Profile.pref_filename, 0);
                String uri = pref.getString("target_uri", "");
                TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
                String name = pref.getString("name", "John Doe");
                tv_name.setText(name);
                if (!uri.equals("")) {
                    Uri urii = Uri.parse(uri);
                    //if (Build.VERSION.SDK_INT >= 19) {
                     //   getContentResolver().takePersistableUriPermission(urii, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   // }
                    try {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(urii));
                            prof_pic.setImageBitmap(bitmap);
                        }
                        catch (SecurityException e) {

                        }
                    } catch (FileNotFoundException e) {

                    }
                } else {
                    prof_pic.setImageResource(R.drawable.ic_android_black_24dp);
                }
            }
        };

        String username = getSharedPreferences(Profile.pref_filename, 0)
                .getString("username", "default");
        mDatabase.child("users").child(username).child("broadcasted").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                t.add(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                t.remove(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("jobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(t.contains(dataSnapshot.getKey()))
                {
                    TaskObj myTaskObj = dataSnapshot.getValue(TaskObj.class);
                    if(!myTaskObj.getAccepted_by().equals(""))
                        notifyThatJobWasAccepted(myTaskObj.getTitle(), myTaskObj.getAccepted_by());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            transaction.replace(R.id.content_main, new TabFragment());
            transaction.addToBackStack("new");
            transaction.commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void checkIfBroadcastedJobIsAccepted(final ArrayList<String> broadcastedJobIDs)
    {
        mDatabase.child("jobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notifyThatJobWasAccepted(String taskTitle, String acceptedBy)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.simplr_icon)
                        .setContentTitle("Your broadcasted task")
                        .setContentText(String.format("%s has accepted your task", acceptedBy));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, mBuilder.build());

    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment frag = new Fragment();
        if (id == R.id.profile) {
            frag = new Profile();
        } else if (id == R.id.tasks) {
            frag = new TabFragment();
        } else if (id == R.id.signout) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            SharedPreferences.Editor edit = pref.edit();
            edit.clear();
            edit.apply();
            Toast.makeText(getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
            Intent menuIntent = new Intent(this, LoadingScreen.class);
            startActivity(menuIntent);
            return true;
        } else if (id == R.id.add_new_job) {
            frag = new NewJob();
        } else if (id == R.id.new_tasks) {
            frag = new AvailableTasks();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, frag)
                .addToBackStack(null)
                .commit();
        return true;
    }

    @Override
    public void TaskClick(Fragment fragment, String s) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.addToBackStack(s);
        transaction.commit();
    }

    @Override
    public void onUpdateClick(String name, String user_name, String email) {
        pref = getSharedPreferences(Profile.pref_filename, 0);
        SharedPreferences.Editor p = pref.edit();
        p.putString("name", name);
        p.putString("username", user_name);
        p.putString("email", email);
        p.apply();
    }

    @Override
    public void onPositiveClick(CustomFragment fragment) {
        fragment.onPositiveClick();
    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void TaskClick() {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
