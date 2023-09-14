package com.example.spomoo.login;

/*
 * Registration_Confirm_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentLoginRegistrationConfirmBinding;
import com.example.spomoo.mainscreen.MainActivity;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.ResponseLogin;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.example.spomoo.utility.Constants;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Used to confirm the user registration and set the user's password
 * Contains a TextInputEditTexts for inputting the passwords
 * Contains a button for verifying the inputted values and opening MainActivity on successful registration
 */
public class Registration_Confirm_Fragment extends Fragment {

    private FragmentLoginRegistrationConfirmBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginRegistrationConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getActivity());

        //cache UI elements
        TextInputLayout emailLayout = binding.registerConfirmEmailLayout;
        TextInputEditText emailInput = binding.registerConfirmEmailInput;
        TextInputEditText passwordTemporaryInput = binding.registerPasswordTemporaryInput;
        TextInputEditText passwordInput = binding.registerPasswordInput;
        TextInputEditText passwordRepeatInput = binding.registerPasswordRepeatInput;

        //check if email input is visible
        boolean emailEnabled = sharedPrefManager.getBool(SharedPrefManager.KEY_USER_VERIFY);
        if(emailEnabled){
            sharedPrefManager.setBool(SharedPrefManager.KEY_USER_VERIFY, false);
            emailLayout.setVisibility(View.VISIBLE);
            emailInput.setVisibility(View.VISIBLE);
            emailLayout.setEnabled(true);
            emailInput.setEnabled(true);
        } else {
            emailLayout.setVisibility(View.GONE);
            emailInput.setVisibility(View.GONE);
            emailLayout.setEnabled(false);
            emailInput.setEnabled(false);
        }

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

        //setup send again text click
        binding.registrationConfirmSendAgainText.setOnClickListener(v -> {
            //check if there is a internet connection
            if(!RetrofitClient.internetIsConnected(getContext())){
                Toast.makeText(getContext(), getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            //get email address
            String emailAddress = "";
            if(emailEnabled){
                if(emailInput.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), getString(R.string.register_enter_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                emailAddress = emailInput.getText().toString().trim();
            } else {
                emailAddress = sharedPrefManager.loadUser(SharedPrefManager.KEY_USER_TEMP).getEmail();
            }

            //make api call
            Call<ResponseDefault> call = RetrofitClient.getInstance().getApi()
                    .sendTempPassword(emailAddress);
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
                                Toast.makeText(getContext(), getString(R.string.server_register_confirm_not_exist), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();   //other errors
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
                    if (!responseDefault.isError())
                        Toast.makeText(getContext(), getString(R.string.server_register_confirm_send_mail), Toast.LENGTH_SHORT).show();
                    //if error show error message
                    else
                        Toast.makeText(getContext(), responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) { }
            });
        });

        //setup continue button
        binding.registerConfirmContinueButton.setOnClickListener(v -> {
            //check if there is a internet connection
            if(!RetrofitClient.internetIsConnected(getContext())){
                Toast.makeText(getContext(), getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            //cache inputs
            String temporaryPassword = passwordTemporaryInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String passwordRepeat = passwordRepeatInput.getText().toString().trim();

            //check if all passwords are entered
            if(temporaryPassword.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()){
                Toast.makeText(getContext(), getString(R.string.register_enter_all_passwords), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if password and repeated password match
            if(!password.equals(passwordRepeat))
            {
                Toast.makeText(getContext(), getString(R.string.register_password_no_match), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if new password meets requirements
            if(passwordInput.getError() != null){
                Toast.makeText(getContext(), getString(R.string.settings_edit_account_input_save_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //get email address
            String emailAddress = "";
            if(emailEnabled){
                if(emailInput.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), getString(R.string.register_enter_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(emailInput.getError() != null){
                    Toast.makeText(getContext(), getString(R.string.login_invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                emailAddress = emailInput.getText().toString().trim();
            } else {
                emailAddress = sharedPrefManager.loadUser(SharedPrefManager.KEY_USER_TEMP).getEmail();
            }

            //make api call
            Call<ResponseLogin> call = RetrofitClient.getInstance().getApi()
                    .verifyUser(emailAddress, temporaryPassword, password);
            call.enqueue(new Callback<ResponseLogin>() {
                @Override
                public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                    ResponseLogin responseLogin = response.body();

                    //check for special defined errors
                    if(response.code() == 422) {
                        Gson gson = new Gson();
                        ResponseDefault responseDefault = gson.fromJson(response.errorBody().charStream(), ResponseDefault.class);
                        if(responseDefault != null){
                            if(responseDefault.getMessage().equals("1"))    //email not registered
                                Toast.makeText(getContext(), getString(R.string.server_register_confirm_not_exist), Toast.LENGTH_SHORT).show();
                            else if(responseDefault.getMessage().equals("2"))   //invalid temporary password
                                Toast.makeText(getContext(), getString(R.string.server_register_confirm_invalid_credentials), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getContext(), getString(R.string.server_login_error), Toast.LENGTH_SHORT).show();   //other errors
                        }
                        else Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //check for any server error
                    if(responseLogin == null){
                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //if no error
                    if (!responseLogin.isError()) {
                        //save user data locally
                        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
                        sharedPrefManager.storeUser(SharedPrefManager.KEY_USER, responseLogin.getUser());
                        sharedPrefManager.setInt(SharedPrefManager.KEY_USER_ID, responseLogin.getUser().getId());
                        sharedPrefManager.storeUser(SharedPrefManager.KEY_USER_TEMP, null);
                        sharedPrefManager.setDefaultSettings(sharedPrefManager);

                        //open MainActivity and close this activity
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();

                    //if error show error message
                    } else {
                        Toast.makeText(getContext(), responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseLogin> call, Throwable t) { }
            });

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}