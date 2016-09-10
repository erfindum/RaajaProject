package com.example.raaja.applockui;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by RAAJA on 08-09-2016.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationLockSample extends NotificationListenerService {

    private static boolean notificationServiceConnected = false;


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
            String packageName = sbn.getPackageName();
        if(!sbn.isOngoing()){
        Log.d("PatternLock","Notificaion canceled" + sbn.getPackageName());
        }
            if (packageName.equals("com.android.messaging")) {
                Log.d("PatternLock","Notificaion canceled" + sbn.getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String key = sbn.getKey();

                } else {
                    String tag = sbn.getTag();
                    int id = sbn.getId();

                }
            }
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        Log.d("PatternLock","Notificaion canceled 21" + sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        Log.d("PatternLock","Notificaion Removed 21" + sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Log.d("PatternLock","Notificaion Removed" + sbn.getPackageName());
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        NotificationLockSample.notificationServiceConnected = true;
        Log.d("PatternLock","Listener Connected");
    }


}
