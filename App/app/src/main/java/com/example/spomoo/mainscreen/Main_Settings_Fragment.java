package com.example.spomoo.mainscreen;

/*
 * Main_Settings_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainSettingsBinding;
import com.example.spomoo.questionnaire.QuestionnaireReminderTimer;
import com.example.spomoo.recordsport.TimerService;
import com.example.spomoo.sensorrecording.SensorsRecordingService;
import com.example.spomoo.settings.Data_Privacy_Activity;
import com.example.spomoo.settings.Edit_Account_Activity;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textview.MaterialTextView;

/*
 * Settings Fragment of Main Activity for providing several options and settings
 * Contains slider to input amount and time range of questionnaire reminder push notifications
 * Contains clickable view to open app's notification settings to mute notifications
 * Contains clickable view to edit personal user data by navigating to Edit_Account_Activity
 * Contains three switches to de-/activate the recording of the accelerometer sensor, rotation sensor and step counter and start recording after boot
 * Contains switch for to de-/activate dynamic colors for Android 12+
 * Contains clickable view to open the researchers' website in the internet browser
 * Contains clickable view to open the privacy policy by navigating to Data_Privacy_Activity
 * Contains clickable view to logout from account
 */
public class Main_Settings_Fragment extends Fragment {

    private FragmentMainSettingsBinding binding;    //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());

        //cache UI elements
        RangeSlider questionnaireAmountSlider = binding.mainSettingsNotificationsQuestionnaireAmountSlider;
        RangeSlider questionnaireTimeSlider = binding.mainSettingsNotificationsQuestionnaireTimeSlider;
        ConstraintLayout notificationToggle = binding.mainSettingsNotificationsDisableLayout;
        ConstraintLayout accountData = binding.mainSettingsAccountLayout;
        MaterialSwitch accelerometerSwitch = binding.mainSettingsSensorAccelerometerSwitch;
        MaterialSwitch rotationSwitch = binding.mainSettingsSensorGyroscopeSwitch;
        MaterialSwitch stepsSwitch = binding.mainSettingsSensorStepsSwitch;
        MaterialSwitch bootSwitch = binding.mainSettingsSensorBootSwitch;
        MaterialTextView colorHeading = binding.mainSettingsOtherThemeHeading;
        MaterialTextView colorDescription = binding.mainSettingsOtherThemeDescription;
        MaterialSwitch colorSwitch = binding.mainSettingsOtherThemeSwitch;
        ConstraintLayout aboutUs = binding.mainSettingsOtherAboutusLayout;
        ConstraintLayout privacy = binding.mainSettingsOtherPrivacyLayout;
        ConstraintLayout logout = binding.mainSettingsOtherLogoutLayout;

        //if on Android below 12, then disable theme UI elements
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            colorHeading.setEnabled(false);
            colorDescription.setEnabled(false);
            colorSwitch.setEnabled(false);
            colorHeading.setVisibility(View.GONE);
            colorDescription.setVisibility(View.GONE);
            colorSwitch.setVisibility(View.GONE);
        }

        //cache questionnaire reminder timer
        QuestionnaireReminderTimer questionnaireReminderTimer = new QuestionnaireReminderTimer(getContext());

        //setup questionnaire amount slider
        questionnaireAmountSlider.setTickVisible(false);
        questionnaireAmountSlider.setThumbRadius(40);
        questionnaireAmountSlider.setTrackHeight(30);
        questionnaireAmountSlider.setValues(sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_AMOUNT));
        //store inputted value after stop touching the slider
        questionnaireAmountSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                //save value
                sharedPrefManager.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_AMOUNT, questionnaireAmountSlider.getValues().get(0));

                //if only one reminder set time to 20 o'clock
                if(questionnaireAmountSlider.getValues().get(0) == 1f) {
                    questionnaireTimeSlider.setValues(20f, 21f);
                    sharedPrefManager.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_FIRST, 20f);
                    sharedPrefManager.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_LAST, 21f);
                }

                //setup questionnaire reminders
                questionnaireReminderTimer.cancelAllNotifications();
                questionnaireReminderTimer.setNotifications();
            }
        });

        //setup questionnaire time range slider
        questionnaireTimeSlider.setTickVisible(false);
        questionnaireTimeSlider.setThumbRadius(40);
        questionnaireTimeSlider.setTrackHeight(30);
        questionnaireTimeSlider.setMinSeparationValue(1f);
        questionnaireTimeSlider.setValues(sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_FIRST),
                sharedPrefManager.getFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_LAST));
        //store inputted values after stop touching the slider
        questionnaireTimeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                //save values
                sharedPrefManager.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_FIRST, questionnaireTimeSlider.getValues().get(0));
                sharedPrefManager.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_LAST, questionnaireTimeSlider.getValues().get(1));
                //setup questionnaire reminders
                questionnaireReminderTimer.cancelAllNotifications();
                questionnaireReminderTimer.setNotifications();
            }
        });

        //setup notification toggle to open app's notification settings
        notificationToggle.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, SensorsRecordingService.CHANNEL_ID);
            startActivity(settingsIntent);
        });

        //setup account data changer to open Edit_Account_Activity
        accountData.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Edit_Account_Activity.class));
        });

        //setup accelerometer switch to store inputted value
        accelerometerSwitch.setChecked(sharedPrefManager.getBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED));
        accelerometerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED, isChecked);
            startOrStopService(isChecked, !isChecked, rotationSwitch.isChecked(), rotationSwitch.isChecked(), stepsSwitch.isChecked(), stepsSwitch.isChecked());
        });

        //setup rotation sensor switch to store inputted value
        rotationSwitch.setChecked(sharedPrefManager.getBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED));
        rotationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED, isChecked);
            startOrStopService(accelerometerSwitch.isChecked(), accelerometerSwitch.isChecked(), isChecked, !isChecked, stepsSwitch.isChecked(), stepsSwitch.isChecked());
        });

        //setup step count sensor
        //on Android 10+ check if physical activity permission is granted
        //if permission is not granted then uncheck switch
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                stepsSwitch.setChecked(false);
                sharedPrefManager.setBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED, false);
            }
        }
        //setup stepper switch to store inputted value
        stepsSwitch.setChecked(sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED));
        stepsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //on Android version lower than 10 no permission check is needed
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q){
                sharedPrefManager.setBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED, isChecked);
                startOrStopService(accelerometerSwitch.isChecked(), accelerometerSwitch.isChecked(), rotationSwitch.isChecked(), rotationSwitch.isChecked(), isChecked, !isChecked);
                return;
            }
            //check if permission for step counter sensor has been granted
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                sharedPrefManager.setBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED, isChecked);
                startOrStopService(accelerometerSwitch.isChecked(), accelerometerSwitch.isChecked(), rotationSwitch.isChecked(), rotationSwitch.isChecked(), isChecked, !isChecked);
            } else {
                //if permission is not granted then disable step counter recording
                stepsSwitch.setChecked(false);
                sharedPrefManager.setBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED, false);
                startOrStopService(accelerometerSwitch.isChecked(), accelerometerSwitch.isChecked(), rotationSwitch.isChecked(), rotationSwitch.isChecked(), false, !isChecked);

                //show dialog to ask for permission
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.main_settings_step_dialogue_heading)
                        .setMessage(R.string.main_settings_step_dialogue_content)
                        .setCancelable(false)
                        .setIcon(R.drawable.physical_activity)
                        .setNegativeButton(R.string.main_settings_step_dialogue_negative, (dialog, which) -> {
                        })
                        .setPositiveButton(R.string.main_settings_step_dialogue_positive, (dialog, which) -> {
                            //open app's settings
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getContext().getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .show();
            }
        });

        //setup direct recording switch to store inputted value
        bootSwitch.setChecked(sharedPrefManager.getBool(SharedPrefManager.KEY_RECORD_ON_BOOT_ENABLED));
        bootSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setBool(SharedPrefManager.KEY_RECORD_ON_BOOT_ENABLED, isChecked);
        });

        //setup color pallet switch
        colorSwitch.setChecked(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED));
        colorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED, isChecked);
            //reload main activity
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent intent = new Intent();
                intent.setAction(ConstantsBroadcast.BROADCAST_RELOAD_COLORS);
                getContext().sendBroadcast(intent);
            }
        });

        //setup about us to open website about research in internet browser
        aboutUs.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sport.ruhr-uni-bochum.de/de/forschung-ehealth-und-sports-analytics"));
            startActivity(browserIntent);
        });

        //setup privacy to open Data_Privacy_Activity
        privacy.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Data_Privacy_Activity.class));
        });

        //setup logout
        logout.setOnClickListener(v -> {
            //create dialog to ask again for log out
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getString(R.string.main_settings_other_logout_dialog_title))
                    .setMessage(getString(R.string.main_settings_other_logout_dialog_content))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.main_settings_other_logout_dialog_yes), (dialog, which) -> {
                        //remove stored user
                        sharedPrefManager.storeUser(SharedPrefManager.KEY_USER, null);

                        //reset database
                        LocalDatabaseManager db = new LocalDatabaseManager(getContext());
                        db.resetDB();

                        //reset sharedPrefManger
                        sharedPrefManager.deleteAll(sharedPrefManager);

                        //cancel questionnaire push notifications
                        QuestionnaireReminderTimer questionnaireReminderTimer1 = new QuestionnaireReminderTimer(getContext());
                        questionnaireReminderTimer1.cancelAllNotifications();
                        NotificationManagerCompat.from(getContext()).cancelAll();

                        //stop all services
                        getContext().stopService(new Intent(getContext(), SensorsRecordingService.class));
                        getContext().stopService(new Intent(getContext(), TimerService.class));

                        //close activity and open new MainActivity
                        FragmentActivity currentActivity = getActivity();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        currentActivity.finish();
                    })
                    .setNegativeButton(getString(R.string.main_settings_other_logout_dialog_no), (dialog, which) -> { })
                    .show();
        });

    }

    //if all three sensors are deactivated then stop the sensor recording service
    //if one sensor gets activated after all have been deactivated then start the sensor recording service
    private void startOrStopService(boolean sensor1Now, boolean sensor1Before, boolean sensor2Now, boolean sensor2Before,
                                    boolean sensor3Now, boolean sensor3Before){
        if(!sensor1Now && !sensor2Now && !sensor3Now){
            getContext().stopService(new Intent(getContext(), SensorsRecordingService.class));
            return;
        }

        if(!sensor1Before && !sensor2Before && !sensor3Before && !MainActivity.isMyServiceRunning(SensorsRecordingService.class, getContext())){
            getContext().startForegroundService(new Intent(getContext(), SensorsRecordingService.class));
            return;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}