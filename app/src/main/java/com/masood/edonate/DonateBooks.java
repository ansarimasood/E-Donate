package com.masood.edonate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DonateBooks extends AppCompatActivity {
    private EditText addressField;
    private EditText quantityField;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView username;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Donate Books");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        addressField = (EditText) findViewById(R.id.booksPickup);
        quantityField = (EditText)findViewById(R.id.booksQuantity);
        username = (TextView) findViewById(R.id.userName);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("DonorUser").child(mCurrentUser.getUid());
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

    }

    public void donateBooksButtonClicked(View view){
        final String address = addressField.getText().toString().trim();
        final String quantity = quantityField.getText().toString().trim();
        final String userid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            userid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(quantity)) {
                final DatabaseReference newPost = mDatabase.push();
                final DatabaseReference Post = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books").child(userid).push();
                @SuppressLint("SimpleDateFormat") final String currentTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS a").format(new Date());
                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String key = Post.getKey();
                        Post.child("address").setValue(address);
                        Post.child("quantity").setValue(quantity);
                        Post.child("accepted").setValue("Your Request is Pending");
                        //Post.child("username").setValue(dataSnapshot.child("username").getValue());
                        //Post.child("image").setValue(dataSnapshot.child("image").getValue());
                        //Post.child("name").setValue(dataSnapshot.child("Name").getValue());
                        Post.child("time").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DonateBooks.this, "Your Request for Donation Books is Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(DonateBooks.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final DatabaseReference Posts = FirebaseDatabase.getInstance().getReference().child("Ngo").child("Books").push();
                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Posts.child("address").setValue(address);
                        Posts.child("quantity").setValue(quantity);
                        Posts.child("time").setValue(currentTime);
                        Posts.child("uid").setValue(mAuth.getCurrentUser().getUid());
                        Posts.child("name").setValue(dataSnapshot.child("Name").getValue());
                        Posts.child("email").setValue(dataSnapshot.child("Email").getValue());
                        Posts.child("contact").setValue(dataSnapshot.child("Contact").getValue());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(this, "Please Fill the details", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String user_id = mCurrentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DonorUser");
        database.child(user_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("Name").getValue(String.class);
                username.setText(value + ",");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

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
