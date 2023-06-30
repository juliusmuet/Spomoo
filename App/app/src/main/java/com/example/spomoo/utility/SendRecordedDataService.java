package com.example.spomoo.utility;

/*
 * SendRecordedDataService of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.spomoo.R;
import com.example.spomoo.mainscreen.MainActivity;
import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Foreground service which contains methods to get all data from the local SQL database which has not been sent and sends these data to the server
 * Updates notification based on progress
 * Sends broadcast to MainActivity to update the Alert Dialog Text
 */
public class SendRecordedDataService extends Service {

    private LocalDatabaseManager db;    //cache local SQL database

    //notification for foreground service
    private final String CHANNEL_ID = "SendDataNotificationChannelID";
    private final int notificationID = 4;
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

        //get local SQL database
        db = new LocalDatabaseManager(this);

        //send data
        sendAllData();

        //start service in foreground
        startForeground(notificationID, createNotification(0));
        return super.onStartCommand(intent, flags, startId);
    }

    //sends accelerometer, rotation, steps, questionnaire and sport data to server
    public void sendAllData(){
        //send accelerometer data
        ArrayList<AccelerometerData> accelerometerData = db.readUnsendAccelerometerData(); //get accelerometer data
        if(accelerometerData.size() > 0) sendAccelerometerData(accelerometerData);
        else updateNotification();

        //send rotation data
        ArrayList<RotationData> rotationData = db.readUnsendRotationData();    //get rotation data
        if(rotationData.size() > 0) sendRotationData(rotationData);
        else updateNotification();

        //send steps data
        ArrayList<StepsData> stepsData = db.readUnsendStepsData(); //get steps data
        Gson gson = new Gson();
        System.out.println(gson.toJson(stepsData));
        if(stepsData.size() > 0) sendStepsData(stepsData);
        else updateNotification();

        //send sport data
        ArrayList<SportData> sportData = db.readUnsendSportData(); //get sport data
        if(sportData.size() > 0) sendSportData(sportData);
        else updateNotification();

        //send questionnaire data
        ArrayList<QuestionnaireData> questionnaireData = db.readUnsendQuestionnaireData(); //get questionnaire data
        if(questionnaireData.size() > 0) sendQuestionnaireData(questionnaireData);
        else updateNotification();
    }

    //send accelerometer data
    private void sendAccelerometerData(ArrayList<AccelerometerData> accelerometerData){
        //check if there is an internet connection
        if(!RetrofitClient.internetIsConnected(this)){
            Toast.makeText(this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            updateNotification();
            return;
        }

        //make API call
        Gson gson = new Gson();
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi().sendAccelerometerData(gson.toJson(accelerometerData));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for errors
                if(response.code() == 422 || responseDefault == null) {
                    Toast.makeText(SendRecordedDataService.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    updateNotification();
                    return;
                }

                //if no error mark data as sent
                if (!responseDefault.isError()) {
                    db.markAccelerometerDataAsSent();
                    updateNotification();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                updateNotification();
            }
        });
    }

    //send rotation data
    private void sendRotationData(ArrayList<RotationData> rotationData){
        //check if there is an internet connection
        if(!RetrofitClient.internetIsConnected(this)){
            Toast.makeText(this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            updateNotification();
            return;
        }

        //make API call
        Gson gson = new Gson();
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi().sendRotationData(gson.toJson(rotationData));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for errors
                if(response.code() == 422 || responseDefault == null) {
                    Toast.makeText(SendRecordedDataService.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    updateNotification();
                    return;
                }

                //if no error mark data as sent
                if (!responseDefault.isError()) {
                    db.markRotationDataAsSent();
                    updateNotification();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                updateNotification();
            }
        });
    }

    //send steps data
    private void sendStepsData(ArrayList<StepsData> stepsData){
        //check if there is an internet connection
        if(!RetrofitClient.internetIsConnected(this)){
            Toast.makeText(this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            updateNotification();
            return;
        }

        //make API call
        Gson gson = new Gson();
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi().sendStepsData(gson.toJson(stepsData));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for errors
                if(response.code() == 422 || responseDefault == null) {
                    Toast.makeText(SendRecordedDataService.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    updateNotification();
                    return;
                }

                //if no error mark data as sent
                if (!responseDefault.isError()) {
                    db.markStepsDataAsSent();
                    updateNotification();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                updateNotification();
            }
        });
    }

    //send sport data
    private void sendSportData(ArrayList<SportData> sportData){
        //check if there is an internet connection
        if(!RetrofitClient.internetIsConnected(this)){
            Toast.makeText(this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            updateNotification();
            return;
        }

        //make API call
        Gson gson = new Gson();
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi().sendSportData(gson.toJson(sportData));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for errors
                if(response.code() == 422 || responseDefault == null) {
                    Toast.makeText(SendRecordedDataService.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    updateNotification();
                    return;
                }

                //if no error mark data as sent
                if (!responseDefault.isError()) {
                    db.markSportDataAsSent();
                    updateNotification();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                updateNotification();
            }
        });
    }

    //send sport data
    private void sendQuestionnaireData(ArrayList<QuestionnaireData> questionnaireData){
        //check if there is an internet connection
        if(!RetrofitClient.internetIsConnected(this)){
            Toast.makeText(this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            updateNotification();
            return;
        }

        //make API call
        Gson gson = new Gson();
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi().sendQuestionnaireData(gson.toJson(questionnaireData));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for errors
                if(response.code() == 422 || responseDefault == null) {
                    Toast.makeText(SendRecordedDataService.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    updateNotification();
                    return;
                }

                //if no error mark data as sent
                if (!responseDefault.isError()) {
                    db.markQuestionnaireDataAsSent();
                    updateNotification();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                updateNotification();
            }
        });
    }

    //create notification channel
    private void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.main_send_data_notification_channel),
                NotificationManager.IMPORTANCE_NONE);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //create notification containing current progress value
    private Notification createNotification(int progress){
        //open MainActivity on click on notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        //setup notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.main_send_data_notification_title))
                .setContentText(getString(R.string.main_send_data_notification_content) +": " + progress + getString(R.string.main_send_data_notification_unit))
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    //update notification and send broadcast to MainActivity with current progress
    private void updateNotification(){
        progress += 20;
        Notification notification = createNotification(progress);
        notificationManager.notify(notificationID, notification);
        sendSensorDataBroadcast(progress);
    }

    //create broadcast for current progress for MainActivity
    private void sendSensorDataBroadcast(int progress){
        Intent intent = new Intent();
        intent.setAction(ConstantsBroadcast.BROADCAST_SEND_DATA_PROGRESS);
        intent.putExtra(ConstantsBroadcast.BROADCAST_SEND_DATA_PROGRESS_VALUE, String.valueOf(progress));
        sendBroadcast(intent);
    }

    //proper termination of service
    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

}
