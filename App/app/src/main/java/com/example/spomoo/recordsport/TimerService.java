package com.example.spomoo.recordsport;

/*
 * TimerService of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.spomoo.R;
import com.example.spomoo.utility.ConstantsBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/*
 * Timer Service for tracking a timer in a foreground activity
 * Create notification with current timer value
 */
public class TimerService extends Service {

    private int time = 0;   //timer value
    private Timer timer;    //timer for tracking time

    //notification for foreground service
    private final String CHANNEL_ID = "TimerServiceNotificationChannelID";
    private final int notificationID = 1;
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create notification channel
        createNotificationChannel();

        //create new timer which runs every seconds
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++; //increase timer value
                updateNotification();   //update notification with new timer value
                //create intent which stores current timer value and send it via broadcast to Record_Live_Activity
                Intent timeIntent = new Intent();
                timeIntent.setAction(ConstantsBroadcast.BROADCAST_TIME);
                timeIntent.putExtra(ConstantsBroadcast.BROADCAST_TIME_VALUE, getTimerText());
                sendBroadcast(timeIntent);
            }
        }, 0,1000);

        //start service in foreground
        startForeground(notificationID, createNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    //get hh:mm:ss of timer
    public String getTimerText(){
        int rounded = (int) Math.round(time);
        int second = ((rounded % 86400) % 3600) % 60;
        int minute = ((rounded % 86400) % 3600) / 60;
        int hour = (rounded % 86400) / 3600;
        return String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" +String.format("%02d", second);
    }

    //create notification channel
    private void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.record_live_notification_channel),
                NotificationManager.IMPORTANCE_NONE);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //create notification containing current timer value
    private Notification createNotification(){
        //open Record_Live_Activity on click on notification
        Intent notificationIntent = new Intent(this, Record_Live_Activity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        //setup notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.main_record_heading_1))
                .setContentText(getString(R.string.record_live_notification_content) +": " + getTimerText())
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    //update notification with current timer
    private void updateNotification(){
        Notification notification = createNotification();
        notificationManager.notify(notificationID, notification);
    }

    //stop all schedulers at termination of service
    @Override
    public void onDestroy() {
        timer.cancel();
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

}
