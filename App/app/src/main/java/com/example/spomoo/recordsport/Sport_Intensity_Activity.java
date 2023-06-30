package com.example.spomoo.recordsport;

/*
 * Sport_Intensity_Activity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.customui.VerticalSeekBar;
import com.example.spomoo.databinding.ActivitySportIntensityBinding;
import com.example.spomoo.questionnaire.Questionnaire_Activity;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textview.MaterialTextView;

/*
 * Contains a vertical seekbar to input the value
 * Text Views' text change accordingly to inputted value
 * Updates intensity value of last saved sport activity
 * Contains a skip text and save floating action button to save the input and open Questionnaire_Activity
 */
public class Sport_Intensity_Activity extends AppCompatActivity {

    private ActivitySportIntensityBinding binding;  //layout binding
    private SharedPrefManager sharedPrefManager;    //sharedPrefManager
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_intensity);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivitySportIntensityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set top action bar
        setSupportActionBar(binding.sportIntensityToolbar);

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorTextPrimary = typedValue.data;
        //set colors
        getWindow().setStatusBarColor(colorPrimary);
        binding.sportIntensityToolbar.setBackgroundColor(colorPrimary);
        binding.sportIntensityToolbar.setTitleTextColor(colorTextPrimary);

        //cache UI elements
        VerticalSeekBar intensityBar = binding.sportIntensityBar;
        MaterialTextView valueText = binding.sportIntensityValueText;
        MaterialTextView descriptionText = binding.sportIntensityValueTypeText;

        //cache strings
        String valueString = getString(R.string.sport_intensity_value) + " ";
        String description1 = getString(R.string.sport_intensity_value_1);
        String description2 = getString(R.string.sport_intensity_value_2);
        String description3 = getString(R.string.sport_intensity_value_3);
        String description4 = getString(R.string.sport_intensity_value_4);
        String description5 = getString(R.string.sport_intensity_value_5);
        String description6 = getString(R.string.sport_intensity_value_6);
        String description7 = getString(R.string.sport_intensity_value_7);
        String description8 = getString(R.string.sport_intensity_value_8);
        String description9 = getString(R.string.sport_intensity_value_9);

        //seekbar functionality to set texts according to progress
        intensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueText.setText(valueString+progress);

                if(progress <= 20) descriptionText.setText(description1);
                else if(20 < progress && progress <= 40) descriptionText.setText(description2);
                else if(40 < progress && progress <= 54) descriptionText.setText(description3);
                else if(54 < progress && progress <= 64) descriptionText.setText(description4);
                else if(64 < progress && progress <= 74) descriptionText.setText(description5);
                else if(74 < progress && progress <= 84) descriptionText.setText(description6);
                else if(84 < progress && progress <= 94) descriptionText.setText(description7);
                else if(94 < progress && progress <= 99) descriptionText.setText(description8);
                else descriptionText.setText(description9);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //setup save data floating action button
        binding.sportIntensityFabSave.setOnClickListener(v -> {
            updateIntensity(intensityBar.getProgress());
        });

        //setup skip
        binding.bottomBarSportIntensity.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                updateIntensity(-1);
                return true;
            } else return false;
        });
    }

    //overwrite for functionality of arrow in top action bar to navigate to show feedback
    @Override
    public boolean onSupportNavigateUp() {
        //create user feedback
        Toast.makeText(getApplicationContext(), getString(R.string.save_past_sport_saved), Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }

    private void updateIntensity(int intensity){
        //get ID of last sport and update the intensity
        int id = sharedPrefManager.getInt(SharedPrefManager.KEY_LAST_SPORT_ID);
        System.out.println(id);
        LocalDatabaseManager db = new LocalDatabaseManager(Sport_Intensity_Activity.this);
        db.updateSportDataIntensity(String.valueOf(id), intensity);

        //delete stored ID of sport
        sharedPrefManager.setInt(SharedPrefManager.KEY_LAST_SPORT_ID, -1);

        //create user feedback
        Toast.makeText(getApplicationContext(), getString(R.string.save_past_sport_saved), Toast.LENGTH_SHORT).show();
        //start questionnaire
        startActivity(new Intent(Sport_Intensity_Activity.this, Questionnaire_Activity.class));
        //close this activity
        finish();
    }
}