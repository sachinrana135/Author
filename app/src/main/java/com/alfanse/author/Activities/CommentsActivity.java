package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.alfanse.author.Fragments.CommentsFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;

public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_comments)
    Toolbar mToolbar;

    private Context mContext;
    private Activity mActivity;
    private CommentsFragment mCommentsFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String mQuoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = CommentsActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_QUOTE_ID)) {
                mQuoteId = intent.getStringExtra(BUNDLE_KEY_QUOTE);
            } else {
                Toast.makeText(mActivity, getString(R.string.error_quote_not_found), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        initToolbar();
        loadCommentFragment();

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_comments);
    }

    private void loadCommentFragment() {

        mCommentsFragment = new CommentsFragment();
        mCommentsFragment.setQuoteId(mQuoteId);
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
