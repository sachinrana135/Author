/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;

import com.alfanse.author.Models.Font;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static com.alfanse.author.Utilities.Constants.ASSETS_DIR_FONTS;
import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_FONTS;

/**
 * Created by Velocity-1601 on 5/10/2017.
 */

public class FontHelper {

    public static final HashMap<String, Font> fontsHashMap = new HashMap<String, Font>();
    public static final ArrayList<Font> fontsArrayList = new ArrayList<Font>();
    private static FontHelper sInstance;
    private static Context mContext;


    public FontHelper(Context context) {
        mContext = context;
    }

    public static synchronized FontHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FontHelper(context);
            initFonts();
        }
        return sInstance;
    }

    private static void initFonts() {

        fontsHashMap.clear();
        fontsArrayList.clear();

        JSONObject jsonObject = Utils.getInstance(mContext).loadJSONFromAsset(ASSETS_FILE_FONTS);
        JSONArray jsonFontsArray = null;
        try {
            jsonFontsArray = jsonObject.getJSONArray("fonts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonFontsArray.length(); i++) {
            try {

                JSONObject jsonFontObject = jsonFontsArray.getJSONObject(i);

                Font font = new Font();
                font.setFontName(jsonFontObject.getString("fontName"));
                font.setFontFileName(jsonFontObject.getString("fileName"));
                font.setFontTypeface(getTypeface(mContext, font.getFontFileName()));

                fontsHashMap.put(font.getFontName(), font);
                fontsArrayList.add(font);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Typeface getTypeface(Context context, String fileName) {
        return Typeface.createFromAsset(context.getAssets(), ASSETS_DIR_FONTS.concat(fileName));
    }

    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     */
    public static void overrideFont() {

        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_FONT_FILE_NAME));

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(Constants.DEFAULT_FONT_NAME_TO_OVERRIDE);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e("Font error", "Can not set custom font " + Constants.APP_CUSTOM_FONT_FILE_NAME + " instead of " + Constants.DEFAULT_FONT_NAME_TO_OVERRIDE);
        }
    }

    public static SpannableString getCustomTypefaceTitle(String titleString) {
        SpannableString title = new SpannableString(titleString);
        // Update the action bar title with the TypefaceSpan instance
        title.setSpan(new TypefaceSpan(Constants.ASSETS_DIR_FONTS + Constants.APP_CUSTOM_FONT_FILE_NAME), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return title;
    }

    public Typeface getAppCustomTypeface() {
        return Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_FONT_FILE_NAME));
    }

    public Typeface getAppCustomLightTypeface() {
        return Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_LIGHT_FONT_FILE_NAME));
    }

    public Typeface getAppCustomMediumTypeface() {
        return Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_MEDIUM_FILE_NAME));
    }

    public Typeface getAppCustomRegularTypeface() {
        return Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_REGULAR_FONT_FILE_NAME));
    }

    public Typeface getAppCustomBoldTypeface() {
        return Typeface.createFromAsset(mContext.getAssets(), ASSETS_DIR_FONTS.concat(Constants.APP_CUSTOM_BOLD_FONT_FILE_NAME));
    }

    public HashMap<String, Font> getFontsHashMap() {
        return fontsHashMap;
    }

    public ArrayList<Font> getFontsArrayList() {
        return fontsArrayList;
    }
}
