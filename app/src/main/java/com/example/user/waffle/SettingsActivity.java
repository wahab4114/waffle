package com.example.user.waffle;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {


    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDispImage;
    private TextView mStatus;
    private TextView mDispName;
    private DatabaseReference mUsersDatabase;

    private Button mChangeStatus;
    private Button mChangeImage;



    private static int GALLARY_PICK=1;

    private ProgressDialog mProgressDialog;

    private StorageReference mImageStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)  {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {

            }

        }



        mProgressDialog  = new ProgressDialog(this);

        mImageStorage = FirebaseStorage.getInstance().getReference();




        mChangeStatus = (Button) findViewById(R.id.main_settings_changestatus);
        mChangeImage = (Button) findViewById(R.id.main_settings_changeImage);


        mDispImage = (CircleImageView) findViewById(R.id.main_setting_image);

        mStatus = (TextView) findViewById(R.id.main_settings_status);

        mDispName = (TextView) findViewById(R.id.main_settings_displayname);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_UID = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_UID);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        mUserDatabase.keepSynced(true);

        mProgressDialog.setTitle("Fetching data");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




                String name = dataSnapshot.child("name").getValue().toString();

                String status = dataSnapshot.child("status").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();

                String thumbnail = dataSnapshot.child("thumb_image").getValue().toString();

                mDispName.setText(name);
                mStatus.setText(status);
                mProgressDialog.dismiss();

                if(!image.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultuser).into(mDispImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            // if offline image is not avaiable

                             Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.defaultuser).into(mDispImage);

                        }
                    });


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChangeStatusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                String mStatusValue = mStatus.getText().toString();
                ChangeStatusIntent.putExtra("status_value",mStatusValue);
                startActivity(ChangeStatusIntent);
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent,GALLARY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLARY_PICK && resultCode == RESULT_OK)
        {

            Uri ImageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(ImageUri).setAspectRatio(1,1)
                    .start(SettingsActivity.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading..");
                mProgressDialog.setMessage("please wait while we updating your profile picture.");
                mProgressDialog.show();


                Uri resultUri = result.getUri();
///////////////////////compression of image
                File thumb_filepath = new File(resultUri.getPath());

               Bitmap thumb_bitmap = new Compressor(this)
                       .setMaxHeight(200).setMaxWidth(200)
                       .setQuality(75)
                       .compressToBitmap(thumb_filepath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                final byte[] thumb_byte = baos.toByteArray();
/////////////////////////////////////////////////////////////////////////////
                StorageReference filepath = mImageStorage.child("profile_images").child(mCurrentUser.getUid().toString()+".jpg");
                final StorageReference thumb_file_path = mImageStorage.child("profile_images").child("thumbs").child(mCurrentUser.getUid().toString()+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()) {

                           final String download_uri = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_file_path.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    String download_url_thumb=task.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful())
                                    {
                                        Map map = new HashMap();
                                        map.put("image",download_uri);
                                        map.put("thumb_image",download_url_thumb);

                                        mUserDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    mProgressDialog.dismiss();

                                                }
                                                else
                                                {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Error!.. please try again", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                    else
                                    {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Error!.. please try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });




                        }
                        else
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error!.. please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {
                    Toast.makeText(this, "Please grant required permissions", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

  @Override
    protected void onStart() {
        super.onStart();
      Log.e("onstart","---------------------------------SETTINGS");
        mUsersDatabase.child("online").setValue(true);
    }

    @Override
    protected void onPause(){
        super.onPause();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        mUsersDatabase.child("online").setValue(false);
        Log.e("onPause","---------------------------------SETTINGS");
    }

}

