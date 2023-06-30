package com.example.spomoo.sensorrecording;

/*
 * RotationData of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import com.google.gson.annotations.SerializedName;

/*
 * Class for storing the attributes of the rotation recording
 * Contains getters and setters for all attributes
 */
public class RotationData {

    //private attributes
    @SerializedName("xrotation")
    private double xRotation;
    @SerializedName("yrotation")
    private double yRotation;
    @SerializedName("zrotation")
    private double zRotation;
    @SerializedName("scalar")
    private double scalar;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    @SerializedName("userid")
    private int userId;
    @SerializedName("beensent")
    private int beenSent;

    //constructors
    public RotationData(){
    }

    public RotationData(double xRotation, double yRotation, double zRotation,double scalar){
        setxRotation(xRotation);
        setyRotation(yRotation);
        setzRotation(zRotation);
        setScalar(scalar);
    }

    public RotationData(double xRotation, double yRotation, double zRotation,double scalar, String date, String time, int userId, int beenSent){
        setxRotation(xRotation);
        setyRotation(yRotation);
        setzRotation(zRotation);
        setScalar(scalar);
        setDate(date);
        setTime(time);
        setUserId(userId);
        setBeenSent(beenSent);
    }

    //getters & setters
    public double getxRotation() {
        return xRotation;
    }

    public void setxRotation(double xRotation) {
        this.xRotation = xRotation;
    }

    public double getyRotation() {
        return yRotation;
    }

    public void setyRotation(double yRotation) {
        this.yRotation = yRotation;
    }

    public double getzRotation() {
        return zRotation;
    }

    public void setzRotation(double zRotation) {
        this.zRotation = zRotation;
    }

    public double getScalar() {
        return scalar;
    }

    public void setScalar(double scalar) {
        this.scalar = scalar;
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
