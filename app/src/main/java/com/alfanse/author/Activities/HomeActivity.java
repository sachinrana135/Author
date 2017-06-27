package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.toolbar_home)
    Toolbar mToolbar;
    @BindView(R.id.bottom_nav_home)
    BottomNavigationView bottomNav;
    @BindView(R.id.button_explore_home)
    Button buttonExplore;
    private Context mContext;
    private Activity mActivity;
    private QuotesFragment mQuotesFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            android.support.v4.app.Fragment selectedFragment = null;

            switch (item.getItemId()) {

                case R.id.bottom_nav_item_explore_home:
                    Intent exploreQuotesIntent = new Intent(mActivity, ExploreQuotesActivity.class);
                    startActivity(exploreQuotesIntent);
                    break;
                case R.id.bottom_nav_item_new_quote_home:

                    Intent newQuoteIntent = new Intent(mActivity, NewQuoteActivity.class);
                    startActivity(newQuoteIntent);

                    break;
                case R.id.bottom_nav_item_account_home:
                    Intent accountIntent = new Intent(mActivity, UserAccountActivity.class);
                    startActivity(accountIntent);
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = HomeActivity.this;

        initToolbar();
        initListener();
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
        getSupportActionBar().setTitle(R.string.title_home);
    }

    private void initListener() {
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        buttonExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exploreQuotesIntent = new Intent(mActivity, ExploreQuotesActivity.class);
                startActivity(exploreQuotesIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav.getSelectedItemId() != R.id.bottom_nav_item_home_home) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_item_home_home);
        }
    }
}
