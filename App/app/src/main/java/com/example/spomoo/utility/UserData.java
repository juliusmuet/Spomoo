package com.example.spomoo.utility;
/*
 * UserData of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

/*
 * Class for storing the attributes of a user
 * Contains getters and setters for all attributes
 */
public class UserData {

    //attributes
    private int id;
    private String username;
    private String email;
    private String gender;
    private String birthday;
    private int height;
    private int weight;

    //constructors
    public UserData(){
        setId(-1);
        setUsername("");
        setEmail("");
        setGender("");
        setBirthday("");
        setHeight(-1);
        setWeight(-1);
    }

    public UserData(int id, String username, String email, String gender, String birthday, int height, int weight){
        setId(id);
        setUsername(username);
        setEmail(email);
        setGender(gender);
        setBirthday(birthday);
        setHeight(height);
        setWeight(weight);
    }

    //getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    //get birthday in dd.mm.yyyy format
    public String getBirthday() {
        if(this.birthday.isEmpty()) return "";
        return TimeDateFormatter.toUIDate(this.birthday);
    }

    //get birthday in yyyy-mm-dd format
    public String getBirthdaySQL(){
        return birthday;
    }

    //set birthday from yyyy-mm-dd format
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    //set birthday from dd.mm.yyyy format
    public void setBirthdayFromUI(String birthday){
        if(birthday.isEmpty()) {
            this.birthday = birthday;
            return;
        }

        this.birthday = TimeDateFormatter.toSQLDate(birthday);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
