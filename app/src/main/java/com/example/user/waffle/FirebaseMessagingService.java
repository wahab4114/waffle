package com.example.user.waffle;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by wahab on 7/18/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String NotificationTitle = remoteMessage.getNotification().getTitle();
        String NotificationBody = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();
        String frmusrid = remoteMessage.getData().get("from_user_id");

        Log.e("",clickAction+"----------------------->");


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(NotificationTitle)
                        .setContentText(NotificationBody);

        Intent resultIntent = new Intent(clickAction.toString());
        resultIntent.putExtra("user_id",frmusrid);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);



        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}
