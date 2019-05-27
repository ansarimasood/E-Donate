package com.masood.edonate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class NgoAcceptedBookDonationDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
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
        setContentView(R.layout.activity_ngo_accepted_book_donation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Donation Accepted Details");
        setSupportActionBar(toolbar);
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mInstaList = (RecyclerView)findViewById(R.id.donation_list);
        mInstaList.setHasFixedSize(true);
        mInstaList.setLayoutManager(new LinearLayoutManager(NgoAcceptedBookDonationDetails.this));
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("DonationAccepted").child("Books").child(mDatabaseUser);
        fetch();
    }


    private void fetch(){
        Query query = mDatabase;
        FirebaseRecyclerOptions<NgoAccepted> options = new FirebaseRecyclerOptions.Builder<NgoAccepted>().setQuery(query, new SnapshotParser<NgoAccepted>() {
            @NonNull
            @Override
            public NgoAccepted parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new NgoAccepted(snapshot.child("quantity").getValue().toString(),
                        snapshot.child("address").getValue().toString(),
                        snapshot.child("time").getValue().toString(),
                        snapshot.child("contact").getValue().toString(),
                        snapshot.child("email").getValue().toString(),
                        snapshot.child("name").getValue().toString(),
                        snapshot.child("acceptedtime").getValue().toString());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<NgoAccepted,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull NgoAccepted model) {
                final String post_key = getRef(position).getKey();

                holder.setQuantity(model.getQuantity());
                holder.setAddress(model.getAddress());
                holder.setTime(model.getTime());
                holder.setContact(model.getName());
                holder.setName(model.getEmail());
                holder.setEmail(model.getContact());
                holder.setAcceptedtime(model.getAcceptedtime());
              /*   holder.setImage(getApplicationContext(),model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),NgoSingleAccBookDonationDetails.class);
                        intent.putExtra("PostId",post_key);
                        startActivity(intent);
                    }

                });*/
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())

                        .inflate(R.layout.ngo_accepted_card, viewGroup, false);
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
            post_time.setText("Request Time : " + time);
        }
        public void setName(String name){
            TextView post_name = mView.findViewById(R.id.textName);
            post_name.setText("Name : " + name);
        }
        public void setContact(String contact){
            TextView post_conatct = mView.findViewById(R.id.textContact);
            post_conatct.setText("Contact : " + contact);
        }
        public void setEmail(String email){
            TextView post_email = mView.findViewById(R.id.textEmail);
            post_email.setText("Email : " + email);
        }
        public void setAcceptedtime(String acceptedtime){
            TextView post_acceptedTime = mView.findViewById(R.id.textAcceptedTime);
            post_acceptedTime.setText("Accept Time : " + acceptedtime);
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
    FirebaseRecyclerAdapter<NgoAccepted,NgoAcceptedBookDonationDetails.InstaViewHolder> FBRA = new FirebaseRecyclerAdapter<NgoAccepted, NgoAcceptedBookDonationDetails.InstaViewHolder>(NgoAccepted.class,R.layout.ngo_accepted_card,NgoAcceptedBookDonationDetails.InstaViewHolder.class,mDatabase) {
        @Override
        protected void populateViewHolder(NgoAcceptedBookDonationDetails.InstaViewHolder viewHolder, NgoAccepted model, int position) {

            final String post_key = getRef(position).getKey().toString();

            viewHolder.setQuantity(model.getQuantity());
            viewHolder.setAddress(model.getAddress());
            //  viewHolder.setImage(getApplicationContext(),model.getImage());
            viewHolder.setTime(model.getTime());
            viewHolder.setName(model.getName());
            viewHolder.setContact(model.getContact());
            viewHolder.setEmail(model.getEmail());
            viewHolder.setAcceptedtime(model.getAcceptedtime());

             */
/*   viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NgoAcceptedBookDonationDetails.this,NgoSingleAcceptedBookDonationDetails.class);
                        intent.putExtra("PostId",post_key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }); *//*

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
