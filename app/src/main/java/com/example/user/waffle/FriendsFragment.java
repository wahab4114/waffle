package com.example.user.waffle;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String CUID;
    private View mMainView;

    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        CUID = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(CUID);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        // creating an adapter
        FirebaseRecyclerAdapter<Users,FriendsViewHolder> friendsRecycleAdapter = new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(
                Users.class,R.layout.users_single_layout,FriendsViewHolder.class,mFriendsDatabase) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Users users, int i) {


                final String list_UID = getRef(i).getKey();
                mUsersDatabase.child(list_UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {

                            Boolean online = (Boolean) dataSnapshot.child("online").getValue();
                            friendsViewHolder.setUserOnline(online);
                        }

                        friendsViewHolder.setuserName(userName);
                        friendsViewHolder.setStatus(userStatus);


                        if(!userThumb.equals("default"))
                        {
                            Picasso.with(getContext()).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultuser).into(friendsViewHolder.getCircleImageView(), new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getContext()).load(userThumb).placeholder(R.drawable.defaultuser).into(friendsViewHolder.getCircleImageView());

                                }
                            });

                        }

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]  = new CharSequence[]{"Open profile","Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0)
                                        {
                                            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("user_id",list_UID);
                                            startActivity(profileIntent);
                                        }
                                        else if(which == 1)
                                        {
                                            Intent ChatIntent = new Intent(getContext(),ChatActivity.class);
                                            ChatIntent.putExtra("user_id",list_UID);
                                            ChatIntent.putExtra("user_name",userName);
                                            ChatIntent.putExtra("user_thumb",userThumb);
                                            startActivity(ChatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mFriendsList.setAdapter(friendsRecycleAdapter);


    }


    // creating View Holder
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setStatus(String Status)
        {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(Status);
        }
        public void setuserName(String name)
        {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public CircleImageView getCircleImageView( )
        {
            CircleImageView Userimage = (CircleImageView) mView.findViewById(R.id.user_single_thumb);
            return Userimage;

        }
        public void setUserOnline(Boolean onlineString)
        {
            ImageView imageView = (ImageView) mView.findViewById(R.id.user_single_online);
            if(onlineString == true)
            {
                imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
