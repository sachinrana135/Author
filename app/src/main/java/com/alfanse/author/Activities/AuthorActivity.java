package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.alfanse.author.Models.Author;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = AuthorActivity.this;
        mAuth = FirebaseAuth.getInstance();

        mAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        // TODO API to get author details

        initToolbar();
        initListener();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_AUTHOR_ID)) {
                mAuthorId = intent.getStringExtra(BUNDLE_KEY_AUTHOR_ID);
            } else {
                Toast.makeText(mActivity, getString(R.string.error_author_not_found), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        renderView();
        loadQuotesFragment();
    }


    private void loadQuotesFragment() {
        mQuotesFragment = new QuotesFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_quotes_author, mQuotesFragment)
                .commit();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setTitle(mAuthor.getName());
    }

    private void initListener() {


        layoutTotalQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                startActivity(quotesIntent);
            }
        });

        layoutTotalFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followersIntent = new Intent(mActivity, AuthorsActivity.class);
                startActivity(followersIntent);
            }
        });

        layoutTotalFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followersIntent = new Intent(mActivity, AuthorsActivity.class);
                startActivity(followersIntent);
            }
        });

        textFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading), null);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonView.getInstance(mContext).dismissProgressDialog();
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
                    }
                }, 2000);
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
