package com.example.spomoo.questionnaire;

/*
 * Questionnaire_Activity of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.SeekBar;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivityQuestionnaireBinding;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textview.MaterialTextView;

/*
 * Questionnaire Activity showing the fragments Questionnaire_MDBF_Fragment, ...
 * Contains a top action bar for navigation between these fragments
 * Navigation between these fragments is defined in nav_graph_questionnaire.xml
 */
public class Questionnaire_Activity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;    //for top action bar
    private ActivityQuestionnaireBinding binding;   //layout binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cache sharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivityQuestionnaireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set top action bar
        setSupportActionBar(binding.toolbar);

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorText = typedValue.data;
        //set colors
        binding.toolbar.setBackgroundColor(colorPrimary);
        binding.toolbar.setTitleTextColor(colorText);

        //setup navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_questionnaire);
        //setup navigation controller for top action bar
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    //overwrite for going back a fragment
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_questionnaire);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //functionality of seekbar to make thumb invisible at start and visible on first touch and showing inputted value in above text view
    public static void seekbarFunc(String value, MaterialTextView valueText, SeekBar seekBar){
        String valueString = value + " ";
        seekBar.setProgress(0);
        seekBar.getThumb().setAlpha(0); //make thumb invisible
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getThumb().getAlpha() == 0){
                    seekBar.getThumb().setAlpha(255);    //make thumb visible on first touch
                }
                valueText.setText(valueString+progress); //update above text view with inputted value
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    //check if a value has been inputted
    public static boolean checkInputted(SeekBar seekBar){
        return seekBar.getThumb().getAlpha() != 0;
    }
}