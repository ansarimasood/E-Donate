package com.masood.edonate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class DonorClothesDonationDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    DatabaseReference mDatabase;
    String mDatabaseUser;
    private StorageReference mStorageRef;
    private RecyclerView mInstaList;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseRecyclerAdapter adapter;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_clothes_donation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Clothes Donation Details");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mInstaList = (RecyclerView)findViewById(R.id.donation_list);
        mInstaList.setHasFixedSize(true);
        mInstaList.setLayoutManager(new LinearLayoutManager(DonorClothesDonationDetails.this));
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child("Clothes").child(mDatabaseUser);
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        fetch();
    }


    private void fetch(){
        Query query = mDatabase;
        FirebaseRecyclerOptions<Donor> options = new FirebaseRecyclerOptions.Builder<Donor>().setQuery(query, new SnapshotParser<Donor>() {
            @NonNull
            @Override
            public Donor parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Donor(snapshot.child("quantity").getValue().toString(),
                        snapshot.child("address").getValue().toString(),
                        snapshot.child("time").getValue().toString(),
                        snapshot.child("accepted").getValue().toString());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<Donor,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Donor model) {
                final String post_key = getRef(position).getKey();

                holder.setAddress(model.getAddress());
                holder.setQuantity(model.getQuantity());
                holder.setAccepted(model.getAccepted());
                holder.setTime(model.getTime());
                // holder.setImage(getApplicationContext(),model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SingleClothesDonationDetails.class);
                        intent.putExtra("PostId",post_key);
                        startActivity(intent);
                    }

                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())

                        .inflate(R.layout.donor_clothes_card, viewGroup, false);
                return new ViewHolder(view);
            }
        };
        mInstaList.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setQuantity(String quantity){
            TextView post_quantity = (TextView) mView.findViewById(R.id.textQuantity);
            post_quantity.setText("Quantity : " + quantity);
        }
        public void setAddress(String address){
            TextView post_address = (TextView) mView.findViewById(R.id.textAddress);
            post_address.setText("Address : " + address);
        }
        /*   public void setImage(Context context, String image) {
               ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
               Picasso.with(context).load(image).into(post_image);
           }
          */
        public void setTime(String time){
            TextView post_time = mView.findViewById(R.id.textTime);
            post_time.setText("Time : " + time);
        }
        public void setAccepted(String accepted){
            TextView post_accepted = mView.findViewById(R.id.textStatus);
            post_accepted.setText("Status : " + accepted);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                adapter.startListening();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onRefresh() {
        fetch();
        adapter.startListening();
        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }


/*
    public void details(){
        swipeRefreshLayout.setRefreshing(true);
        FirebaseRecyclerAdapter<Donor,DonorClothesDonationDetails.InstaViewHolder> FBRA = new FirebaseRecyclerAdapter<Donor, DonorClothesDonationDetails.InstaViewHolder>(Donor.class,R.layout.donor_clothes_card,DonorClothesDonationDetails.InstaViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(DonorClothesDonationDetails.InstaViewHolder viewHolder, Donor model, int position) {

                final String post_key = getRef(position).getKey().toString();

                viewHolder.setQuantity(model.getQuantity());
                viewHolder.setAddress(model.getAddress());
                //  viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setTime(model.getTime());
                viewHolder.setAccepted(model.getAccepted());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DonorClothesDonationDetails.this,SingleClothesDonationDetails.class);
                        intent.putExtra("PostId",post_key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        mInstaList.setAdapter(FBRA);
    }
*/

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
