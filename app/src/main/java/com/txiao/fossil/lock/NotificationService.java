package com.txiao.fossil.lock;

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

    private BroadcastReceiver mPowerKeyReceiver = new LockBroadcastReceiver(this);

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

    private boolean locked() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    private class LockBroadcastReceiver extends BroadcastReceiver {

        private Service service;

        private LockBroadcastReceiver(Service service) {
            super();
            this.service = service;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                //if the user unlocks the phone, remove notifications
                hasShownAfterLock = false;
                Util.hideNotification(service);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action) && !hasShownAfterLock && locked()) {
                //if the phone is locked and is waiting to show,
                //show the notification
                Util.showAndHideNotification(service);
                hasShownAfterLock = true;
            } else if (Intent.ACTION_SCREEN_ON.equals(action) && !DEBUG) {
                //hide notificate if screen turns off, and not debuggging
                Util.hideNotification(service);
            }
        }
    }
}
