package com.example.spomoo.questionnaire;

/*
 * QuestionnaireData of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import com.google.gson.annotations.SerializedName;

/*
 * Stores inputted values of the questionnaire
 * Contains getters and setters for all attributes
 */
public class QuestionnaireData {

    //metadata
    @SerializedName("userid")
    private int userId;
    @SerializedName("questionnaireid")
    private int questionnaireId;
    @SerializedName("beensent")
    private int beenSent;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;

    //MDBF, scala 0-100
    @SerializedName("mdbf_satisfied")
    private int mdbfSatsified;
    @SerializedName("mdbf_calm")
    private int mdbfCalm;
    @SerializedName("mdbf_well")
    private int mdbfWell;
    @SerializedName("mdbf_relaxed")
    private int mdbfRelaxed;
    @SerializedName("mdbf_energetic")
    private int mdbfEnergetic;
    @SerializedName("mdbf_awake")
    private int mdbfAwake;

    //event appraisal, scala 0-100
    @SerializedName("event_negative")
    private int eventNegative;
    @SerializedName("event_positive")
    private int eventPositive;

    //social context & situation, scala 0-100
    @SerializedName("social_alone")
    private int socialAlone;
    @SerializedName("social_dislike")
    private int socialDislike;
    @SerializedName("social_people")
    private String socialPeople;

    //location
    @SerializedName("location")
    private String location;

    //rumination, scala 0-9
    @SerializedName("rumination_properties")
    private int ruminationProperties;
    @SerializedName("rumination_rehash")
    private int ruminationRehash;
    @SerializedName("rumination_turnoff")
    private int ruminationTurnOff;
    @SerializedName("rumination_dispute")
    private int ruminationDispute;

    //self worth, scala 0-9
    @SerializedName("selfworth_satisfied")
    private int selfworthSatisfied;
    @SerializedName("selfworth_dissatisfied")
    private int selfworthDissatisfied;

    //impulsiveness, scala 0-6
    @SerializedName("impulsive")
    private int impulsive;
    @SerializedName("impulsive_angry")
    private int impulsiveAngry;

    //message
    @SerializedName("message")
    private String message;

    //needed for data representation
    @SerializedName("expanded")
    private boolean expanded = false;

    //constructor
    public QuestionnaireData(){
    }

    public QuestionnaireData(int mdbfSatsified, int mdbfCalm, int mdbfWell, int mdbfRelaxed, int mdbfEnergetic, int mdbfAwake, int eventNegative,
                             int eventPositive, int socialAlone, int socialDislike, String socialPeople, String location, int ruminationProperties,
                             int ruminationRehash, int ruminationTurnOff, int ruminationDispute, int selfworthSatisfied, int selfworthDissatisfied,
                             int impulsive, int impulsiveAngry, String message, String date, String time, int questionnaireId, int userId, int beenSent) {
        setMdbfSatsified(mdbfSatsified);
        setMdbfCalm(mdbfCalm);
        setMdbfWell(mdbfWell);
        setMdbfRelaxed(mdbfRelaxed);
        setMdbfEnergetic(mdbfEnergetic);
        setMdbfAwake(mdbfAwake);
        setEventNegative(eventNegative);
        setEventPositive(eventPositive);
        setSocialAlone(socialAlone);
        setSocialDislike(socialDislike);
        setSocialPeople(socialPeople);
        setLocation(location);
        setRuminationProperties(ruminationProperties);
        setRuminationRehash(ruminationRehash);
        setRuminationTurnOff(ruminationTurnOff);
        setRuminationDispute(ruminationDispute);
        setSelfworthSatisfied(selfworthSatisfied);
        setSelfworthDissatisfied(selfworthDissatisfied);
        setImpulsive(impulsive);
        setImpulsiveAngry(impulsiveAngry);
        setMessage(message);
        setDate(date);
        setTime(time);
        setQuestionnaireId(questionnaireId);
        setUserId(userId);
        setBeenSent(beenSent);
    }

    //getters & setters
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

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public int getBeenSent() {
        return beenSent;
    }

    public void setBeenSent(int beenSent) {
        this.beenSent = beenSent;
    }

    public int getMdbfSatsified() {
        return mdbfSatsified;
    }

    public void setMdbfSatsified(int mdbfSatsified) {
        this.mdbfSatsified = mdbfSatsified;
    }

    public int getMdbfCalm() {
        return mdbfCalm;
    }

    public void setMdbfCalm(int mdbfCalm) {
        this.mdbfCalm = mdbfCalm;
    }

    public int getMdbfWell() {
        return mdbfWell;
    }

    public void setMdbfWell(int mdbfWell) {
        this.mdbfWell = mdbfWell;
    }

    public int getMdbfRelaxed() {
        return mdbfRelaxed;
    }

    public void setMdbfRelaxed(int mdbfRelaxed) {
        this.mdbfRelaxed = mdbfRelaxed;
    }

    public int getMdbfEnergetic() {
        return mdbfEnergetic;
    }

    public void setMdbfEnergetic(int mdbfEnergetic) {
        this.mdbfEnergetic = mdbfEnergetic;
    }

    public int getMdbfAwake() {
        return mdbfAwake;
    }

    public void setMdbfAwake(int mdbfAwake) {
        this.mdbfAwake = mdbfAwake;
    }

    public int getEventNegative() {
        return eventNegative;
    }

    public void setEventNegative(int eventNegative) {
        this.eventNegative = eventNegative;
    }

    public int getEventPositive() {
        return eventPositive;
    }

    public void setEventPositive(int eventPositive) {
        this.eventPositive = eventPositive;
    }

    public int getSocialAlone() {
        return socialAlone;
    }

    public void setSocialAlone(int socialAlone) {
        this.socialAlone = socialAlone;
    }

    public int getSocialDislike() {
        return socialDislike;
    }

    public void setSocialDislike(int socialDislike) {
        this.socialDislike = socialDislike;
    }

    public String getSocialPeople() {
        return socialPeople;
    }

    public void setSocialPeople(String socialPeople) {
        this.socialPeople = socialPeople;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRuminationProperties() {
        return ruminationProperties;
    }

    public void setRuminationProperties(int ruminationProperties) {
        this.ruminationProperties = ruminationProperties;
    }

    public int getRuminationRehash() {
        return ruminationRehash;
    }

    public void setRuminationRehash(int ruminationRehash) {
        this.ruminationRehash = ruminationRehash;
    }

    public int getRuminationTurnOff() {
        return ruminationTurnOff;
    }

    public void setRuminationTurnOff(int ruminationTurnOff) {
        this.ruminationTurnOff = ruminationTurnOff;
    }

    public int getRuminationDispute() {
        return ruminationDispute;
    }

    public void setRuminationDispute(int ruminationDispute) {
        this.ruminationDispute = ruminationDispute;
    }

    public int getSelfworthSatisfied() {
        return selfworthSatisfied;
    }

    public void setSelfworthSatisfied(int selfworthSatisfied) {
        this.selfworthSatisfied = selfworthSatisfied;
    }

    public int getSelfworthDissatisfied() {
        return selfworthDissatisfied;
    }

    public void setSelfworthDissatisfied(int selfworthDissatisfied) {
        this.selfworthDissatisfied = selfworthDissatisfied;
    }

    public int getImpulsive() {
        return impulsive;
    }

    public void setImpulsive(int impulsive) {
        this.impulsive = impulsive;
    }

    public int getImpulsiveAngry() {
        return impulsiveAngry;
    }

    public void setImpulsiveAngry(int impulsiveAngry) {
        this.impulsiveAngry = impulsiveAngry;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}

