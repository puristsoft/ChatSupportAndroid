package com.puristit.livechat.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.puristit.livechat.R;
import com.puristit.livechat.activities.MainActivity;
import com.puristit.livechat.utils.MyUtils;

import org.json.JSONObject;

import java.util.Map;

import puristit.com.library.PurisitChat;

/**
 * Created by Anas on 3/18/2018.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    private Context mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
//        String message = remoteMessage.getData("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);
//

        if (MyUtils.getSharedPrefString(this, "UserName", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "Password", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "PuristKey", "").isEmpty()) {
            return;
        }

        sendNotification(object);
    }


    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param jsonObject GCM message received.
     */
    private void sendNotification(JSONObject jsonObject) {
        Intent intent;
        try {

            String room = jsonObject.optString("pcs_room");
            String notiMsg = jsonObject.optString("alert");
            long roomId = jsonObject.optLong("room_id");

            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Room_ID", roomId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Purist Message")
                    .setContentText(notiMsg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception ignored) {

        }
    }

}
