package com.alfanse.author.Utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alfanse.author.Interfaces.bitmapRequestListener;
import com.alfanse.author.R;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

import static android.content.Context.DOWNLOAD_SERVICE;
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
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
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
        return Uri.fromFile(file);

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
}
