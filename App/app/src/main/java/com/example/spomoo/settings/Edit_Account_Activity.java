package com.example.spomoo.settings;

/*
 * Edit_Account_Activity of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.spomoo.databinding.ActivityEditAccountBinding;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spomoo.R;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.ResponseLogin;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.example.spomoo.utility.Constants;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;
import com.example.spomoo.utility.UserData;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Edit Account Activity for editing the account data
 * Contains a top action bar for navigating back to Main Activity
 * Contains a TextInputEditTexts for inputting the data
 * Contains a floating action button for saving the inputted values
 */
public class Edit_Account_Activity extends AppCompatActivity {

    private ActivityEditAccountBinding binding;

    private boolean datePickerAdded = false;    //avoid that date picker can be opened multiple times at once
    private UserData userData;  //current user data
    private SharedPrefManager sharedPrefManager;    //cache sharedPrefManager


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        //apply dynamic color for Android 12+
        if(sharedPrefManager.getBool(SharedPrefManager.KEY_DYNAMIC_COLOR_ENABLED))
            DynamicColors.applyIfAvailable(this);

        //setup layout
        binding = ActivityEditAccountBinding.inflate(getLayoutInflater());
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

        //cache UI elements
        TextInputEditText usernameInput = binding.settingsEditAccountUsernameInput;
        TextInputEditText emailInput = binding.settingsEditAccountEmailInput;
        MaterialAutoCompleteTextView genderInput = binding.settingsEditAccountGenderInput;
        TextInputEditText birthdayInput = binding.settingsEditAccountBirthdayInput;
        TextInputEditText heightInput = binding.settingsEditAccountHeightInput;
        TextInputEditText weightInput = binding.settingsEditAccountWeightInput;

        //set initial values
        userData = sharedPrefManager.loadUser(SharedPrefManager.KEY_USER);
        usernameInput.setText(userData.getUsername());
        emailInput.setText(userData.getEmail());
        if(userData.getGender().equals("Männlich")) genderInput.setText(getString(R.string.settings_edit_account_gender_male));
        if(userData.getGender().equals("Weiblich")) genderInput.setText(getString(R.string.settings_edit_account_gender_female));
        if(userData.getGender().equals("")) genderInput.setText(getString(R.string.settings_edit_account_gender_no));
        if(userData.getBirthday() != null) birthdayInput.setText(userData.getBirthday());
        if(userData.getHeight() != -1) heightInput.setText(String.valueOf(userData.getHeight()));
        if(userData.getWeight() != -1) weightInput.setText(String.valueOf(userData.getWeight()));

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameInput.getText().toString().trim().length() < 2)
                    usernameInput.setError(getString(R.string.settings_edit_account_input_error));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //check that email address has valid format
        String emailRegex = Constants.EMAIL_REGEX;
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.compile(emailRegex).matcher(emailInput.getText().toString().trim()).find())
                    emailInput.setError(getString(R.string.settings_edit_account_input_error));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //setup gender dropdown menu
        String[] genders = {getString(R.string.settings_edit_account_gender_male), getString(R.string.settings_edit_account_gender_female),
                getString(R.string.settings_edit_account_gender_no)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_items, genders);
        genderInput.setAdapter(arrayAdapter);

        //setup date picker for birthday
        //create date picker
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder
                .datePicker()
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new DateValidator120years()).build())
                .setTitleText(getText(R.string.settings_edit_account_birthday_enter))
                .build();
        //setup on confirm button listener
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            long dateInMillis = Long.parseLong(selection.toString());
            Date date = new Date(dateInMillis);
            DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_UI);  //convert inputted date to dd.mm.yyyy format
            birthdayInput.setText(formatter.format(date));   //set date as text of text view
            datePickerAdded = false;    //date picker is not shown anymore
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
        //open date picker on click on birthday text view
        birthdayInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(!datePickerAdded && hasFocus) {
                datePickerAdded = true;
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        birthdayInput.setOnClickListener(v -> {
            if(!datePickerAdded) {
                datePickerAdded = true;
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        //check that inputted height has 3 numbers
        heightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(heightInput.getText().toString().trim().length() < 3 || heightInput.getText().toString().trim().length() > 3)
                    heightInput.setError(getString(R.string.settings_edit_account_input_error));
                if(heightInput.getText().toString().trim().length() == 0)
                    heightInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //check that inputted weight has min. 2 numbers
        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(weightInput.getText().toString().trim().length() < 2 || weightInput.getText().toString().trim().length() > 3)
                    weightInput.setError(getString(R.string.settings_edit_account_input_error));
                if(weightInput.getText().toString().trim().length() == 0)
                    weightInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //setup save FAB
        binding.settingsEditAccountSaveFab.setOnClickListener(v -> {
            //check if there are changes
            if(noChangesHappened(userData, usernameInput, emailInput, genderInput, birthdayInput, heightInput, weightInput)){
                finish();
                return;
            }

            //check if 7 days since last change have passed
            if(!sharedPrefManager.getString(SharedPrefManager.KEY_USER_NEXT_CHANGE).equals("")
                    && !sharedPrefManager.getString(SharedPrefManager.KEY_USER_NEXT_CHANGE).equals(TimeDateFormatter.getDateString()))
            {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.settings_edit_account_input_save_date) + " " + sharedPrefManager.getString(SharedPrefManager.KEY_USER_NEXT_CHANGE),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //check for invalid inputs before saving
            if(usernameInput.getError() != null || emailInput.getError() != null || heightInput.getError() != null || weightInput.getError() != null){
                Toast.makeText(getApplicationContext(), getString(R.string.settings_edit_account_input_save_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //setup dialog to enter password
            //inflate view
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_password, binding.getRoot(), false);
            //cache dialog UI elements
            TextInputEditText passwordInput = dialogView.findViewById(R.id.dialog_password_input);
            MaterialTextView forgotPasswordText = dialogView.findViewById(R.id.dialog_forgot_password_text);

            //set click listener for forgot password text to open the Reset_Password_Activity
            forgotPasswordText.setOnClickListener(v1 -> {
                startActivity(new Intent(this, Reset_Password_Activity.class));
            });

            //build dialog
            new MaterialAlertDialogBuilder(Edit_Account_Activity.this)
                    .setView(dialogView)
                    .setTitle(getString(R.string.settings_edit_account_password_dialog_title))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.settings_edit_account_password_dialog_confirm), (dialog, which) -> {
                        //if password is empty
                        if(passwordInput.getText().toString().trim().isEmpty()){
                            Toast.makeText(Edit_Account_Activity.this, getString(R.string.login_no_password), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //get inputted values
                        String username = usernameInput.getText().toString().trim();
                        String email = emailInput.getText().toString().trim();
                        String gender = "";
                        if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_male))) gender = "Männlich";
                        if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_female))) gender = "Weiblich";
                        String birthday = TimeDateFormatter.toSQLDate(birthdayInput.getText().toString().trim());
                        int height;
                        if(!heightInput.getText().toString().isEmpty()) height = Integer.parseInt(heightInput.getText().toString().trim());
                        else height = -1;
                        int weight;
                        if(!weightInput.getText().toString().isEmpty()) weight = Integer.parseInt(weightInput.getText().toString().trim());
                        else weight = -1;

                        //make API call
                        makeApiCall(sharedPrefManager.getInt(SharedPrefManager.KEY_USER_ID), username, email, gender, birthday,
                                height, weight, passwordInput.getText().toString().trim());
                    })
                    .show();
        });

    }

    //helper function to make the API call
    private void makeApiCall(int id, String username, String email, String gender, String birthday, int height, int weight, String password){
        Call<ResponseLogin> call = RetrofitClient.getInstance().getApi()
                .updateUser(id, username, email, gender, birthday, height, weight, password);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                ResponseLogin responseLogin = response.body();

                //check for special defined errors
                if(response.code() == 422) {
                    Gson gson = new Gson();
                    ResponseDefault responseDefault = gson.fromJson(response.errorBody().charStream(), ResponseDefault.class);
                    if(responseDefault != null){
                        if(responseDefault.getMessage().equals("1"))    //password is incorrect
                            Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_edit_user_data_incorrect_password), Toast.LENGTH_SHORT).show();
                        else if(responseDefault.getMessage().equals("2"))   //email is taken
                            Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_edit_user_data_email_taken), Toast.LENGTH_SHORT).show();
                        else if(responseDefault.getMessage().equals("3"))   //error while updating
                            Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_edit_user_data_error), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();   //other errors
                    }
                    else Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //check for any server error
                if(responseLogin == null){
                    Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //on success
                if (!responseLogin.isError() && (responseLogin.getUser() != null)){
                    //save updated data
                    sharedPrefManager.storeUser(SharedPrefManager.KEY_USER, responseLogin.getUser());
                    //sharedPrefManager.setString(SharedPrefManager.KEY_USER_NEXT_CHANGE, TimeDateFormatter.getNextDateString(7));

                    //feedback for user
                    Toast.makeText(Edit_Account_Activity.this, getString(R.string.settings_edit_account_saved), Toast.LENGTH_SHORT).show();

                    //navigate back to Main Activity
                    finish();
                } else {
                    //feedback for user
                    Toast.makeText(Edit_Account_Activity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) { }
        });
    }

    //helper function to check if values have been changed
    private boolean noChangesHappened(UserData userData, TextInputEditText usernameInput, TextInputEditText emailInput, MaterialAutoCompleteTextView genderInput,
                                      TextInputEditText birthdayInput, TextInputEditText heightInput, TextInputEditText weightInput){
        if(!userData.getUsername().equals(usernameInput.getText().toString().trim())) return false;
        if(!userData.getEmail().equals(emailInput.getText().toString().trim())) return false;
        if(!userData.getBirthday().equals(birthdayInput.getText().toString().trim())) return false;

        if(userData.getHeight() == -1 && !heightInput.getText().toString().trim().equals("")) return false;
        if(userData.getWeight() == -1 && !weightInput.getText().toString().trim().equals("")) return false;
        if(!String.valueOf(userData.getHeight()).equals(heightInput.getText().toString().trim())) return false;
        if(!String.valueOf(userData.getWeight()).equals(weightInput.getText().toString().trim())) return false;

        String gender = "";
        if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_male))) gender = "Männlich";
        if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_female))) gender = "Weiblich";
        if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_no))) gender = "";
        if(!userData.getGender().equals(gender)) return false;

        return true;
    }

    //overwrite for functionality of arrow in top action bar to navigate back to Main Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //custom date validator to only allow 120 years backwards
    private class DateValidator120years implements CalendarConstraints.DateValidator {

        private Calendar dateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  //calendar for inputted date
        private Calendar todayCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //calendar for today

        public final Creator<com.example.spomoo.recordsport.DateValidatorWeekdays> CREATOR =
                new Creator<com.example.spomoo.recordsport.DateValidatorWeekdays>() {
                    @Override
                    public com.example.spomoo.recordsport.DateValidatorWeekdays createFromParcel(Parcel source) {
                        return new com.example.spomoo.recordsport.DateValidatorWeekdays();
                    }

                    @Override
                    public com.example.spomoo.recordsport.DateValidatorWeekdays[] newArray(int size) {
                        return new com.example.spomoo.recordsport.DateValidatorWeekdays[size];
                    }
                };

        //logic for checking inputted date
        @Override
        public boolean isValid(long date) {
            dateCalendar.setTimeInMillis(date);
            todayCalendar.setTimeInMillis(System.currentTimeMillis());
            long difference = todayCalendar.getTimeInMillis()-dateCalendar.getTimeInMillis();
            long maxDifference = Math.round(120*365.25*24*60*60*1000);
            return difference <= maxDifference && dateCalendar.getTimeInMillis() <= todayCalendar.getTimeInMillis();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof com.example.spomoo.recordsport.DateValidatorWeekdays)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            Object[] hashedFields = {};
            return Arrays.hashCode(hashedFields);
        }
    }
}