package com.txiao.fossil.lock;

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

    private static final long UNLOCK_TIME_MILLIS = 20000l;
    private static final int NOTIFICATION_ID = 2;
    private static final String TITLE = "Screen Unlock Action";
    private static final String MESSAGE = "This notification should automatically close";

    boolean hasNotificationsBeenCleared = false;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Util.onNotificationPosted(sbn, this.getPackageName());
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Util.onNotificationRemoved(sbn, this);
        super.onNotificationRemoved(sbn);
    }
}
