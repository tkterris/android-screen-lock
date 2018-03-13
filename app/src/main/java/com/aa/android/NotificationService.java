package com.aa.android;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by txiao on 12/14/16.
 */

public class NotificationService extends Service {

    private static boolean DEBUG = true;
    private static final long UNLOCK_TIME_MILLIS = 20000l;
    private static final int NOTIFICATION_ID = 2;
    private static final String TITLE = "Screen Unlock Action";
    private static final String MESSAGE = "This notification should automatically close";

    private boolean hasShownAfterLock = false;

    private BroadcastReceiver mPowerKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                //if the user unlocks the phone, remove notifications
                hasShownAfterLock = false;
                hideNotification();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action) && !hasShownAfterLock && locked()) {
                //if the phone is locked and is waiting to show,
                //show the notification
                showAndHideNotification();
                hasShownAfterLock = true;
            } else if (Intent.ACTION_SCREEN_ON.equals(action) && !DEBUG) {
                //hide notificate if screen turns off, and not debuggging
                hideNotification();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        configureNotifications();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
    }

    public void configureNotifications() {
        final IntentFilter screenFilter = new IntentFilter();
        /** System Defined Broadcast */
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        getApplicationContext().registerReceiver(mPowerKeyReceiver, screenFilter);
    }

    private void showAndHideNotification() {

        // intent triggered, you can add other intent for other actions
        //Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        //PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new NotificationCompat.Builder(this)

                .setContentTitle(TITLE)
                .setContentText(MESSAGE)
                .setSmallIcon(R.drawable.small_icon)
                //.setContentIntent(pIntent)
                .setChannelId("channel_01")

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

    private void hideNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancelAll();
    }

    private boolean locked() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }
}
