package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.Country;
import com.alfanse.author.R;
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

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_COUNTRIES;

public class ChooseCountryActivity extends AppCompatActivity {


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
            country.setCountryId(mHashCountries.get(country.getCountryId()));

            Author author = SharedManagement.getInstance(mContext).getLoggedUser();
            author.setCountry(country);
            SharedManagement.getInstance(mContext).setLoggedUser(author);

            //TODO update country API

            Intent homeIntent = new Intent(mActivity, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);

        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = ChooseCountryActivity.this;

        fillCountrySpinner();
        fabContinue.setOnClickListener(continueClickListener);


    }

    private void fillCountrySpinner() {

        //TODO get language API

        Locale locale = Utils.getInstance(mContext).getCurrentLocale();

        String countryISO3Code = locale.getISO3Country();

        String countryJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_COUNTRIES);

        Type countryListType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        mCountries = new Gson().fromJson(countryJson, countryListType);

        for (Country country : mCountries) {
            mHashCountries.put(country.getCountryName(), country.getIsoCode3());
            mReverseHashCountries.put(country.getIsoCode3(), country.getCountryName());
            mListCountries.add(country.getCountryName());
        }
        mCountryAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mListCountries);
        mCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountries.setTitle(getString(R.string.text_select_country));
        spinnerCountries.setAdapter(mCountryAdapter);

        String defaultLanguage = mReverseHashCountries.get(countryISO3Code);

        int index = mListCountries.indexOf(defaultLanguage);

        if (index != -1) {
            spinnerCountries.setSelection(index);
        }

    }


}
