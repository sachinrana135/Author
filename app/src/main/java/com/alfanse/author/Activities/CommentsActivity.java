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

package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alfanse.author.Fragments.CommentsFragment;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsActivity extends BaseActivity {

    @BindView(R.id.toolbar_comments)
    Toolbar mToolbar;

    private Context mContext;
    private Activity mActivity;
    private CommentsFragment mCommentsFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String mQuoteId;
    private CommentFilters mCommentFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = CommentsActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(Constants.BUNDLE_KEY_COMMENTS_FILTERS)) {
                mCommentFilters = (CommentFilters) intent.getSerializableExtra(Constants.BUNDLE_KEY_COMMENTS_FILTERS);
            }

        }
        initToolbar();
        loadCommentFragment();

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_comments)));
    }

    private void loadCommentFragment() {

        mCommentsFragment = new CommentsFragment();
        mCommentsFragment.setCommentFilters(mCommentFilters);
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_comments_comments, mCommentsFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }
}
