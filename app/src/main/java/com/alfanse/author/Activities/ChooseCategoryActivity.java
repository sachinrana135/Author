package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alfanse.author.CustomViews.FlowLayout;
import com.alfanse.author.Models.Category;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_CATEGORY;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_SELECTED_CATEGORIES;

public class ChooseCategoryActivity extends BaseActivity {

    private static final int RESULT_CODE = 4234;
    @BindView(R.id.toolbar_choose_category)
    Toolbar mToolbar;
    @BindView(R.id.tags_container_choose_activity)
    FlowLayout tagsContainer;
    @BindView(R.id.search_edit_choose_category)
    EditText searchBar;

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Category> mSelectedCategories;
    private int mMaximumSelectAllow = 3;
    private ArrayList<Category> mCategories;


    private View.OnClickListener tagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v instanceof TextView) {

                TextView categoryTag = (TextView) v;
                Category category = (Category) categoryTag.getTag();

                if ((categoryTag).getCompoundDrawables()[2] == null) { // Right drawable
                    if (mSelectedCategories.size() < mMaximumSelectAllow) {
                        categoryTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_white_24dp, 0);
                        categoryTag.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.spacing_xsmall));
                        mSelectedCategories.add(category);
                    }
                } else {
                    categoryTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mSelectedCategories.remove(category);

                }
            }
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null) {

                for (int index = 0; index < tagsContainer.getChildCount(); index++) {

                    if (tagsContainer.getChildAt(index) instanceof TextView) {

                        TextView categoryTag = (TextView) tagsContainer.getChildAt(index);

                        Category category = (Category) categoryTag.getTag();

                        if (Pattern.compile(Pattern.quote(s.toString()), Pattern.CASE_INSENSITIVE).matcher(category.getName()).find()) {
                            categoryTag.setVisibility(View.VISIBLE);
                        } else {
                            categoryTag.setVisibility(View.GONE);
                        }
                    }
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = ChooseCategoryActivity.this;

        initToolbar();

        mSelectedCategories = new ArrayList<Category>();
        searchBar.addTextChangedListener(searchTextWatcher);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        loadCategoriesList();

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_choose_category));
    }

    private void loadCategoriesList() {
        String categoryJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_CATEGORY);

        Type categoryListType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        mCategories = new Gson().fromJson(categoryJson, categoryListType);

        for (Category category : mCategories) {

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
            categoryTag.setOnClickListener(tagOnClickListener);

            addTag(categoryTag);

        }
    }

    public void addTag(View view) {

        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.rightMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.leftMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.topMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        params.bottomMargin = (int) getResources().getDimension(R.dimen.spacing_xsmall);
        view.setLayoutParams(params);
        tagsContainer.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_choose_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult();
                return true;
            case R.id.action_done_choose_category:
                setResult();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }

    private void setResult() {
            Intent intent = new Intent();
            intent.putExtra(BUNDLE_KEY_SELECTED_CATEGORIES, mSelectedCategories);
        setResult(RESULT_CODE, intent);
            finish();//finishing activity
    }
}
