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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alfanse.author.Activities.CommentsActivity;
import com.alfanse.author.Activities.HomeActivity;
import com.alfanse.author.Activities.QuoteActivity;
import com.alfanse.author.BuildConfig;
import com.alfanse.author.Interfaces.bitmapRequestListener;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.Models.FirebaseRemoteMessageData;
import com.alfanse.author.R;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.alfanse.author.Services.MyFirebaseMessagingService.PUSH_TYPE_COMMENT;
import static com.alfanse.author.Services.MyFirebaseMessagingService.PUSH_TYPE_QUOTE;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;
import static com.alfanse.author.Utilities.Constants.QUOTE_SHARE_TEMP_FILE_NAME;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class Utils {

    private static Utils sInstance;
    private static Context mContext;

    private Utils(Context context) {
        mContext = context;
    }

    public static synchronized Utils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Utils(context);
        }
        return sInstance;
    }

    public static String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    /**
     * used to return a random string of 10 characters
     *
     * @return a string of alphanumeric characters
     */
    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            deviceId = "";
        }
        return deviceId;
    }

    public static void logException(Exception exception) {

        if (!BuildConfig.DEBUG) {
            if (Fabric.isInitialized()) {
                //Log user details
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Crashlytics.setUserIdentifier(currentUser.getUid());
                    Crashlytics.setUserEmail(currentUser.getEmail());
                    Crashlytics.setUserName(currentUser.getDisplayName());
                }
                //Log exception
                Crashlytics.logException(exception);
            }
        }
    }

    public static void printFacebookHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static void logInfo(String message) {
        try {
            deleteContentOfLogFileIfAccordingToSize();
            if (BuildConfig.DEBUG) {
                String tabSeprator = "\t";
                String separator = "\r\n";
                message = separator + getCurrentDateTimeForLog() + tabSeprator + message;
                generateLogOnDevice(message);
            }
        } catch (Exception ex) {
            logException(ex);
        }
    }

    public static void deleteContentOfLogFileIfAccordingToSize() {
        File root = new File(mContext.getExternalFilesDir(null).getPath());
        String fileName = Constants.LOG_FILE_NAME;
        File file = new File(root, fileName);
        if (file.exists()) {
            if (file.length() > Constants.LOG_FILE_SIZE) {
                clearContentOfLogFile();
                String message = "Message:";
                message = message + "\t" + "Previous Log Deleted!";
                logInfo(message);
            } else {
            }
        } else {

        }
    }

    public static void clearContentOfLogFile() {
        File root = new File(mContext.getExternalFilesDir(null).getPath());
        String fileName = Constants.LOG_FILE_NAME;
        File file = new File(root, fileName);
        if (file.exists()) {
            try {
                new RandomAccessFile(file, "rw").setLength(0);
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }

    public static void generateLogOnDevice(String message) {
        try {
            File root = new File(mContext.getExternalFilesDir(null).getPath());
            File file = new File(root, Constants.LOG_FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append(message + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logException(e);
        }
    }

    public static String getCurrentDateTimeForLog() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String encodeBase64(String string) {
        byte[] data;
        try {
            data = string.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            logException(e);
            return string;
        }

    }

    public static String decodeBase64(String string) {
        byte[] data;
        try {
            data = Base64.decode(string, Base64.DEFAULT);
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logException(e);
            return string;
        }

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String filePath, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public Drawable getDrawable(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            return ContextCompat.getDrawable(mContext, id);
        } else {
            return mContext.getResources().getDrawable(id);
        }
    }

    public Float pixelToSp(Float px) {
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return mContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return mContext.getResources().getConfiguration().locale;
        }
    }

    public int getColor(int id) {
        return ContextCompat.getColor(mContext, id);
    }

    public int[] getTagColors() {
        return mContext.getResources().getIntArray(R.array.tagColors);
    }

    public int getRandomTagColor() {
        int[] tagColors = this.getTagColors();
        return tagColors[new Random().nextInt(tagColors.length)];
    }

    public String getJsonResponse(String fileName) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;

        /*JSONObject jsonResponseObject = null;
        try {
            jsonResponseObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponseObject;*/
    }

    public JSONObject loadJSONFromAsset(String fontsJsonFileName) {

        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fontsJsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonResponseObject = null;
        try {
            jsonResponseObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponseObject;
    }

    public String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hideSoftKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null && permissions != null) {
            for (String permission : permissions) {
                if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getStringImage(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public long downloadImageToDisk(String url) {

        Uri uri = Uri.parse(url);

        long downloadReference;

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(mContext.getString(R.string.app_name));

        //Setting description of request
        request.setDescription(mContext.getString(R.string.text_loading_downloading_quote));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Utils.getTimeStamp() + Constants.QUOTE_OUTPUT_FORMAT);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    public void getBitmapFromUrl(final String url, final bitmapRequestListener bitmapRequestListener) {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                Bitmap quoteBitmap = null;
                try {
                    quoteBitmap = Glide.with(mContext)
                            .asBitmap()
                            .load(url)
                            .submit()
                            .get();

                    bitmapRequestListener.onSuccess(quoteBitmap);
                } catch (InterruptedException e) {
                    bitmapRequestListener.onError(e);
                } catch (ExecutionException e) {
                    bitmapRequestListener.onError(e);
                }
            }
        });
        thread.start();
    }

    public Uri saveBitmapToDisk(Bitmap bitmap) {

        File file = null;
        try {

            file = new File(mContext.getExternalCacheDir().getAbsolutePath(), QUOTE_SHARE_TEMP_FILE_NAME + Constants.QUOTE_OUTPUT_FORMAT);
            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(mContext,
                mContext.getPackageName().concat(".fileprovider"),
                file);
    }

    public String getAppVersionName() {
        String versionName = "";
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public int getAppVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public void closeApplication() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void goToPlayStore() {

        final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void FirebaseSubscribeTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public static Class<?> getActivityFromStringClassName(String className) {

        String StringClassname = Constants.ACTIVITY_PACKAGE_PATH + "." + className;
        Class<?> c = null;
        if (StringClassname != null) {
            try {
                c = Class.forName(StringClassname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    public Intent getFirebaseMessageTargetIntent(FirebaseRemoteMessageData fcmData) {
        Intent intent;
        if (fcmData.getPushType() != null) {
            if (fcmData.getPushType().equalsIgnoreCase(PUSH_TYPE_QUOTE)) {
                intent = new Intent(mContext, QuoteActivity.class);
                intent.putExtra(BUNDLE_KEY_QUOTE_ID, fcmData.getQuoteId());
            } else if (fcmData.getPushType().equalsIgnoreCase(PUSH_TYPE_COMMENT)) {
                intent = new Intent(mContext, CommentsActivity.class);
                CommentFilters commentFilters = new CommentFilters();
                commentFilters.setQuoteID(fcmData.getQuoteId());
                intent.putExtra(Constants.BUNDLE_KEY_COMMENTS_FILTERS, commentFilters);
            } else {
                intent = new Intent(mContext, HomeActivity.class);
            }
        } else {
            if (fcmData.getTargetActivity() != null) {
                intent = new Intent(mContext, Utils.getActivityFromStringClassName(fcmData.getTargetActivity()));
            } else {
                intent = new Intent(mContext, HomeActivity.class);
            }
        }

        intent.putExtra(Constants.BUNDLE_KEY_CAME_VIA_NOTIFICATION, true);

        return intent;
    }
}
