package com.aa.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Util.configure(this.getApplicationContext(), mNotificationManager);

        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
    }
}
