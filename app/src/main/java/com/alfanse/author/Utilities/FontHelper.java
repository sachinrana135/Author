package com.alfanse.author.Utilities;

import android.content.Context;
import android.graphics.Typeface;

import com.alfanse.author.Models.Font;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public HashMap<String, Font> getFontsHashMap() {
        return fontsHashMap;
    }

    public ArrayList<Font> getFontsArrayList() {
        return fontsArrayList;
    }
}
