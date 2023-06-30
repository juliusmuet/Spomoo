package com.example.spomoo.utility;

/*
 * Constants of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

/*
 * Contains constants shared among multiple classes
 */
public class Constants {

    //allows only gmail.com
    public static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@gmail.com";

    //allows all mail servers (web.de, t-online.de, gmail.com,...)
    public static final String EMAIL_REGEX_LEGACY = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static final String PASSWORD_REGEX = "^" +                //start-of-string
            "(?=.*[0-9])" +         //a digit must occur at least once
            "(?=.*[a-z])" +         //a lower case letter must occur at least once
            "(?=.*[A-Z])" +         //an upper case letter must occur at least once
            "(?=.*[@#$%^&+=_])" +    //a special character must occur at least once you can replace with your special characters
            "(?=\\S+$)" +           //no whitespace allowed in the entire string
            ".{6,}" +               //minimum of 6 characters
            "$";                    //end-of-string

    public static final String DATE_FORMAT_SQL = "yyyy-MM-dd";

    public static final String DATE_FORMAT_UI = "dd.MM.yyyy";
}
