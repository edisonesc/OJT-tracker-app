package com.example.edison.internshiptrackerapp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.edison.internshiptrackerapp.HomeActivity;
import com.example.edison.internshiptrackerapp.InfoActivity;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    public PagerAdapter (FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.numOfTabs = NumberOfTabs;

    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                HomeActivity homeActivity = new HomeActivity();
                return homeActivity;

            case 1:
                InfoActivity infoActivity = new InfoActivity();
                return infoActivity;



            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
