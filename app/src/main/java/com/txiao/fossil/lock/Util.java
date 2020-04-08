package com.txiao.fossil.lock;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

/**
 * Created by txiao on 3/13/18.
 */

public class Util {

    private static final String NOTIFICATION_ALERT_PACKAGE = "com.txiao.fossil.notification";
    private static final long UNLOCK_TIME_MILLIS = 20000l;
    private static final int NOTIFICATION_ID = 2;
    private static final String TITLE = "Screen Unlock Action";
    private static final String MESSAGE = "This notification should automatically close";
    public static final String LOCK_TAG = "android-screen-lock:mywakelock";

    private static boolean hasNotificationsBeenCleared = false;

    public static void configure(Context context, NotificationManager mNotificationManager) {
        // The id of the channel.
        String id = "channel_01";
        // The user-visible name of the channel.
        CharSequence name = "Fossil Trigger";
        // The user-visible description of the channel.
        String description = "Fossil Trigger";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mNotificationManager.createNotificationChannel(mChannel);
        Intent pushIntent = new Intent(context, NotificationService.class);
        context.startService(pushIntent);
        pushIntent = new Intent(context, PhoneNotificationListener.class);
        context.startService(pushIntent);
    }

    public static void onNotificationPosted(StatusBarNotification sbn, String packageName) {
        if (NOTIFICATION_ALERT_PACKAGE.equals(sbn.getPackageName())) {
            hasNotificationsBeenCleared = false;
        } else if (packageName.equals(sbn.getPackageName())) {
            hasNotificationsBeenCleared = true;
        }
    }

    public static void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService service) {
        if (!hasNotificationsBeenCleared
                && locked(service)
                && isAllLockScreenNotificationsClosed(sbn, service)) {
            showAndHideNotification(service);
        }
    }

    private static boolean locked(Service service) {
        KeyguardManager km = (KeyguardManager) service.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    public static void hideNotification(Service service) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) service.getApplicationContext().getSystemService(ns);
        nMgr.cancelAll();
        PowerManager.WakeLock lock = getLock(service);
        if (lock.isHeld()) {
            lock.release();
        }
    }

    private static boolean isAllLockScreenNotificationsClosed(StatusBarNotification removedSbn, NotificationListenerService service) {

        for (StatusBarNotification sbn : service.getActiveNotifications()) {
            if (!sbn.equals(removedSbn)) {
                return false;
            }
        }

        return true;
    }

    public static void showAndHideNotification(Service service) {

        PowerManager.WakeLock lock = getLock(service);
        if (!lock.isHeld()) {
            lock.acquire();
        }

        // intent triggered, you can add other intent for other actions
        //Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        //PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new NotificationCompat.Builder(service)

                .setContentTitle(TITLE)
                .setContentText(MESSAGE)
                .setSmallIcon(R.drawable.small_icon)
                //.setContentIntent(pIntent)
                .setChannelId("channel_01")

                .build();

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NotificationService.NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, mNotification);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            Service service;
            @Override
            public void run() {
                hideNotification(service);
            }
            public Runnable init(Service service) {
                this.service = service;
                return this;
            }
        }.init(service), UNLOCK_TIME_MILLIS);
    }

    private static PowerManager.WakeLock getLock(Service service) {
        return ((PowerManager) service.getApplicationContext().getSystemService(Service.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
    }
}
