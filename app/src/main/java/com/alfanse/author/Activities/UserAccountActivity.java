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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.AuthorFilters;
import com.alfanse.author.Models.QuoteFilters;
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
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

public class UserAccountActivity extends BaseActivity {

    private static final String IMAGE_REQUIRED_FOR_COVER = "IMAGE_REQUIRED_FOR_COVER";
    private static final String IMAGE_REQUIRED_FOR_PROFILE = "IMAGE_REQUIRED_FOR_PROFILE";
    private static final int ALL_PERMISSIONS_REQUEST_CODE = 54353;
    @BindView(R.id.toolbar_account_user)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout_account_user)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.image_view_cover_image_account_user)
    ImageView imageCover;
    @BindView(R.id.fab_edit_cover_photo_account_user)
    FloatingActionButton fabChangeCoverImage;
    @BindView(R.id.image_view_profile_image_account_user)
    ImageView imageProfile;
    @BindView(R.id.fab_edit_profile_image_account_user)
    FloatingActionButton fabChangeProfileImage;
    @BindView(R.id.text_author_name_account_user)
    TextView textAuthorName;
    @BindView(R.id.text_author_status_account_user)
    TextView textAuthorStatus;
    @BindView(R.id.layout_total_quote_account_user)
    LinearLayout layoutTotalQuotes;
    @BindView(R.id.text_total_quotes_account_user)
    TextView textTotalQuotes;
    @BindView(R.id.layout_total_followers_account_user)
    LinearLayout layoutTotalFollowers;
    @BindView(R.id.text_total_followers_account_user)
    TextView textTotalFollowers;
    @BindView(R.id.layout_total_following_account_user)
    LinearLayout layoutTotalFollowing;
    @BindView(R.id.text_total_following_account_user)
    TextView textTotalFollowing;
    @BindView(R.id.text_view_edit_profile_account_user)
    TextView textEditProfile;
    @BindView(R.id.text_update_password_account_user)
    TextView textUpdatePassword;
    @BindView(R.id.text_view_logout_account_user)
    TextView textLogout;
    @BindView(R.id.text_view_share_app_account_user)
    TextView textShareApp;
    @BindView(R.id.progress_bar_cover_image_account_user)
    ProgressBar progressBarCoverImage;
    @BindView(R.id.progress_bar_profile_image_account_user)
    ProgressBar progressBarProfileImage;

    private FirebaseAuth mAuth;
    private Activity mActivity;
    private Context mContext;
    private Author mAuthor;
    private String imageRequiredFor;
    private Uri mCropImageUri;
    private Uri mCroppedImageUri;
    private String mImagePath;
    private ProgressDialog mProgressDialog;
    private Author mLoggedAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = UserAccountActivity.this;

        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        getAuthor();
        initListener();
    }


    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setTitle(FontHelper.getCustomTypefaceTitle(mAuthor.getName()));
    }

    private void initListener() {

        fabChangeCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_COVER;
                startPickImageActivity();
            }
        });

        fabChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_PROFILE;
                startPickImageActivity();
            }
        });

        textEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editProfileIntent = new Intent(mActivity, EditProfileActivity.class);
                startActivity(editProfileIntent);
            }
        });

        textUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent updatePasswordInIntent = new Intent(mActivity, UpdatePasswordActivity.class);
                startActivity(updatePasswordInIntent);
            }
        });

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonView.showToast(mActivity, getString(R.string.success_logout), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                FirebaseAuth.getInstance().signOut();
                SharedManagement.getInstance(mContext).remove(SharedManagement.LOGGED_USER);
                Intent intent = new Intent(mActivity, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        layoutTotalQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quotesIntent = new Intent(mActivity, QuotesActivity.class);
                quotesIntent.putExtra(Constants.BUNDLE_KEY_TITLE, String.format(getString(R.string.text_yours), getString(R.string.text_quotes)));
                QuoteFilters quoteFilters = new QuoteFilters();
                quoteFilters.setAuthorID(mAuthor.getId());
                quotesIntent.putExtra(Constants.BUNDLE_KEY_QUOTES_FILTERS, quoteFilters);
                startActivity(quotesIntent);
            }
        });

        layoutTotalFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorsIntent = new Intent(mActivity, AuthorsActivity.class);

                authorsIntent.putExtra(Constants.BUNDLE_KEY_TITLE, String.format(getString(R.string.text_yours), getString(R.string.text_followers)));

                AuthorFilters authorFilters = new AuthorFilters();
                authorFilters.setAuthorID(mAuthor.getId());
                authorFilters.setFilterType(Constants.AUTHOR_FILTER_TYPE_FOLLOWER);
                authorsIntent.putExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS, authorFilters);
                startActivity(authorsIntent);
            }
        });

        layoutTotalFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorsIntent = new Intent(mActivity, AuthorsActivity.class);

                authorsIntent.putExtra(Constants.BUNDLE_KEY_TITLE, String.format(getString(R.string.text_you), getString(R.string.text_following)));

                AuthorFilters authorFilters = new AuthorFilters();
                authorFilters.setAuthorID(mAuthor.getId());
                authorFilters.setFilterType(Constants.AUTHOR_FILTER_TYPE_FOLLOWING);

                authorsIntent.putExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS, authorFilters);
                startActivity(authorsIntent);
            }
        });

        textShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
    }

    private void getAuthor() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, getString(R.string.text_loading));
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mLoggedAuthor.getId());
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
        SharedManagement.getInstance(mContext).setLoggedUser(mAuthor);
        initToolbar();
        renderView();
    }

    private void shareApp() {

        Intent i = new Intent(Intent.ACTION_SEND);
        //text type
        i.setType("text/plain");
        //extrs subject
        i.putExtra(Intent.EXTRA_SUBJECT, getApplication().getApplicationInfo().name);
        //share strings
        String share = getString(R.string.msg_share_app);
        share = share + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
        //putExtra
        i.putExtra(Intent.EXTRA_TEXT, share);
        //start intent
        startActivity(Intent.createChooser(i, getString(R.string.text_share_via)));
    }

    @SuppressLint("NewApi")
    private void startPickImageActivity() {

        // For API >= 23 we need to check specifically that we have permissions to read external storage,
        // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, ALL_PERMISSIONS_REQUEST_CODE);
        } else {
            startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }

    }

    private void startCropImageActivity(Uri imageUri) {
        if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COVER)) {
            CropImage.activity(imageUri).setAspectRatio(16, 9).start(mActivity); // Need square image
        } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_PROFILE)) {
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(mActivity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE: {
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = CropImage.getPickImageResultUri(mActivity, data);
                    mCropImageUri = imageUri;
                    // For API >= 23 we need to check specifically that we have permissions to read external storage.
                    if (CropImage.isReadExternalStoragePermissionsRequired(mActivity, imageUri)) {
                        // request permissions and handle the result in onRequestPermissionsResult()

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    } else {
                        // no permissions required or already grunted, can start crop image activity
                        startCropImageActivity(imageUri);
                    }
                }
                break;
            }

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {
                    mCroppedImageUri = result.getUri();
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCroppedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COVER)) {

                        imageCover.setImageBitmap(imageBitmap);

                        new saveImageTask().execute();

                    } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_PROFILE)) {
                        new saveImageTask().execute();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    CommonView.showToast(mActivity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                }
                break;
            }
        }
    }

    private void updateProfileImage() {
        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mAuthor.getId());
        param.put(Constants.API_PARAM_KEY_PROFILE_IMAGE, Utils.getInstance(mContext).getStringImage(mImagePath));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_UPDATE_PROFILE_IMAGE)
                .setParams(param)
                .setMessage("UserAccountActivity.java|updateProfileImage")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        CommonView.showToast(mActivity, getString(R.string.success_profile_image_updated), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }


    private void updateCoverImage() {
        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mAuthor.getId());
        param.put(Constants.API_PARAM_KEY_COVER_IMAGE, Utils.getInstance(mContext).getStringImage(mImagePath));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_UPDATE_COVER_IMAGE)
                .setParams(param)
                .setMessage("UserAccountActivity.java|updateCoverImage")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        CommonView.showToast(mActivity, getString(R.string.success_cover_image_updated), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END

    }

    private String saveCroppedImage() {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCroppedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = null;
        try {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.QUOTE_PUBLIC_OUTPUT_DIRECTORY);

            if (!dir.mkdirs()) {
                //  CommonView.showToast(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
            }

            file = new File(dir.getAbsolutePath() + "/" + Utils.getTimeStamp() + Constants.QUOTE_OUTPUT_FORMAT);

            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    private void renderView() {

        textAuthorName.setText(mAuthor.getName());
        textAuthorStatus.setText(mAuthor.getStatus());
        textTotalFollowers.setText(mAuthor.getTotalFollowers());
        textTotalFollowing.setText(mAuthor.getTotalFollowing());
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

    private class saveImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_updating_image), null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mImagePath = saveCroppedImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COVER)) {
                updateCoverImage();
            } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_PROFILE)) {

                RequestOptions profileImageOptions = new RequestOptions()
                        .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                        .fitCenter()
                        .circleCrop();

                Glide.with(mContext)
                        .load(new File(mImagePath))
                        .apply(profileImageOptions)
                        .into(imageProfile);

                updateProfileImage();
            }
        }
    }


}
