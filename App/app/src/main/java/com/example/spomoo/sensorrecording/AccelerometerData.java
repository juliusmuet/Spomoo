package com.example.spomoo.sensorrecording;

/*
 * AccelerometerData of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import com.google.gson.annotations.SerializedName;

/*
 * Class for storing the attributes of the accelerometer recording
 * Contains getters and setters for all attributes
 */
public class AccelerometerData {

    //private attributes
    @SerializedName("xaxis")
    private double xAxis;
    @SerializedName("yaxis")
    private double yAxis;
    @SerializedName("zaxis")
    private double zAxis;
    @SerializedName("acceleration")
    private double acceleration;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    @SerializedName("userid")
    private int userId;
    @SerializedName("beensent")
    private int beenSent;

    //constructors
    public AccelerometerData(){
    }

    public AccelerometerData(double xAxis, double yAxis, double zAxis, double acceleration){
        setxAxis(xAxis);
        setyAxis(yAxis);
        setzAxis(zAxis);
        setAcceleration(acceleration);
    }

    public AccelerometerData(double xAxis, double yAxis, double zAxis, double acceleration, String date, String time, int userId, int beenSent){
        setxAxis(xAxis);
        setyAxis(yAxis);
        setzAxis(zAxis);
        setAcceleration(acceleration);
        setDate(date);
        setTime(time);
        setUserId(userId);
        setBeenSent(beenSent);
    }

    //getters & setters
    public double getxAxis() {
        return xAxis;
    }

    public void setxAxis(double xAxis) {
        this.xAxis = xAxis;
    }

    public double getyAxis() {
        return yAxis;
    }

    public void setyAxis(double yAxis) {
        this.yAxis = yAxis;
    }

    public double getzAxis() {
        return zAxis;
    }

    public void setzAxis(double zAxis) {
        this.zAxis = zAxis;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
