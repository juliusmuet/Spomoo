package com.example.spomoo.mainscreen;

/*
 * MainActivity of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.spomoo.R;
import com.example.spomoo.login.LoginActivity;
import com.example.spomoo.questionnaire.QuestionnaireReminderTimer;
import com.example.spomoo.sensorrecording.SensorsRecordingService;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.SendRecordedDataService;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.core.splashscreen.SplashScreen;

import com.example.spomoo.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

/*
 * Main Activity showing the fragments Main_Home_Fragment, Main_Record_Fragment, Main_Data_Fragment and Main_Settings_Fragment
 * Contains a top action bar for navigation between these fragments and showing the streaks
 * Contains a bottom action bar for navigation between these fragments and a Floating Action Button to send locally stored data to the server
 * Navigation between these fragments is defined in nav_graph.xml
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;    //for top action bar
    private ActivityMainBinding binding; //layout binding
    private SharedPrefManager sharedPrefManager;    //cache sharedPrefManager
    private AlertDialog alertDialog = null; //cache alert dialog for showing the data sending progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //create splashscreen
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //open login if not logged in
        if(sharedPrefManager.loadUser(SharedPrefManager.KEY_USER) == null){
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0,0);
            finish();
            return;
        }

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //inflate layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set top action bar
        setSupportActionBar(binding.toolbar);

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorText = typedValue.data;
        //set colors of status bar
        getWindow().setStatusBarColor(colorPrimary);
        //set colors of top action bar
        binding.toolbar.setBackgroundColor(colorPrimary);
        binding.toolbar.setTitleTextColor(colorText);
        binding.mainStreaksNumber.setTextColor(colorText);

        //setup navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //setup navigation controller for top action bar
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //setup bottom action bar
        BottomNavigationView bottomNavigationView = binding.bottomNavMain;
        bottomNavigationView.setBackground(null);   //fix for buggy background
        bottomNavigationView.getMenu().getItem(2).setEnabled(false); //disable placeholder button below floating action button
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //setup send data floating action button
        binding.fab.setOnClickListener(view -> {
            //if data is not being send create dialog to ask for confirmation
            if(!isMyServiceRunning(SendRecordedDataService.class, this)){
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getString(R.string.main_send_data_dialog_title))
                        .setMessage(getString(R.string.main_send_data_confirm_dialog_content))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.main_send_data_confirm_dialog_yes), (dialog, which) -> {
                            startService(new Intent(MainActivity.this, SendRecordedDataService.class));
                            alertDialog = createSendDataDialog().show();
                        })
                        .setNegativeButton(getString(R.string.main_send_data_confirm_dialog_no), (dialog, which) -> { })
                        .show();
            }
        });

        //if data is currently being send create the dialog
        if(isMyServiceRunning(SendRecordedDataService.class, this)){
            alertDialog = createSendDataDialog().show();
        }

        //set streak
        getStreak();

        //setup questionnaire reminders
        QuestionnaireReminderTimer questionnaireReminderTimer = new QuestionnaireReminderTimer(this);
        questionnaireReminderTimer.cancelAllNotifications();
        questionnaireReminderTimer.setNotifications();

        //start sensor recording service
        if((sharedPrefManager.getBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED) || sharedPrefManager.getBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED) ||
                sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED)) && !isMyServiceRunning(SensorsRecordingService.class, MainActivity.this))
            startService(new Intent(MainActivity.this, SensorsRecordingService.class));

        //reload if color palette has changed in settings
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_RELOAD_COLORS);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    //overwrite for going back a fragment
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //create send data dialog
    private MaterialAlertDialogBuilder createSendDataDialog(){
        //inflate view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_sending_data, binding.getRoot(), false);
        CircularProgressIndicator progressIndicator = dialogView.findViewById(R.id.dialog_sending_data_progress_indicator);
        MaterialTextView progressText = dialogView.findViewById(R.id.dialog_sending_data_progress_text);

        //build dialog
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this)
                .setView(dialogView)
                .setTitle(getString(R.string.main_send_data_dialog_title))
                .setMessage(getString(R.string.main_send_data_dialog_content))
                .setCancelable(false);

        //setup broadcast receiver to adjust progress value text
        IntentFilter intentFilterSendData = new IntentFilter();
        intentFilterSendData.addAction(ConstantsBroadcast.BROADCAST_SEND_DATA_PROGRESS);
        BroadcastReceiver broadcastReceiverSendData = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = Integer.parseInt(intent.getStringExtra(ConstantsBroadcast.BROADCAST_SEND_DATA_PROGRESS_VALUE));  //get value
                progressIndicator.setProgress(progress);    //set progress indicator
                progressText.setText(progress + getString(R.string.main_send_data_notification_unit));  //set progress text

                //close dialog and stop service at 100%
                if(progress == 100 && alertDialog != null){
                    stopService(new Intent(MainActivity.this, SendRecordedDataService.class));
                    if(alertDialog != null) alertDialog.cancel();
                    Toast.makeText(MainActivity.this, getString(R.string.main_send_data_finished), Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(broadcastReceiverSendData, intentFilterSendData);

        return alertDialogBuilder;
    }

    //return current streak value
    public void getStreak() {
        //get SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(MainActivity.this);
        //first day
        if (sharedPrefManager.getInt(SharedPrefManager.KEY_STREAK_VALUE) == 0) {
            sharedPrefManager.setInt(SharedPrefManager.KEY_STREAK_VALUE, 1);
            sharedPrefManager.setString(SharedPrefManager.KEY_STREAK_DATE, TimeDateFormatter.getDateString());
            binding.mainStreaksNumber.setText(Integer.toString(1));
            return;
        }

        String currentStreakDate = sharedPrefManager.getString(SharedPrefManager.KEY_STREAK_DATE);
        int streaks = sharedPrefManager.getInt(SharedPrefManager.KEY_STREAK_VALUE);
        String today = TimeDateFormatter.getDateString();
        String yesterday = TimeDateFormatter.getPrevDateString(1);
        //if stored streak date equals yesterday's date then increase streak
        if (currentStreakDate.equals(yesterday)) {
            streaks+= 1;
            currentStreakDate = today;
        //if stored streak date equals today's date then do nothing
        } else if (currentStreakDate.equals(today)) {
        //else the streak broke
        } else {
            streaks = 1;
            currentStreakDate = today;
        }

        //store values and set it to text view
        sharedPrefManager.setString(SharedPrefManager.KEY_STREAK_DATE, currentStreakDate);
        sharedPrefManager.setInt(SharedPrefManager.KEY_STREAK_VALUE, streaks);
        binding.mainStreaksNumber.setText(Integer.toString(streaks));
    }

    //check if service is running
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //reset inputted date of Main_Data_Fragment on each reload
        sharedPrefManager.setString(SharedPrefManager.KEY_DATA_DATE, "");
    }

    @Override
    protected void onDestroy() {
        //if sending data dialog is shown, destroy it
        if(alertDialog != null && alertDialog.isShowing()) alertDialog.cancel();
        super.onDestroy();
    }

    /* Settings Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}