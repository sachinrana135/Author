package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.alfanse.author.Fragments.AuthorsFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;


public class AuthorsActivity extends BaseActivity {

    @BindView(R.id.toolbar_authors)
    Toolbar mToolbar;

    private Context mContext;
    private Activity mActivity;
    private AuthorsFragment mAuthorsFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String mQuoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = AuthorsActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_QUOTE_ID)) {
                mQuoteId = intent.getStringExtra(BUNDLE_KEY_QUOTE_ID);
            } else {
                Toast.makeText(mActivity, getString(R.string.error_quote_not_found), Toast.LENGTH_LONG).show();
                finish();
            }
        }


        mAuthorsFragment = new AuthorsFragment();
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_authors_authors, mAuthorsFragment)
                .commit();

        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_followers);
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
