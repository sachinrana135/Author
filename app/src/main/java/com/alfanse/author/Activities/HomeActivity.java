package com.alfanse.author.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.alfanse.author.Fragments.AccountFragment;
import com.alfanse.author.Fragments.FollowQuotesFragment;
import com.alfanse.author.Fragments.HomeFragment;
import com.alfanse.author.Fragments.WriteQuoteFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        HomeFragment.OnFragmentInteractionListener,
        WriteQuoteFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener,
        FollowQuotesFragment.OnFragmentInteractionListener {

    private Activity thisActivity;
    private HomeFragment mHomeFragment = null;
    private FollowQuotesFragment mFollowQuotesFragment = null;
    private WriteQuoteFragment mWriteQuoteFragment = null;
    private AccountFragment mAccountFragment = null;
    private FragmentManager fragmentManager;

    @BindView(R.id.fragment_container_home) FrameLayout fragmentContainer;
    @BindView(R.id.bottom_nav_home) BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        thisActivity = HomeActivity.this;
        fragmentManager = getSupportFragmentManager();


        initToolbar();
        loadHomeFragment();
        initListener();

    }

    private void initToolbar() {

    }

    private void loadHomeFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        fragmentTransaction.replace(R.id.fragment_container_home, mHomeFragment);
        fragmentTransaction.commit();
    }

    private void initListener() {
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            android.support.v4.app.Fragment selectedFragment = null;

            switch (item.getItemId()) {

                case R.id.bottom_nav_item_home_home:
                    if(mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                    }
                    selectedFragment = mHomeFragment;
                    break;
                case R.id.bottom_nav_item_quotes_home:
                    if(mFollowQuotesFragment == null) {
                        mFollowQuotesFragment = new FollowQuotesFragment();
                    }
                    selectedFragment = mFollowQuotesFragment;
                    break;
                case R.id.bottom_nav_item_new_quote_home:
                    if(mWriteQuoteFragment == null) {
                        mWriteQuoteFragment = new WriteQuoteFragment();
                    }
                    selectedFragment = mWriteQuoteFragment;
                    break;
                case R.id.bottom_nav_item_account_home:
                    if(mAccountFragment == null) {
                        mAccountFragment = new AccountFragment();
                    }
                    selectedFragment = mAccountFragment;
                    break;
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container_home, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
