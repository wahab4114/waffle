package com.example.user.waffle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout memail;
    private TextInputLayout mpassword;
    private Button msubmit;
    private ProgressDialog mLoginProgDialog;
    private Toolbar logintoolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginProgDialog = new ProgressDialog(this);
        memail = (TextInputLayout) findViewById(R.id.login_email);
        mpassword = (TextInputLayout) findViewById(R.id.login_password);
        msubmit = (Button) findViewById(R.id.login_submit);
        logintoolbar = (Toolbar) findViewById(R.id.login_page_app_bar);

        setSupportActionBar(logintoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();

                if( TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {

                }
                else
                {
                    mLoginProgDialog.setTitle("Logging in");
                    mLoginProgDialog.setMessage("Please wait while we check your credentials.");
                    mLoginProgDialog.show();
                    login_user(email,password);
                }
            }
        });


    }

    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // getting token of the device
                            String devicetoken = FirebaseInstanceId.getInstance().getToken();
                            String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mUsersDataBase.child(currUID).child("devicetoken").setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mLoginProgDialog.dismiss();
                                    Intent mainIntent = new Intent(LoginActivity.this , MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });


                        }
                        else
                        {
                            mLoginProgDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error. Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
