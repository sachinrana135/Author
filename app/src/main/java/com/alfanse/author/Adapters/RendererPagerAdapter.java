/*
 * Copyright (c) 2019. Alfanse Developers
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
import android.support.v4.app.FragmentPagerAdapter;

import com.alfanse.author.Fragments.PreviewFragment;


/**
 * Created by mkallingal on 6/12/2016.
 */
public class RendererPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 1;
    final String content;
    private String tabTitles[] = new String[]{"Rendered"};
    private Context context;

    public RendererPagerAdapter(FragmentManager fm, Context context, String content) {
        super(fm);
        this.context = context;
        this.content = content;
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewFragment.newInstance(content);

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
