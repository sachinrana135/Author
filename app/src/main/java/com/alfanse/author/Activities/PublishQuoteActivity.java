package com.alfanse.author.Activities;

import android.app.Activity;
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
import com.alfanse.author.Models.Category;
import com.alfanse.author.Models.Language;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_LANGUAGES;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_SELECTED_CATEGORIES;

public class PublishQuoteActivity extends BaseActivity {

    private static final int REQUEST_CODE_CHOOSE_CATEGORY = 5345;
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
        fillLanguageSpinner();
    }

    private void fillLanguageSpinner() {

        //TODO get language API

        String languageJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_LANGUAGES);

        Type languageListType = new TypeToken<ArrayList<Language>>() {
        }.getType();
        mLanguages = new Gson().fromJson(languageJson, languageListType);

        for (Language language : mLanguages) {
            mHashLanguages.put(language.getLanguageName(), language.getLanguageId());
            mListLanguages.add(language.getLanguageName());
        }
        mLanguageAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mListLanguages);
        mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguages.setTitle(getString(R.string.text_select_language));
        spinnerLanguages.setAdapter(mLanguageAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_publish_quote));
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

                mQuote.setCaption(editTextQuoteCaption.getText().toString().trim());
                mQuote.setLanguage(language);
                mQuote.setSource(editTextQuoteSource.getText().toString().trim());
                mQuote.setTags(mListTags);
                mQuote.setCategories(mListCategories);

                String quoteJson = new Gson().toJson(mQuote);
                saveQuote(quoteJson);
                //TODO save quote and upload image

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

    private void saveQuote(String quoteJson) {

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
                    //TODO catch Exception
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
}
