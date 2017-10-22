/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.alfanse.author.Fragments.QuotesFragment;

import java.util.ArrayList;

/**
 * Created by Velocity-1601 on 6/26/2017.
 */

public class ExploreQuotesPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
    int tabCount = 3;
    private Context mContext;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public ExploreQuotesPagerAdapter(Context context, FragmentManager fm) {
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

    @Override
    public int getItemPosition(Object object) {

        QuotesFragment quotesFragment = (QuotesFragment) object;
        if (quotesFragment != null) {
            quotesFragment.update();
        }
        return super.getItemPosition(object);
    }


}
