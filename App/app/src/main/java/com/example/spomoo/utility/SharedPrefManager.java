package com.example.spomoo.utility;

/*
 * SharedPrefManager of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.spomoo.questionnaire.QuestionnaireData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;

/*
 * Singleton Manager for Shared Preferences System
 * Uses an encrypted shared preference
 * Contains getter and setter for String, Integer, Boolean and Float
 * Contains helper functions
 */
public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private static SharedPrefManager mInstance;
    private Context mCtx;
    private SharedPreferences sharedPreferences;

    //Key constants
    public static String KEY_USER = "user"; //store user object
    public static String KEY_USER_ID = "user_id";   //store id of user object
    public static String KEY_USER_TEMP = "user_temporary";  //store not verified user
    public static String KEY_USER_VERIFY = "verify_user";   //store whether to show email input in Registration_Confirm_Fragment
    public static String KEY_RESET_PASSWORD_EMAIL = "reset_password_email"; //store email used to reset password
    public static String KEY_USER_NEXT_CHANGE = "user_next_change"; //store date for next possible personal data change
    public static String KEY_QUESTIONNAIRE_REMINDER_AMOUNT = "questionnaire_reminder_amount";   //store amount of daily questionnaire reminders
    public static String KEY_QUESTIONNAIRE_REMINDER_FIRST = "questionnaire_reminder_first"; //store first hour of questionnaire reminders
    public static String KEY_QUESTIONNAIRE_REMINDER_LAST = "questionnaire_reminder_last";   //store last hour of questionnaire reminders
    public static String KEY_ACCELEROMETER_ENABLED = "accelerometer_enabled";   //store boolean whether accelerometer recording is enabled
    public static String KEY_ROTATION_SENSOR_ENABLED = "rotation_sensor_enabled";   //store boolean whether rotation sensor recording is enabled
    public static String KEY_STEP_COUNTER_ENABLED = "step_counter_enabled"; //store boolean whether step counter recording is enabled
    public static String KEY_RECORD_ON_BOOT_ENABLED = "record_on_boot_enabled"; //store boolean whether start recording on smartphone boot is enabled
    public static String KEY_DYNAMIC_COLOR_ENABLED = "dynamic_color_enabled";   //store boolean whether dynamic colors are enabled
    public static String KEY_STREAK_VALUE = "streak";   //store streak count
    public static String KEY_STREAK_DATE = "streakDate";    //store date for streak calculation
    public static String KEY_RECORD_SPORT_TYPE = "TimerSport";  //store type of currently recorded sport
    public static String KEY_RECORD_SPORT_START = "TimerStart"; //store start time of currently recorded sport
    public static String KEY_RECORD_SPORT_DATE = "TimerDate";   //store date of currently recorded sport
    public static String KEY_LAST_SPORT_ID = "last_sport_id";   //store id of last added sport
    public static String KEY_CURRENT_QUESTIONNAIRE = "questionnaire";   //store current used questionnaire
    public static String KEY_STEP_COUNT_DATE = "step_count_date";   //store date for step counter calculation
    public static String KEY_STEP_COUNT_PREVIOUS = "step_count_previous";   //store previous steps for step counter calculation
    public static String KEY_STEP_COUNT_TODAY = "step_count_today"; //store today's steps for step counter calculation
    public static String KEY_DATA_DATE = "data_date";   //store date for data representation
    public static String KEY_PRIVACY_POLICY_CONFIRM = "privacy_policy_confirmed";   //store boolean whether privacy policy has been accepted

    //singleton pattern
    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
        this.sharedPreferences = getEncryptedSharedPref(mCtx);
    }

    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    private SharedPreferences getEncryptedSharedPref(Context context){
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return EncryptedSharedPreferences.create(
                    SHARED_PREF_NAME, masterKey,
                    mCtx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //getter and setter for Integer, String, Boolean and Float
    public void setInt(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public void setString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }

    public void setBool(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBool(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void setFloat(String key, float value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key){
        return sharedPreferences.getFloat(key, 0);
    }

    //delete all stored values
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //helper functions for user
    public UserData loadUser(String key){
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(UserData.class).getType();
        Gson gson = new Gson();
        UserData data = gson.fromJson(json, type);
        return data;
    }

    public void storeUser(String key, UserData userData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userData);
        editor.putString(key, json);
        editor.apply();
    }

    //helper functions for questionnaire
    public QuestionnaireData loadQuestionnaire(String key){
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(QuestionnaireData.class).getType();
        Gson gson = new Gson();
        QuestionnaireData data = gson.fromJson(json, type);
        return data;
    }

    public void storeQuestionnaire(String key, QuestionnaireData questionnaireData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(questionnaireData);
        editor.putString(key, json);
        editor.apply();
    }

    //helper functions
    public void setDefaultSettings(SharedPrefManager instance){
        instance.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_AMOUNT, 4);
        instance.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_FIRST, 8);
        instance.setFloat(SharedPrefManager.KEY_QUESTIONNAIRE_REMINDER_LAST, 22);
        instance.setBool(SharedPrefManager.KEY_ACCELEROMETER_ENABLED, true);
        instance.setBool(SharedPrefManager.KEY_ROTATION_SENSOR_ENABLED, true);
        instance.setBool(SharedPrefManager.KEY_STEP_COUNTER_ENABLED, true);
        instance.setBool(SharedPrefManager.KEY_RECORD_ON_BOOT_ENABLED, true);
    }

    public void deleteAll(SharedPrefManager instance){
        this.sharedPreferences.edit().clear().commit();
        instance.setBool(SharedPrefManager.KEY_PRIVACY_POLICY_CONFIRM, true);
    }

}
