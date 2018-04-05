package com.example.user.waffle;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private DatabaseReference mUsersDatabase;
    private SectionsPagerAdapter mSectionPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Waffle Chat");



        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    protected void onStart() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser == null)
        {
            send_to_start();
        }

        {
           mUsersDatabase.child("online").setValue(true);
            Log.e("onstart","---------------------------------Main");
        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null)
        mUsersDatabase.child("online").setValue(false);
        Log.e("onPause","---------------------------------Main");

    }

    private void send_to_start() {
        Intent intent = new Intent(MainActivity.this,StartActivity.class);

        startActivity(intent);

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn)
        {
            FirebaseAuth.getInstance().signOut();
            send_to_start();
        }
        else if(item.getItemId() == R.id.main_account_settings_btn)
        {
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if(item.getItemId() == R.id.main_all_users_btn)
        {
            Intent allusersIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(allusersIntent);
        }

        return true;

    }


}
