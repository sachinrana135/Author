package com.alfanse.author.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Velocity-1601 on 6/26/2017.
 */

public class ExplorePagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
    int tabCount = 3;
    private Context mContext;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public ExplorePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
