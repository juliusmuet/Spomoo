package com.example.spomoo.recordsport;

/*
 * Save_PastSport_Activity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivitySavePastSportBinding;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.customui.ResizeAutoCompleteTextView;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Save Past Sport Activity for saving a not recorded sport activity
 * Contains a top action bar for navigating back to Main Activity
 * Contains a TextInputEditText for inputting a sport activity
 * Contains three clickable TextInputEditText to input date, start time and duration via date picker and time pickers
 * Contains a button to save inputs and to open Sport_Intensity_Activity
 */
public class Save_PastSport_Activity extends AppCompatActivity {

    private ActivitySavePastSportBinding binding;   //layout binding

    private SharedPrefManager sharedPrefManager;    //cache sharedPrefManager

    //avoid date and time pickers to be shown multiple times at once
    private boolean datePickerAdded = false;
    private boolean timePickerAdded = false;
    private boolean durationPickerAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_past_sport);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivitySavePastSportBinding.inflate(getLayoutInflater());
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
        //set colors
        getWindow().setStatusBarColor(colorPrimary);
        binding.toolbar.setBackgroundColor(colorPrimary);
        binding.toolbar.setTitleTextColor(colorTextPrimary);

        //UI elements
        ResizeAutoCompleteTextView sportText = binding.savePastSportAutocompleteText;
        TextInputEditText dateText = binding.savePastSportDate;
        TextInputEditText startText = binding.savePastSportStart;
        TextInputEditText durationText = binding.savePastSportDuration;
        MaterialButton saveButton = binding.savePastSportButtonSave;

        //setup autocomplete text view's items
        String[] strings = getResources().getStringArray(R.array.sport_types_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_items, strings);
        sportText.setAdapter(arrayAdapter);

        //setup date picker
        //setup calender constraints to allow date between today and past two weeks
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();
        calendarConstraints.setValidator(new DateValidatorWeekdays());  //set valid dates between today and past two weeks
        //create date picker
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder
                .datePicker()
                .setCalendarConstraints(calendarConstraints.build())
                .build();
        //setup on confirm button listener
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            long dateInMillis = Long.parseLong(selection.toString());
            Date date = new Date(dateInMillis);
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");  //convert inputted date to dd.mm.yyyy format
            dateText.setText(formatter.format(date));   //set date as text of text view
            datePickerAdded = false;    //date picker not shown anymore
        });
        //setup all other buttons to set that date picker is not shown anymore
        materialDatePicker.addOnCancelListener(dialog -> {
            datePickerAdded = false;
        });
        materialDatePicker.addOnDismissListener(dialog -> {
            datePickerAdded = false;
        });
        materialDatePicker.addOnNegativeButtonClickListener(v -> {
            datePickerAdded = false;
        });
        //open date picker on click on date text view
        dateText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!datePickerAdded && hasFocus) {
                datePickerAdded = true;
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        dateText.setOnClickListener(v -> {
            if(!datePickerAdded) {
                datePickerAdded = true;
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });


        //setup time picker to start time
        MaterialTimePicker materialTimePickerStart = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText(getText(R.string.save_past_sport_start_enter))
                .build();
        //setup on confirm button listener
        materialTimePickerStart.addOnPositiveButtonClickListener(v -> {
            startText.setText(String.format("%02d:%02d",materialTimePickerStart.getHour(), materialTimePickerStart.getMinute()));   //convert inputted time to hh:mm and set as text of text view
            timePickerAdded = false;    //time picker not shown anymore
        });
        //setup all other buttons to set that time picker is not shown anymore
        materialTimePickerStart.addOnCancelListener(dialog -> {
            timePickerAdded = false;
        });
        materialTimePickerStart.addOnDismissListener(dialog -> {
            timePickerAdded = false;
        });
        materialTimePickerStart.addOnNegativeButtonClickListener(v -> {
            timePickerAdded = false;
        });
        //open time picker on click on start time text view
        startText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!timePickerAdded && hasFocus) {
                timePickerAdded = true;
                materialTimePickerStart.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_START");
            }
        });
        startText.setOnClickListener(v -> {
            if(!timePickerAdded) {
                timePickerAdded = true;
                materialTimePickerStart.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_START");
            }
        });

        //setup time picker to duration time
        MaterialTimePicker materialTimePickerDuration = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .setTitleText(getText(R.string.save_past_sport_duration_enter))
                .build();
        //setup on confirm button listener
        materialTimePickerDuration.addOnPositiveButtonClickListener(v -> {
            durationText.setText(String.format("%02d:%02d",materialTimePickerDuration.getHour(), materialTimePickerDuration.getMinute()));  //convert inputted time to hh:mm and set as text of text view
            durationPickerAdded = false;    //time picker not shown anymore
        });
        //setup all other buttons to set that time picker is not shown anymore
        materialTimePickerDuration.addOnCancelListener(dialog -> {
            durationPickerAdded = false;
        });
        materialTimePickerDuration.addOnDismissListener(dialog -> {
            durationPickerAdded = false;
        });
        materialTimePickerDuration.addOnNegativeButtonClickListener(v -> {
            durationPickerAdded = false;
        });
        //open time picker on click on duration text view
        durationText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!durationPickerAdded && hasFocus) {
                durationPickerAdded = true;
                materialTimePickerDuration.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_DURATION");
            }
        });
        durationText.setOnClickListener(v -> {
            if(!durationPickerAdded) {
                durationPickerAdded = true;
                materialTimePickerDuration.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_DURATION");
            }
        });

        //save button functionality
        saveButton.setOnClickListener(v -> {
            //if inputs are missing, do not save
            if(sportText.getText().toString().isEmpty() || dateText.getText().toString().isEmpty()
                || startText.getText().toString().isEmpty() || durationText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.save_past_sport_missing), Toast.LENGTH_SHORT).show();
                return;
            }

            //save inputted values as sport activity in local SQL Database
            LocalDatabaseManager db = new LocalDatabaseManager(Save_PastSport_Activity.this);
            SportData sportData = new SportData(sportText.getText().toString().trim(), durationText.getText().toString().trim()+":00",
                    dateText.getText().toString().trim(), startText.getText().toString().trim());
            int id =  db.addSportData(sportData, 0);

            //store id of sport
            sharedPrefManager.setInt(SharedPrefManager.KEY_LAST_SPORT_ID, id);

            //open Sport_Intensity_Activity
            startActivity(new Intent(Save_PastSport_Activity.this, Sport_Intensity_Activity.class));
            overridePendingTransition(0,0);
            //close this activity
            finish();
        });
    }

    //overwrite for functionality of arrow in top action bar to navigate back to Main Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}