package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_CATEGORY_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_LANGUAGE_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_SEARCH_KEYWORD;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = QuotesActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_CATEGORY_ID)) {
                mCategoryID = intent.getStringExtra(BUNDLE_KEY_CATEGORY_ID);
            }

            if (intent.hasExtra(BUNDLE_KEY_SEARCH_KEYWORD)) {
                mSearchKeyword = intent.getStringExtra(BUNDLE_KEY_SEARCH_KEYWORD);
            }

            if (intent.hasExtra(BUNDLE_KEY_LANGUAGE_ID)) {
                mLanguageId = intent.getStringExtra(BUNDLE_KEY_LANGUAGE_ID);
            }
        }
        initToolbar();
        loadQuotesFragment();
    }

    private void loadQuotesFragment() {
        mQuotesFragment = new QuotesFragment();
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_quotes_quotes, mQuotesFragment)
                .commit();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_quotes);
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
