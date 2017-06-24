package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.Models.Author;
import com.alfanse.author.R;
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
    @BindView(R.id.image_view_cover_image_author)
    ImageView imageCover;
    @BindView(R.id.image_view_profile_image_author)
    ImageView imageProfile;
    @BindView(R.id.text_author_name_author)
    TextView textAuthorName;
    @BindView(R.id.text_author_status_author)
    TextView textAuthorStatus;
    @BindView(R.id.text_total_quotes_author)
    TextView textTotalQuotes;
    @BindView(R.id.text_total_likes_author)
    TextView textTotalLikes;
    @BindView(R.id.text_total_followers_author)
    TextView textTotalFollowers;
    @BindView(R.id.text_total_following_author)
    TextView textTotalFollowing;
    @BindView(R.id.text_view_edit_profile_author)
    TextView textEditProfile;
    @BindView(R.id.text_view_your_quotes_author)
    TextView textViewQuotes;
    @BindView(R.id.text_view_your_followers_author)
    TextView textViewFollowers;
    @BindView(R.id.text_view_you_follows_author)
    TextView textViewFollowings;
    @BindView(R.id.progress_bar_cover_image_author)
    ProgressBar progressBarCoverImage;
    @BindView(R.id.progress_bar_profile_image_author)
    ProgressBar progressBarProfileImage;

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

        mAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        renderView();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void initListener() {

        textEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editProfileIntent = new Intent(mActivity, EditProfileActivity.class);
                startActivity(editProfileIntent);
            }
        });

        textViewQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                startActivity(quotesIntent);
            }
        });

        textViewFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followersIntent = new Intent(mActivity, AuthorsActivity.class);
                startActivity(followersIntent);
            }
        });

        textViewFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followersIntent = new Intent(mActivity, AuthorsActivity.class);
                startActivity(followersIntent);
            }
        });
    }

    private void renderView() {

        textAuthorName.setText(mAuthor.getName());
        textAuthorStatus.setText(mAuthor.getStatus());
        textTotalFollowers.setText(mAuthor.getTotalFollowers());
        textTotalFollowing.setText(mAuthor.getTotalFollowing());
        textTotalLikes.setText(mAuthor.getTotalLikes());
        textTotalQuotes.setText(mAuthor.getTotalQuotes());

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
