package com.aa.android;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by txiao on 12/14/16.
 */

public class PhoneNotificationListener extends NotificationListenerService {

    private static final String NOTIFICATION_ALERT_PACKAGE = "com.united.mobile.android";
    private static final long UNLOCK_TIME_MILLIS = 20000l;
    private static final int NOTIFICATION_ID = 2;
    private static final String TITLE = "Screen Unlock Action";
    private static final String MESSAGE = "This notification should automatically close";

    boolean hasNotificationsBeenCleared = false;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (NOTIFICATION_ALERT_PACKAGE.equals(sbn.getPackageName())) {
            hasNotificationsBeenCleared = false;
        } else if (this.getPackageName().equals(sbn.getPackageName())) {
            hasNotificationsBeenCleared = true;
        }
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        if (!hasNotificationsBeenCleared
                && locked()
                && isAllLockScreenNotificationsClosed(sbn)) {
            showAndHideNotification();
        }
        super.onNotificationRemoved(sbn);
    }

    private boolean locked() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    private void hideNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancelAll();
    }

    private boolean isAllLockScreenNotificationsClosed(StatusBarNotification removedSbn) {

        for (StatusBarNotification sbn : getActiveNotifications()) {
            if (!sbn.equals(removedSbn) && sbn.getNotification().priority >= Notification.PRIORITY_LOW) {
                return false;
            }
        }

        return true;
    }

    private void showAndHideNotification() {

        // intent triggered, you can add other intent for other actions
        //Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        //PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle(TITLE)
                .setContentText(MESSAGE)
                .setSmallIcon(R.drawable.small_icon)
                //.setContentIntent(pIntent)

                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, mNotification);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNotification();
            }
        }, UNLOCK_TIME_MILLIS);
    }
}
