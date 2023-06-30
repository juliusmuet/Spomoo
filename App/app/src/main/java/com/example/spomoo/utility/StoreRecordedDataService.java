package com.example.spomoo.utility;

/*
 * StoreRecordedDataService of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.spomoo.R;
import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/*
 * Foreground service which runs commands to insert the recorded data received from the server
 * Updates notification based on progress
 */
public class StoreRecordedDataService extends Service {

    //notification for foreground service
    private final String CHANNEL_ID = "StoreDataNotificationChannelID";
    private final int notificationID = 5;
    private NotificationManager notificationManager;
    private int progress = 0;   //store progress

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create notification channel
        createNotificationChannel();

        //get data array lists
        Gson gson = new Gson();
        //accelerometer
        Type type = new TypeToken<ArrayList<AccelerometerData>>() {}.getType();
        ArrayList<AccelerometerData> accelerometer = gson.fromJson(intent.getStringExtra("accelerometer"), type);
        //rotation
        type = new TypeToken<ArrayList<RotationData>>() {}.getType();
        ArrayList<RotationData> rotation  = gson.fromJson(intent.getStringExtra("rotation"), type);
        //rotation
        type = new TypeToken<ArrayList<StepsData>>() {}.getType();
        ArrayList<StepsData> steps  = gson.fromJson(intent.getStringExtra("steps"), type);
        //sport
        type = new TypeToken<ArrayList<SportData>>() {}.getType();
        ArrayList<SportData> sport  = gson.fromJson(intent.getStringExtra("sport"), type);
        //questionnaire
        type = new TypeToken<ArrayList<QuestionnaireData>>() {}.getType();
        ArrayList<QuestionnaireData> questionnaire  = gson.fromJson(intent.getStringExtra("questionnaire"), type);

        //insert data in local SQL database
        LocalDatabaseManager db = new LocalDatabaseManager(this);
        for(AccelerometerData a : accelerometer) db.addAccelerometerData(a, 1);
        updateNotification();
        for(RotationData r : rotation) db.addRotationData(r, 1);
        updateNotification();
        for(StepsData s : steps) db.addStepsData(s, 1);
        updateNotification();
        for(SportData sp : sport) db.addSportData(sp, 1);
        updateNotification();
        for(QuestionnaireData q : questionnaire) db.addQuestionnaireData(q, 1);
        updateNotification();

        startForeground(notificationID, createNotification(0));
        return super.onStartCommand(intent, flags, startId);
    }

    //create notification channel
    private void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.login_store_data_notification_channel),
                NotificationManager.IMPORTANCE_NONE);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //create notification containing current progress value
    private Notification createNotification(int progress){
        //setup notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.login_store_data_notification_title))
                .setContentText(getString(R.string.main_send_data_notification_content) +": " + progress + getString(R.string.main_send_data_notification_unit))
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    //update notification
    private void updateNotification(){
        progress += 20;
        Notification notification = createNotification(progress);
        notificationManager.notify(notificationID, notification);

        if(progress == 100) onDestroy();    //stop service when insertion is completed
    }

    //proper termination of service
    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
