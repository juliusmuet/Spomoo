package com.example.spomoo.recordsport;

/*
 * SportData of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import com.google.gson.annotations.SerializedName;

/*
 * Class for storing the attributes of a sport activity
 * Contains getters and setters for all attributes
 */
public class SportData {

    //attributes
    @SerializedName("type")
    private String type;
    @SerializedName("date")
    private String date;
    @SerializedName("start")
    private String start;
    @SerializedName("duration")
    private String duration;
    @SerializedName("intensity")
    private int intensity;
    @SerializedName("sportid")
    private int sportId;
    @SerializedName("userid")
    private int userId;
    @SerializedName("beensent")
    private int beenSent;

    //constructors
    public SportData(){
    }

    public SportData(String type, String duration, String date, String start){
        setType(type);
        setDuration(duration);
        setDate(date);
        setStart(start);
        setIntensity(-1);
    }

    public SportData(String type, String duration, String date, String start, int intensity, int sportId, int userId, int beenSent){
        setType(type);
        setDuration(duration);
        setDate(date);
        setStart(start);
        setIntensity(intensity);
        setSportId(sportId);
        setUserId(userId);
        setBeenSent(beenSent);
    }

    //getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public int getSportId() {
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
    }

    public int getBeenSent() {
        return beenSent;
    }

    public void setBeenSent(int beenSent) {
        this.beenSent = beenSent;
    }
}
