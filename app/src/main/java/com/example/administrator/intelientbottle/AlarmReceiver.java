package com.example.administrator.intelientbottle;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2018/12/21.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
         NotificationManager mNManager;
        Toast.makeText(context, "够钟啦，够钟啦~", Toast.LENGTH_LONG).show();
        mNManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("已到定时时间~").setContentText("IntelientBottle").setSmallIcon(R.drawable.bottle)
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.bottle).setTicker("IntelientBottle发来一条新消息").build();
        mNManager.notify(1, notification);
    }
}
