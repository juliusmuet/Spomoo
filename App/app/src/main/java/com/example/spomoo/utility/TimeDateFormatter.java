package com.example.spomoo.utility;

/*
 * TimeDateFormatter of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * Contains helper methods to get date and time strings
 */
public class TimeDateFormatter {

    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    //return current time
    public static String getTimeString() {
        Date date = new Date();
        //time in hh:mm
        DateFormat dFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
        String timeString = dFormat.format(date);
        return timeString;
    }

    //return current day
    public static String getDateString() {
        Date date = new Date();
        //Date in dd.mm.yyyy
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(date);
        return dateString;
    }

    //converts dd.MM.yyyy to yyyy-MM-dd
    public static String toSQLDate(String UIDate){
        if(UIDate.isEmpty()) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_UI);
        Date d = null;
        try {
            d = sdf.parse(UIDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        sdf.applyPattern(Constants.DATE_FORMAT_SQL);
        return sdf.format(d);
    }

    //converts yyyy-MM-dd to dd.MM.yyyy
    public static String toUIDate(String UIDate){
        if(UIDate.isEmpty()) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SQL);
        Date d = null;
        try {
            d = sdf.parse(UIDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        sdf.applyPattern(Constants.DATE_FORMAT_UI);
        return sdf.format(d);
    }

    //get day which was daysBackwards days ago
    public static String getPrevDateString(int daysBackwards) {
        Date today = new Date();
        Date prevDay = new Date(today.getTime() - MILLIS_IN_A_DAY * daysBackwards);
        //Date in day.month.year (all two numbers long)
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(prevDay);
        return dateString;
    }

    //get day which is daysForwards days ahead
    public static String getNextDateString(int daysForwards) {
        Date today = new Date();
        Date nextDay = new Date(today.getTime() + MILLIS_IN_A_DAY * daysForwards);
        //Date in day.month.year (all two numbers long)
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(nextDay);
        return dateString;
    }

    //get days which are daysBackwards days ago in increasing size (e.g. 01.01.2000, 02.01.2000,...)
    public static ArrayList<String> getPrevDatesString(int daysBackwards) {
        ArrayList<String> days = new ArrayList<>();
        for (int i = daysBackwards; i >= 1; i--) {
            days.add(getPrevDateString(i));
        }
        return days;
    }

    //get days which are daysForwards days ahead in increasing size (e.g. 01.01.2000, 02.01.2000,...)
    public static ArrayList<String> getNextDatesString(int daysForwards) {
        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= daysForwards; i++) {
            days.add(getNextDateString(i));
        }
        return days;
    }

    //return day from inputted milliseconds
    public static String getDayFromMillis(long millis){
        Date date = new Date(millis);
        //Date in dd.mm.yyyy
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(date);
        return dateString;
    }

    //return milliseconds of inputted day
    public static long getMillisOfDay(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dateFormat = null;
        try {
            dateFormat = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.getTime();
    }

    public static String getDayName() {
        Date date = new Date();
        return date.toString().substring(0, 3).toLowerCase();
    }

    public static String getDayName(long millis) {
        Date date = new Date(millis);
        return date.toString().substring(0, 3).toLowerCase();
    }

    private static String convertDateToString(Date date){
        //Date in dd.mm.yyyy
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static ArrayList<String> getWeekDates(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());

        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
            calendar.add(Calendar.DATE, -1);

        ArrayList<String> dates = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            dates.add(convertDateToString(new Date(calendar.getTimeInMillis())));
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    /* legacy code
    //gets day which is daysBackwards days ago from inputted date string
    public static String getPrevDayStringFromPastDay(String date, int daysBackwards) {
        Date today = new Date();
        Date prevDay = new Date(getMillisOfDay(date) - MILLIS_IN_A_DAY * daysBackwards);
        //Date in day.month.year (all two numbers long)
        DateFormat dFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
        String dateString = dFormat.format(prevDay);
        return dateString;
    }
    //gets days which are daysBackwards days ago from inputted date string INCLUDING INPUTTED DATE
    public static ArrayList<String> getPrevDaysStringFromPastDay(String date, int daysBackwards) {
        ArrayList<String> days = new ArrayList<>();
        for(int i = daysBackwards; i >= 0; i--){
            days.add(getPrevDayStringFromPastDay(date, i));
        }
        return days;
    }
    */

}
