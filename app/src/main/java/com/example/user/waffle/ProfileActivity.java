package com.example.user.waffle;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileFrndReqSndBtn;

    private DatabaseReference mUsersDatabaseRef;

    private ProgressDialog mProgressDialog;

    private String mCurrentState;

    private DatabaseReference mFriendReqDataBase;
    private DatabaseReference mFriendDataBase;
    private DatabaseReference mNotificationDataBase;
    private DatabaseReference mRootref;
    private DatabaseReference mUsersDatabase;

    private FirebaseUser mUser;

    private Button mDeclineBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mCurrentState = "notfriends";

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

       final String UID = getIntent().getStringExtra("user_id").toString();



        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayname);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_ttlfirend);
        mProfileFrndReqSndBtn = (Button) findViewById(R.id.profile_sndreqbtn);
        mDeclineBtn = (Button) findViewById(R.id.profile_declinereq);


        mUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        mNotificationDataBase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRootref = FirebaseDatabase.getInstance().getReference();

        mFriendReqDataBase = FirebaseDatabase.getInstance().getReference().child("friendrequest");
        mFriendDataBase = FirebaseDatabase.getInstance().getReference().child("friends");


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User's data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mProfileFrndReqSndBtn.setVisibility(View.INVISIBLE);
        mProfileFrndReqSndBtn.setEnabled(false);




        mUsersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String DspName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(DspName);
                mProfileStatus.setText(status);

                mDeclineBtn.setVisibility(View.INVISIBLE);
                mDeclineBtn.setEnabled(false);



                Picasso.with(ProfileActivity.this).load(image).fit().placeholder(R.drawable.defaultuser).into(mProfileImage);

                //________________Friends list/request feature
                mFriendReqDataBase.child(mUser.getUid().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(UID))// if user has either sent or received request
                        {
                            String req_type = dataSnapshot.child(UID).child("request_type").getValue().toString();
                            if(req_type.equals("received"))
                            {
                                mCurrentState = "requestreceived";
                                mProfileFrndReqSndBtn.setText("Accept friend request");

                                Log.e("request received","----------------------------------->");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);
                                mProfileFrndReqSndBtn.setVisibility(View.VISIBLE);
                                mProfileFrndReqSndBtn.setEnabled(true);
                                mProgressDialog.dismiss();

                            }
                            else if(req_type.equals("sent"))
                            {
                                mCurrentState = "requestsent";
                                mProfileFrndReqSndBtn.setText("cancel friend request");


                                Log.e("request sent","----------------------------------->");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                                mProfileFrndReqSndBtn.setVisibility(View.VISIBLE);
                                mProfileFrndReqSndBtn.setEnabled(true);
                                mProgressDialog.dismiss();


                            }


                        }
                        else
                        {
                            // case when other person canceled request and we want an imidiate change
                            // can be written in else
                           /* mCurrentState = "notfriends";
                            mProfileFrndReqSndBtn.setText("send friend request");
                            mDeclineBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setEnabled(false);*/

                            mFriendDataBase.child(mUser.getUid().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(UID))
                                    {
                                        mCurrentState="friends";
                                        mProfileFrndReqSndBtn.setText("Unfriend");

                                        Log.e("now friend","----------------------------------->");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                        mProfileFrndReqSndBtn.setVisibility(View.VISIBLE);
                                        mProfileFrndReqSndBtn.setEnabled(true);
                                        mProgressDialog.dismiss();

                                    }
                                    else
                                    {
                                        mCurrentState = "notfriends";
                                        mProfileFrndReqSndBtn.setText("send friend request");
                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                        mProfileFrndReqSndBtn.setVisibility(View.VISIBLE);
                                        mProfileFrndReqSndBtn.setEnabled(true);
                                        mProgressDialog.dismiss();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }





            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









        mProfileFrndReqSndBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                mProfileFrndReqSndBtn.setEnabled(false);

                if(mCurrentState.equals("notfriends"))// send req updated
                {

                    DatabaseReference mNotRef = mRootref.child("Notifications").child(UID).push();
                    String mNotid = mNotRef.getKey();
                    // the user to whom we are sending request  

                            HashMap<String,String> notificationsdata = new HashMap<String, String>();
                            notificationsdata.put("from",mUser.getUid());
                            notificationsdata.put("type","request");



                    Map requestMap  =  new HashMap();
                    requestMap.put("friendrequest/"+mUser.getUid().toString() + "/" + UID + "/request_type","sent");
                    requestMap.put("friendrequest/"+UID + "/" + mUser.getUid().toString() + "/request_type","received");
                    requestMap.put("Notifications/" + UID + mNotid,notificationsdata);



                    mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null) {
                                Toast.makeText(ProfileActivity.this, "sent", Toast.LENGTH_SHORT).show();
                                mProfileFrndReqSndBtn.setEnabled(true);
                                mCurrentState = "requestsent";
                                mProfileFrndReqSndBtn.setText("Cancel friend request");


                            }
                        }
                    });



                }

                // cancel request

                if(mCurrentState.equals("requestsent"))
                {
                    mFriendReqDataBase.child(mUser.getUid()).child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                          mFriendReqDataBase.child(UID).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  mProfileFrndReqSndBtn.setEnabled(true);
                                  mCurrentState = "notfriends";
                                  mProfileFrndReqSndBtn.setText("send friend request");
                                  Toast.makeText(ProfileActivity.this, "Canceled", Toast.LENGTH_SHORT).show();

                                  mDeclineBtn.setVisibility(View.INVISIBLE);
                                  mDeclineBtn.setEnabled(false);

                              }
                          });

                        }
                    });
                }

                // req receive state

                if(mCurrentState.equals("requestreceived")) //acceptreq -updated
                {
                    final String mCurrentDate = DateFormat.getDateTimeInstance().format(new Date());


                    HashMap mAcceptReq = new HashMap();
                    mAcceptReq.put("friends/"+mUser.getUid().toString()+"/"+UID+"/date",mCurrentDate);
                    mAcceptReq.put("friends/"+UID+"/"+mUser.getUid().toString()+"/date",mCurrentDate);

                    //remove request
                    mAcceptReq.put("friendrequest/"+mUser.getUid().toString() + "/" + UID,null);
                    mAcceptReq.put("friendrequest/"+UID + "/" + mUser.getUid().toString(),null );

                    mRootref.updateChildren(mAcceptReq, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null) {
                                mProfileFrndReqSndBtn.setEnabled(true);
                                mCurrentState = "friends";
                                mProfileFrndReqSndBtn.setText("Unfriend");
                                Toast.makeText(ProfileActivity.this, "friends", Toast.LENGTH_SHORT).show();

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                                Toast.makeText(ProfileActivity.this, "Friends", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

                if(mCurrentState.equals("friends")) //unfriend
                {


                    HashMap mUnfriendReq = new HashMap();
                    mUnfriendReq.put("friends/"+mUser.getUid().toString()+"/"+UID,null);
                    mUnfriendReq.put("friends/"+UID+"/"+mUser.getUid().toString(),null);

                    mRootref.updateChildren(mUnfriendReq, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null)
                            {
                                mProfileFrndReqSndBtn.setEnabled(true);
                                mCurrentState = "notfriends";
                                mProfileFrndReqSndBtn.setText("send friend request");
                                Toast.makeText(ProfileActivity.this, "not friends anymore", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }

            }
        });






    }

   @Override
    protected void onStart() {
        super.onStart();
        mUsersDatabase.child("online").setValue(true);
       Log.e("onstart","---------------------------------PROFILE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
        mUsersDatabase.child("online").setValue(false);
        Log.e("onPause","---------------------------------PROFILE");
    }
}
