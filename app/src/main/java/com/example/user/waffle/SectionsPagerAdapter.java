package com.example.user.waffle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by user on 6/25/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                Log.e("asda","requests---------------------->");
                return requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                Log.e("asda","chats---------------------->");
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                Log.e("asda","friends---------------------->");
                return friendsFragment;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 3; // becaus// e we have 3 tabs
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
