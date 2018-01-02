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
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.alfanse.author.CustomViews.FlowLayout;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Category;
import com.alfanse.author.Models.Language;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_SELECTED_CATEGORIES;

public class PublishQuoteActivity extends BaseActivity {

    private static final int REQUEST_CODE_CHOOSE_CATEGORY = 5345;
    private static final int RESULT_CODE = 5252;
    private final int maxCategoriesSelectionAllowed = 3;
    @BindView(R.id.toolbar_publish_quote)
    Toolbar mToolbar;
    @BindView(R.id.edit_text_quote_caption_publish_quote)
    EditText editTextQuoteCaption;
    @BindView(R.id.spinner_select_language_publish_quote)
    SearchableSpinner spinnerLanguages;
    @BindView(R.id.button_choose_category_publish_quote)
    Button buttonChooseCategory;
    @BindView(R.id.category_tags_container_publish_quote)
    FlowLayout categoryTagsContainer;
    @BindView(R.id.quotes_tags_container_publish_quote)
    FlowLayout quoteTagsContainer;
    @BindView(R.id.edit_text_enter_tags_publish_quote)
    EditText editTextTags;
    @BindView(R.id.switch_button_copyright_publish_quote)
    Switch switchButtonCopyright;
    @BindView(R.id.layout_enter_quote_source_publish_quote)
    TextInputLayout layoutQuoteSource;
    @BindView(R.id.edit_text_enter_quote_source_publish_quote)
    EditText editTextQuoteSource;
    @BindView(R.id.button_publish_publish_quote)
    Button buttonPublishQuote;
    private Context mContext;
    private Activity mActivity;
    private Quote mQuote;
    private ArrayList<Category> mListCategories;
    private ArrayList<Language> mLanguages;
    private HashMap<String, String> mHashLanguages;
    private ArrayList<String> mListLanguages;
    private ArrayList<String> mListTags;
    private ArrayAdapter<String> mLanguageAdapter;
    private View.OnClickListener categoryTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {
                TextView categoryTag = (TextView) v;
                Category category = (Category) categoryTag.getTag();
                categoryTagsContainer.removeView(v);
                mListCategories.remove(category);
            }
        }
    };
    private View.OnClickListener quoteTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {
                TextView quoteTag = (TextView) v;
                quoteTagsContainer.removeView(v);
                mListTags.remove(mListTags.indexOf((quoteTag.getText())));
            }
        }
    };

    private TextWatcher quoteTagsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String tagString = s.toString();
            if (tagString.length() == 0) return;
            if (tagString.contains(",")) // another method
            {
                // comma is entered
                String actualTagString = tagString.substring(0, tagString.length() - 1);// escape last comma
                if (!actualTagString.trim().isEmpty()) {

                    int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
                    Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
                    mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

                    TextView quoteTag = new TextView(mContext);
                    quoteTag.setText(actualTagString);
                    quoteTag.setTypeface(null, Typeface.BOLD);

                    quoteTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
                    quoteTag.setBackground(mRoundBorderDrawable);
                    quoteTag.setGravity(Gravity.CENTER_VERTICAL);
                    quoteTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
                    quoteTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_white_24dp, 0);
                    quoteTag.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.spacing_xsmall));
                    quoteTag.setOnClickListener(quoteTagOnClickListener);
                    editTextTags.setText("");
                    mListTags.add(quoteTag.getText().toString());
                    addTag(quoteTagsContainer, quoteTag);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView.OnEditorActionListener quoteTagsEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO && !editTextTags.getText().toString().trim().isEmpty()) {

                int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
                Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
                mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

                TextView quoteTag = new TextView(mContext);
                quoteTag.setText(editTextTags.getText().toString());
                quoteTag.setTypeface(null, Typeface.BOLD);

                quoteTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
                quoteTag.setBackground(mRoundBorderDrawable);
                quoteTag.setGravity(Gravity.CENTER_VERTICAL);
                quoteTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
                quoteTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_white_24dp, 0);
                quoteTag.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.spacing_xsmall));
                quoteTag.setOnClickListener(quoteTagOnClickListener);
                editTextTags.setText("");
                mListTags.add(quoteTag.getText().toString());
                addTag(quoteTagsContainer, quoteTag);

                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_quote);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = PublishQuoteActivity.this;
        initToolbar();
        initListener();

        mListTags = new ArrayList<String>();
        mListCategories = new ArrayList<Category>();
        mListLanguages = new ArrayList<String>();
        mHashLanguages = new HashMap<String, String>();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_QUOTE)) {
                mQuote = (Quote) intent.getSerializableExtra(BUNDLE_KEY_QUOTE);
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getLanguages();
    }

    private void getLanguages() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_LANGUAGES)
                .setParams(param)
                .setMessage("PublishQuoteActivity.java|getLanguages")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetLanguagesResponse(stringResponse);
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

    private void parseGetLanguagesResponse(String stringResponse) {

        //String languageJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_LANGUAGES);

        Type languageListType = new TypeToken<ArrayList<Language>>() {
        }.getType();
        mLanguages = new Gson().fromJson(stringResponse, languageListType);
        int selectionIndex = 0;
        int index = 0;
        for (Language language : mLanguages) {
            mHashLanguages.put(language.getLanguageName(), language.getLanguageId());
            mListLanguages.add(language.getLanguageName());
            if (language.getLanguageIsoCode().equalsIgnoreCase(Constants.ENGLISH_LANGUAGE_CODE)) {
                selectionIndex = index;
            }
            index++;
        }
        mLanguageAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mListLanguages);
        mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguages.setTitle(getString(R.string.text_select_language));
        spinnerLanguages.setAdapter(mLanguageAdapter);
        spinnerLanguages.setSelection(selectionIndex);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_publish_quote)));
    }

    private void initListener() {

        editTextTags.addTextChangedListener(quoteTagsTextWatcher);
        editTextTags.setOnEditorActionListener(quoteTagsEditorActionListener);

        buttonChooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseCategoryIntent = new Intent(mActivity, ChooseCategoryActivity.class);
                chooseCategoryIntent.putExtra(BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW, maxCategoriesSelectionAllowed);
                chooseCategoryIntent.putExtra(BUNDLE_KEY_SELECTED_CATEGORIES, mListCategories);

                startActivityForResult(chooseCategoryIntent, REQUEST_CODE_CHOOSE_CATEGORY);
            }
        });

        buttonPublishQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Language language = new Language();
                language.setLanguageName(spinnerLanguages.getSelectedItem().toString());
                language.setLanguageId(mHashLanguages.get(language.getLanguageName()));
                if (editTextQuoteCaption.getText() != null) {
                    mQuote.setCaption(editTextQuoteCaption.getText().toString().trim());
                } else {
                    mQuote.setCaption("");
                }
                mQuote.setLanguage(language);

                if (switchButtonCopyright.isChecked()) {
                    mQuote.setCopyrighted(true);
                } else {
                    mQuote.setCopyrighted(false);
                }

                if (editTextQuoteSource.getText() != null) {
                    mQuote.setSource(editTextQuoteSource.getText().toString().trim());
                } else {
                    mQuote.setSource("");
                }
                mQuote.setTags(mListTags);
                mQuote.setCategories(mListCategories);
                saveQuote();

            }
        });

        switchButtonCopyright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    layoutQuoteSource.setVisibility(View.GONE);
                } else {
                    layoutQuoteSource.setVisibility(View.VISIBLE);
                    editTextQuoteSource.requestFocus();
                }

            }
        });
    }

    private void saveQuote() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_save_quote), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_QUOTE, new Gson().toJson(mQuote));
        param.put(Constants.API_PARAM_KEY_QUOTE_IMAGE, Utils.getInstance(mContext).getStringImage(mQuote.getLocalImagePath()));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_SAVE_QUOTE)
                .setParams(param)
                .setMessage("PublishQuoteActivity.java|saveQuote")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseSaveQuoteResponse(stringResponse);
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

    private void parseSaveQuoteResponse(String stringResponse) {
        Quote quote = new Gson().fromJson(stringResponse, Quote.class);
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_KEY_QUOTE_ID, quote.getId());
        setResult(RESULT_CODE, intent);
        finish();//finishing activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_CHOOSE_CATEGORY: {

                if (data.getExtras() != null) {

                    if (data.hasExtra(BUNDLE_KEY_SELECTED_CATEGORIES)) {
                        mListCategories = (ArrayList<Category>) data.getSerializableExtra(BUNDLE_KEY_SELECTED_CATEGORIES);
                        updateTagsView();
                    }

                } else {

                }

                break;
            }
        }
    }

    private void updateTagsView() {

        categoryTagsContainer.removeAllViews();

        for (Category category : mListCategories) {

            int randomTagColor = Utils.getInstance(mContext).getRandomTagColor();
            Drawable mRoundBorderDrawable = Utils.getInstance(mContext).getDrawable(R.drawable.round_border);
            mRoundBorderDrawable.setColorFilter(new PorterDuffColorFilter(randomTagColor, PorterDuff.Mode.SRC_IN));

            TextView categoryTag = new TextView(mContext);
            categoryTag.setText(category.getName());
            categoryTag.setTypeface(null, Typeface.BOLD);
            categoryTag.setTag(category);
            categoryTag.setGravity(Gravity.CENTER_VERTICAL);
            categoryTag.setTextColor(Utils.getInstance(mContext).getColor(R.color.colorWhite));
            categoryTag.setBackground(mRoundBorderDrawable);
            categoryTag.setPadding((int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall), (int) getResources().getDimension(R.dimen.spacing_normal), (int) getResources().getDimension(R.dimen.spacing_xsmall));
            categoryTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_white_24dp, 0);
            categoryTag.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.spacing_xsmall));
            categoryTag.setOnClickListener(categoryTagOnClickListener);

            addTag(categoryTagsContainer, categoryTag);

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
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE, intent);
        finish();//finishing activity
    }

    @Override
    public void onPause() {
        super.onPause();
        removeSearchableDialog();
    }

    public void removeSearchableDialog() {
        Fragment searchableSpinnerDialog = getFragmentManager().findFragmentByTag("TAG");

        if (searchableSpinnerDialog != null && searchableSpinnerDialog.isAdded()) {
            getFragmentManager().beginTransaction().remove(searchableSpinnerDialog).commit();
        }
    }
}
