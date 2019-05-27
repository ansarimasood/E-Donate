package com.masood.edonate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class
SingleBooksDonationDetails extends AppCompatActivity {
    private String post_key,post = null;
    private DatabaseReference mDatabase;
    private TextView singlePostQuantity;
    private TextView singlePostAddress;
    private TextView singleNgoName;
    private TextView singleNgoContact;
    private TextView singleNgoEmail;
    private TextView singlePostTime;
    private TextView singlePostAcceptedTime;
    private Button deleteButton,detailsButton;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";

    String userid;
    List<AcceptedUsers> usersList;
    int i = 0;
    List<Ngo> ngoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_books_donation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
       // mCurrentUser =mAuth.getCurrentUser().getUid();
        post_key = getIntent().getExtras().getString("PostId");
      //  mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();


        singlePostQuantity = findViewById(R.id.singleQuantity);
        singlePostAddress = findViewById(R.id.singleAddress);
        singlePostTime = findViewById(R.id.singleTime);
        singlePostAcceptedTime = findViewById(R.id.singleAcceptedTime);
        singlePostAcceptedTime.setVisibility(View.INVISIBLE);
        singleNgoName = findViewById(R.id.singleNgoName);
        singleNgoContact=findViewById(R.id.singleNgoContact);
        singleNgoEmail=findViewById(R.id.singleNgoEmail);
        singleNgoName.setVisibility(View.INVISIBLE);
        singleNgoContact.setVisibility(View.INVISIBLE);
        singleNgoEmail.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        deleteButton = findViewById(R.id.singleDeleteButton);
        detailsButton = findViewById(R.id.singleDetailsButton);
        detailsButton.setVisibility(View.INVISIBLE);
        usersList = new ArrayList<>();
        ngoList = new ArrayList<>();
    /*    if(mAuth.getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books").child(mAuth.getCurrentUser().getUid());
            final String statusCheck = "Your Request is Approved";
            mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String post_quantity = (String) dataSnapshot.child("quantity").getValue();
                    String post_address = (String) dataSnapshot.child("address").getValue();
                    String post_time = (String) dataSnapshot.child("time").getValue();
                    String status = (String) dataSnapshot.child("accepted").getValue();

                    singlePostQuantity.setText("Quantity : " + post_quantity);
                    singlePostAddress.setText("Address : " + post_address);
                    singlePostTime.setText("Time : " + post_time);
                    if(mDatabase.child(post_key)!=null) {
                        if (status.equals("Your Request is Approved")) {
                            deleteButton.setVisibility(View.INVISIBLE);
                            detailsButton.setVisibility(View.VISIBLE);
                            singlePostAcceptedTime.setVisibility(View.VISIBLE);
                            String ngouid = (String) dataSnapshot.child("ngouid").getValue();
                            CheckDonorInfo(post_time, ngouid);
                        } else {
                            deleteButtonClicked();
                        }
                    }else {
                        Intent intent = new Intent(SingleBooksDonationDetails.this,DonationDetails.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Intent intent = new Intent(this,DonorLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books").child(mAuth.getCurrentUser().getUid());
        final String statusCheck = "Your Request is Approved";
        mDatabase.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_quantity = (String) dataSnapshot.child("quantity").getValue();
                String post_address = (String) dataSnapshot.child("address").getValue();
                String post_time = (String) dataSnapshot.child("time").getValue();
                String status = (String) dataSnapshot.child("accepted").getValue();

                singlePostQuantity.setText("Quantity : " + post_quantity);
                singlePostAddress.setText("Address : " + post_address);
                singlePostTime.setText("Time : " + post_time);
                if(mDatabase.child(post_key)==null) {
                    Intent intent = new Intent(SingleBooksDonationDetails.this,DonationDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    if (status.equals("Your Request is Approved")) {
                        deleteButton.setVisibility(View.INVISIBLE);
                        detailsButton.setVisibility(View.VISIBLE);
                        singlePostAcceptedTime.setVisibility(View.VISIBLE);
                        String ngouid = (String) dataSnapshot.child("ngouid").getValue();
                        CheckDonorInfo(post_time, ngouid);
                    } else {
                        deleteButtonClicked(post_time);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
    public void deleteButtonClicked(final String post_time){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                final DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Ngo").child("Books");
                posts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ngoList.clear();
                        i = 0;
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){

                            final String idem = userSnapshot.getKey();
                            Ngo users = userSnapshot.getValue(Ngo.class);
                            ngoList.add(users);
                            Ngo usercheck = ngoList.get(i);
                            String currenttime = usercheck.getTime();
                            if (currenttime.equals(post_time)) {
                                userSnapshot.getRef().removeValue();
                            }
                            i++;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(SingleBooksDonationDetails.this,"Your Request is Deleted",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SingleBooksDonationDetails.this,DonationDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
    public void CheckDonorInfo(final String post_time,final String ngouid){
detailsButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        singleNgoName.setVisibility(View.VISIBLE);
        singleNgoContact.setVisibility(View.VISIBLE);
        singleNgoEmail.setVisibility(View.VISIBLE);
        final DatabaseReference post = FirebaseDatabase.getInstance().getReference().child("DonationAccepted").child("Books").child(ngouid);
        final DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("NgoUser").child(ngouid);
        post.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                i = 0;
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){

                    final String idem = userSnapshot.getKey();
                    AcceptedUsers users = userSnapshot.getValue(AcceptedUsers.class);
                    usersList.add(users);
                    AcceptedUsers usercheck = usersList.get(i);
                    String currenttime = usercheck.getTime();
                    String acceptedtime = usercheck.getAcceptedtime();
                    if (currenttime.equals(post_time)) {
                        singlePostAcceptedTime.setText("Accepted Time : " + acceptedtime);
                    }
                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Name = (String) dataSnapshot.child("Name").getValue();
                String Contact = (String) dataSnapshot.child("Contact").getValue();
                String Email = (String) dataSnapshot.child("Email").getValue();
                singleNgoName.setText("Ngo Name : " + Name);
                singleNgoContact.setText("Ngo Contact : " + Contact);
                singleNgoEmail.setText("Ngo Email : " + Email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
});
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
