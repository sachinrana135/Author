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
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfanse.author.CustomViews.ComponentBoxView;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.ComponentView;
import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Fragments.CanvasOptionsFragment;
import com.alfanse.author.Fragments.ComponentBoxViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentImageViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentTextViewOptionsFragment;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Fragments.CanvasOptionsFragment.CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Fragments.ComponentBoxViewOptionsFragment.COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Fragments.ComponentTextViewOptionsFragment.COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;

public class NewQuoteActivity extends BaseActivity implements
        CanvasOptionsFragment.OnFragmentInteractionListener,
        ComponentTextViewOptionsFragment.OnFragmentInteractionListener,
        ComponentImageViewOptionsFragment.OnFragmentInteractionListener,
        ComponentBoxViewOptionsFragment.OnFragmentInteractionListener,
        ColorPickerDialogListener,
        ComponentView.onComponentViewInteractionListener {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 3242;
    private static final int REQUEST_CODE_PUBLISH_QUOTE = 5235;
    @BindView(R.id.toolbar_new_quote)
    Toolbar mToolbar;
    @BindView(R.id.SquareFrameLayoutWriteQuoteCanvas)
    QuoteCanvas mQuoteCanvas;
    @BindView(R.id.container_new_quote)
    LinearLayout container;

    private Context mContext;
    private Activity mActivity;
    private GestureDetectorCompat mComponentTextViewGestureDetector;


    private android.support.v4.app.FragmentManager mFragmentManager;
    private CanvasOptionsFragment mCanvasOptionsFragment;
    private ComponentImageViewOptionsFragment mComponentImageViewOptionsFragment;
    private ComponentTextViewOptionsFragment mComponentTextViewOptionsFragment;
    private ComponentBoxViewOptionsFragment mComponentBoxViewOptionsFragment;
    private android.support.v4.app.Fragment mActiveOptionFragment = null;

    private ComponentTextView mActiveComponentTextView;
    private ComponentImageView mActiveComponentImageView;
    private ComponentBoxView mActiveComponentBoxView;
    private ArrayList<CanvasTheme> mListCanvasThemes = new ArrayList<CanvasTheme>();
    private Author mLoggedAuthor;

    private Runnable fontsLoaderTask = new Runnable() {
        @Override
        public void run() {
            FontHelper.getInstance(mContext);
        }
    };
    private ViewTreeObserver.OnGlobalLayoutListener quoteCanvasLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mQuoteCanvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            getDefaultCanvasTheme();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);
        ButterKnife.bind(this);
        initToolbar();
        mComponentTextViewGestureDetector = new GestureDetectorCompat(this, new ComponentTextViewGestureListener());

        mContext = getApplicationContext();
        mActivity = NewQuoteActivity.this;
        mFragmentManager = getSupportFragmentManager();

        mQuoteCanvas.getViewTreeObserver().addOnGlobalLayoutListener(quoteCanvasLayoutListener);

        fontsLoaderTask.run();

        mQuoteCanvas.setOnTouchListener(new CanvasTouchListener());
        if (mActiveOptionFragment == null) {
            mActiveOptionFragment = new CanvasOptionsFragment();
        }

        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setOrientation(LinearLayout.HORIZONTAL);
        } else if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setOrientation(LinearLayout.VERTICAL);
        }

        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        loadCanvasOptionsFragment();
    }

    private void getDefaultCanvasTheme() {
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_CANVAS_THEMES)
                .setParams(param)
                .setMessage("NewQuoteActivity.java|getDefaultCanvasTheme")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetDefaultCanvasThemeResponse(stringResponse);
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

    private void parseGetDefaultCanvasThemeResponse(String stringResponse) {
        // String themesJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_CANVAS_THEMES);

        Type themeListType = new TypeToken<ArrayList<CanvasTheme>>() {
        }.getType();

        mListCanvasThemes = new Gson().fromJson(stringResponse, themeListType);
        for (CanvasTheme canvasTheme : mListCanvasThemes) {
            mQuoteCanvas.setBackground(canvasTheme.getImageUrl());
            //mQuoteCanvas.getBackground();
            addComponentTextView(canvasTheme);
            break;
        }
    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_new_quote)));
    }

    private void loadCanvasOptionsFragment() {

        if (mCanvasOptionsFragment == null) {
            mCanvasOptionsFragment = new CanvasOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        if (!mCanvasOptionsFragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.option_container_new_quote, mCanvasOptionsFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            mCanvasOptionsFragment.showCanvasThemes();
        }
    }

    @Override
    public void onBackPressed() {
        showExitWarningDialog();
    }

    public void loadComponentImageViewOptionsFragment() {

        mComponentImageViewOptionsFragment = new ComponentImageViewOptionsFragment();
        mComponentImageViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentImageViewOptionsFragment.setComponentImageView(mActiveComponentImageView);

        mFragmentManager.beginTransaction()
                .replace(R.id.option_container_new_quote, mComponentImageViewOptionsFragment)
                .commit();
    }

    public void loadComponentBoxViewOptionsFragment() {

        mComponentBoxViewOptionsFragment = new ComponentBoxViewOptionsFragment();
        mComponentBoxViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentBoxViewOptionsFragment.setComponentBoxView(mActiveComponentBoxView);

        mFragmentManager.beginTransaction()
                .replace(R.id.option_container_new_quote, mComponentBoxViewOptionsFragment)
                .commit();
    }

    public void loadComponentTextViewOptionsFragment() {


        mComponentTextViewOptionsFragment = new ComponentTextViewOptionsFragment();
        mComponentTextViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentTextViewOptionsFragment.setComponentTextView(mActiveComponentTextView);

        mFragmentManager.beginTransaction()
                .replace(R.id.option_container_new_quote, mComponentTextViewOptionsFragment)
                .commit();
    }

    @Override
    public void onComponentTextViewAdded(ComponentTextView componentTextView) {

        componentTextView.setOnTouchListener(new componentTextViewTouchListener());
        mActiveComponentTextView = componentTextView;
    }

    @Override
    public void onComponentImageViewAdded(ComponentImageView componentImageView) {
        componentImageView.setOnTouchListener(new componentImageViewTouchListener());
        mActiveComponentImageView = componentImageView;
    }

    @Override
    public void onComponentBoxViewAdded(ComponentBoxView componentBoxView) {

        componentBoxView.setOnTouchListener(new componentBoxViewTouchListener());
        mActiveComponentBoxView = componentBoxView;
    }

    @Override
    public void onFragmentBackPressed() {
        mQuoteCanvas.setStateFocused();
        loadCanvasOptionsFragment();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mCanvasOptionsFragment.onColorSelected(dialogId, color);
                break;

            case COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mComponentTextViewOptionsFragment.onColorSelected(dialogId, color);
                break;

            case COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID:
                mComponentBoxViewOptionsFragment.onColorSelected(dialogId, color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

        switch (dialogId) {
            case CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mCanvasOptionsFragment.onDialogDismissed(dialogId);
                break;

            case COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mComponentTextViewOptionsFragment.onDialogDismissed(dialogId);
                break;

            case COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID:
                mComponentBoxViewOptionsFragment.onDialogDismissed(dialogId);
                break;
        }

    }

    @Override
    public void onRemoveButtonClick() {
        mQuoteCanvas.setStateFocused();
        loadCanvasOptionsFragment();
    }

    private void addComponentTextView(CanvasTheme canvasTheme) {

        ComponentTextView textView = new ComponentTextView(mActivity, mQuoteCanvas);

        if (canvasTheme != null) {
            textView.setTheme(canvasTheme);
        } else {
            textView.setTheme(CanvasTheme.getDefaultTheme());
        }

        textView.setTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW);

        textView.setMinimumHeight(textView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mQuoteCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

        mQuoteCanvas.addView(textView, layoutParams);

        onComponentTextViewAdded(textView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reset_new_quote:
                showResetWarningDialog();
                return true;

            case R.id.action_done_new_quote:

                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    startPublishActivity();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }

    private void startPublishActivity() {

        mQuoteCanvas.setStateFocused();
        mQuoteCanvas.removeProgressBar();// to fix the issue of loading icon seen in some quotes
        loadCanvasOptionsFragment();

        String localImagePath = saveCanvasIntoImage();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Quote quote = new Quote();
            quote.setLocalImagePath(localImagePath);
            quote.setContent(getQuoteContent());

            Author author = new Author();
            author.setId(mLoggedAuthor.getId());
            quote.setAuthor(author);

            Intent publishQuoteIntent = new Intent(mActivity, PublishQuoteActivity.class);
            publishQuoteIntent.putExtra(BUNDLE_KEY_QUOTE, quote);
            startActivityForResult(publishQuoteIntent, REQUEST_CODE_PUBLISH_QUOTE);
        } else {
            Intent signInIntent = new Intent(mActivity, SignInActivity.class);
            startActivity(signInIntent);
        }
    }

    private ArrayList<String> getQuoteContent() {

        ArrayList<String> quoteContent = new ArrayList<String>();

        try {

            for (int index = 0; index < mQuoteCanvas.getChildCount(); ++index) {

                View view = mQuoteCanvas.getChildAt(index);

                if ((view instanceof ComponentTextView)) {

                    ComponentTextView componentTextView = (ComponentTextView) view;

                    quoteContent.add(componentTextView.getText());
                }
            }
        } catch (Exception e) {

        }
        return quoteContent;
    }

    private String saveCanvasIntoImage() {

        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, getString(R.string.text_loading_save_quote));

        Bitmap bitmap = Bitmap.createBitmap(mQuoteCanvas.getWidth(), mQuoteCanvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mQuoteCanvas.draw(canvas);
        File file = null;
        try {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.QUOTE_PUBLIC_OUTPUT_DIRECTORY);

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("Error", "Directory not created");
                }
            }

            file = new File(dir.getAbsolutePath() + "/" + Utils.getTimeStamp() + Constants.QUOTE_OUTPUT_FORMAT);

            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.QUOTE_QUALITY, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonView.getInstance(mContext).dismissProgressDialog();
        return file.getAbsolutePath();
    }

    private void showResetWarningDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(getIntent());
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_reset_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showExitWarningDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_exit_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_quote, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startPublishActivity();

                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }

                return;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setOrientation(LinearLayout.VERTICAL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PUBLISH_QUOTE: {
                if (data != null) {
                    if (data.getExtras() != null) {
                        if (data.hasExtra(BUNDLE_KEY_QUOTE_ID)) {
                            String quoteId = data.getStringExtra(BUNDLE_KEY_QUOTE_ID);
                            Intent quoteIntent = new Intent(mActivity, QuoteActivity.class);
                            quoteIntent.putExtra(BUNDLE_KEY_QUOTE_ID, quoteId);
                            CommonView.showToast(mActivity, getString(R.string.success_quote_published), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                            startActivity(quoteIntent);
                            finish();
                        }

                    } else {
                        // Do nothing
                    }
                }
                break;
            }
        }
    }

    private class CanvasTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mQuoteCanvas.setStateFocused();
                loadCanvasOptionsFragment();
            }
            return false;
        }
    }

    private class componentTextViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentTextView, MotionEvent event) {
            mActiveComponentTextView = (ComponentTextView) componentTextView;
            return mComponentTextViewGestureDetector.onTouchEvent(event);
        }
    }

    private class componentImageViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentImageView, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mActiveComponentImageView = (ComponentImageView) componentImageView;
                loadComponentImageViewOptionsFragment();
            }
            return false;
        }
    }

    private class componentBoxViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentBoxView, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mActiveComponentBoxView = (ComponentBoxView) componentBoxView;
                loadComponentBoxViewOptionsFragment();
            }
            return false;
        }
    }

    public class ComponentTextViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            loadComponentTextViewOptionsFragment();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            LayoutInflater layoutInflater = mActivity.getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.layout_edit_text_dialog, null);
            final EditText quoteTextView = (EditText) view.findViewById(R.id.edit_text_layout_edit_text_dialog);
            quoteTextView.setText(mActiveComponentTextView.getText());
            quoteTextView.setSelection(quoteTextView.getText().length());
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            // Add the buttons
            builder.setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // DO nothing
                }
            });
            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Set other dialog properties
            builder.setView(view);
            // Create the AlertDialog
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quoteTextView.setError(null);
                    if (quoteTextView.getText().toString().isEmpty()) {
                        quoteTextView.setError(getString(R.string.error_required));
                    } else {
                        mActiveComponentTextView.setText(quoteTextView.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            return false;
        }
    }
}
