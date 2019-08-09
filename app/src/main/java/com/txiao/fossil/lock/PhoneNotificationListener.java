package com.txiao.fossil.lock;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by txiao on 12/14/16.
 */

public class PhoneNotificationListener extends NotificationListenerService {

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
