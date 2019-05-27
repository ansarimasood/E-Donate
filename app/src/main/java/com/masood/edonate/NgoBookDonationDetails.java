package com.masood.edonate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import com.facebook.network.connectionclass.ConnectionClassManager;
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
import java.util.Objects;

public class NgoBookDonationDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    DatabaseReference mDatabase;
    String mDatabaseUser;
    private StorageReference mStorageRef;
    private RecyclerView mInstaList;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseRecyclerAdapter adapter;
    private ConnectionClassManager mConnectionClassManager;
       private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_book_donation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Books Donation Details");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

       // mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mInstaList = (RecyclerView)findViewById(R.id.donation_list);
        mInstaList.setHasFixedSize(true);
        mInstaList.setLayoutManager(new LinearLayoutManager(NgoBookDonationDetails.this));
        mAuth = FirebaseAuth.getInstance();
       // mDatabaseUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Ngo").child("Books");
        fetch();
    }


    private void fetch(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Query query = mDatabase;
            FirebaseRecyclerOptions<Ngo> options = new FirebaseRecyclerOptions.Builder<Ngo>().setQuery(query, new SnapshotParser<Ngo>() {
                @NonNull
                @Override
                public Ngo parseSnapshot(@NonNull DataSnapshot snapshot) {
                    //  if (!snapshot.hasChildren())
                    return new Ngo(Objects.requireNonNull(snapshot.child("quantity").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("address").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("time").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("contact").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("email").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("name").getValue()).toString());

                }

            }).build();
            adapter = new FirebaseRecyclerAdapter<Ngo, ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Ngo model) {
                    final String post_key = getRef(position).getKey();
                    holder.setQuantity(model.getQuantity());
                    holder.setAddress(model.getAddress());
                    holder.setTime(model.getTime());
                    holder.setContact(model.getName());
                    holder.setName(model.getEmail());
                    holder.setEmail(model.getContact());
                    // holder.setImage(getApplicationContext(),model.getImage());
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), NgoSingleBookDonationDetails.class);
                            intent.putExtra("PostId", post_key);
                            startActivity(intent);
                        }

                    });
                }

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext())

                            .inflate(R.layout.ngo_card, viewGroup, false);
                    return new ViewHolder(view);
                }
            };
            mInstaList.setAdapter(adapter);

        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView post_quantity,post_address,post_time,post_name,post_conatct,post_email;
         ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
              post_quantity =  mView.findViewById(R.id.ngobookstextQuantity);
              post_address =  mView.findViewById(R.id.ngobookstextAddress);
              post_time = mView.findViewById(R.id.ngobookstextTime);
              post_name = mView.findViewById(R.id.ngobookstextName);
              post_conatct = mView.findViewById(R.id.ngobookstextContact);
              post_email = mView.findViewById(R.id.ngobookstextEmail);

        }

        public void setQuantity(String quantity){
            post_quantity.setText("Quantity : " + quantity);
        }
        public void setAddress(String address){
            post_address.setText("Address : " + address);
        }
        /*   public void setImage(Context context, String image) {
               ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
               Picasso.with(context).load(image).into(post_image);
           }
          */
        public void setTime(String time){
            post_time.setText("Time : " + time);
        }
        public void setName(String name){
            post_name.setText("Name : " + name);
        }
        public void setContact(String contact){
            post_conatct.setText("Contact : " + contact);
        }
        public void setEmail(String email){
            post_email.setText("Email : "+email);
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
    FirebaseRecyclerAdapter<Ngo,NgoBookDonationDetails.InstaViewHolder> FBRA = new FirebaseRecyclerAdapter<Ngo, NgoBookDonationDetails.InstaViewHolder>(Ngo.class,R.layout.ngo_card,NgoBookDonationDetails.InstaViewHolder.class,mDatabase) {
        @Override
        protected void populateViewHolder(NgoBookDonationDetails.InstaViewHolder viewHolder, Ngo model, int position) {

            final String post_key = getRef(position).getKey().toString();
                            viewHolder.setQuantity(model.getQuantity());
                viewHolder.setAddress(model.getAddress());
                //  viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setTime(model.getTime());
                viewHolder.setName(model.getName());
                viewHolder.setContact(model.getContact());
                viewHolder.setEmail(model.getEmail());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NgoBookDonationDetails.this, NgoSingleBookDonationDetails.class);
                        intent.putExtra("PostId", post_key);
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
                Intent intent = new Intent(getApplicationContext(),ConnectivityLost.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

}
