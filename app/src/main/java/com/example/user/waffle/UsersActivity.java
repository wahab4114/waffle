package com.example.user.waffle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    private DatabaseReference mUsersdatabase;

    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mUsersdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersdatabase.keepSynced(true);

        mToolBar = (Toolbar) findViewById(R.id.users_appbar);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.users_list);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersDatabase.child("online").setValue(true);

        FirebaseRecyclerAdapter<Users,UsersViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersdatabase) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final Users model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());

                if(!model.getImage().equals("default")) {

                    Picasso.with(UsersActivity.this).load(model.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultuser).into(viewHolder.getCircleImageView(), new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(UsersActivity.this).load(model.getImage()).placeholder(R.drawable.defaultuser).into(viewHolder.getCircleImageView());

                        }
                    });


                }

               final String user_id = getRef(position).getKey();

                //Toast.makeText(UsersActivity.this, ""+user_id, Toast.LENGTH_SHORT).show();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);


                    }
                });


            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name)
        {
            TextView Username = (TextView) mView.findViewById(R.id.user_single_name);
            Username.setText(name);
        }
        public void setStatus(String status)
        {
            TextView Userstatus = (TextView) mView.findViewById(R.id.user_single_status);
            Userstatus.setText(status);
        }
        public CircleImageView getCircleImageView( )
        {
            CircleImageView Userimage = (CircleImageView) mView.findViewById(R.id.user_single_thumb);
            return Userimage;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        mUsersDatabase.child("online").setValue(false);
    }
}
