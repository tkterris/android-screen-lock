package com.txiao.fossil.lock;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

/**
 * Created by txiao on 12/14/16.
 */

public class LockedNotificationTimerService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Util.logicForNotificationTimer(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
