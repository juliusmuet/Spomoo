package com.example.spomoo.mainscreen;

/*
 * Main_Home_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainHomeBinding;
import com.example.spomoo.questionnaire.Questionnaire_Activity;
import com.example.spomoo.recordsport.Record_Live_Activity;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;
import com.google.android.material.textview.MaterialTextView;

/*
 * Contains a text view for greeting the user based on the daytime and their username
 * Displays today's sport duration and steps
 * Displays today's amount of done questionnaires and the resulting mood
 */

public class Main_Home_Fragment extends Fragment {

    private FragmentMainHomeBinding binding;    //layout binding

    private LocalDatabaseManager db;    //cache local SQL Database
    SharedPrefManager sharedPrefManager;    //cache sharedPrefManager

    //cache variables for the broadcast
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    //cache texts
    MaterialTextView welcomeText;
    MaterialTextView sportDurationText;
    MaterialTextView stepsText;
    MaterialTextView moodText;
    MaterialTextView questionnairesText;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        //cache local SQL database
        db = new LocalDatabaseManager(getContext());

        //cache texts
        welcomeText = binding.mainHomeWelcomeText;
        sportDurationText = binding.mainHomeActivityDurationText;
        stepsText = binding.mainHomeActivityStepsText;
        moodText = binding.mainHomeMentalMoodText;
        questionnairesText = binding.mainHomeMentalQuestionnairesText;

        //setup activity card click to open sport recording activity
        binding.mainHomeActivityCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Record_Live_Activity.class));
        });

        //setup mental card click to open questionnaire activity
        binding.mainHomeMentalCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Questionnaire_Activity.class));
        });

        //set all texts
        setAllTexts();

        //setup broadcast receiver to adjust steps value texts
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_SENSORS);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED))
                    stepsText.setText(intent.getStringExtra(ConstantsBroadcast.BROADCAST_SENSORS_STEPS));
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(broadcastReceiver); //unregister broadcast
        super.onPause();
    }

    @Override
    public void onResume() {
        setAllTexts();  //set all texts

        getContext().registerReceiver(broadcastReceiver, intentFilter); //re-register broadcast

        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setAllTexts(){
        setWelcomeText();
        setSportDurationText();
        setStepsText();
        setMoodText();
        setQuestionnairesText();
    }

    private void setWelcomeText(){
        String welcome = "";
        int hour = Integer.parseInt(TimeDateFormatter.getTimeString().substring(0,2));
        if((hour >= 0 && hour < 6) || (hour >=22 && hour <= 24))
            welcome += getString(R.string.main_home_night_text);
        else if(hour >= 6 && hour < 12)
            welcome += getString(R.string.main_home_morning_text);
        else if(hour >= 12 && hour < 15)
            welcome += getString(R.string.main_home_day_text);
        else if(hour >= 15 && hour < 18)
            welcome += getString(R.string.main_home_afternoon_text);
        else if(hour >= 18 && hour < 22)
            welcome += getString(R.string.main_home_evening_text);
        else welcome += getString(R.string.main_home_day_text);
        if(sharedPrefManager.loadUser(SharedPrefManager.KEY_USER) != null)
            welcome += " " + sharedPrefManager.loadUser(SharedPrefManager.KEY_USER).getUsername();
        welcomeText.setText(welcome);
    }

    private void setSportDurationText(){
        int min = db.getSportDurationFromDateInMinutes(TimeDateFormatter.getDateString());
        int hours = min/60;
        int minutes = min % 60;
        String text = hours + getString(R.string.main_home_activity_duration_hour) + " " + minutes + getString(R.string.main_home_activity_duration_minute);
        sportDurationText.setText(text);
    }

    private void setStepsText(){
        //if step recording is disabled
        if(!sharedPrefManager.getBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED)) {
            stepsText.setText(R.string.main_home_activity_steps_inactive);
            return;
        }

        //get stored steps
        int stepsValue = 0;
        if(db.readStepsDataFromDate(TimeDateFormatter.getDateString()) != null)
            stepsValue = db.readStepsDataFromDate(TimeDateFormatter.getDateString()).getSteps();
        stepsText.setText(String.valueOf(stepsValue));
    }

    private void setMoodText(){
        int moodValue = db.getMoodValue(TimeDateFormatter.getDateString());
        if(0 < moodValue && moodValue <= 20) moodText.setText(getString(R.string.main_home_mental_mood_0));
        else if(20 < moodValue && moodValue <= 40) moodText.setText(getString(R.string.main_home_mental_mood_1));
        else if(40 < moodValue && moodValue <= 60) moodText.setText(getString(R.string.main_home_mental_mood_2));
        else if(60 < moodValue && moodValue <= 80) moodText.setText(getString(R.string.main_home_mental_mood_3));
        else if(80 < moodValue && moodValue <= 100) moodText.setText(getString(R.string.main_home_mental_mood_4));
        else moodText.setText(getString(R.string.main_home_mental_mood_2));
    }

    private void setQuestionnairesText(){
        int questionnaireCount = db.getQuestionnaireCount(TimeDateFormatter.getDateString());
        binding.mainHomeMentalQuestionnairesText.setText(String.valueOf(questionnaireCount));
    }

}