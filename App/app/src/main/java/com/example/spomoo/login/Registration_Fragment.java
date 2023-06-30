package com.example.spomoo.login;

/*
 * Registration_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentLoginRegistrationBinding;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.example.spomoo.utility.Constants;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.UserData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
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
 * Contains a TextInputEditTexts for inputting the data
 * Contains a button for saving the inputted values and navigating to the Registration_Confirm_Fragment
 */
public class Registration_Fragment extends Fragment {

    private FragmentLoginRegistrationBinding binding;    //layout binding
    private boolean datePickerAdded = false;    //avoid that date picker can be opened multiple times at once

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache UI elements
        TextInputEditText usernameInput = binding.registerUsernameInput;
        TextInputEditText emailInput = binding.registerEmailInput;
        MaterialAutoCompleteTextView genderInput = binding.registerGenderInput;
        TextInputEditText birthdayInput = binding.registerBirthdayInput;
        TextInputEditText heightInput = binding.registerHeightInput;
        TextInputEditText weightInput = binding.registerWeightInput;
        MaterialButton continueButton = binding.registerContinueButton;

        //check that username has minimum one letter
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_items, genders);
        genderInput.setAdapter(arrayAdapter);

        //setup date picker for birthday
        //create date picker
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder
                .datePicker()
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new Registration_Fragment.DateValidator120years()).build())
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
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        birthdayInput.setOnClickListener(v -> {
            if(!datePickerAdded) {
                datePickerAdded = true;
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
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
                if(heightInput.getText().toString().isEmpty())
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
                if(weightInput.getText().toString().isEmpty())
                    weightInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //setup continue button
        continueButton.setOnClickListener(v -> {
            //check if there is a internet connection
            if(!RetrofitClient.internetIsConnected(getContext())){
                Toast.makeText(getContext(), getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if username and email-address is entered
            if(usernameInput.getText().toString().isEmpty()){
                Toast.makeText(getContext(), getString(R.string.register_enter_username), Toast.LENGTH_SHORT).show();
                return;
            }
            if(emailInput.getText().toString().isEmpty()){
                Toast.makeText(getContext(), getString(R.string.register_enter_email), Toast.LENGTH_SHORT).show();
                return;
            }

            //check for invalid inputs before saving
            if(usernameInput.getError() != null || emailInput.getError() != null || heightInput.getError() != null || weightInput.getError() != null){
                Toast.makeText(getContext(), getString(R.string.settings_edit_account_input_save_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //cache inputs
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String gender = "";
            if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_male))) gender = "Männlich";
            else if(genderInput.getText().toString().trim().equals(getString(R.string.settings_edit_account_gender_female))) gender = "Weiblich";
            String birthday = birthdayInput.getText().toString().trim();
            int height;
            if(!heightInput.getText().toString().isEmpty()) height = Integer.parseInt(heightInput.getText().toString().trim());
            int weight;
            if(!weightInput.getText().toString().isEmpty()) weight = Integer.parseInt(weightInput.getText().toString().trim());

            //save inputs
            UserData userData = new UserData();
            userData.setUsername(username);
            userData.setEmail(email);
            userData.setGender(gender);
            userData.setBirthdayFromUI(birthday);
            if(!heightInput.getText().toString().isEmpty()) userData.setHeight(Integer.parseInt(heightInput.getText().toString().trim()));
            if(!weightInput.getText().toString().isEmpty()) userData.setWeight(Integer.parseInt(weightInput.getText().toString().trim()));

            //make api call
            Call<ResponseDefault> call = RetrofitClient.getInstance().getApi()
                    .registerUser(username, email, gender, userData.getBirthdaySQL(), userData.getHeight(), userData.getWeight());
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    ResponseDefault responseDefault = response.body();

                    //check for special defined errors
                    if(response.code() == 422) {
                        Gson gson = new Gson();
                        ResponseDefault responseDefaultError = gson.fromJson(response.errorBody().charStream(), ResponseDefault.class);
                        if(responseDefaultError != null){
                            if(responseDefaultError.getMessage().equals("1"))   //email already registered
                                Toast.makeText(getContext(), getString(R.string.server_register_already_registered), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getContext(), getString(R.string.server_register_error), Toast.LENGTH_SHORT).show();    //other error
                        }
                        else Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //check for any server error
                    if(responseDefault == null){
                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //if no error
                    if (!responseDefault.isError()) {
                        //save temporary user data locally
                        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
                        sharedPrefManager.storeUser(SharedPrefManager.KEY_USER_TEMP, userData);

                        //navigate to Registration_Confirm_Fragment
                        NavHostFragment.findNavController(Registration_Fragment.this)
                                .navigate(R.id.action_registration_Fragment_to_registration_Confirm_Fragment);

                    //if error show error message
                    } else {
                        Toast.makeText(getContext(), responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) { }
            });

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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