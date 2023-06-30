package com.example.spomoo.questionnaire;

/*
 * QuestionnaireReminder of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.spomoo.R;

/*
 * Sets up a push notification to remind the user to do a questionnaire
 * When clicking on the push notification the QuestionnaireActivity will be opened
 */
public class QuestionnaireReminder extends BroadcastReceiver {

    public static final String CHANNEL_ID = "QuestionnaireNotificationChannelID";   //notification channel id
    private final int notificationID = 2;

    public void onReceive(Context context, Intent intent) {
        //create notification channel
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.questionnaire_reminder_channel),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        //initialise intent to open QuestionnaireActivity
        Intent notificationIntent = new Intent(context, Questionnaire_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        //define all parts of push notification (icon, title, description text, priority, channel ID)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getString(R.string.questionnaire_reminder_title))
                .setContentText(context.getString(R.string.questionnaire_reminder_content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //set listener for notification on ID 2
        notificationManager.notify(notificationID, builder.build());
    }
}
