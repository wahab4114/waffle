package com.example.user.waffle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout displayName;
    private TextInputLayout email;
    private TextInputLayout password;
    private Toolbar mToolbar;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersDataBase;

    private ProgressDialog mRegProgressDialog;

    // FireBase
    private FirebaseAuth mAuth;

    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgressDialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.reg_page_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // FireBase
        mAuth = FirebaseAuth.getInstance();
        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users");


        displayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        email = (TextInputLayout) findViewById(R.id.reg_email);
        password = (TextInputLayout) findViewById(R.id.reg_password);
        submit = (Button) findViewById(R.id.reg_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayname = displayName.getEditText().getText().toString();
                String dispEmail = email.getEditText().getText().toString();
                String dispPass = password.getEditText().getText().toString();

                if(TextUtils.isEmpty(displayname) || TextUtils.isEmpty(dispEmail) ||TextUtils.isEmpty(dispPass) )
                {

                }
                else {
                    mRegProgressDialog.setTitle("Registering User");
                    mRegProgressDialog.setMessage("Please wait while we create your account");
                    mRegProgressDialog.setCanceledOnTouchOutside(false);
                    mRegProgressDialog.show();
                    register_user(displayname, dispEmail, dispPass);
                }
            }
        });

    }

    private void register_user(final String displayname, String dispEmail, String dispPass) {
        // FireBase



        mAuth.createUserWithEmailAndPassword(dispEmail, dispPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful())
                        {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = currentUser.getUid().toString();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
                                                    // root dir                         first child   2nd child
                            HashMap<String,String> UsersMap = new HashMap<String, String>();
                            UsersMap.put("name",displayname);
                            UsersMap.put("status","Hi there, I am using Waffle Chat App.");
                            UsersMap.put("image","default");
                            UsersMap.put("thumb_image","default");

                            mDatabase.setValue(UsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    // getting token of the device
                                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                                    String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    mUsersDataBase.child(currUID).child("devicetoken").setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mRegProgressDialog.dismiss();
                                            Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();

                                        }
                                    });


                                }
                            });


                        }
                        else
                        {
                            mRegProgressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
