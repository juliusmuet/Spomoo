package com.example.spomoo.settings;

/*
 * Reset_Password_Activity of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.ActivityResetPasswordBinding;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.example.spomoo.utility.Constants;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Used to reset the current user password
 * Contains a TextInputEditTexts for inputting the passwords
 * Contains a button for verifying the inputted values and storing the new password
 */
public class Reset_Password_Activity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;   //layout binding
    private SharedPrefManager sharedPrefManager;    //sharedPrefManager
    private String emailAddress = "";     //store email address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add arrow in top bar for navigating back to Main Activity
        setSupportActionBar(binding.resetPasswordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get colors for dynamic coloring
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorTextPrimary = typedValue.data;
        //set colors
        getWindow().setStatusBarColor(colorPrimary);
        binding.resetPasswordToolbar.setBackgroundColor(colorPrimary);
        binding.resetPasswordToolbar.setTitleTextColor(colorTextPrimary);

        //cache UI elements
        TextInputEditText tempPasswordInput = binding.resetPasswordTempInput;
        TextInputEditText passwordInput = binding.resetPasswordInput;
        TextInputEditText passwordRepeatInput = binding.resetPasswordRepeatInput;

        //if user is not logged in show dialog to enter email address and send email afterwards
        boolean notLoggedIn = sharedPrefManager.loadUser(SharedPrefManager.KEY_USER) == null;
        if(notLoggedIn) emailAddress = sharedPrefManager.getString(SharedPrefManager.KEY_RESET_PASSWORD_EMAIL);
        else emailAddress = sharedPrefManager.loadUser(SharedPrefManager.KEY_USER).getEmail();

        //send email with temporary password
        requestEmail(emailAddress);

        //check password to fulfil requirements
        String passwordRegex = Constants.PASSWORD_REGEX;
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.compile(passwordRegex).matcher(passwordInput.getText().toString().trim()).find())
                    passwordInput.setError(getString(R.string.settings_edit_account_input_error));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //setup send again text click
        binding.registrationConfirmSendAgainText.setOnClickListener(v -> {
            requestEmail(emailAddress);
        });

        //setup button functionality
        binding.resetPasswordChangeButton.setOnClickListener(v -> {
            //check if there is a internet connection
            if(!RetrofitClient.internetIsConnected(Reset_Password_Activity.this)){
                Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            //cache inputs
            String tempPassword = tempPasswordInput.getText().toString().trim();
            String newPassword = passwordInput.getText().toString().trim();
            String newPasswordRepeat = passwordRepeatInput.getText().toString().trim();

            //check if all fields are filled
            if(tempPassword.isEmpty() || newPassword.isEmpty() || newPasswordRepeat.isEmpty()){
                Toast.makeText(getApplicationContext(), getString(R.string.register_enter_all_passwords), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if new password meets requirements
            if(passwordInput.getError() != null){
                Toast.makeText(Reset_Password_Activity.this, getString(R.string.settings_edit_account_input_save_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if new passwords equal
            if(!newPassword.equals(newPasswordRepeat)){
                Toast.makeText(getApplicationContext(), getString(R.string.register_password_no_match), Toast.LENGTH_SHORT).show();
                return;
            }

            //make api call
            Call<ResponseDefault> call = RetrofitClient.getInstance().getApi()
                    .updatePassword(emailAddress, tempPassword, newPassword);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    ResponseDefault responseDefault = response.body();

                    //check for special defined errors
                    if(response.code() == 422) {
                        Gson gson = new Gson();
                        ResponseDefault responseDefaultError = gson.fromJson(response.errorBody().charStream(), ResponseDefault.class);
                        if(responseDefaultError != null){
                            if(responseDefaultError.getMessage().equals("1"))    //email not registered
                                Toast.makeText(Reset_Password_Activity.this, getString(R.string.reset_password_temporary_incorrect), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_login_error), Toast.LENGTH_SHORT).show();   //other errors
                        }
                        else Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //check for any server error
                    if(responseDefault == null){
                        Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //if no error
                    if (!responseDefault.isError()) {
                        //feedback for user
                        Toast.makeText(Reset_Password_Activity.this, getString(R.string.reset_password_success), Toast.LENGTH_SHORT).show();
                        //exit activity
                        finish();
                    }
                    //if error show error message
                    else Toast.makeText(Reset_Password_Activity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) { }
            });

        });
    }

    //helper function to make the API call for getting the email with temporary password
    private void requestEmail(String emailAddress){
        //check if there is a internet connection
        if(!RetrofitClient.internetIsConnected(Reset_Password_Activity.this)){
            Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        //make api call
        Call<ResponseDefault> call = RetrofitClient.getInstance().getApi()
                .updatePasswordGetTemporary(emailAddress);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                ResponseDefault responseDefault = response.body();

                //check for special error
                if(response.code() == 422) {
                    Gson gson = new Gson();
                    ResponseDefault responseDefaultError = gson.fromJson(response.errorBody().charStream(), ResponseDefault.class);
                    if(responseDefaultError != null){
                        if(responseDefaultError.getMessage().equals("1"))    //email not registered
                            Toast.makeText(Reset_Password_Activity.this, getString(R.string.reset_password_email_not_exist), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();   //other errors
                    }
                    else Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                //check for any server error
                if(responseDefault == null){
                    Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                //if no error
                if (!responseDefault.isError()) Toast.makeText(Reset_Password_Activity.this, getString(R.string.server_register_confirm_send_mail), Toast.LENGTH_SHORT).show();
                //if error show error message
                else {
                    Toast.makeText(Reset_Password_Activity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) { }
        });
    }

    //overwrite for functionality of arrow in top action bar to navigate back to Main Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}