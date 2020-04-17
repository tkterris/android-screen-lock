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
import android.util.Log;

/**
 * Created by txiao on 12/14/16.
 */

public class PhoneLockService extends Service {

    private boolean hasShownAfterLock = false;

    private BroadcastReceiver mPowerKeyReceiver = new LockBroadcastReceiver(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
            Log.v("Unregister", "Previous receiver unregistered.");
        } catch (Exception e) {
            Log.v("Unregister", "Receiver already unregistered, skipping.");
        }
        configureNotifications();
        return START_STICKY;
    }

    public void configureNotifications() {
        final IntentFilter screenFilter = new IntentFilter();
        /** System Defined Broadcast */
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
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
            }
        }
    }
}
