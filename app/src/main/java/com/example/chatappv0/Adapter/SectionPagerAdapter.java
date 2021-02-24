package com.example.chatappv0.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatappv0.Fragments.callFragment;
import com.example.chatappv0.Fragments.chatFragment;
import com.example.chatappv0.Fragments.groupFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new chatFragment();
            case 1:
                return new groupFragment();
            case 2:
                return new callFragment();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chat";
            case 1:
                return "Groups";
            case 2:
                return "List";
            default:
                return null;
        }
    }
}
