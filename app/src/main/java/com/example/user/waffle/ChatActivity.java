package com.example.user.waffle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUserRecevier;
    private String mChatUserRecevierName;
    private Toolbar mChatToolBar;
    private DatabaseReference mRootRef;
    private TextView mDispNameView, mLastSeenView;
    private CircleImageView mProfPicView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatUserRecevier  = getIntent().getStringExtra("user_id");
        mChatUserRecevierName = getIntent().getStringExtra("user_name");
        mRootRef = FirebaseDatabase.getInstance().getReference();


        mChatToolBar = (Toolbar) findViewById(R.id.Chat_app_bar);
        setSupportActionBar(mChatToolBar);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(mChatUserRecevierName);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View chat_action_bar = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(chat_action_bar);

       // getSupportActionBar().setTitle(mChatUserRecevierName);

        mDispNameView = (TextView) findViewById(R.id.custom_bar_dispname);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_last_seen);
        mProfPicView = (CircleImageView) findViewById(R.id.custom_bar_image);
        




    }
}
