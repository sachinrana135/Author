package com.alfanse.author.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.alfanse.author.Adapters.ExploreQuotesPagerAdapter;
import com.alfanse.author.Fragments.QuoteFiltersFragment;
import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.Interfaces.quoteFiltersUpdateListener;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExploreQuotesActivity extends BaseActivity {


    @BindView(R.id.toolbar_explore_quotes)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout_explore_quotes)
    TabLayout mTabLayout;
    @BindView(R.id.pager_explore_quotes)
    ViewPager mPager;
    @BindView(R.id.fab_quote_filter_explore_quotes)
    FloatingActionButton fabFilter;
    @BindView(R.id.fragment_container_quote_filters_explore_quotes)
    FrameLayout filterLayout;
    QuotesFragment latestQuotesFragment;
    QuotesFragment trendingQuotesFragment;
    QuotesFragment popularQuotesFragment;
    QuoteFiltersFragment quoteFiltersFragment;
    private Context mContext;
    private Activity mActivity;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private String FILTER_FRAGMENT_TAG = "FILTER_FRAGMENT";

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
    private ExploreQuotesPagerAdapter mPagerAdapter;
    private quoteFiltersUpdateListener mQuoteFiltersUpdateListener = new quoteFiltersUpdateListener() {
        @Override
        public void onQuoteFiltersUpdate(QuoteFilters quoteFilters) {
            latestQuotesFragment.getQuoteFilters().setCategories(quoteFilters.getCategories());
            latestQuotesFragment.getQuoteFilters().setLanguages(quoteFilters.getLanguages());

            trendingQuotesFragment.getQuoteFilters().setCategories(quoteFilters.getCategories());
            trendingQuotesFragment.getQuoteFilters().setLanguages(quoteFilters.getLanguages());

            popularQuotesFragment.getQuoteFilters().setCategories(quoteFilters.getCategories());
            popularQuotesFragment.getQuoteFilters().setLanguages(quoteFilters.getLanguages());

            mPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void closeQuoteFiltersFragment() {
            if (quoteFiltersFragment.isVisible()) {
                closeFilterFragment();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_quotes);

        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = ExploreQuotesActivity.this;
        quoteFiltersFragment = new QuoteFiltersFragment();
        quoteFiltersFragment.setListener(mQuoteFiltersUpdateListener);
        mFragmentManager = getSupportFragmentManager();

        initToolbar();
        initTabs();
        initPager();
        initListener();
    }


    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_explore);
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
        mPagerAdapter = new ExploreQuotesPagerAdapter(mContext, getSupportFragmentManager());

        trendingQuotesFragment = new QuotesFragment();
        trendingQuotesFragment.getQuoteFilters().setFilterType(Constants.QUOTE_FILTER_TYPE_TRENDING);
        mPagerAdapter.addFragment(trendingQuotesFragment, getString(R.string.title_trending));

        latestQuotesFragment = new QuotesFragment();
        latestQuotesFragment.getQuoteFilters().setFilterType(Constants.QUOTE_FILTER_TYPE_LATEST);
        mPagerAdapter.addFragment(latestQuotesFragment, getString(R.string.title_latest));

        popularQuotesFragment = new QuotesFragment();
        popularQuotesFragment.getQuoteFilters().setFilterType(Constants.QUOTE_FILTER_TYPE_POPULAR);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore_quotes, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search_explore_quotes).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                updateViewPagerContent(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void updateViewPagerContent(String query) {

        latestQuotesFragment.getQuoteFilters().setSearchKeyword(query);
        trendingQuotesFragment.getQuoteFilters().setSearchKeyword(query);
        popularQuotesFragment.getQuoteFilters().setSearchKeyword(query);
        mPagerAdapter.notifyDataSetChanged();

    }

    private void initListener() {

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quoteFiltersFragment.isVisible()) {
                    closeFilterFragment();
                } else {
                    addFilterFragment();
                }
            }
        });

    }

    public void closeFilterFragment() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(quoteFiltersFragment)
                .commit();
        mFragmentManager.popBackStack(FILTER_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void addFilterFragment() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container_quote_filters_explore_quotes, quoteFiltersFragment)
                .addToBackStack(FILTER_FRAGMENT_TAG)
                .commit();
    }

}
