package com.example.spomoo.questionnaire;

/*
 * QuestionnaireReminderTimer of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.spomoo.utility.SharedPrefManager;

import java.util.Calendar;

/*
 * Schedules the QuestionnaireReminder notifications in the AlarmManager based on the settings for amount, first and last time
 * Can cancel all schedules notifications
 */
public class QuestionnaireReminderTimer {

    //private attributes
    private Context context;
    private Intent intent;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private SharedPrefManager sharedPrefManager;
    private int ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    //constructor
    public QuestionnaireReminderTimer (Context context) {
        this.context = context;
        this.intent = new Intent(context, QuestionnaireReminder.class);
        this.calendar = Calendar.getInstance();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.sharedPrefManager = SharedPrefManager.getInstance(context);
    }

    //schedules the QuestionnaireReminder notifications in the AlarmManager based on the settings for amount, first and last time for today and next day
    public void setNotifications() {
        //load settings
        int amount = (int) sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_AMOUNT);
        int firstTime = (int) sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_FIRST);
        int lastTime = (int) sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_LAST);

        //if no reminder
        if(amount == 0)
            return;

        //declare pending intent
        PendingIntent pendingIntent;

        //set calendar hour to first time
        calendar.set(Calendar.HOUR_OF_DAY, firstTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //if one reminder
        if(amount == 1){
            //set alarms
            //notification for next day
            pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + ONE_DAY_IN_MILLIS, pendingIntent);
            //notification for today
            if (System.currentTimeMillis() > (calendar.getTimeInMillis())) return;  //do not set past notifications again
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            return;
        }

        //if more than 1 reminder
        //calculate time in milliseconds between the alarms
        int timeRange = lastTime - firstTime;
        timeRange = timeRange * 60 * 60 * 1000;
        timeRange = timeRange / (amount-1);
        //set alarms
        for(int i = 0; i  < amount; i++) {
            //notification for next day
            pendingIntent = PendingIntent.getBroadcast(context, (i+amount), intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + i * timeRange + ONE_DAY_IN_MILLIS, pendingIntent);
            //notification for today
            if (System.currentTimeMillis() > (calendar.getTimeInMillis() + i * timeRange)) continue;    //do not set past notifications again
            pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + i * timeRange, pendingIntent);
        }
    }

    //cancels all notifications scheduled in the alarm manager
    public void cancelAllNotifications() {
        Intent intent = new Intent(context, QuestionnaireReminder.class);
        PendingIntent pendingIntent;

        //create pending intents 20 times (max value of variable 'amount' multiplied by 2) to cancel all alarms
        for(int i = 0; i  <= 19; i++) {
            pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }

}
