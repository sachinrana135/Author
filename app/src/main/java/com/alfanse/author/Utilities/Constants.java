package com.alfanse.author.Utilities;

/**
 * Created by Velocity-1601 on 5/1/2017.
 */

public class Constants {

    /******************** Bundle Keys reference ************/

    public static final String BUNDLE_KEY_SELECTED_CATEGORIES = "selectedCategories";
    public static final String BUNDLE_KEY_QUOTE = "quote";

    /********  Firebase Reference*******/

    public static final String FIREBASE_REFERENCE_CANVAS_THEMES = "canvas_themes";
    public static final String FIREBASE_REFERENCE_AUTHORS = "authors";
    public static final String TAG_DEFAULT_CANVASE_TEXT_VIEW = "TAG_DEFAULT_CANVASE_TEXT_VIEW";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_FONT = "Helvetica";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_STYLE = "1";//Bold
    public static final String DEFAULT_COMPONENT_TEXTVIEW_COLOR = "#FF000000";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_SIZE = "20";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_LOCATION_X = "30";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_LOCATION_Y = "30";
    public static final String QUOTE_PUBLIC_OUTPUT_DIRECTORY = "Author";
    public static final String QUOTE_OUTPUT_FORMAT = ".JPG";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static final String DEFAULT_APP_NAME_FONT_FILE_NAME = "Helvetica.ttf";
    public final static String PATTERN_EMAIL = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String ASSETS_DIR_FONTS = "fonts/";
    public static final String ASSETS_DIR_JSON = "json/";
    public static final String ASSETS_FILE_FONTS = ASSETS_DIR_JSON + "fonts.json";
    public static final String ASSETS_FILE_CATEGORY = ASSETS_DIR_JSON + "category.json";
    public static final String ASSETS_FILE_AUTHOR = ASSETS_DIR_JSON + "author.json";
    public static final String ASSETS_FILE_FOLLOWERS = ASSETS_DIR_JSON + "followers.json";
    public static int ANIMATION_DELAY_SEC = 500;
}
