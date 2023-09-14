package com.example.spomoo.sensorrecording;

/*
 * SensorsRecordingService of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.spomoo.R;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Service for tracking the accelerometer sensor in a foreground activity
 * Creates notification with current speed
 * Takes the average of every second within two minutes and stores the data in the local SQL Database
 */
public class SensorsRecordingService extends Service implements SensorEventListener {

    //store current accelerometer data
    private float xAxis = 0f;
    private float yAxis = 0f;
    private float zAxis = 0f;
    private double acceleration = 0.0;

    //arrays for storing accelerometer data of two minutes
    private ArrayList<Float> tempXAxisData = new ArrayList<>();
    private ArrayList<Float> tempYAxisData = new ArrayList<>();
    private ArrayList<Float> tempZAxisData = new ArrayList<>();
    private ArrayList<Double> tempAccelerationData = new ArrayList<>();

    //store current rotation sensor data
    private float xRotation = 0f;
    private float yRotation = 0f;
    private float zRotation = 0f;
    private float scalarRotation = 0f;

    //arrays for storing rotation data of two minutes
    private ArrayList<Float> tempXRotationData = new ArrayList<>();
    private ArrayList<Float> tempYRotationData = new ArrayList<>();
    private ArrayList<Float> tempZRotationData = new ArrayList<>();
    private ArrayList<Float> tempScalarRotationData = new ArrayList<>();

    //store current steps, previous steps from other days, and steps from today before rebooting
    private float steps = 0f;
    private float prevSteps = 0f;
    private float todaySteps = 0f;

    //determines the amount of decimals of doubles
    private int DOUBLE_DECIMALS_AMOUNT = 2;

    //sharedPrefManager to get settings to check if sensor recording is enabled and local SQL database to store data
    SharedPrefManager sharedPrefManager;
    LocalDatabaseManager db;

    //schedulers for 1 seconds and two minutes
    private ScheduledExecutorService scheduleSeconds = Executors.newScheduledThreadPool(5);
    private ScheduledExecutorService scheduleMinutes = Executors.newScheduledThreadPool(5);

    //notification for foreground service
    public static final String CHANNEL_ID = "SensorRecordingServiceNotificationChannelID";
    private final int notificationID = 3;
    private NotificationManager notificationManager;

    //listener to get accelerometer's data
    private final SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            xAxis = sensorEvent.values[0];
            yAxis = sensorEvent.values[1];
            zAxis = sensorEvent.values[2];
            acceleration = Math.sqrt(xAxis * xAxis + yAxis * yAxis + zAxis * zAxis);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }
    };

    //listener to get rotation sensor's data
    SensorEventListener sensorEventListenerRotation = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            xRotation = (180 * event.values[0] + 360) % 360;
            yRotation = (180 * event.values[1] + 360) % 360;
            zRotation = (180 * event.values[2] + 360) % 360;
            scalarRotation = event.values[3];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    //listener to get step counter's data
    SensorEventListener sensorEventListenerSteps = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(!sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED))
                return;
            if(sharedPrefManager.getString(SharedPrefManager.KEY_STEP_COUNT_DATE).equals(TimeDateFormatter.getDateString())) {
                //if system has been rebooted after app has already been opened once
                if(event.values[0] == 0f){
                    prevSteps = 0f; //reset previous steps
                    sharedPrefManager.setFloat(SharedPrefManager.KEY_STEP_COUNT_PREVIOUS, prevSteps);
                    todaySteps = sharedPrefManager.getFloat(SharedPrefManager.KEY_STEP_COUNT_TODAY);    //get today's steps
                }
                steps = event.values[0] - prevSteps + todaySteps;   //steps after reboot minus steps from past days plus steps from today before reboot is actual today's steps amount
                sharedPrefManager.setFloat(SharedPrefManager.KEY_STEP_COUNT_TODAY, steps);
                db.addStepsData(new StepsData(Math.round(steps), TimeDateFormatter.getDateString()), 0);
            } else {
                sharedPrefManager.setString(SharedPrefManager.KEY_STEP_COUNT_DATE, TimeDateFormatter.getDateString());
                prevSteps = event.values[0];
                sharedPrefManager.setFloat(SharedPrefManager.KEY_STEP_COUNT_PREVIOUS, prevSteps);
                steps = 0;
                sharedPrefManager.setFloat(SharedPrefManager.KEY_STEP_COUNT_TODAY, steps);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(this);

        //cache database
        db = new LocalDatabaseManager(SensorsRecordingService.this);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //setup accelerometer sensor excluding gravity
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(accelerometerSensor != null)
            sensorManager.registerListener(sensorEventListenerAccelerometer, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //setup rotation sensor
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(rotationSensor != null)
            sensorManager.registerListener(sensorEventListenerRotation, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //setup step counter sensor
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepCounterSensor != null)
            sensorManager.registerListener(sensorEventListenerSteps, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //get last previous step count
        prevSteps = sharedPrefManager.getFloat(SharedPrefManager.KEY_STEP_COUNT_PREVIOUS);

        //create notification channel
        createNotificationChannel();

        //save acceleration data every second
        scheduleSeconds.scheduleAtFixedRate (() -> {
            //store current sensors' values
            storeCurrentAccelerometerValues();
            storeCurrentRotationValues();

            //only keep two decimals
            double acceleration = cutDouble(this.acceleration, DOUBLE_DECIMALS_AMOUNT);
            double xRotation = cutDouble(this.xRotation, DOUBLE_DECIMALS_AMOUNT);
            double yRotation = cutDouble(this.yRotation, DOUBLE_DECIMALS_AMOUNT);
            double zRotation = cutDouble(this.zRotation, DOUBLE_DECIMALS_AMOUNT);
            int steps = Math.round(this.steps);

            //send broadcast with current value
            sendSensorDataBroadcast(acceleration, xRotation, yRotation, zRotation, steps);

            //update the notification text
            updateNotification(acceleration, xRotation, yRotation, zRotation, steps);
        }, 1, 1, TimeUnit.SECONDS);

        //store the calculated values in the local SQL Database every two minutes
        scheduleMinutes.scheduleAtFixedRate (() -> {
            storeAverageAccelerometerValues();
            storeAverageRotationValues();
        }, 2, 2, TimeUnit.MINUTES);

        //start service in foreground
        startForeground(notificationID, createNotification(0f, 0f, 0f, 0f, 0));

        //if no sensor available stop service
        if(accelerometerSensor == null && rotationSensor == null && stepCounterSensor == null)
            stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    //stores current accelerometer's values in the temp Array Lists for two minutes
    private void storeCurrentAccelerometerValues(){
        if(!Float.isNaN(xAxis)) tempXAxisData.add(xAxis);
        if(!Float.isNaN(yAxis)) tempYAxisData.add(yAxis);
        if(!Float.isNaN(zAxis)) tempZAxisData.add(zAxis);
        if (!Double.isNaN(acceleration)) tempAccelerationData.add(acceleration);
    }

    //store average of two minutes of accelerometer's values in local SQL Database
    private void storeAverageAccelerometerValues(){
        //clone array lists to avoid concurrency collisions/errors
        ArrayList<Float> tempXAxisData = (ArrayList<Float>) this.tempXAxisData.clone();
        ArrayList<Float> tempYAxisData = (ArrayList<Float>) this.tempYAxisData.clone();
        ArrayList<Float> tempZAxisData = (ArrayList<Float>) this.tempZAxisData.clone();
        ArrayList<Double> tempAccelerationData = (ArrayList<Double>) this.tempAccelerationData.clone();

        //calculate the averages
        double xAxisAverage = cutDouble(calcFloatAverage(tempXAxisData), DOUBLE_DECIMALS_AMOUNT);
        double yAxisAverage = cutDouble(calcFloatAverage(tempYAxisData), DOUBLE_DECIMALS_AMOUNT);
        double zAxisAverage = cutDouble(calcFloatAverage(tempZAxisData), DOUBLE_DECIMALS_AMOUNT);
        double accelerationAverage = cutDouble(calcDoubleAverage(tempAccelerationData), DOUBLE_DECIMALS_AMOUNT);

        //check if sensor recording is enabled
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED))
            db.addAccelerometerData(new AccelerometerData(xAxisAverage, yAxisAverage, zAxisAverage, accelerationAverage, TimeDateFormatter.getDateString(),
                    TimeDateFormatter.getTimeString(), 0, 0), 0);

        //clear the global arrays
        this.tempXAxisData.clear();
        this.tempYAxisData.clear();
        this.tempZAxisData.clear();
        this.tempAccelerationData.clear();
    }

    //stores current rotation sensor's values in the temp Array Lists for two minutes
    private void storeCurrentRotationValues(){
        if(!Float.isNaN(xRotation)) tempXRotationData.add(xRotation);
        if(!Float.isNaN(yRotation)) tempYRotationData.add(yRotation);
        if(!Float.isNaN(zRotation)) tempZRotationData.add(zRotation);
        if (!Float.isNaN(scalarRotation)) tempScalarRotationData.add(scalarRotation);
    }

    //store average of two minutes of rotation sensor's values in local SQL Database
    private void storeAverageRotationValues(){
        //clone array lists to avoid concurrency collisions/errors
        ArrayList<Float> tempXRotationData = (ArrayList<Float>) this.tempXRotationData.clone();
        ArrayList<Float> tempYRotationData = (ArrayList<Float>) this.tempYRotationData.clone();
        ArrayList<Float> tempZRotationData = (ArrayList<Float>) this.tempZRotationData.clone();
        ArrayList<Float> tempScalarRotationData = (ArrayList<Float>) this.tempScalarRotationData.clone();

        //calculate the averages
        double xRotationAverage = cutDouble(calcFloatAverage(tempXRotationData), DOUBLE_DECIMALS_AMOUNT);
        double yRotationAverage = cutDouble(calcFloatAverage(tempYRotationData), DOUBLE_DECIMALS_AMOUNT);
        double zRotationAverage = cutDouble(calcFloatAverage(tempZRotationData), DOUBLE_DECIMALS_AMOUNT);
        double scalarAverage = cutDouble(calcFloatAverage(tempScalarRotationData), DOUBLE_DECIMALS_AMOUNT);

        //check if sensor recording is enabled
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED))
            db.addRotationData(new RotationData(xRotationAverage, yRotationAverage, zRotationAverage, scalarAverage, TimeDateFormatter.getDateString(),
                    TimeDateFormatter.getTimeString(), 0, 0), 0);

        //clear the global arrays
        this.tempXRotationData.clear();
        this.tempYRotationData.clear();
        this.tempZRotationData.clear();
        this.tempScalarRotationData.clear();
    }

    //create broadcast for current sensor values for Main_Home_Fragment
    private void sendSensorDataBroadcast(double speed, double xRotation, double yRotation, double zRotation, int steps){
        Intent intent = new Intent();
        intent.setAction(ConstantsBroadcast.BROADCAST_SENSORS);
        intent.putExtra(ConstantsBroadcast.BROADCAST_SENSORS_STEPS, String.valueOf(steps));
        sendBroadcast(intent);
    }

    //create notification channel
    private void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.record_sensors_notification_channel),
                NotificationManager.IMPORTANCE_NONE);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //create notification containing current acceleration value
    private Notification createNotification(double acceleration, double xRotation, double yRotation, double zRotation, int steps){
        //setup content text
        String content = "";
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED))
            content += getString(R.string.record_accelerometer_notification_content) + ": " + acceleration + " " + getString(R.string.record_accelerometer_notification_unit) + "\n";
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED))
            content += getString(R.string.record_rotation_notification_content) + ": x: " + xRotation + "°, y: " + yRotation + "°, z: " + zRotation + "° \n";
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED))
            content += getString(R.string.record_steps_notification_content) + ": " + steps;

        //create notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.record_sensors_notification_title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentText(content)
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }

    //update notification with current speed
    private void updateNotification(double acceleration, double xRotation, double yRotation, double zRotation, int steps){
        Notification notification = createNotification(acceleration, xRotation, yRotation, zRotation, steps);
        notificationManager.notify(notificationID, notification);
    }

    //returns the inputted double with only "decimals" many decimal values
    private double cutDouble(double value, int decimals){
        value = value * Math.pow(10, decimals);
        value = Math.floor(value);
        return value / Math.pow(10, decimals);
    }

    //stop all schedulers at termination of service
    @Override
    public void onDestroy() {
        scheduleMinutes.shutdown();
        scheduleSeconds.shutdown();
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    //calculate the average of a list of doubles
    private double calcDoubleAverage(List<Double> list) {
        return list.stream().mapToDouble(d -> d).average().orElse(0.0);
    }
    //calculate the average of a list of floats
    private double calcFloatAverage(List<Float> list) {
        return list.stream().mapToDouble (d -> d).average().orElse(0.0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
