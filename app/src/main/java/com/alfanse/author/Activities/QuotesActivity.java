/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuotesActivity extends BaseActivity {

    @BindView(R.id.toolbar_quotes)
    Toolbar mToolbar;

    private Context mContext;
    private Activity mActivity;
    private QuotesFragment mQuotesFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String mCategoryID = null;
    private String mSearchKeyword = null;
    private String mLanguageId = null;
    private String mTitle = null;
    private QuoteFilters mQuoteFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = QuotesActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            if (intent.hasExtra(Constants.BUNDLE_KEY_TITLE)) {
                mTitle = intent.getStringExtra(Constants.BUNDLE_KEY_TITLE);
            }

            if (intent.hasExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS)) {
                mQuoteFilters = (QuoteFilters) intent.getSerializableExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS);
            }
        }
        initToolbar();
        loadQuotesFragment();
    }

    private void loadQuotesFragment() {
        mQuotesFragment = new QuotesFragment();

        if (mQuoteFilters != null) {
            mQuotesFragment.setQuoteFilters(mQuoteFilters);
        }

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_quotes_quotes, mQuotesFragment)
                .commit();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (mTitle != null) {
            getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(mTitle));
        } else {
            getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_quotes)));
        }
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
