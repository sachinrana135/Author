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

package com.alfanse.author.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.alfanse.author.Activities.AppUpgradeActivity;
import com.alfanse.author.Activities.SignInActivity;
import com.alfanse.author.Models.FirebaseRemoteMessageData;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTO_UPGRADE;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public final static String PUSH_TYPE_QUOTE = "quote";
    public final static String PUSH_TYPE_COMMENT = "comment";
    public final static String PUSH_TYPE_AUTHOR = "author";
    public final static String PUSH_APP_UPGRADE = "app_upgrade";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData());
        }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data
     */

    private void sendNotification(Map<String, String> data) {

        FirebaseRemoteMessageData FCMData = new Gson().fromJson(new JSONObject(data).toString(), FirebaseRemoteMessageData.class);

        //To get a Bitmap image from the URL received
        Bitmap image = null;
        if (FCMData.getImageUrl() != null && !FCMData.getImageUrl().isEmpty()) {
            image = getBitmapfromUrl(FCMData.getImageUrl());
        }
        String title = FCMData.getTitle();
        String message = FCMData.getMessage();

        Intent intent;

        if (FCMData.getPushType() != null && FCMData.getPushType().equals(PUSH_APP_UPGRADE)) {
            if (Integer.parseInt(FCMData.getAppLiveVersionCode()) > Utils.getInstance(getApplicationContext()).getAppVersionCode()) {
                intent = new Intent(getApplicationContext(), AppUpgradeActivity.class);
                intent.putExtra(BUNDLE_KEY_AUTO_UPGRADE, FCMData.isAutoUpgrade());
            } else {
                return;
            }
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            intent = Utils.getInstance(getApplicationContext()).getFirebaseMessageTargetIntent(FCMData);
        } else {
            intent = new Intent(this, SignInActivity.class);
            intent.putExtra(Constants.BUNDLE_KEY_FCM_MESSAGE_DATA, FCMData);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))/*Notification icon image*/
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (image != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(image));/*Notification with Image*/
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}
