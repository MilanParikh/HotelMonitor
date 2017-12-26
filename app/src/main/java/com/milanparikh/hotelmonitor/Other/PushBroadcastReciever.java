package com.milanparikh.hotelmonitor.Other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by milan on 8/7/2017.
 */

public class PushBroadcastReciever extends ParsePushBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alert = new Intent(context, NotificationAlert.class);
        alert.putExtras(intent.getExtras());
        context.getApplicationContext().startActivity(alert);
    }
}
