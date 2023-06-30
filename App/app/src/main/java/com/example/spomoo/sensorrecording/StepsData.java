package com.example.spomoo.sensorrecording;

/*
 * StepsData of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import com.google.gson.annotations.SerializedName;

/*
 * Class for storing the attributes of the step recording
 * Contains getters and setters for all attributes
 */
public class StepsData {

    //private attributes
    @SerializedName("steps")
    private int steps;
    @SerializedName("date")
    private String date;
    @SerializedName("userid")
    private int userId;
    @SerializedName("beensent")
    private int beenSent;

    //constructors
    public StepsData(){
    }

    public StepsData(int steps){
        setSteps(steps);
    }

    public StepsData(int steps, String date){
        setSteps(steps);
        setDate(date);
    }

    public StepsData(int steps, String date, int userId, int beenSent){
        setSteps(steps);
        setDate(date);
        setUserId(userId);
        setBeenSent(beenSent);
    }

    //getters & setters
    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBeenSent() {
        return beenSent;
    }

    public void setBeenSent(int beenSent) {
        this.beenSent = beenSent;
    }
}
