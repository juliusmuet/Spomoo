package com.example.spomoo.serverinteraction;

/*
 * ResponseLogin of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;
import com.example.spomoo.utility.UserData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Response for http responses at login
 */
public class ResponseLogin {

    //private attributes
    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private UserData user;
    @SerializedName("accelerometer")
    private List<AccelerometerData> accelerometer;
    @SerializedName("rotation")
    private List<RotationData> rotation;
    @SerializedName("steps")
    private List<StepsData> steps;
    @SerializedName("sport")
    private List<SportData> sport;
    @SerializedName("questionnaire")
    private List<QuestionnaireData> questionnaire;

    //constructor
    public ResponseLogin(boolean error, String message, UserData user, List<AccelerometerData> accelerometer, List<RotationData> rotation, List<StepsData> steps,
                         List<SportData> sport, List<QuestionnaireData> questionnaire) {
        this.error = error;
        this.message = message;
        this.user = user;
        this.accelerometer = accelerometer;
        this.rotation = rotation;
        this.steps = steps;
        this.sport = sport;
        this.questionnaire = questionnaire;
    }

    //getters
    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public UserData getUser() {
        return user;
    }

    public List<AccelerometerData> getAccelerometer() {
        return accelerometer;
    }

    public List<RotationData> getRotation() {
        return rotation;
    }

    public List<StepsData> getSteps() {
        return steps;
    }

    public List<SportData> getSport() {
        return sport;
    }

    public List<QuestionnaireData> getQuestionnaire() {
        return questionnaire;
    }
}
