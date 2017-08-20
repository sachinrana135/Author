package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alfanse.author.Fragments.AuthorsFragment;
import com.alfanse.author.Models.AuthorFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AuthorsActivity extends BaseActivity {

    @BindView(R.id.toolbar_authors)
    Toolbar mToolbar;

    private Context mContext;
    private Activity mActivity;
    private AuthorsFragment mAuthorsFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String mAuthorId;
    private String mTitle;
    private AuthorFilters mAuthorFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = AuthorsActivity.this;

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.hasExtra(Constants.BUNDLE_KEY_TITLE)) {
                mTitle = intent.getStringExtra(Constants.BUNDLE_KEY_TITLE);
            }

            if (intent.hasExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS)) {
                mAuthorFilters = (AuthorFilters) intent.getSerializableExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS);
            }
        }
        loadAuthorsFragment();


        initToolbar();
    }

    private void loadAuthorsFragment() {
        mAuthorsFragment = new AuthorsFragment();
        mFragmentManager = getSupportFragmentManager();

        if (mAuthorFilters != null) {
            mAuthorsFragment.setAuthorFilters(mAuthorFilters);
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_authors_authors, mAuthorsFragment)
                .commit();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (mTitle != null) {
            getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(mTitle));
        } else {
            getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_authors)));
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
