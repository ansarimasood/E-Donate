package com.masood.edonate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class UsersActivity extends AppCompatActivity {
private ImageButton ngo;
private ImageButton donor;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("  E-Donate");
        setSupportActionBar(toolbar);
        ngo=findViewById(R.id.imageButtonNgo);
        donor=findViewById(R.id.imageButtonDonor);
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        // ngo.setVisibility(View.INVISIBLE);
     //   donor.setVisibility(View.INVISIBLE);

       /* final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
        builder.setTitle("E-Donate").setMessage("Choose a User");
//        builder.create();
        builder.setCancelable(false);

        builder.setPositiveButton("Donor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                donor.setVisibility(View.VISIBLE);
              //  builder.setCancelable(true);
              *//*  Intent intent = new Intent(UsersActivity.this,UsersActivity.class);
                intent.putExtra("Donor","donorkey");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();*//*
            }
        });
        builder.setNegativeButton("Ngo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ngo.setVisibility(View.VISIBLE);
                //builder.setCancelable(true);
                *//*Intent intent = new Intent(UsersActivity.this,UsersActivity.class);
                intent.putExtra("Ngo","ngokey");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*//*
            }
        });
        builder.show();
*/
        if (!getUser().equals(getResources().getString(R.string.donor))){
            donorButtonClicked(getUser());

        }
        if (!getUser().equals(getResources().getString(R.string.ngo))){
            ngoButtonClicked(getUser());

        }


    }

    public void ngoButtonClicked(String user){

        if (user.equals(getResources().getString(R.string.ngo))){
            Intent intent = new Intent(this,NgoHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            ngo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UsersActivity.this,NgoHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    storeUser(getResources().getString(R.string.ngo));
                }
            });
        }
    }
    public void donorButtonClicked(String user){

        if (user.equals(getResources().getString(R.string.donor))) {
            //startActivity(new Intent(this,Home.class));
            Intent intent = new Intent(this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            donor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UsersActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    storeUser(getResources().getString(R.string.donor));
                }
            });

        }
        }
    private void storeUser(String user){
        SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user",user);
        editor.apply();
    }
    private String getUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        String selectedUser = sharedPreferences.getString("user",getResources().getString(R.string.login_preferences));
        return selectedUser;
    }

    @Override
    public void onBackPressed() {
      /* Intent intent = new Intent(Intent.ACTION_MAIN);
       intent.addCategory(Intent.CATEGORY_HOME);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
      */
      finish();
      super.onBackPressed();


    }


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
                Intent intent = new Intent(getApplicationContext(),ConnectivityLost.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mURL = "https://perfectible-grain.000webhostapp.com/1.png";
        new DownloadImage().execute(mURL);
    }
}
