package com.example.spomoo.login;

/*
 * LoginActivity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivityLoginBinding;
import com.example.spomoo.settings.Data_Privacy_Activity;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/*
 * Login Activity showing the fragments Login_Fragment, Registration_Fragment and Registration_Confirm_Fragment
 * Contains a top action bar for navigation between these fragments
 * Navigation between these fragments is defined in nav_graph_login.xml
 * Asks for permission "Activity Recognition" on Android 10+
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding; //layout binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cache SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //inflate layout
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        //set colors of status bar
        getWindow().setStatusBarColor(colorPrimary);

        if(!sharedPrefManager.getBool(SharedPrefManager.KEY_PRIVACY_POLICY_CONFIRM))
            createPrivacyPolicyDialog(sharedPrefManager);
        else
            createActivityRecognitionDialog();
    }

    //dialog to ask for acceptance of privacy policy
    public void createPrivacyPolicyDialog(SharedPrefManager sharedPrefManager){
        //inflate view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_privacy_policy, binding.getRoot(), false);
        //setup click listener to open website with data privacy information
        dialogView.findViewById(R.id.dialog_privacy_policy_text).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Data_Privacy_Activity.DATA_PRIVACY_URL)));
        });
        //build dialog
        new MaterialAlertDialogBuilder(LoginActivity.this)
                .setView(dialogView)
                .setTitle(R.string.privacy_policy_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.privacy_policy_dialog_yes, (dialog, which) -> {
                    sharedPrefManager.setBool(SharedPrefManager.KEY_PRIVACY_POLICY_CONFIRM, true);
                    createActivityRecognitionDialog();
                })
                .show();
    }

    //dialog to ask for permission "Activity Recognition" on Android 10+
    public void createActivityRecognitionDialog(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                new MaterialAlertDialogBuilder(LoginActivity.this)
                        .setTitle(R.string.request_permission_dialog_title)
                        .setMessage(R.string.request_permission_dialog_content)
                        .setCancelable(false)
                        .setIcon(R.drawable.physical_activity)
                        .setPositiveButton(R.string.request_permission_dialog_okay, (dialog, which) -> {
                            //ask for permission for step counter sensor
                            String[] permissions = {Manifest.permission.ACTIVITY_RECOGNITION};
                            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
                        })
                        .show();
            }
        }
    }
}