/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.Activities.ChooseCategoryActivity;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Interfaces.quoteFiltersUpdateListener;
import com.alfanse.author.Models.Category;
import com.alfanse.author.Models.Language;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_SELECTED_CATEGORIES;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuoteFiltersFragment extends Fragment {

    private static final int REQUEST_CODE_CHOOSE_CATEGORIES = 958;
    @BindView(R.id.layout_select_language_filters_quote)
    LinearLayout layoutSelectLanguages;
    @BindView(R.id.layout_select_category_filters_quote)
    LinearLayout layoutSelectCategories;
    @BindView(R.id.text_reset_filters_quote)
    TextView textReset;
    @BindView(R.id.button_apply_filters_quote)
    Button buttonApplyFilter;
    @BindView(R.id.text_total_selected_languages)
    TextView textTotalSelectedLanguages;
    @BindView(R.id.text_total_selected_categories)
    TextView textTotalSelectedCategories;


    private Context mContext;
    private Activity mActivity;
    private AlertDialog.Builder builder;
    private QuoteFilters quoteFilters = new QuoteFilters();
    private ArrayList mSelectedLanguagesIndex = new ArrayList();
    private ArrayList<Category> mSelectedCategories = new ArrayList<Category>();
    private ArrayList<Language> mSelectedLanguages = new ArrayList<Language>();
    private HashMap<String, Language> hashLanguages = new HashMap<>();
    private ArrayList<String> listLanguagesNames = new ArrayList<>();
    private ArrayList<Language> languages = new ArrayList<>();
    private quoteFiltersUpdateListener listener;

    public QuoteFiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters_quote, container, false);
        ButterKnife.bind(this, view);

        updateTotalSelectedLanguagesView();
        updateTotalSelectedCategoriesView();

        initListener();
        return view;
    }

    private void initListener() {

        layoutSelectLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLanguages();
            }
        });
        layoutSelectCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseCategoryIntent = new Intent(mActivity, ChooseCategoryActivity.class);
                chooseCategoryIntent.putExtra(BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW, ChooseCategoryActivity.UNLIMITED);
                chooseCategoryIntent.putExtra(BUNDLE_KEY_SELECTED_CATEGORIES, mSelectedCategories);
                startActivityForResult(chooseCategoryIntent, REQUEST_CODE_CHOOSE_CATEGORIES);

            }
        });
        textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelectedLanguages.clear();
                mSelectedCategories.clear();
                mSelectedLanguagesIndex.clear();

                quoteFilters.setLanguages(null);
                quoteFilters.setCategories(null);
                quoteFilters.setSearchKeyword(null);
                updateTotalSelectedLanguagesView();
                updateTotalSelectedCategoriesView();
                listener.onQuoteFiltersUpdate(quoteFilters);
                closeFragment();

            }
        });
        buttonApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onQuoteFiltersUpdate(quoteFilters);
                closeFragment();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getLanguages() {

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

        // String languageJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_LANGUAGES);

        Type languageListType = new TypeToken<ArrayList<Language>>() {
        }.getType();

        languages = new Gson().fromJson(stringResponse, languageListType);
        showLanguageDialog();
    }

    private void showLanguageDialog() {

        hashLanguages = new HashMap<String, Language>();
        listLanguagesNames = new ArrayList<String>();

        for (Language language : languages) {
            hashLanguages.put(language.getLanguageName(), language);
            listLanguagesNames.add(language.getLanguageName());
        }

        boolean[] checkedLanguages = new boolean[listLanguagesNames.size()];
        Arrays.fill(checkedLanguages, false);

        final ArrayList mSelectedLanguagesIndexTemp = new ArrayList();

        mSelectedLanguagesIndexTemp.addAll(mSelectedLanguagesIndex);

        if (mSelectedLanguagesIndex.size() > 0) {
            for (int i = 0; i < mSelectedLanguagesIndex.size(); i++) {
                int selectedIndex = (int) mSelectedLanguagesIndex.get(i);
                checkedLanguages[selectedIndex] = true;
            }
        }

        final String[] arrayLanguages = listLanguagesNames.toArray(new String[listLanguagesNames.size()]);
        builder = new AlertDialog.Builder(mActivity);

        // Set the alert dialog title
        builder.setTitle(mContext.getString(R.string.text_select_language));

        // Add the choices
        builder.setMultiChoiceItems(arrayLanguages, checkedLanguages, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    mSelectedLanguagesIndexTemp.add(which);
                } else if (mSelectedLanguagesIndexTemp.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    mSelectedLanguagesIndexTemp.remove(Integer.valueOf(which));
                }

            }
        });
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mSelectedLanguagesIndex = new ArrayList();
                mSelectedLanguages = new ArrayList<Language>();

                mSelectedLanguagesIndex.addAll(mSelectedLanguagesIndexTemp);

                ArrayList<String> selectedLanguagesId = new ArrayList<String>();

                for (int i = 0; i < mSelectedLanguagesIndex.size(); i++) {
                    int selectedIndex = (int) mSelectedLanguagesIndex.get(i);
                    String languageName = listLanguagesNames.get(selectedIndex);
                    Language language = hashLanguages.get(languageName);
                    mSelectedLanguages.add(language);
                }
                quoteFilters.setLanguages(mSelectedLanguages);
                updateTotalSelectedLanguagesView();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_CHOOSE_CATEGORIES: {

                if (data.getExtras() != null) {

                    if (data.hasExtra(BUNDLE_KEY_SELECTED_CATEGORIES)) {
                        mSelectedCategories = (ArrayList<Category>) data.getSerializableExtra(BUNDLE_KEY_SELECTED_CATEGORIES);
                        quoteFilters.setCategories(mSelectedCategories);
                        updateTotalSelectedCategoriesView();
                    }

                } else {
                    CommonView.showToast(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                }

                break;
            }
        }
    }

    public void updateTotalSelectedCategoriesView() {
        if (quoteFilters.getCategories() != null) {
            textTotalSelectedCategories.setText(String.format(getString(R.string.text_filter_selected), Integer.toString(quoteFilters.getCategories().size())));
        } else {
            textTotalSelectedCategories.setText(String.format(getString(R.string.text_filter_selected), "0"));
        }
    }

    public void updateTotalSelectedLanguagesView() {
        if (quoteFilters.getLanguages() != null) {
            textTotalSelectedLanguages.setText(String.format(getString(R.string.text_filter_selected), Integer.toString(quoteFilters.getLanguages().size())));
        } else {
            textTotalSelectedLanguages.setText(String.format(getString(R.string.text_filter_selected), "0"));
        }
    }

    public void closeFragment() {
        listener.closeQuoteFiltersFragment();
    }

    public quoteFiltersUpdateListener getListener() {
        return listener;
    }

    public void setListener(quoteFiltersUpdateListener listener) {
        this.listener = listener;
    }
}

