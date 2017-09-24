package com.alfanse.author.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.CustomViews.FlowLayout;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Interfaces.bitmapRequestListener;
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.Category;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.Models.Language;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.Models.ReportReason;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.SUCCESS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;

public class QuoteActivity extends BaseActivity {

    private static final int SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE = 6369;
    private static final int DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE = 6462;
    @BindView(R.id.toolbar_quote)
    Toolbar mToolbar;
    @BindView(R.id.layout_author_details_quote)
    LinearLayout layoutAuthorDetails;
    @BindView(R.id.progress_bar_author_image_quote)
    ProgressBar progressBarLoadingAuthorImage;
    @BindView(R.id.image_author_shared)
    ImageView imageAuthor;
    @BindView(R.id.text_author_name_quote)
    TextView textAuthorName;
    @BindView(R.id.text_author_follow_quote)
    TextView textFollow;
    @BindView(R.id.text_quote_date_quote)
    TextView textDateQuote;
    @BindView(R.id.text_caption_quote_quote)
    TextView textCaptionQuote;
    @BindView(R.id.layout_caption_quote_quote)
    LinearLayout layoutCaptionQuote;
    @BindView(R.id.progress_bar_quote_quote)
    ProgressBar progressBarLoadingQuoteImage;
    @BindView(R.id.image_quote_shared)
    ImageView imageQuote;
    @BindView(R.id.text_total_likes_quote)
    TextView textTotalLikes;
    @BindView(R.id.text_total_comments_quote)
    TextView textTotalComments;
    @BindView(R.id.image_like_quote)
    ImageView imageLikeQuote;
    @BindView(R.id.image_comment_quote)
    ImageView imageCommentQuote;
    @BindView(R.id.image_share_quote)
    ImageView imageShareQuote;
    @BindView(R.id.image_more_options_quote)
    ImageView imageMoreOptions;
    @BindView(R.id.label_quote_source_quote)
    TextView labelQuoteSource;
    @BindView(R.id.text_quote_source_quote)
    TextView textQuoteSource;
    @BindView(R.id.category_tag_container_quote)
    FlowLayout categoryTagsContainer;
    @BindView(R.id.language_container_quote)
    FlowLayout languagesTagsContainer;
    @BindView(R.id.parent_category_tag_container_quote)
    LinearLayout parentCategoryTagsContainer;
    @BindView(R.id.quote_tag_container_quote)
    FlowLayout quoteTagsContainer;
    @BindView(R.id.parent_quote_tag_container_quote)
    LinearLayout parentQuoteTagsContainer;

    private FirebaseAuth mAuth;
    private Activity mActivity;
    private Context mContext;
    private Author mLoggedAuthor;
    private Quote mQuote;
    private String mQuoteId;
    private FirebaseUser firebaseUser;
    private ObjectAnimator mObjectAnimator;
    private Bitmap quoteBitmapToSave;
    private View.OnClickListener categoryTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {

                TextView categoryTag = (TextView) v;
                Category category = (Category) categoryTag.getTag();

                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_TITLE, category.getName() + " " + getString(R.string.title_quotes));

                QuoteFilters quoteFilters = new QuoteFilters();
                ArrayList<Category> filterCategories = new ArrayList<Category>();
                filterCategories.add(category);
                quoteFilters.setCategories(filterCategories);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS, quoteFilters);
                startActivity(quotesIntent);
            }
        }
    };

    private View.OnClickListener languageTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {

                TextView languageTag = (TextView) v;
                Language language = (Language) languageTag.getTag();

                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_TITLE, language.getLanguageName() + " " + getString(R.string.title_quotes));

                QuoteFilters quoteFilters = new QuoteFilters();
                ArrayList<Language> filterLanguages = new ArrayList<Language>();
                filterLanguages.add(language);
                quoteFilters.setLanguages(filterLanguages);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS, quoteFilters);
                startActivity(quotesIntent);

            }
        }
    };

    private View.OnClickListener searchTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {
                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                String searchKeyword = ((TextView) v).getText().toString().trim();
                quotesIntent.putExtra(Constants.BUNDLE_KEY_TITLE, String.format(getString(R.string.text_quotes_on), "'" + searchKeyword + "'"));

                QuoteFilters quoteFilters = new QuoteFilters();
                quoteFilters.setSearchKeyword(searchKeyword);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS, quoteFilters);
                startActivity(quotesIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = QuoteActivity.this;

        initToolbar();
        initListener();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_QUOTE_ID)) {
                mQuoteId = intent.getStringExtra(BUNDLE_KEY_QUOTE_ID);
            } else {
                CommonView.showToast(mActivity, getString(R.string.error_quote_not_found), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                finish();
            }
        }
        supportPostponeEnterTransition();//To temporarily prevent the shared element transition from starting
        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        getQuote();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_quote)));
    }

    private void initListener() {

        imageAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
                authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, mQuote.getAuthor().getId());
                startActivity(authorIntent);

            }
        });

        textAuthorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
                authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, mQuote.getAuthor().getId());
                startActivity(authorIntent);
            }
        });

        textFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followAuthor();
            }
        });

        imageLikeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeQuote();
            }
        });

        imageCommentQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(mActivity, CommentsActivity.class);

                CommentFilters commentFilters = new CommentFilters();
                commentFilters.setQuoteID(mQuoteId);
                commentIntent.putExtra(Constants.BUNDLE_KEY_COMMENTS_FILTERS, commentFilters);
                startActivity(commentIntent);
            }
        });

        imageShareQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading), null);

                Utils.getInstance(mContext).getBitmapFromUrl(mQuote.getImageUrl(), new bitmapRequestListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (bitmap != null) {
                            quoteBitmapToSave = bitmap;
                            checkPermissionAndSaveBitmapToDisk(bitmap);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });
            }
        });

        imageMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuPopup(v);
            }
        });
    }

    private void likeQuote() {
        int dest = 0;
        if (mQuote.isLikeQuote()) {
            dest = -360;// rotate anti-clockwise

        } else {
            dest = 360;// rotate clockwise
        }

        if (imageLikeQuote.getRotation() == 360 || imageLikeQuote.getRotation() == -360) {
            dest = 0;
        }
        mObjectAnimator = ObjectAnimator.ofFloat(imageLikeQuote,
                "rotation", dest);
        mObjectAnimator.setDuration(500);
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);

        mObjectAnimator.start();
        imageLikeQuote.setClickable(false);

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, mQuote.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_LIKE_QUOTE)
                .setParams(param)
                .setMessage("QuotesActivity.java|likeQuote")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        imageLikeQuote.setClickable(true);
                        if (mQuote.isLikeQuote()) {
                            imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_border_accent_24dp);
                            mObjectAnimator.end();
                            mQuote.setLikeQuote(false);
                            textTotalLikes.setText(Integer.toString(Integer.parseInt(mQuote.getTotalLikes()) - 1));
                            mQuote.setTotalLikes(Integer.toString(Integer.parseInt(mQuote.getTotalLikes()) - 1));
                        } else {
                            imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_accent_24dp);
                            mObjectAnimator.end();
                            mQuote.setLikeQuote(true);
                            textTotalLikes.setText(Integer.toString(Integer.parseInt(mQuote.getTotalLikes()) + 1));
                            mQuote.setTotalLikes(Integer.toString(Integer.parseInt(mQuote.getTotalLikes()) + 1));
                        }
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        mObjectAnimator.end();
                        // Do nothing
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void followAuthor() {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_please_wait), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mQuote.getAuthor().getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_FOLLOW_AUTHOR)
                .setParams(param)
                .setMessage("QuoteActivity.java|followAuthor")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {

                        PopupMenu popupMenu = new PopupMenu(mActivity, textFollow);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        inflater.inflate(R.menu.menu_item_quote, popupMenu.getMenu());
                        MenuItem followItem = popupMenu.getMenu().findItem(R.id.action_follow_author_item_quote);

                        if (mQuote.getAuthor().isFollowingAuthor()) {
                            textFollow.setText(mContext.getString(R.string.action_follow));
                            followItem.setTitle(mContext.getString(R.string.action_follow));
                            mQuote.getAuthor().setFollowingAuthor(false);

                        } else {
                            textFollow.setText(mContext.getString(R.string.action_unfollow));
                            followItem.setTitle(mContext.getString(R.string.action_unfollow));
                            mQuote.getAuthor().setFollowingAuthor(true);
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

    private void getQuote() {
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, mQuoteId);
        param.put(Constants.API_PARAM_KEY_LOGGED_AUTHOR_ID, mLoggedAuthor.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_QUOTE)
                .setParams(param)
                .setMessage("QuoteActivity.java|getQuote")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetQuoteResponse(stringResponse);
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

    private void parseGetQuoteResponse(String stringResponse) {
        mQuote = new Gson().fromJson(stringResponse, Quote.class);
        renderView();
    }

    private void renderView() {

        RequestOptions quoteImageOptions = new RequestOptions()
                .fitCenter()
                .dontAnimate()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .centerCrop();

        Glide.with(mContext)
                .load(mQuote.getImageUrl())
                .apply(quoteImageOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarLoadingQuoteImage.setVisibility(View.GONE);
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarLoadingQuoteImage.setVisibility(View.GONE);
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageQuote);

        RequestOptions authorImageOptions = new RequestOptions()
                .fitCenter()
                .dontAnimate()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .circleCrop();

        Glide.with(mContext)
                .load(mQuote.getAuthor().getProfileImage())
                .apply(authorImageOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarLoadingAuthorImage.setVisibility(View.GONE);
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarLoadingAuthorImage.setVisibility(View.GONE);
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageAuthor);

        if (mQuote.isLikeQuote()) {
            imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_accent_24dp);
        } else {
            imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_border_accent_24dp);
        }


        textAuthorName.setText(mQuote.getAuthor().getName());
        textDateQuote.setText(mQuote.getDateAdded());

        // Hide follow option if user is viewing his quote
        if (mQuote.getAuthor().getId().equalsIgnoreCase(mLoggedAuthor.getId())) {
            textFollow.setVisibility(View.GONE);
        }
        if (mQuote.getAuthor().isFollowingAuthor()) {
            textFollow.setText(getString(R.string.action_unfollow));
        } else {
            textFollow.setText(getString(R.string.action_follow));
        }
        if (!mQuote.getCaption().equalsIgnoreCase("") && !mQuote.getCaption().equalsIgnoreCase(null)) {
            layoutCaptionQuote.setVisibility(View.VISIBLE);
            textCaptionQuote.setText(mQuote.getCaption());
        }
        textTotalLikes.setText(mQuote.getTotalLikes());
        textTotalComments.setText(mQuote.getTotalComments());
        if (!mQuote.getSource().isEmpty() && mQuote.getSource() != null) {
            labelQuoteSource.setVisibility(View.VISIBLE);
            textQuoteSource.setVisibility(View.VISIBLE);
            textQuoteSource.setText(mQuote.getSource());
        }

        addLanguageTags();
        addCategoryTags();
        addSearchTags();
    }

    private void addLanguageTags() {


        int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
        Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
        mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

        TextView languageTag = new TextView(mContext);
        languageTag.setText(mQuote.getLanguage().getLanguageName());
        languageTag.setTypeface(FontHelper.getInstance(mContext).getAppCustomMediumTypeface());
        languageTag.setTag(mQuote.getLanguage());
        languageTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
        languageTag.setBackground(mRoundBorderDrawable);
        languageTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
        languageTag.setOnClickListener(languageTagOnClickListener);
        addTag(languagesTagsContainer, languageTag);
    }

    private void addCategoryTags() {

        ArrayList<Category> listCategory = mQuote.getCategories();

        if (listCategory.size() > 0) {
            for (Category category : listCategory) {

                int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
                Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
                mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

                TextView categoryTag = new TextView(mContext);
                categoryTag.setText(category.getName());
                categoryTag.setTypeface(FontHelper.getInstance(mContext).getAppCustomMediumTypeface());
                categoryTag.setTag(category);
                categoryTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
                categoryTag.setBackground(mRoundBorderDrawable);
                categoryTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
                categoryTag.setOnClickListener(categoryTagOnClickListener);
                addTag(categoryTagsContainer, categoryTag);
            }
        } else {
            parentCategoryTagsContainer.setVisibility(View.GONE);
        }
    }

    private void addSearchTags() {
        ArrayList<String> listTags = mQuote.getTags();

        if (listTags.size() > 0) {
            for (String tag : listTags) {

                int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
                Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
                mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

                TextView searchTag = new TextView(mContext);
                searchTag.setText(tag);
                searchTag.setTypeface(FontHelper.getInstance(mContext).getAppCustomMediumTypeface());
                searchTag.setTag(tag);
                searchTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
                searchTag.setBackground(mRoundBorderDrawable);
                searchTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
                searchTag.setOnClickListener(searchTagOnClickListener);
                addTag(quoteTagsContainer, searchTag);
            }
        } else {
            parentQuoteTagsContainer.setVisibility(View.GONE);
        }
    }

    public void addTag(FlowLayout container, View tag) {

        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.rightMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.leftMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.topMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.bottomMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        tag.setLayoutParams(params);
        container.addView(tag);
    }


    @SuppressLint("NewApi")
    private void checkPermissionAndSaveBitmapToDisk(Bitmap quoteBitmap) {

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE);
        } else {
            Uri uri = Utils.getInstance(mContext).saveBitmapToDisk(quoteBitmap);
            openImageShareIntent(uri);
        }
    }

    private void openImageShareIntent(Uri quoteImageUri) {

        if (quoteImageUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, quoteImageUri);
            shareIntent.setType("image/jpeg");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, getString(R.string.text_share_quote)));
        } else {
            // ...sharing failed, handle error
        }
    }

    @SuppressLint("NewApi")
    private void checkPermissionAndDownloadQuote(Quote quote) {

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE);
        } else {
            Utils.getInstance(mContext).downloadImageToDisk(quote.getOriginalImageUrl());
        }
    }

    public void showMenuPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(mActivity, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item_quote, popupMenu.getMenu());
        MenuItem followItem = popupMenu.getMenu().findItem(R.id.action_follow_author_item_quote);

        // Hide follow option if user is viewing his quote
        if (mQuote.getAuthor().getId().equalsIgnoreCase(mLoggedAuthor.getId())) {
            followItem.setVisible(false);
        }
        if (mQuote.getAuthor().isFollowingAuthor()) {
            followItem.setTitle(mContext.getString(R.string.action_unfollow));
        } else {
            followItem.setTitle(mContext.getString(R.string.action_follow));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_follow_author_item_quote:
                        followAuthor();
                        break;
                    case R.id.action_download_quote_item_quote:
                        checkPermissionAndDownloadQuote(mQuote);
                        break;
                    case R.id.action_report_quote_item_quote:
                        getReportReasonsAndShowDialog();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void getReportReasonsAndShowDialog() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_REPORT_REASONS)
                .setParams(param)
                .setMessage("QuoteActivity.java|getReportReasonsAndShowDialog")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetReportReasonsResponse(stringResponse);
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

    private void parseGetReportReasonsResponse(String stringResponse) {

        //String reportReasonsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_REPORTS);

        Type reportListType = new TypeToken<ArrayList<ReportReason>>() {
        }.getType();

        ArrayList<ReportReason> listReportReasons = new Gson().fromJson(stringResponse, reportListType);

        final HashMap<String, String> hashReportReasons = new HashMap<String, String>();
        ArrayList<String> listReportReasonsTitle = new ArrayList<String>();
        for (ReportReason reportReason : listReportReasons) {
            hashReportReasons.put(reportReason.getTitle(), reportReason.getId());
            listReportReasonsTitle.add(reportReason.getTitle());
        }
        CommonView.getInstance(mContext).showReportDialog(listReportReasonsTitle, mActivity, new onReportItemSubmitListener() {
            @Override
            public void onReportItemSubmit(String titleReport) {
                String reportId = hashReportReasons.get(titleReport);
                submitQuoteReport(reportId);
            }
        });
    }

    private void submitQuoteReport(String reportId) {

        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_submitting_report), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_REPORT_ID, reportId);
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, mQuoteId);
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_REPORT_QUOTE)
                .setParams(param)
                .setMessage("QuoteActivity.java|submitQuoteReport")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseSubmitReportResponse(stringResponse);
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

    private void parseSubmitReportResponse(String stringResponse) {

        CommonView.getInstance(mContext).showDialog(
                new CustomDialog().setActivity(mActivity)
                        .setDialogType(SUCCESS)
                        .setTitle(getString(R.string.success_quote_reported))
                        .setMessage(getString(R.string.msg_post_quote_report_submit))
        );
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
    }
}
