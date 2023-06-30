package com.example.spomoo.settings;

/*
 * Data_Privacy_Activity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivityDataPrivacyBinding;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.color.DynamicColors;

/*
 * Contains clickable text to open website with data privacy information
 * Contains clickable text to open eMail client to send an eMail about the data privacy
 */

public class Data_Privacy_Activity extends AppCompatActivity {

    //TODO: change URL
    public static final String DATA_PRIVACY_URL = "https://sport.ruhr-uni-bochum.de/de/forschung-ehealth-und-sports-analytics"; //URL of the data privacy website

    private ActivityDataPrivacyBinding binding; //layout binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cache sharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivityDataPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        //setup click on text to open website about privacy policy in internet browser
        binding.settingsDataPrivacyContentText.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DATA_PRIVACY_URL)));
        });

        //setup click on email address to open email app
        binding.settingsDataPrivacyMailAddressText.setOnClickListener(v -> {
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?subject=" + getString(R.string.settings_data_privacy_email_subject) +
                    "&body=" + getString(R.string.settings_data_privacy_email_content) + "&to=" + getString(R.string.settings_data_privacy_email_address));
            mailIntent.setData(data);
            try {
                startActivity(Intent.createChooser(mailIntent, getString(R.string.settings_data_privacy_email_chooser)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(Data_Privacy_Activity.this, getString(R.string.settings_data_privacy_email_chooser_failure), Toast.LENGTH_SHORT).show();
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