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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.kila.apprater_dialog.lars.AppRater;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends BaseActivity implements
        QuotesFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar_home)
    Toolbar mToolbar;
    @BindView(R.id.bottom_nav_home)
    BottomNavigationView bottomNav;
    @BindView(R.id.layout_explore_quotes_home)
    LinearLayout layout_explore_quotes;
    @BindView(R.id.fragment_container_quotes_home)
    FrameLayout layout_quotes_fragment_container;
    @BindView(R.id.button_explore_home)
    Button buttonExplore;
    private Context mContext;
    private Activity mActivity;
    private QuotesFragment mQuotesFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private Author mAuthor;
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
        mAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        initToolbar();
        initListener();
        showAppRateDialog();
        Utils.FirebaseSubscribeTopic(Constants.FIREBASE_SUBSCRIPTION_TOPIC_ANDROID_USERS);
        mapFcmIdToUser(SharedManagement.getInstance(mContext).getString(SharedManagement.FIREBASE_TOKEN));
        loadQuotesFragment();
        //Utils.getInstance(mContext).printFacebookHashKey();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadQuotesFragment() {

        mQuotesFragment = new QuotesFragment();

        QuoteFilters quoteFilters = new QuoteFilters();
        quoteFilters.setFilterType(Constants.QUOTE_FILTER_TYPE_FEED);
        mQuotesFragment.setQuoteFilters(quoteFilters);

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_quotes_home, mQuotesFragment)
                .commit();
    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_home)));
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

    public void showAppRateDialog() {

        new AppRater.StarBuilder(this, getPackageName())
                .showDefault()
                .minimumNumberOfStars(Constants.MINIMUM_STAR_RATING)
                .email(Constants.SUPPORT_EMAIL)
                .appLaunched();
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

    @Override
    public void quotesAvailable(Boolean quotesAvailable) {

        if (quotesAvailable) {
            layout_quotes_fragment_container.setVisibility(View.VISIBLE);
            layout_explore_quotes.setVisibility(View.GONE);
        } else {
            layout_quotes_fragment_container.setVisibility(View.GONE);
            layout_explore_quotes.setVisibility(View.VISIBLE);
        }
    }

    private void mapFcmIdToUser(String fcmId) {
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, SharedManagement.getInstance(mContext).getLoggedUser().getId());
        param.put(Constants.API_PARAM_KEY_FCM_ID, fcmId);
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_MAP_FCM_ID)
                .setParams(param)
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {

                    }

                    @Override
                    public void onFailureCallBack(Exception e) {

                    }
                });
        api.call();
        //endregion API_CALL_END

    }


}
