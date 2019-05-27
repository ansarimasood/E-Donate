package com.masood.edonate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NgoSingleBookDonationDetails extends AppCompatActivity {
    private String post_key,post = null;
    private DatabaseReference mDatabase,mDatabseAccepted,mDatabaseReflected;
    private TextView singlePostQuantity;
    private TextView singlePostAddress;
    private TextView singlePostTime;
    private TextView singlePostConatact;
    private TextView singlePostName;
    private TextView singlePostEmail;
    private Button deleteButton;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";

    String userid;
    List<Users> usersList;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_single_book_donation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // mCurrentUser =mAuth.getCurrentUser().getUid();
        post_key = getIntent().getExtras().getString("PostId");
        //  mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        usersList = new ArrayList<>();
        singlePostQuantity = findViewById(R.id.singleQuantity);
        singlePostAddress = findViewById(R.id.singleAddress);
        singlePostTime = findViewById(R.id.singleTime);
        singlePostName=findViewById(R.id.singleName);
        singlePostConatact=findViewById(R.id.singleContact);
        singlePostEmail=findViewById(R.id.singleEmail);
        mAuth = FirebaseAuth.getInstance();
        deleteButton = findViewById(R.id.singleDeleteButton);
        mDatabaseReflected = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Ngo").child("Books");
        mDatabseAccepted=FirebaseDatabase.getInstance().getReference().child("Ngo").child("Books").child(post_key);
        if(mAuth.getCurrentUser() != null) {

            mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String post_quantity = (String) dataSnapshot.child("quantity").getValue();
                    String post_address = (String) dataSnapshot.child("address").getValue();
                    String post_time = (String) dataSnapshot.child("time").getValue();
                    String post_name = (String) dataSnapshot.child("name").getValue();
                    String post_contact = (String) dataSnapshot.child("contact").getValue();
                    String post_email = (String) dataSnapshot.child("email").getValue();
                    singlePostQuantity.setText("Quantity : " + post_quantity);
                    singlePostAddress.setText("Address : " + post_address);
                    singlePostTime.setText("Time : " + post_time);
                    singlePostName.setText("Name : " + post_name);
                    singlePostConatact.setText("Contact : " + post_contact);
                    singlePostEmail.setText("Email : " + post_email);


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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
    public void acceptButtonClicked(View view){

        final String currentTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS a").format(new Date());
        final DatabaseReference Post =  FirebaseDatabase.getInstance().getReference().child("DonationAccepted").child("Books").child(mAuth.getCurrentUser().getUid()).push();
mDatabseAccepted.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        Post.child("quantity").setValue(dataSnapshot.child("quantity").getValue());
        Post.child("address").setValue(dataSnapshot.child("address").getValue());
        Post.child("time").setValue(dataSnapshot.child("time").getValue());
        Post.child("acceptedtime").setValue(currentTime);

        userid = (String) dataSnapshot.child("uid").getValue();
        Post.child("uid").setValue(userid);
        Post.child("name").setValue(dataSnapshot.child("name").getValue());
        Post.child("contact").setValue(dataSnapshot.child("contact").getValue());
        Post.child("email").setValue(dataSnapshot.child("email").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Intent intent = new Intent(NgoSingleBookDonationDetails.this,NgoHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(NgoSingleBookDonationDetails.this,"Donation Accepted",Toast.LENGTH_LONG).show();
                    String time = (String) dataSnapshot.child("time").getValue();
                    DonationAccepted(time,userid);

                   mDatabase.child(post_key).removeValue();
                }
            }
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
       }

    public void DonationAccepted(final String time,final String userid){
        final DatabaseReference post = FirebaseDatabase.getInstance().getReference().child("Donors").child("Books").child(userid);
           post.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                i = 0;
                int n = 0;
                   for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){

                       final String idem = userSnapshot.getKey();
                       Users users = userSnapshot.getValue(Users.class);
                       usersList.add(users);
                       Users usercheck = usersList.get(i);
                        String currenttime = usercheck.getTime();
                       if (currenttime.equals(time)) {
                           post.child(idem).child("accepted").setValue("Your Request is Approved");
                           post.child(idem).child("ngouid").setValue(mAuth.getCurrentUser().getUid());
                       }
                       i++;
                   }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

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
