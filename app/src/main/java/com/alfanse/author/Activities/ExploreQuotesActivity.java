package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alfanse.author.Adapters.ExplorePagerAdapter;
import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExploreQuotesActivity extends AppCompatActivity {


    @BindView(R.id.tab_layout_explore_quotes)
    TabLayout mTabLayout;
    @BindView(R.id.pager_explore_quotes)
    ViewPager mPager;

    private Context mContext;
    private Activity mActivity;

    private TabLayout.OnTabSelectedListener mExploreQuotesOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ExplorePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_quotes);

        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = ExploreQuotesActivity.this;
        initTabs();
        initPager();
    }


    private void initTabs() {
        //Adding the tabs using addTab() method
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.title_trending)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.title_latest)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.title_popular)));
        //Adding onTabSelectedListener to swipe views
        mTabLayout.addOnTabSelectedListener(mExploreQuotesOnTabSelectedListener);
        mTabLayout.setupWithViewPager(mPager);
    }

    private void initPager() {
        mPagerAdapter = new ExplorePagerAdapter(mContext, getSupportFragmentManager());

        QuotesFragment trendingQuotesFragment = new QuotesFragment();
        mPagerAdapter.addFragment(trendingQuotesFragment, getString(R.string.title_trending));

        QuotesFragment latestQuotesFragment = new QuotesFragment();
        mPagerAdapter.addFragment(latestQuotesFragment, getString(R.string.title_latest));

        QuotesFragment popularQuotesFragment = new QuotesFragment();
        mPagerAdapter.addFragment(popularQuotesFragment, getString(R.string.title_popular));

        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
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
