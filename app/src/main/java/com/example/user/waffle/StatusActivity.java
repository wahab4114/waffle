package com.example.user.waffle;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;

    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        mStatus = (TextInputLayout)findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_button);

        mStatus.getEditText().setText(getIntent().getStringExtra("status_value").toString());

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = mCurrentUser.getUid().toString();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);


        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.show();

                String status = mStatus.getEditText().getText().toString();
                mDatabaseRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgressDialog.dismiss();
                        }
                        else
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(StatusActivity.this, "error! please try again.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
