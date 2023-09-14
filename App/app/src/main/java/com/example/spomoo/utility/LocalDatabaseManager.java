package com.example.spomoo.utility;

/*
 * LocalDatabaseManager of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

/*
 * Manager for the local SQL database
 * Has a password protection
 * Contains tables for Sport Activities, Questionnaires, ...
 * Contains getter and setter for the tables
 */
public class LocalDatabaseManager extends SQLiteOpenHelper {

    //attributes
    private Context context;    //application context
    private SQLiteDatabase db;  //writeable database
    private static final String DATABASE_PASSWORD = "fEZP4xTX632asFyNo2yhEHo+xc7nH4IIc4ZClelUxGjFnju78zW9x0TusqdRe5Tz"; //password to open database
    private static final String DATABASE_NAME = "SpomooDatabase.db";    //local database name
    private static final int DATABASE_VERSION = 1;  //local database version

    //constructor
    public LocalDatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase.loadLibs(context);
        this.db = this.getWritableDatabase(DATABASE_PASSWORD);
    }

    //general columns
    private static final String USER_COLUMN_ID = "user_id";
    private static final String COLUMN_SENT = "been_sent";

    //table for sport activities
    public static final String SPORT_TABLE_NAME = "sport_activities";
    private static final String SPORT_COLUMN_ID = "sport_id";
    private static final String SPORT_COLUMN_TYPE = "sport_type";
    private static final String SPORT_COLUMN_DATE = "sport_date";
    private static final String SPORT_COLUMN_START = "sport_start";
    private static final String SPORT_COLUMN_DURATION = "sport_duration";
    private static final String SPORT_COLUMN_INTENSITY = "sport_intensity";
    private static final String SPORT_TABLE_CREATE =
            "CREATE TABLE " + SPORT_TABLE_NAME + " (" +
                    SPORT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SPORT_COLUMN_TYPE + " TEXT, " +
                    SPORT_COLUMN_DATE + " TEXT, " +
                    SPORT_COLUMN_START + " TEXT, " +
                    SPORT_COLUMN_DURATION + " TEXT, " +
                    SPORT_COLUMN_INTENSITY + " INTEGER, " +
                    USER_COLUMN_ID + " INTEGER, " +
                    COLUMN_SENT + " INTEGER" + ");";

    //table for questionnaire data
    public static final String QUESTIONNAIRE_TABLE_NAME = "questionnaire_data";
    private static final String QUESTIONNAIRE_COLUMN_ID = "questionnaire_id";
    private static final String QUESTIONNAIRE_COLUMN_DATE = "questionnaire_date";
    private static final String QUESTIONNAIRE_COLUMN_TIME = "questionnaire_time";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_SATISFIED = "questionnaire_mdbf_satisfied";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_CALM = "questionnaire_mdbf_calm";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_WELL = "questionnaire_mdbf_well";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_RELAXED = "questionnaire_mdbf_relaxed";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_ENERGETIC = "questionnaire_mdbf_energetic";
    private static final String QUESTIONNAIRE_COLUMN_MDBF_AWAKE = "questionnaire_mdbf_awake";
    private static final String QUESTIONNAIRE_COLUMN_EVENT_NEGATIVE = "questionnaire_event_negative";
    private static final String QUESTIONNAIRE_COLUMN_EVENT_POSITIVE = "questionnaire_event_positive";
    private static final String QUESTIONNAIRE_COLUMN_SOCIAL_ALONE = "questionnaire_social_alone";
    private static final String QUESTIONNAIRE_COLUMN_SOCIAL_DISLIKE = "questionnaire_social_dislilke";
    private static final String QUESTIONNAIRE_COLUMN_SOCIAL_PEOPLE = "questionnaire_social_people";
    private static final String QUESTIONNAIRE_COLUMN_LOCATION = "questionnaire_location";
    private static final String QUESTIONNAIRE_COLUMN_RUMINATION_PROPERTIES = "questionnaire_rumination_properties";
    private static final String QUESTIONNAIRE_COLUMN_RUMINATION_REHASH = "questionnaire_rumination_rehash";
    private static final String QUESTIONNAIRE_COLUMN_RUMINATION_TURNOFF = "questionnaire_rumination_turnoff";
    private static final String QUESTIONNAIRE_COLUMN_RUMINATION_DISPUTE = "questionnaire_rumination_dispute";
    private static final String QUESTIONNAIRE_COLUMN_SELFWORTH_SATISFIED = "questionnaire_selfworth_satisfied";
    private static final String QUESTIONNAIRE_COLUMN_SELFWORTH_DISSATISFIED = "questionnaire_selfworth_dissatisfied";
    private static final String QUESTIONNAIRE_COLUMN_IMPULSIVE = "questionnaire_impulsive";
    private static final String QUESTIONNAIRE_COLUMN_IMPULSIVE_ANGRY = "questionnaire_impulsive_angry";
    private static final String QUESTIONNAIRE_COLUMN_MESSAGE = "questionnaire_message";
    private static final String QUESTIONNAIRE_TABLE_CREATE =
            "CREATE TABLE " + QUESTIONNAIRE_TABLE_NAME + " (" +
                    QUESTIONNAIRE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    QUESTIONNAIRE_COLUMN_DATE + " TEXT, " +
                    QUESTIONNAIRE_COLUMN_TIME + " TEXT, " +
                    QUESTIONNAIRE_COLUMN_MDBF_SATISFIED + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MDBF_CALM + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MDBF_WELL + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MDBF_RELAXED + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MDBF_ENERGETIC + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MDBF_AWAKE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_EVENT_NEGATIVE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_EVENT_POSITIVE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_SOCIAL_ALONE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_SOCIAL_DISLIKE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_SOCIAL_PEOPLE + " TEXT, " +
                    QUESTIONNAIRE_COLUMN_LOCATION + " TEXT, " +
                    QUESTIONNAIRE_COLUMN_RUMINATION_PROPERTIES + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_RUMINATION_REHASH + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_RUMINATION_TURNOFF + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_RUMINATION_DISPUTE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_SELFWORTH_SATISFIED + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_SELFWORTH_DISSATISFIED + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_IMPULSIVE + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_IMPULSIVE_ANGRY + " INTEGER, " +
                    QUESTIONNAIRE_COLUMN_MESSAGE + " TEXT, " +
                    USER_COLUMN_ID + " INTEGER, " +
                    COLUMN_SENT + " INTEGER" + ");";

    //table for accelerometer data
    public static final String ACCELEROMETER_TABLE_NAME = "accelerometer_data";
    private static final String ACCELEROMETER_COLUMN_DATE = "accelerometer_date";
    private static final String ACCELEROMETER_COLUMN_TIME = "accelerometer_time";
    private static final String ACCELEROMETER_COLUMN_X = "accelerometer_x_axis";
    private static final String ACCELEROMETER_COLUMN_Y = "accelerometer_y_axis";
    private static final String ACCELEROMETER_COLUMN_Z = "accelerometer_z_axis";
    private static final String ACCELEROMETER_COLUMN_ACCELERATION = "accelerometer_acceleration";
    private static final String ACCELEROMETER_TABLE_CREATE =
            "CREATE TABLE " + ACCELEROMETER_TABLE_NAME + " (" +
                    ACCELEROMETER_COLUMN_DATE + " TEXT, " +
                    ACCELEROMETER_COLUMN_TIME + " TEXT, " +
                    ACCELEROMETER_COLUMN_X + " DOUBLE, " +
                    ACCELEROMETER_COLUMN_Y + " DOUBLE, " +
                    ACCELEROMETER_COLUMN_Z + " DOUBLE, " +
                    ACCELEROMETER_COLUMN_ACCELERATION + " DOUBLE, " +
                    USER_COLUMN_ID + " INTEGER, " +
                    COLUMN_SENT + " INTEGER" + ");";

    //table for rotation data
    public static final String ROTATION_TABLE_NAME = "rotation_data";
    private static final String ROTATION_COLUMN_DATE = "rotation_date";
    private static final String ROTATION_COLUMN_TIME = "rotation_time";
    private static final String ROTATION_COLUMN_X = "rotation_x_rotation";
    private static final String ROTATION_COLUMN_Y = "rotation_y_rotation";
    private static final String ROTATION_COLUMN_Z = "rotation_z_rotation";
    private static final String ROTATION_COLUMN_SCALAR = "rotation_scalar";
    private static final String ROTATION_TABLE_CREATE =
            "CREATE TABLE " + ROTATION_TABLE_NAME + " (" +
                    ROTATION_COLUMN_DATE + " TEXT, " +
                    ROTATION_COLUMN_TIME + " TEXT, " +
                    ROTATION_COLUMN_X + " DOUBLE, " +
                    ROTATION_COLUMN_Y + " DOUBLE, " +
                    ROTATION_COLUMN_Z + " DOUBLE, " +
                    ROTATION_COLUMN_SCALAR + " DOUBLE, " +
                    USER_COLUMN_ID + " INTEGER, " +
                    COLUMN_SENT + " INTEGER" + ");";

    //table for steps data
    public static final String STEPS_TABLE_NAME = "steps_data";
    private static final String STEPS_COLUMN_DATE = "steps_date";
    private static final String STEPS_COLUMN_VALUE = "steps_value";
    private static final String STEPS_TABLE_CREATE =
            "CREATE TABLE " + STEPS_TABLE_NAME + " (" +
                    STEPS_COLUMN_DATE + " TEXT, " +
                    STEPS_COLUMN_VALUE + " INTEGER, " +
                    USER_COLUMN_ID + " INTEGER, " +
                    COLUMN_SENT + " INTEGER" + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SPORT_TABLE_CREATE);
        db.execSQL(QUESTIONNAIRE_TABLE_CREATE);
        db.execSQL(ACCELEROMETER_TABLE_CREATE);
        db.execSQL(ROTATION_TABLE_CREATE);
        db.execSQL(STEPS_TABLE_CREATE);
        //TODO: add other tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SPORT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONNAIRE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCELEROMETER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ROTATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STEPS_TABLE_NAME);
        onCreate(db);
    }

    public void resetDB(){
        db.execSQL("DROP TABLE IF EXISTS " + SPORT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONNAIRE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCELEROMETER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ROTATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STEPS_TABLE_NAME);
        onCreate(db);
    }

    public Cursor readAllDataFromTable(String table){
        String query = "SELECT * FROM " + table;

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //helper function for feedback
    void response(long result){
        /*if(result == -1){
            System.out.println("Failed");
        }else{
            System.out.println("Successful");
        }*/
    }

    //methods for sport activities table
    public int addSportData(SportData sportData, int beenSent){
        ContentValues cv = new ContentValues();

        cv.put(SPORT_COLUMN_TYPE, sportData.getType());
        if(beenSent == 0) cv.put(SPORT_COLUMN_DATE, sportData.getDate());
        else cv.put(SPORT_COLUMN_DATE, TimeDateFormatter.toUIDate(sportData.getDate()));
        cv.put(SPORT_COLUMN_START, sportData.getStart());
        cv.put(SPORT_COLUMN_DURATION, sportData.getDuration());
        cv.put(SPORT_COLUMN_INTENSITY, sportData.getIntensity());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, beenSent);

        long result = db.insert(SPORT_TABLE_NAME,null, cv);
        response(result);

        if(beenSent == 0) return getLastSportID(sportData.getDate());
        else return 0;
    }

    //return ID of added sport
    private int getLastSportID(String date){
        String query = "SELECT * FROM " + SPORT_TABLE_NAME + " WHERE " + SPORT_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0)
            return -1;

        int id = -1;
        while(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return id;
    }

    public void updateSportDataIntensity(String id, int intensity){
        ContentValues cv = new ContentValues();

        cv.put(SPORT_COLUMN_INTENSITY, intensity);

        long result = db.update(SPORT_TABLE_NAME, cv, SPORT_COLUMN_ID+"=?", new String[]{id});
        response(result);
    }

    public void updateSportData(SportData sportData, String id, int sent){
        ContentValues cv = new ContentValues();

        cv.put(SPORT_COLUMN_TYPE, sportData.getType());
        cv.put(SPORT_COLUMN_DATE, sportData.getDate());
        cv.put(SPORT_COLUMN_START, sportData.getStart());
        cv.put(SPORT_COLUMN_DURATION, sportData.getDuration());
        cv.put(SPORT_COLUMN_INTENSITY, sportData.getIntensity());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, sent);

        long result = db.update(SPORT_TABLE_NAME, cv, SPORT_COLUMN_ID+"=?", new String[]{id});
        response(result);
    }

    public ArrayList<SportData> readSportDataFromDate(String date){
        String query = "SELECT * FROM " + SPORT_TABLE_NAME + " WHERE " + SPORT_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<SportData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()) {
            SportData data = new SportData();
            data.setSportId(cursor.getInt(0));
            data.setType(cursor.getString(1));
            data.setDate(cursor.getString(2));
            data.setStart(cursor.getString(3));
            data.setDuration(cursor.getString(4));
            data.setIntensity(cursor.getInt(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public ArrayList<SportData> readUnsendSportData(){
        String query = "SELECT * FROM " + SPORT_TABLE_NAME + " WHERE " + COLUMN_SENT + "= 0";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<SportData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()) {
            SportData data = new SportData();
            data.setSportId(cursor.getInt(0));
            data.setType(cursor.getString(1));
            data.setDate(TimeDateFormatter.toSQLDate(cursor.getString(2))); //return date as YYYY-MM-DD
            data.setStart(cursor.getString(3));
            data.setDuration(cursor.getString(4));
            data.setIntensity(cursor.getInt(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public void markSportDataAsSent(){
        String query = "UPDATE " + SPORT_TABLE_NAME + " SET " + COLUMN_SENT + "= 1 "  + " WHERE " + COLUMN_SENT + "= 0";
        db.execSQL(query);
    }

    public void deleteSportData(String id){
        long result = db.delete(SPORT_TABLE_NAME, SPORT_COLUMN_ID+"=?", new String[]{id});
        response(result);
    }

    public void deleteAllSportData(){
        db.execSQL("DELETE FROM " + SPORT_TABLE_NAME);
    }

    public int getSportDurationFromDateInMinutes(String date){
        ArrayList<SportData> data = readSportDataFromDate(date);
        int min = 0;

        for (SportData d : data){
            min += Integer.parseInt(d.getDuration().substring(0,2)) * 60;
            min += Integer.parseInt(d.getDuration().substring(3,5));
        }

        return min;
    }

    public ArrayList<Integer> getSportDurationOfWeek(String date){
        ArrayList<Integer> durations = new ArrayList<>();

        ArrayList<String> dates = TimeDateFormatter.getWeekDates(date);
        for(String s : dates){
            durations.add(getSportDurationFromDateInMinutes(s));
        }

        return  durations;
    }

    //methods for questionnaire data table
    public void addQuestionnaireData(QuestionnaireData data, int beenSent){
        ContentValues cv = new ContentValues();

        if(beenSent == 0) cv.put(QUESTIONNAIRE_COLUMN_DATE, data.getDate());
        else cv.put(QUESTIONNAIRE_COLUMN_DATE, TimeDateFormatter.toUIDate(data.getDate()));
        cv.put(QUESTIONNAIRE_COLUMN_TIME, data.getTime());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_SATISFIED, data.getMdbfSatsified());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_CALM, data.getMdbfCalm());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_WELL, data.getMdbfWell());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_RELAXED, data.getMdbfRelaxed());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_ENERGETIC, data.getMdbfEnergetic());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_AWAKE, data.getMdbfAwake());
        cv.put(QUESTIONNAIRE_COLUMN_EVENT_NEGATIVE, data.getEventNegative());
        cv.put(QUESTIONNAIRE_COLUMN_EVENT_POSITIVE, data.getEventPositive());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_ALONE, data.getSocialAlone());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_DISLIKE, data.getSocialDislike());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_PEOPLE, data.getSocialPeople());
        cv.put(QUESTIONNAIRE_COLUMN_LOCATION, data.getLocation());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_PROPERTIES, data.getRuminationProperties());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_REHASH, data.getRuminationRehash());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_TURNOFF, data.getRuminationTurnOff());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_DISPUTE, data.getRuminationDispute());
        cv.put(QUESTIONNAIRE_COLUMN_SELFWORTH_SATISFIED, data.getSelfworthSatisfied());
        cv.put(QUESTIONNAIRE_COLUMN_SELFWORTH_DISSATISFIED, data.getSelfworthDissatisfied());
        cv.put(QUESTIONNAIRE_COLUMN_IMPULSIVE, data.getImpulsive());
        cv.put(QUESTIONNAIRE_COLUMN_IMPULSIVE_ANGRY, data.getImpulsiveAngry());
        cv.put(QUESTIONNAIRE_COLUMN_MESSAGE, data.getMessage());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, beenSent);

        long result = db.insert(QUESTIONNAIRE_TABLE_NAME,null, cv);
        response(result);
    }

    public void updateQuestionnaireData(QuestionnaireData data, String id, int sent){
        ContentValues cv = new ContentValues();

        cv.put(QUESTIONNAIRE_COLUMN_DATE, data.getDate());
        cv.put(QUESTIONNAIRE_COLUMN_TIME, data.getTime());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_SATISFIED, data.getMdbfSatsified());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_CALM, data.getMdbfCalm());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_WELL, data.getMdbfWell());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_RELAXED, data.getMdbfRelaxed());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_ENERGETIC, data.getMdbfEnergetic());
        cv.put(QUESTIONNAIRE_COLUMN_MDBF_AWAKE, data.getMdbfAwake());
        cv.put(QUESTIONNAIRE_COLUMN_EVENT_NEGATIVE, data.getEventNegative());
        cv.put(QUESTIONNAIRE_COLUMN_EVENT_POSITIVE, data.getEventPositive());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_ALONE, data.getSocialAlone());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_DISLIKE, data.getSocialDislike());
        cv.put(QUESTIONNAIRE_COLUMN_SOCIAL_PEOPLE, data.getSocialPeople());
        cv.put(QUESTIONNAIRE_COLUMN_LOCATION, data.getLocation());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_PROPERTIES, data.getRuminationProperties());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_REHASH, data.getRuminationRehash());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_TURNOFF, data.getRuminationTurnOff());
        cv.put(QUESTIONNAIRE_COLUMN_RUMINATION_DISPUTE, data.getRuminationDispute());
        cv.put(QUESTIONNAIRE_COLUMN_SELFWORTH_SATISFIED, data.getSelfworthSatisfied());
        cv.put(QUESTIONNAIRE_COLUMN_SELFWORTH_DISSATISFIED, data.getSelfworthDissatisfied());
        cv.put(QUESTIONNAIRE_COLUMN_IMPULSIVE, data.getImpulsive());
        cv.put(QUESTIONNAIRE_COLUMN_IMPULSIVE_ANGRY, data.getImpulsiveAngry());
        cv.put(QUESTIONNAIRE_COLUMN_MESSAGE, data.getMessage());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, sent);

        long result = db.update(QUESTIONNAIRE_TABLE_NAME, cv, QUESTIONNAIRE_COLUMN_ID+"=?", new String[]{id});
        response(result);
    }

    public ArrayList<QuestionnaireData> readQuestionnaireDataFromDate(String date){
        String query = "SELECT * FROM " + QUESTIONNAIRE_TABLE_NAME + " WHERE " + QUESTIONNAIRE_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<QuestionnaireData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            QuestionnaireData data = new QuestionnaireData();
            data.setQuestionnaireId(cursor.getInt(0));
            data.setDate(cursor.getString(1));
            data.setTime(cursor.getString(2));
            data.setMdbfSatsified(cursor.getInt(3));
            data.setMdbfCalm(cursor.getInt(4));
            data.setMdbfWell(cursor.getInt(5));
            data.setMdbfRelaxed(cursor.getInt(6));
            data.setMdbfEnergetic(cursor.getInt(7));
            data.setMdbfAwake(cursor.getInt(8));
            data.setEventNegative(cursor.getInt(9));
            data.setEventPositive(cursor.getInt(10));
            data.setSocialAlone(cursor.getInt(11));
            data.setSocialDislike(cursor.getInt(12));
            data.setSocialPeople(cursor.getString(13));
            data.setLocation(cursor.getString(14));
            data.setRuminationProperties(cursor.getInt(15));
            data.setRuminationRehash(cursor.getInt(16));
            data.setRuminationTurnOff(cursor.getInt(17));
            data.setRuminationDispute(cursor.getInt(18));
            data.setSelfworthSatisfied(cursor.getInt(19));
            data.setSelfworthDissatisfied(cursor.getInt(20));
            data.setImpulsive(cursor.getInt(21));
            data.setImpulsiveAngry(cursor.getInt(22));
            data.setMessage(cursor.getString(23));
            data.setUserId(cursor.getInt(24));
            data.setBeenSent(cursor.getInt(25));
            dataArray.add(data);
        }

        return dataArray;
    }

    public ArrayList<QuestionnaireData> readUnsendQuestionnaireData(){
        String query = "SELECT * FROM " + QUESTIONNAIRE_TABLE_NAME + " WHERE " + COLUMN_SENT + "= 0";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<QuestionnaireData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            QuestionnaireData data = new QuestionnaireData();
            data.setQuestionnaireId(cursor.getInt(0));
            data.setDate(TimeDateFormatter.toSQLDate(cursor.getString(1))); //return date as YYYY-MM-DD
            data.setTime(cursor.getString(2));
            data.setMdbfSatsified(cursor.getInt(3));
            data.setMdbfCalm(cursor.getInt(4));
            data.setMdbfWell(cursor.getInt(5));
            data.setMdbfRelaxed(cursor.getInt(6));
            data.setMdbfEnergetic(cursor.getInt(7));
            data.setMdbfAwake(cursor.getInt(8));
            data.setEventNegative(cursor.getInt(9));
            data.setEventPositive(cursor.getInt(10));
            data.setSocialAlone(cursor.getInt(11));
            data.setSocialDislike(cursor.getInt(12));
            data.setSocialPeople(cursor.getString(13));
            data.setLocation(cursor.getString(14));
            data.setRuminationProperties(cursor.getInt(15));
            data.setRuminationRehash(cursor.getInt(16));
            data.setRuminationTurnOff(cursor.getInt(17));
            data.setRuminationDispute(cursor.getInt(18));
            data.setSelfworthSatisfied(cursor.getInt(19));
            data.setSelfworthDissatisfied(cursor.getInt(20));
            data.setImpulsive(cursor.getInt(21));
            data.setImpulsiveAngry(cursor.getInt(22));
            data.setMessage(cursor.getString(23));
            data.setUserId(cursor.getInt(24));
            data.setBeenSent(cursor.getInt(25));
            dataArray.add(data);
        }

        return dataArray;
    }

    public int getQuestionnaireCount(String date){
        String query = "SELECT * FROM " + QUESTIONNAIRE_TABLE_NAME + " WHERE " + QUESTIONNAIRE_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }

    public int getMoodValue(String date){
        ArrayList<QuestionnaireData> data = readQuestionnaireDataFromDate(date);
        int mood = 0;
        int questionnaireCount = 0;

        for(QuestionnaireData d : data){
            int questionnaireMood = 0;
            int questions = 0;

            if(d.getMdbfSatsified() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfSatsified();
            }
            if(d.getMdbfCalm() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfCalm();
            }
            if(d.getMdbfWell() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfWell();
            }
            if(d.getMdbfRelaxed() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfRelaxed();
            }
            if(d.getMdbfEnergetic() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfEnergetic();
            }
            if(d.getMdbfAwake() >= 0){
                questions += 1;
                questionnaireMood += d.getMdbfAwake();
            }

            if(questions == 0) continue;

            questionnaireCount += 1;
            questionnaireMood = questionnaireMood / questions;
            mood += questionnaireMood;
        }

        if(questionnaireCount == 0) return -1;

        mood = mood / questionnaireCount;
        return mood;
    }

    public ArrayList<Integer> getMoodOfWeek(String date){
        ArrayList<Integer> moods = new ArrayList<>();

        ArrayList<String> dates = TimeDateFormatter.getWeekDates(date);
        for(String s : dates){
            moods.add(getMoodValue(s));
        }

        return  moods;
    }

    public void markQuestionnaireDataAsSent(){
        String query = "UPDATE " + QUESTIONNAIRE_TABLE_NAME + " SET " + COLUMN_SENT + "= 1 "  + " WHERE " + COLUMN_SENT + "= 0";
        db.execSQL(query);
    }

    public void deleteQuestionnaireData(String id){
        long result = db.delete(QUESTIONNAIRE_TABLE_NAME, QUESTIONNAIRE_COLUMN_ID+"=?", new String[]{id});
        response(result);
    }

    public void deleteAllQuestionnaireData(){
        db.execSQL("DELETE FROM " + QUESTIONNAIRE_TABLE_NAME);
    }

    //methods for accelerometer data table
    public void addAccelerometerData(AccelerometerData data, int beenSent){
        ContentValues cv = new ContentValues();

        if(beenSent == 0) cv.put(ACCELEROMETER_COLUMN_DATE, data.getDate());
        else cv.put(ACCELEROMETER_COLUMN_DATE, TimeDateFormatter.toUIDate(data.getDate()));
        cv.put(ACCELEROMETER_COLUMN_TIME, data.getTime());
        cv.put(ACCELEROMETER_COLUMN_X, data.getxAxis());
        cv.put(ACCELEROMETER_COLUMN_Y, data.getyAxis());
        cv.put(ACCELEROMETER_COLUMN_Z, data.getzAxis());
        cv.put(ACCELEROMETER_COLUMN_ACCELERATION, data.getAcceleration());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, beenSent);

        long result = db.insert(ACCELEROMETER_TABLE_NAME,null, cv);
        response(result);
    }

    public ArrayList<AccelerometerData> readAccelerometerDataFromDate(String date){
        String query = "SELECT * FROM " + ACCELEROMETER_TABLE_NAME + " WHERE " + ACCELEROMETER_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<AccelerometerData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            AccelerometerData data = new AccelerometerData();
            data.setDate(cursor.getString(0));
            data.setTime(cursor.getString(1));
            data.setxAxis(cursor.getDouble(2));
            data.setyAxis(cursor.getDouble(3));
            data.setzAxis(cursor.getDouble(4));
            data.setAcceleration(cursor.getDouble(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public ArrayList<AccelerometerData> readUnsendAccelerometerData(){
        String query = "SELECT * FROM " + ACCELEROMETER_TABLE_NAME + " WHERE " + COLUMN_SENT + "= 0";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<AccelerometerData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            AccelerometerData data = new AccelerometerData();
            data.setDate(TimeDateFormatter.toSQLDate(cursor.getString(0))); //return date as YYYY-MM-DD
            data.setTime(cursor.getString(1));
            data.setxAxis(cursor.getDouble(2));
            data.setyAxis(cursor.getDouble(3));
            data.setzAxis(cursor.getDouble(4));
            data.setAcceleration(cursor.getDouble(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public void markAccelerometerDataAsSent (){
        String query = "UPDATE " + ACCELEROMETER_TABLE_NAME + " SET " + COLUMN_SENT + "= 1" + " WHERE " + COLUMN_SENT + "= 0";
        db.execSQL(query);
    }

    public void deleteAccelerometerData(String date){
        long result = db.delete(ACCELEROMETER_TABLE_NAME, ACCELEROMETER_COLUMN_DATE+"=?", new String[]{date});
        response(result);
    }

    public void deleteAllAccelerometerData(){
        db.execSQL("DELETE FROM " + ACCELEROMETER_TABLE_NAME);
    }

    //methods for rotation data table
    public void addRotationData(RotationData data, int beenSent){
        ContentValues cv = new ContentValues();

        if(beenSent == 0) cv.put(ROTATION_COLUMN_DATE, data.getDate());
        else cv.put(ROTATION_COLUMN_DATE, TimeDateFormatter.toUIDate(data.getDate()));
        cv.put(ROTATION_COLUMN_TIME, data.getTime());
        cv.put(ROTATION_COLUMN_X, data.getxRotation());
        cv.put(ROTATION_COLUMN_Y, data.getyRotation());
        cv.put(ROTATION_COLUMN_Z, data.getzRotation());
        cv.put(ROTATION_COLUMN_SCALAR, data.getScalar());
        cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
        cv.put(COLUMN_SENT, beenSent);

        long result = db.insert(ROTATION_TABLE_NAME,null, cv);
        response(result);
    }

    public ArrayList<RotationData> readRotationDataFromDate(String date){
        String query = "SELECT * FROM " + ROTATION_TABLE_NAME + " WHERE " + ROTATION_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<RotationData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            RotationData data = new RotationData();
            data.setDate(cursor.getString(0));
            data.setTime(cursor.getString(1));
            data.setxRotation(cursor.getDouble(2));
            data.setyRotation(cursor.getDouble(3));
            data.setzRotation(cursor.getDouble(4));
            data.setScalar(cursor.getDouble(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public ArrayList<RotationData> readUnsendRotationData(){
        String query = "SELECT * FROM " + ROTATION_TABLE_NAME + " WHERE " + COLUMN_SENT + "= 0";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<RotationData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while (cursor.moveToNext()){
            RotationData data = new RotationData();
            data.setDate(TimeDateFormatter.toSQLDate(cursor.getString(0))); //return date as YYYY-MM-DD
            data.setTime(cursor.getString(1));
            data.setxRotation(cursor.getDouble(2));
            data.setyRotation(cursor.getDouble(3));
            data.setzRotation(cursor.getDouble(4));
            data.setScalar(cursor.getDouble(5));
            data.setUserId(cursor.getInt(6));
            data.setBeenSent(cursor.getInt(7));
            dataArray.add(data);
        }

        return dataArray;
    }

    public void markRotationDataAsSent(){
        String query = "UPDATE " + ROTATION_TABLE_NAME + " SET " + COLUMN_SENT + "= 1" +" WHERE " + COLUMN_SENT + "= 0";
        db.execSQL(query);
    }

    public void deleteRotationData(String date){
        long result = db.delete(ROTATION_TABLE_NAME, ROTATION_COLUMN_DATE+"=?", new String[]{date});
        response(result);

    }

    public void deleteAllRotationData(){
        db.execSQL("DELETE FROM " + ROTATION_TABLE_NAME);
    }

    //methods for steps data table
    public void addStepsData(StepsData data, int beenSent){
        ContentValues cv = new ContentValues();

        if(readStepsDataFromDate(data.getDate()) == null) {
            if(beenSent == 0) cv.put(STEPS_COLUMN_DATE, data.getDate());
            else cv.put(STEPS_COLUMN_DATE, TimeDateFormatter.toUIDate(data.getDate()));
            cv.put(STEPS_COLUMN_VALUE, data.getSteps());
            cv.put(USER_COLUMN_ID, SharedPrefManager.getInstance(context.getApplicationContext()).getInt(SharedPrefManager.KEY_USER_ID));
            cv.put(COLUMN_SENT, beenSent);
            long result = db.insert(STEPS_TABLE_NAME, null, cv);
            response(result);
            return;
        }

        updateStepsValueData(data.getDate(), data.getSteps());
    }

    private void updateStepsValueData(String date, int steps){
        String query = "UPDATE " + STEPS_TABLE_NAME + " SET " + STEPS_COLUMN_VALUE + "=" + steps + ", " + COLUMN_SENT + "=" + 0 +
                " WHERE " + STEPS_COLUMN_DATE + "=" + "\"" + date + "\"";
        db.execSQL(query);
    }

    public StepsData readStepsDataFromDate(String date){
        String query = "SELECT * FROM " + STEPS_TABLE_NAME + " WHERE " + STEPS_COLUMN_DATE + "=" + "\"" + date + "\"";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0)
            return null;

        cursor.moveToNext();
        StepsData data = new StepsData();
        data.setDate(cursor.getString(0));
        data.setSteps(cursor.getInt(1));
        data.setUserId(cursor.getInt(2));
        data.setBeenSent(cursor.getInt(3));
        return data;
    }

    public ArrayList<StepsData> readUnsendStepsData(){
        String query = "SELECT * FROM " + STEPS_TABLE_NAME + " WHERE " + COLUMN_SENT + "= 0";

        Cursor cursor = null;
        if(db != null)
            cursor = db.rawQuery(query, null);

        ArrayList<StepsData> dataArray = new ArrayList<>();

        if (cursor.getCount() == 0)
            return dataArray;

        while(cursor.moveToNext()){
            StepsData data = new StepsData();
            data.setDate(TimeDateFormatter.toSQLDate(cursor.getString(0))); //return date as YYYY-MM-DD
            data.setSteps(cursor.getInt(1));
            data.setUserId(cursor.getInt(2));
            data.setBeenSent(cursor.getInt(3));
            dataArray.add(data);
        };

        return dataArray;
    }

    public void markStepsDataAsSent(){
        String query = "UPDATE " + STEPS_TABLE_NAME + " SET " + COLUMN_SENT + "= 1 "  + " WHERE " + COLUMN_SENT + "= 0";
        db.execSQL(query);
    }

    public void deleteStepsData(String date){
        long result = db.delete(STEPS_TABLE_NAME, STEPS_COLUMN_DATE+"=?", new String[]{date});
        response(result);

    }

    public void deleteAllStepsData(){
        db.execSQL("DELETE FROM " + STEPS_TABLE_NAME);
    }

    public ArrayList<Integer> getStepsOfWeek(String date){
        ArrayList<Integer> steps = new ArrayList<>();

        ArrayList<String> dates = TimeDateFormatter.getWeekDates(date);
        for(String s : dates){
            StepsData stepsData = readStepsDataFromDate(s);
            if(stepsData != null)
                steps.add(stepsData.getSteps());
            else steps.add(0);
        }

        return  steps;
    }

}
