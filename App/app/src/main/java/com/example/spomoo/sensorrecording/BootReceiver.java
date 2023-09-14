package com.example.spomoo.sensorrecording;

/*
 * BootReceiver of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.spomoo.utility.SharedPrefManager;

/*
 * Receives broadcast after system boot to start sensor recording
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        //if not logged in
        if(sharedPrefManager.loadUser(SharedPrefManager.KEY_USER) == null)
            return;
        //if direct sensor recording is disabled do nothing
        if(!sharedPrefManager.getBool(SharedPrefManager.KEY_RECORD_ON_BOOT_ENABLED))
            return;

        //if service is not running, start service
        Intent intent1 = new Intent(context, SensorsRecordingService.class);
        context.startForegroundService(intent1);
    }
}
