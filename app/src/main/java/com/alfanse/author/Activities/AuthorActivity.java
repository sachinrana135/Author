package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.Fragments.QuotesFragment;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.AuthorFilters;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;

public class AuthorActivity extends BaseActivity {

    @BindView(R.id.toolbar_author)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout_author)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.image_view_cover_image_author)
    ImageView imageCover;
    @BindView(R.id.image_view_profile_image_author)
    ImageView imageProfile;
    @BindView(R.id.text_author_name_author)
    TextView textAuthorName;
    @BindView(R.id.text_author_status_author)
    TextView textAuthorStatus;
    @BindView(R.id.layout_total_quote_author)
    LinearLayout layoutTotalQuotes;
    @BindView(R.id.text_total_quotes_author)
    TextView textTotalQuotes;
    @BindView(R.id.layout_total_followers_author)
    LinearLayout layoutTotalFollowers;
    @BindView(R.id.text_total_followers_author)
    TextView textTotalFollowers;
    @BindView(R.id.layout_total_following_author)
    LinearLayout layoutTotalFollowing;
    @BindView(R.id.text_total_following_author)
    TextView textTotalFollowing;
    @BindView(R.id.text_follow_author)
    TextView textFollow;
    @BindView(R.id.progress_bar_cover_image_author)
    ProgressBar progressBarCoverImage;
    @BindView(R.id.progress_bar_profile_image_author)
    ProgressBar progressBarProfileImage;

    private QuotesFragment mQuotesFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private FirebaseAuth mAuth;
    private Activity mActivity;
    private Context mContext;
    private Author mAuthor;
    private String mAuthorId;
    private Author mLoggedAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = AuthorActivity.this;
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_AUTHOR_ID)) {
                mAuthorId = intent.getStringExtra(BUNDLE_KEY_AUTHOR_ID);
            } else {
                CommonView.showToast(mActivity, getString(R.string.error_author_not_found), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                finish();
            }
        }

        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        getAuthor();
        initListener();
        loadQuotesFragment();
    }

    private void getAuthor() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, getString(R.string.text_loading));
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mAuthorId);
        param.put(Constants.API_PARAM_KEY_LOGGED_AUTHOR_ID, mLoggedAuthor.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_AUTHOR)
                .setParams(param)
                .setMessage("AuthorActivity.java|getAuthor")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetAuthorResponse(stringResponse);
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseGetAuthorResponse(String stringResponse) {
        mAuthor = new Gson().fromJson(stringResponse, Author.class);
        initToolbar();
        renderView();
    }


    private void loadQuotesFragment() {
        mQuotesFragment = new QuotesFragment();

        QuoteFilters quoteFilters = new QuoteFilters();
        quoteFilters.setAuthorID(mAuthorId);

        if (quoteFilters != null) {
            mQuotesFragment.setQuoteFilters(quoteFilters);
        }
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_quotes_author, mQuotesFragment)
                .commit();
    }

    private void initToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        collapsingToolbarLayout.setTitle(mAuthor.getName());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initListener() {


        layoutTotalQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_TITLE, mAuthor.getName() + " " + getString(R.string.title_quotes));
                QuoteFilters quoteFilters = new QuoteFilters();
                quoteFilters.setAuthorID(mAuthorId);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS, quoteFilters);
                startActivity(quotesIntent);
            }
        });

        layoutTotalFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorsIntent = new Intent(mActivity, AuthorsActivity.class);
                authorsIntent.putExtra(Constants.BUNDLE_KEY_TITLE, mAuthor.getName() + " " + getString(R.string.text_followers));

                AuthorFilters authorFilters = new AuthorFilters();
                authorFilters.setAuthorID(mAuthorId);
                authorFilters.setFilterType(Constants.AUTHOR_FILTER_TYPE_FOLLOWER);

                authorsIntent.putExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS, authorFilters);
                startActivity(authorsIntent);
            }
        });

        layoutTotalFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorsIntent = new Intent(mActivity, AuthorsActivity.class);
                authorsIntent.putExtra(Constants.BUNDLE_KEY_TITLE, mAuthor.getName() + " " + getString(R.string.text_following));

                AuthorFilters authorFilters = new AuthorFilters();
                authorFilters.setAuthorID(mAuthorId);
                authorFilters.setFilterType(Constants.AUTHOR_FILTER_TYPE_FOLLOWING);

                authorsIntent.putExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS, authorFilters);
                startActivity(authorsIntent);
            }
        });

        textFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //region API_CALL_START
                CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_please_wait), null);
                HashMap<String, String> param = new HashMap<>();
                param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mAuthorId);
                ApiUtils api = new ApiUtils(mContext)
                        .setActivity(mActivity)
                        .setUrl(Constants.API_URL_FOLLOW_AUTHOR)
                        .setParams(param)
                        .setMessage("AuthorActivity.java|initListener")
                        .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                            @Override
                            public void onSuccessCallBack(String stringResponse) {
                                try {
                                    if (mAuthor.isFollowingAuthor()) {
                                        textFollow.setText(mContext.getString(R.string.action_follow));
                                        mAuthor.setFollowingAuthor(false);
                                        textTotalFollowers.setText(Integer.toString(Integer.parseInt(mAuthor.getTotalFollowers()) - 1));
                                        mAuthor.setTotalFollowers(Integer.toString(Integer.parseInt(mAuthor.getTotalFollowers()) - 1));
                                    } else {
                                        textFollow.setText(mContext.getString(R.string.action_unfollow));
                                        mAuthor.setFollowingAuthor(true);
                                        textTotalFollowers.setText(Integer.toString(Integer.parseInt(mAuthor.getTotalFollowers()) + 1));
                                        mAuthor.setTotalFollowers(Integer.toString(Integer.parseInt(mAuthor.getTotalFollowers()) + 1));
                                    }
                                } catch (Exception e) {
                                    Utils.getInstance(mContext).logException(e);
                                }
                                CommonView.getInstance(mContext).dismissProgressDialog();
                            }

                            @Override
                            public void onFailureCallBack(Exception e) {
                                CommonView.getInstance(mContext).dismissProgressDialog();
                            }
                        });

                api.call();
                //endregion API_CALL_END
            }
        });
    }

    private void renderView() {

        textAuthorName.setText(mAuthor.getName());
        textAuthorStatus.setText(mAuthor.getStatus());
        textTotalFollowers.setText(mAuthor.getTotalFollowers());
        textTotalFollowing.setText(mAuthor.getTotalFollowing());
        textTotalQuotes.setText(mAuthor.getTotalQuotes());

        if (mAuthor.isFollowingAuthor()) {
            textFollow.setText(getString(R.string.action_unfollow));
        } else {
            textFollow.setText(getString(R.string.action_follow));
        }

        RequestOptions coverImageOptions = new RequestOptions()
                .fitCenter()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .centerCrop();

        Glide.with(mActivity)
                .load(mAuthor.getCoverImage())
                .apply(coverImageOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarCoverImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarCoverImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageCover);

        RequestOptions profileImageOptions = new RequestOptions()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .fitCenter()
                .circleCrop();

        Glide.with(mActivity)
                .load(mAuthor.getProfileImage())
                .apply(profileImageOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarProfileImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarProfileImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageProfile);
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
