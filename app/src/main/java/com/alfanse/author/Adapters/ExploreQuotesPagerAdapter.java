/*
 * Copyright (c) 2017. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
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
