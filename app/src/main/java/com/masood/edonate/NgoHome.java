package com.masood.edonate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NgoHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListner;
    private DatabaseReference mDatabase;
    private TextView name,email;
    private CircleImageView profileimage;
    private static final String TAG = "ConnectionClass-Sample";
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
 //   private ConnectionChangedListener mListener;
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("E-Donate");
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("NgoUser");
        mAuth = FirebaseAuth.getInstance();

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
  /*      mRunningBar = findViewById(R.id.runningBar);
        mRunningBar.setVisibility(View.GONE);
  */  //    mListener = new ConnectionChangedListener();

    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        if (mAuth.getCurrentUser()==null) {
            mAuthListner = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Intent loginIntent = new Intent(NgoHome.this, NgoLogin.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                        finish();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListner);
        }
        else {
            getCurrentinfo();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        name =navigationView.getHeaderView(0).findViewById(R.id.userName);
        profileimage =navigationView.getHeaderView(0).findViewById(R.id.profileImage);
        email = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ngo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
  /*      if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.acceptedbooks) {
                Intent intent = new Intent(this, NgoAcceptedBookDonationDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }else if (id == R.id.acceptedclothes) {
                Intent intent = new Intent(this,NgoAcceptedClothesDonationDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        } else if (id == R.id.acceptedewaste) {
                Intent intent = new Intent(this, NgoAcceptedEwasteDonationDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //startActivity(new Intent(NgoHome.this,NgoEwasteDonationDetails.class));
        }else if (id == R.id.acceptedaboutus) {
                startActivity(new Intent(NgoHome.this,AboutUs.class));
        }
        else if (id == R.id.acceptedprofile){
            // startActivity(new Intent(Home.this, DonorProfileSetup.class));
            Intent intent = new Intent(NgoHome.this,NgoProfileSetup.class);
            intent.putExtra("Profile","pic");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.acceptedlogout){
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(NgoHome.this, NgoLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void booksButtonClicked(View view){
            Intent intent = new Intent(NgoHome.this, NgoBookDonationDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }
    public void clothButtonClicked(View view) {
            Intent intent = new Intent(NgoHome.this, NgoClothesDonationDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    public void ewasteButtonClicked(View view) {
            Intent intent = new Intent(NgoHome.this, NgoEwasteDonationDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
       }
private void getCurrentinfo(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*TextView name = findViewById(R.id.userName);
                ImageView profileimage = findViewById(R.id.profileImage);
                TextView email = findViewById(R.id.userEmail);
                */
                String donorname = (String) dataSnapshot.child("Name").getValue();
                name.setText(donorname);
                String donorimage = (String) dataSnapshot.child("image").getValue();
                Picasso.with(NgoHome.this).load(donorimage).into(profileimage);
                email.setText(mAuth.getCurrentUser().getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
public void checkOnlineState(){
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager != null){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            boolean info = networkInfo.isConnected();
        boolean reachable = false;
        if (networkInfo!=null && networkInfo.isConnectedOrConnecting()) {
            try {
                reachable = InetAddress.getByName("google.co.in").isReachable(2000);

            }catch (Exception e){
            }
            if (reachable){
                Toast.makeText(this,"Internet is Connnected",Toast.LENGTH_SHORT).show();
            }else {

                Toast.makeText(this,"Please Check Your Internet Connnectivity",Toast.LENGTH_SHORT).show();
            }
        }

    }

}

    @Override
    protected void onResume() {
        super.onResume();
   /*     mConnectionClassManager.register(mListener);
        //      mTextView.setText(mConnectionClassManager.getCurrentBandwidthQuality().toString());
        mConnectionClass.equals(ConnectionQuality.UNKNOWN);
        //    private View mRunningBar;*/
        String mURL = "https://perfectible-grain.000webhostapp.com/1.png";
        new DownloadImage().execute(mURL);
    }
/*

    @Override
    protected void onPause() {
        super.onPause();
   //     mConnectionClassManager.remove(mListener);
    }
*/

   /* private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //                  mTextView.setText(mConnectionClass.toString());
                }
            });
        }
    }*/
    private  class DownloadImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mDeviceBandwidthSampler.startSampling();
            //        mRunningBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... url) {
            String imageURL = url[0];
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
              //  InputStream input = connection.getInputStream();
                /*try {
                    byte[] buffer = new byte[1024];

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }*/
            } catch (IOException e) {
                //  Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error while downloading image.");
                return "No Internet";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mDeviceBandwidthSampler.stopSampling();
            if ("No Internet".equalsIgnoreCase(result)) {
                Toast.makeText(getApplicationContext(), "No Internet Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
