package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.fragment_container_home) FrameLayout fragmentContainer;
    @BindView(R.id.bottom_nav_home)
    BottomNavigationView bottomNav;
    private Context mContext;
    private Activity mActivity;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            android.support.v4.app.Fragment selectedFragment = null;

            switch (item.getItemId()) {

                case R.id.bottom_nav_item_home_home:
                    startActivity(getIntent());
                    break;
                case R.id.bottom_nav_item_quotes_home:

                    break;
                case R.id.bottom_nav_item_new_quote_home:

                    Intent newQuoteIntent = new Intent(mActivity, NewQuoteActivity.class);
                    startActivity(newQuoteIntent);

                    break;
                case R.id.bottom_nav_item_account_home:
                    Intent loginIntent = new Intent(mActivity, SignInActivity.class);
                    startActivity(loginIntent);
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

    }

    private void initToolbar() {

    }

    private void initListener() {
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
