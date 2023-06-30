package com.example.spomoo.login;

/*
 * Login_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentLoginBinding;
import com.example.spomoo.mainscreen.MainActivity;
import com.example.spomoo.serverinteraction.ResponseDefault;
import com.example.spomoo.serverinteraction.ResponseLogin;
import com.example.spomoo.serverinteraction.RetrofitClient;
import com.example.spomoo.settings.Reset_Password_Activity;
import com.example.spomoo.utility.Constants;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.StoreRecordedDataService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Contains two input fields for the eMail-address and password
 * Contains two clickable text views to open the Reset_Password_Activity and to navigate to the Registration_Fragment
 * Contains a button to check the inputted data on the remote database and navigates to the MainActivity if values are correct
 */
public class Login_Fragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());

        //cache text fields
        TextInputEditText emailInput = binding.loginEmailInput;
        TextInputEditText passwordInput = binding.loginPasswordInput;

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

        //setup forgot password text to open the Reset_Password_Activity
        binding.loginForgotPasswordText.setOnClickListener(v -> {
            if(emailInput.getError() != null || emailInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.login_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            sharedPrefManager.setString(SharedPrefManager.KEY_RESET_PASSWORD_EMAIL, emailInput.getText().toString().trim());
            startActivity(new Intent(getContext(), Reset_Password_Activity.class));
        });

        //setup confirm registration text to navigate to Fragment_Login_Registration_Confirm
        binding.loginRegisterConfirmText.setOnClickListener(v -> {
            sharedPrefManager.setBool(SharedPrefManager.KEY_USER_VERIFY, true);
            NavHostFragment.findNavController(Login_Fragment.this)
                    .navigate(R.id.action_login_Fragment_to_registration_Confirm_Fragment);
        });

        //setup login button
        binding.loginButton.setOnClickListener(v -> {
            //check if there is a internet connection
            if(!RetrofitClient.internetIsConnected(getContext())){
                Toast.makeText(getContext(), getString(R.string.server_no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            //check if email is valid
            if(emailInput.getError() != null || emailInput.getText().toString().trim().isEmpty()){
                Toast.makeText(getContext(), getString(R.string.login_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            //check if password is entered
            if(passwordInput.getText().toString().trim().isEmpty()){
                Toast.makeText(getContext(), getString(R.string.login_no_password), Toast.LENGTH_SHORT).show();
                return;
            }

            //create dialog to show loading
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, binding.getRoot(), false);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                    .setView(dialogView)
                    .setTitle(getString(R.string.login_dialog_loading_title))
                    .setMessage(getString(R.string.login_dialog_loading_content))
                    .setCancelable(false)
                    .show();

            //make api call
            Call<ResponseLogin> call = RetrofitClient.getInstance().getApi()
                    .loginUser(emailInput.getText().toString().trim(), passwordInput.getText().toString().trim());
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
                                Toast.makeText(getContext(), getString(R.string.server_login_user_not_exist), Toast.LENGTH_SHORT).show();
                            else if(responseDefault.getMessage().equals("2"))   //invalid credentials
                                Toast.makeText(getContext(), getString(R.string.server_login_invalid_credentials), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getContext(), getString(R.string.server_login_error), Toast.LENGTH_SHORT).show();   //other errors
                        }
                        else Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();
                        return;
                    }

                    //check for any server error
                    if(responseLogin == null){
                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();
                        return;
                    }

                    //if no error
                    if (!responseLogin.isError()) {
                        //start foreground service for data insertion in local SQL database
                        Gson gson = new Gson();
                        Intent serviceIntent = new Intent(getContext(), StoreRecordedDataService.class);
                        serviceIntent.putExtra("accelerometer", gson.toJson(responseLogin.getAccelerometer()));
                        serviceIntent.putExtra("rotation", gson.toJson(responseLogin.getRotation()));
                        serviceIntent.putExtra("steps", gson.toJson(responseLogin.getSteps()));
                        serviceIntent.putExtra("sport", gson.toJson(responseLogin.getSport()));
                        serviceIntent.putExtra("questionnaire", gson.toJson(responseLogin.getQuestionnaire()));
                        getContext().startService(serviceIntent);

                        //save user data locally
                        sharedPrefManager.storeUser(SharedPrefManager.KEY_USER, responseLogin.getUser());
                        sharedPrefManager.setInt(SharedPrefManager.KEY_USER_ID, responseLogin.getUser().getId());

                        //set default settings
                        sharedPrefManager.setDefaultSettings(sharedPrefManager);

                        //close loading dialog
                        alertDialog.cancel();

                        //open MainActivity and close LoginActivity
                        FragmentActivity currentActivity = getActivity();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        currentActivity.finish();

                    //if error show error message
                    } else {
                        alertDialog.cancel();
                        Toast.makeText(getContext(), responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseLogin> call, Throwable t) { }
            });

        });

        //setup not registered text to open Registration_Fragment
        binding.loginRegisterText.setOnClickListener(v -> {
            NavHostFragment.findNavController(Login_Fragment.this)
                    .navigate(R.id.action_login_Fragment_to_registration_Fragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}