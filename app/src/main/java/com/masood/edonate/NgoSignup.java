package com.masood.edonate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class NgoSignup extends AppCompatActivity {
    private EditText name;
    private EditText contact;
    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    //FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_signup);
        name= (EditText) findViewById(R.id.userSignUpIdentity);
        contact=(EditText)findViewById(R.id.userContact);
        email=(EditText)findViewById(R.id.userEmail);
        password=(EditText)findViewById(R.id.userPassword);
        confirmpassword = findViewById(R.id.userConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("NgoUser");
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

    }
    public void submitButtonClicked(View view){
        final String Name = name.getText().toString().trim();
        final String Contact = contact.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String ConfirmPassword = confirmpassword.getText().toString().trim();

        if(!TextUtils.isEmpty(Name)&&!TextUtils.isEmpty(Contact) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password) && !TextUtils.isEmpty(ConfirmPassword)){
            final ProgressDialog progressDialog = new ProgressDialog(NgoSignup.this,R.style.MyAlertDialogStyle);
            progressDialog.setTitle("Signup");
            progressDialog.setMessage("Processing...Plz wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (Password.equals(ConfirmPassword)) {
                progressDialog.cancel();
                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mDatabase.child(user_id);
                            current_user_db.child("Name").setValue(Name);
                            current_user_db.child("Contact").setValue(Contact);
                            current_user_db.child("Email").setValue(mAuth.getCurrentUser().getEmail());
                            Toast.makeText(NgoSignup.this,"Successful",Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(NgoSignup.this, NgoProfileSetup.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.putExtra("Profile","pic1");
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });
            }
            else {
                Toast.makeText(NgoSignup.this,"Password is mismatched",Toast.LENGTH_LONG).show();

            }
        }
        else {
            Toast.makeText(NgoSignup.this,"Please fill the required details",Toast.LENGTH_LONG).show();
        }
    }
    public void existingUserButtonClicked(View view){
        Intent intent = new Intent(NgoSignup.this,NgoLogin.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
