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

package com.alfanse.author.Utilities;

import com.alfanse.author.BuildConfig;

/**
 * Created by Velocity-1601 on 5/1/2017.
 */

public class Constants {

    /******************** Bundle Keys reference ************/

    public static final String BUNDLE_KEY_SELECTED_CATEGORIES = "selectedCategories";
    public static final String BUNDLE_KEY_QUOTE = "quote";
    public static final String BUNDLE_KEY_QUOTE_ID = "quoteID";
    public static final String BUNDLE_KEY_AUTHOR_ID = "authorID";
    public static final String BUNDLE_KEY_CATEGORY_ID = "categoryID";
    public static final String BUNDLE_KEY_LANGUAGE_ID = "languageID";
    public static final String BUNDLE_KEY_SEARCH_KEYWORD = "searchKeyword";
    public static final String BUNDLE_KEY_MAXIMUM_CATEGORY_SELECT_ALLOW = "maximumCategorySelectAllow";
    public static final String BUNDLE_KEY_TITLE = "title";
    public static final String BUNDLE_KEY_QUOTES_FILTERS = "quoteFilters";
    public static final String BUNDLE_KEY_AUTHORS_FILTERS = "authorFilters";
    public static final String BUNDLE_KEY_COMMENTS_FILTERS = "commentFilters";
    public static final String BUNDLE_KEY_FCM_MESSAGE_DATA = "fcmData";
    public static final String BUNDLE_KEY_CAME_VIA_NOTIFICATION = "viaNotification";
    public static final String BUNDLE_KEY_FORCE_UPGRADE = "isForceUpgrade";
    public static final String BUNDLE_KEY_AUTO_UPGRADE = "isAutoUpgrade";

    public static final String TAG_DEFAULT_CANVASE_TEXT_VIEW = "TAG_DEFAULT_CANVASE_TEXT_VIEW";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_FONT = "Helvetica";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_STYLE = "1";//Bold
    public static final String DEFAULT_COMPONENT_TEXTVIEW_COLOR = "#FF000000";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_SIZE = "20";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_LOCATION_X = "30";
    public static final String DEFAULT_COMPONENT_TEXTVIEW_LOCATION_Y = "30";
    public static final int QUOTE_HEIGHT = 500;
    public static final int QUOTE_WIDTH = 500;
    public static final int QUOTE_QUALITY = 100;
    public static final String QUOTE_PUBLIC_OUTPUT_DIRECTORY = "Author";
    public static final String QUOTE_SHARE_TEMP_FILE_NAME = "Author_App_Quote";
    public static final String QUOTE_OUTPUT_FORMAT = ".JPG";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    public static final String QUOTE_FILTER_TYPE_LATEST = "latest";
    public static final String QUOTE_FILTER_TYPE_TRENDING = "trending";
    public static final String QUOTE_FILTER_TYPE_POPULAR = "popular";
    public static final String QUOTE_FILTER_TYPE_FEED = "feed";
    public static final String AUTHOR_FILTER_TYPE_FOLLOWING = "following";
    public static final String AUTHOR_FILTER_TYPE_FOLLOWER = "follower";
    public static final String AUTHOR_FILTER_TYPE_QUOTE_LIKED_BY = "quoteLikedBy";

    public static final String APP_CUSTOM_LIGHT_FONT_FILE_NAME = "DIN-Light.otf";
    public static final String APP_CUSTOM_MEDIUM_FILE_NAME = "DIN-Medium.otf";
    public static final String APP_CUSTOM_REGULAR_FONT_FILE_NAME = "DIN-Regular.otf";
    public static final String APP_CUSTOM_BOLD_FONT_FILE_NAME = "DIN-Bold.otf";
    public static final String APP_CUSTOM_FONT_FILE_NAME = APP_CUSTOM_LIGHT_FONT_FILE_NAME;
    public static final String DEFAULT_FONT_NAME_TO_OVERRIDE = "SERIF";
    public static final String LOG_FILE_NAME = "author_log.txt";
    public static final int LOG_FILE_SIZE = 2097152; // 2mb(2*1024*1024)
    public static final String ENGLISH_LANGUAGE_CODE = "en"; // 2mb(2*1024*1024)

    public final static String PATTERN_EMAIL = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String ASSETS_DIR_FONTS = "fonts/";
    public static final String ASSETS_DIR_JSON = "json/";
    public static final String ASSETS_FILE_FONTS = ASSETS_DIR_JSON + "fonts.json";

    /*public static final String ASSETS_FILE_CATEGORY = ASSETS_DIR_JSON + "category.json";
    public static final String ASSETS_FILE_AUTHOR = ASSETS_DIR_JSON + "author.json";
    public static final String ASSETS_FILE_AUTHORS = ASSETS_DIR_JSON + "authors.json";
    public static final String ASSETS_FILE_QUOTES = ASSETS_DIR_JSON + "quotes.json";
    public static final String ASSETS_FILE_COMMENTS = ASSETS_DIR_JSON + "comments.json";
    public static final String ASSETS_FILE_LANGUAGES = ASSETS_DIR_JSON + "languages.json";
    public static final String ASSETS_FILE_COUNTRIES = ASSETS_DIR_JSON + "countries.json";
    public static final String ASSETS_FILE_QUOTE = ASSETS_DIR_JSON + "quote.json";
    public static final String ASSETS_FILE_REPORTS = ASSETS_DIR_JSON + "report_reasons.json";
    public static final String ASSETS_FILE_CANVAS_THEMES = ASSETS_DIR_JSON + "canvas_themes.json";*/


    /***************************** API Url **************************/
    public static final String WEB_BASE_URL = BuildConfig.WEB_BASE_URL;
    public static final String API_BASE_URL = BuildConfig.API_BASE_URL;
    public static final String API_TOKEN = BuildConfig.API_TOKEN;
    public static final String API_URL_GET_QUOTES = API_BASE_URL + "getQuotes";
    public static final String API_URL_GET_QUOTE = API_BASE_URL + "getQuote";
    public static final String API_URL_GET_COMMENTS = API_BASE_URL + "getComments";
    public static final String API_URL_GET_AUTHORS = API_BASE_URL + "getAuthors";
    public static final String API_URL_GET_AUTHOR = API_BASE_URL + "getAuthor";
    public static final String API_URL_GET_CANVAS_THEMES = API_BASE_URL + "getCanvasThemes";
    public static final String API_URL_GET_CATEGORIES = API_BASE_URL + "getCategories";
    public static final String API_URL_GET_COUNTRIES = API_BASE_URL + "getCountries";
    public static final String API_URL_GET_LANGUAGES = API_BASE_URL + "getLanguages";
    public static final String API_URL_GET_REPORT_REASONS = API_BASE_URL + "getReportReasons";
    public static final String API_URL_SAVE_AUTHOR = API_BASE_URL + "saveAuthor";
    public static final String API_URL_SAVE_QUOTE = API_BASE_URL + "saveQuote";
    public static final String API_URL_SAVE_COMMENT = API_BASE_URL + "saveComment";
    public static final String API_URL_REPORT_QUOTE = API_BASE_URL + "reportQuote";
    public static final String API_URL_DELETE_QUOTE = API_BASE_URL + "deleteQuote";
    public static final String API_URL_REPORT_COMMENT = API_BASE_URL + "reportComment";
    public static final String API_URL_FOLLOW_AUTHOR = API_BASE_URL + "followAuthor";
    public static final String API_URL_LIKE_QUOTE = API_BASE_URL + "likeQuote";
    public static final String API_URL_UPDATE_AUTHOR = API_BASE_URL + "updateAuthor";
    public static final String API_URL_UPDATE_COVER_IMAGE = API_BASE_URL + "updateCoverImage";
    public static final String API_URL_UPDATE_PROFILE_IMAGE = API_BASE_URL + "updateProfileImage";
    public static final String API_URL_UPDATE_USER_COUNTRY = API_BASE_URL + "updateUserCountry";
    public static final String API_URL_GET_USER_FEED = API_BASE_URL + "getUserFeed";
    public static final String API_URL_MAP_FCM_ID = API_BASE_URL + "mapFcmIdToUser";
    public static final String API_URL_GET_STARTUP_CONFIG = API_BASE_URL + "getStartUpConfig";

    public static final String WEB_URL_UPGRADE_APP = WEB_BASE_URL + "upgradeApp";

    /***************************** API param keys  **************************/
    public static final String API_PARAM_KEY_QUOTE_FILTERS = "quoteFilters";
    public static final String API_PARAM_KEY_COMMENT_FILTERS = "commentFilters";
    public static final String API_PARAM_KEY_AUTHOR_FILTERS = "authorFilters";
    public static final String API_PARAM_KEY_PAGE = "page";
    public static final String API_PARAM_KEY_AUTHOR = "author";
    public static final String API_PARAM_KEY_COMMENT = "comment";
    public static final String API_PARAM_KEY_QUOTE = "quote";
    public static final String API_PARAM_KEY_AUTHOR_ID = "authorId";
    public static final String API_PARAM_KEY_COUNTRY_ID = "countryId";
    public static final String API_PARAM_KEY_PROFILE_IMAGE = "profileImage";
    public static final String API_PARAM_KEY_COVER_IMAGE = "coverImage";
    public static final String API_PARAM_KEY_QUOTE_IMAGE = "quoteImage";
    public static final String API_PARAM_KEY_REPORT_ID = "reportId";
    public static final String API_PARAM_KEY_COMMENT_ID = "commentId";
    public static final String API_PARAM_KEY_QUOTE_ID = "quoteId";
    public static final String API_PARAM_KEY_FCM_ID = "fcmId";
    public static final String API_PARAM_KEY_LOGGED_AUTHOR_ID = "loggedAuthorId";
    public static final String API_HEADER_PARAM_KEY_DEVICE_ID = "deviceId";
    public static final String API_HEADER_PARAM_KEY_CORRELATION_ID = "correlationId";
    public static final String API_HEADER_PARAM_KEY_API_TOKEN = "apiToken";
    public static final String API_HEADER_PARAM_KEY_CALL_SOURCE = "callSource";
    public static final String API_HEADER_PARAM_KEY_APP_VERSION_NAME = "appVersionName";
    public static final String API_HEADER_PARAM_KEY_APP_VERSION_CODE = "appVersionCode";
    public static int ANIMATION_TOTAL_DURATION = 1000;
    public static int ANIMATION_CYCLE_DURATION = 500;

    public static String SUPPORT_EMAIL = "support@alfanse.com";
    public static int MINIMUM_STAR_RATING = 4;

    public static String NOTIFICATION_CHANNEL = "AUTHOR_CHANNEL_ID";
    public static String FIREBASE_SUBSCRIPTION_TOPIC_ANDROID_USERS = "ANDROID_USERS";

    public static String ACTIVITY_PACKAGE_PATH = "com.alfanse.author.Activities";
}
