/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;

import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.Country;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChooseCountryActivity extends BaseActivity {


    @BindView(R.id.spinner_choose_country_choose_country)
    SearchableSpinner spinnerCountries;
    @BindView(R.id.fab_continue_choose_country)
    FloatingActionButton fabContinue;

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Country> mCountries = new ArrayList<>();
    private HashMap<String, String> mHashCountries = new HashMap<>();
    private HashMap<String, String> mReverseHashCountries = new HashMap<>();
    private ArrayList<String> mListCountries = new ArrayList<>();
    private ArrayAdapter<String> mCountryAdapter;

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Country country = new Country();
            country.setCountryName(spinnerCountries.getSelectedItem().toString());
            country.setCountryId(mHashCountries.get(country.getCountryName()));

            Author author = SharedManagement.getInstance(mContext).getLoggedUser();
            author.setCountry(country);
            SharedManagement.getInstance(mContext).setLoggedUser(author);

            updateUserCountry(country);
        }
    };

    private void updateUserCountry(Country country) {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_updating_country), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, SharedManagement.getInstance(mContext).getLoggedUser().getId());
        param.put(Constants.API_PARAM_KEY_COUNTRY_ID, country.getCountryId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_UPDATE_USER_COUNTRY)
                .setParams(param)
                .setMessage("ChooseCountryActivity.java|updateUserCountry")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        Intent homeIntent = new Intent(mActivity, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        Intent homeIntent = new Intent(mActivity, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);

        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = ChooseCountryActivity.this;

        getCountries();
        fabContinue.setOnClickListener(continueClickListener);


    }

    private void getCountries() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_COUNTRIES)
                .setParams(param)
                .setMessage("ChooseCountryActivity.java|getCountries")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetCountriesResponse(stringResponse);
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

    private void parseGetCountriesResponse(String stringResponse) {

        Locale locale = Utils.getInstance(mContext).getCurrentLocale();

        String countryISO3Code = locale.getISO3Country();

        Type countryListType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        mCountries = new Gson().fromJson(stringResponse, countryListType);
        int defaultCountryIndex = -1;
        int index = 0;
        for (Country country : mCountries) {
            mHashCountries.put(country.getCountryName(), country.getCountryId());
            mReverseHashCountries.put(country.getCountryId(), country.getCountryName());
            mListCountries.add(country.getCountryName());
            if (countryISO3Code.equalsIgnoreCase(country.getIsoCode3())) {
                defaultCountryIndex = index;
            }
            index++;
        }
        mCountryAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mListCountries);
        mCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountries.setTitle(getString(R.string.text_select_country));
        spinnerCountries.setAdapter(mCountryAdapter);

        if (defaultCountryIndex != -1) {
            spinnerCountries.setSelection(defaultCountryIndex);
        }


    }


}
