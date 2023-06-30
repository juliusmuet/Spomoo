package com.example.spomoo.recordsport;

/*
 * Record_Live_Activity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivityRecordLiveBinding;
import com.example.spomoo.mainscreen.MainActivity;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.customui.ResizeAutoCompleteTextView;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;

/*
 * Record Live Activity for recording a timer for a inputted sport activity
 * Contains a top action bar for navigating back to Main Activity
 * Contains a TextInputEditText for inputting a sport activity
 * Contains a text view to show the timer value
 * Contains a toggle button to start and end recording of the timer and opening Sport_Intensity_Activity
 */
public class Record_Live_Activity extends AppCompatActivity {

    private ActivityRecordLiveBinding binding;  //layout binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_live);

        //cache sharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivityRecordLiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set top action bar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //add arrow for navigating back to Main Activity

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorTextPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true);
        int colorClicked = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnError, typedValue, true);
        int colorTextClicked = typedValue.data;
        //set colors
        getWindow().setStatusBarColor(colorPrimary);
        binding.toolbar.setBackgroundColor(colorPrimary);
        binding.toolbar.setTitleTextColor(colorTextPrimary);

        //UI elements
        TextInputEditText timeText = binding.recordLiveTimeText;
        ResizeAutoCompleteTextView sportText = binding.recordLiveSportAutocompleteText;
        MaterialButton toggleButton = binding.recordLiveToggleButton;

        //setup autocomplete text view's items
        String[] strings = getResources().getStringArray(R.array.sport_types_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_items, strings);
        sportText.setAdapter(arrayAdapter);

        //setup broadcast receiver to adjust timer value text
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_TIME);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String time = intent.getStringExtra(ConstantsBroadcast.BROADCAST_TIME_VALUE);
                timeText.setText(time);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        //if timer is being recorded set UI elements accordingly
        if(MainActivity.isMyServiceRunning(TimerService.class, Record_Live_Activity.this)){
            sportText.setText(sharedPrefManager.getString(SharedPrefManager.KEY_RECORD_SPORT_TYPE));
            sportText.setClickable(false);
            toggleButton.setChecked(true);
            toggleButton.setEnabled(true);
            toggleButton.setBackgroundColor(colorClicked);
            toggleButton.setText(getString(R.string.record_live_toggle_button_on));
            toggleButton.setTextColor(colorTextClicked);
        } else {
            sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_TYPE, "");
            sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_DATE, "");
            sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_START, "");
        }

        //toggle button functionality
        binding.recordLiveToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            //if no sport activity is inputted, do not start timer recording
            if(sportText.getText().toString().isEmpty()){
                toggleButton.setChecked(false);
                Toast.makeText(getApplicationContext(), getString(R.string.record_live_insert_sport), Toast.LENGTH_SHORT).show();
                return;
            }

            //start timer recording
            if(isChecked) {
                //set UI elements accordingly
                toggleButton.setBackgroundColor(colorClicked);
                toggleButton.setText(getString(R.string.record_live_toggle_button_on));
                toggleButton.setTextColor(colorTextClicked);
                sportText.setEnabled(false);

                //store values in SharedPrefManager
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_TYPE, sportText.getText().toString()); //sport activity type
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_DATE, TimeDateFormatter.getDateString());  //today's date
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_START, TimeDateFormatter.getTimeString());  //time at start of timer recording

                //start timer recording service TimerService
                startService(new Intent(getApplicationContext(), TimerService.class));

            //stop timer recording
            } else {
                //set UI elements accordingly
                toggleButton.setBackgroundColor(colorPrimary);
                toggleButton.setText(getString(R.string.record_live_toggle_button_off));
                toggleButton.setTextColor(colorTextPrimary);
                sportText.setEnabled(true);
                //stop timer recording service TimerService
                stopService(new Intent(this, TimerService.class));

                //store sport activity data in local SQL Database
                LocalDatabaseManager db = new LocalDatabaseManager(Record_Live_Activity.this);
                SportData sportData = new SportData(sportText.getText().toString().trim(), timeText.getText().toString().trim(),
                        sharedPrefManager.getString(SharedPrefManager.KEY_RECORD_SPORT_DATE).trim(),
                        sharedPrefManager.getString(SharedPrefManager.KEY_RECORD_SPORT_START).trim());
                int id = db.addSportData(sportData, 0);

                //delete values in SharedPrefManager
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_TYPE, "");
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_DATE, "");
                sharedPrefManager.setString(SharedPrefManager.KEY_RECORD_SPORT_START, "");

                //store id of sport
                sharedPrefManager.setInt(SharedPrefManager.KEY_LAST_SPORT_ID, id);

                //open Sport_Intensity_Activity
                startActivity(new Intent(Record_Live_Activity.this, Sport_Intensity_Activity.class));
                overridePendingTransition(0,0);
                //close this activity
                finish();
            }
        });
    }

    //overwrite for functionality of arrow in top action bar to navigate back to Main Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}