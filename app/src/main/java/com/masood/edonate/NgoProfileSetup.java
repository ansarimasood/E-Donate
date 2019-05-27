package com.masood.edonate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class NgoProfileSetup extends AppCompatActivity {
    private EditText editDisplayContact;
    private ImageView displayImage;
 //   private static final String TAG = "SetupActivity";
//    private Uri selectedImageUri = null;
  private static final int SELECT_PICTURE = 2;
    private Intent GalIntent, CropIntent ;
 //   private Uri uri;
    private Uri resultUri;
    private Bitmap bitmap = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseusers;
    private StorageReference mStorageRef;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private static final String TAG = "ConnectionClass-Sample";
    String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_profile_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Setup");
        setSupportActionBar(toolbar);
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        editDisplayContact = findViewById(R.id.displayName);
        displayImage = findViewById(R.id.setupImageButton);

        mDatabaseusers = FirebaseDatabase.getInstance().getReference().child("NgoUser");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
      //  String key = mAuth.getCurrentUser().getUid();
        String profile= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            profile = Objects.requireNonNull(getIntent().getExtras()).getString("Profile");

            if (Objects.requireNonNull(profile).equals("pic")) {
                mDatabaseusers.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         image = (String) dataSnapshot.child("image").getValue();
                        String contact = (String) dataSnapshot.child("Contact").getValue();
                        Picasso.with(NgoProfileSetup.this).load(image).into(displayImage);
                        editDisplayContact.setText(contact);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
/*

    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {

            ImageCropFunction();

        }
        else if (requestCode == 2) {

            if (data != null) {

                uri = data.getData();

                ImageCropFunction();

            }
        }
        else if (requestCode == 1) {

            if (data != null) {

                Bundle bundle = data.getExtras();
              */
/*  String path = getPathFromURI(selectedImageUri);
                Log.i(TAG, "Image Path : " + path);
*//*

                //uri = data.getData();
                bitmap = bundle.getParcelable("data");
                displayImage.setImageBitmap(bitmap);

            }
        }
    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);


            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = Objects.requireNonNull(result).getUri();
                    displayImage.setImageURI(resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = Objects.requireNonNull(result).getError();
                    Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public void profileImageButtonClicked(View view) {
     //   GetImageFromGallery();
        CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(this);
    }

    public void doneButtonClicked(View view) {
        final String contact = editDisplayContact.getText().toString().trim();
//        final String user_id = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(contact) && resultUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(NgoProfileSetup.this, R.style.MyAlertDialogStyle);
            progressDialog.setTitle("Profile Updating");
            progressDialog.setMessage("Uploading...Plz wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                storageReference.delete();
            final StorageReference filepath = mStorageRef.child("ngo_profile_image").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(Objects.requireNonNull(resultUri.getLastPathSegment()));
            filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                         Uri downloadUrl = task.getResult();
  //                      Toast.makeText(getApplicationContext(),"Upload Complete",Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        mDatabaseusers.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Contact").setValue(contact);
                        mDatabaseusers.child(mAuth.getCurrentUser().getUid()).child("image").setValue(Objects.requireNonNull(downloadUrl).toString());
                        Toast.makeText(NgoProfileSetup.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NgoProfileSetup.this,NgoHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            });
}
    }
        else{
            Toast.makeText(this,"Please fill details",Toast.LENGTH_LONG).show();
        }
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
