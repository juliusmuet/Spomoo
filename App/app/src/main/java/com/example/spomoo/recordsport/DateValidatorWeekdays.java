package com.example.spomoo.recordsport;

/*
 * DateValidatorWeekdays of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

/*
 * Custom Date Validator to allow dates between today and past two weeks
 */
public class DateValidatorWeekdays implements CalendarConstraints.DateValidator {

    private Calendar dateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  //calendar for inputted date
    private Calendar todayCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //calendar for today

    public static final Creator<DateValidatorWeekdays> CREATOR =
            new Creator<DateValidatorWeekdays>() {
                @Override
                public DateValidatorWeekdays createFromParcel(Parcel source) {
                    return new DateValidatorWeekdays();
                }

                @Override
                public DateValidatorWeekdays[] newArray(int size) {
                    return new DateValidatorWeekdays[size];
                }
            };

    //logic for checking inputted date
    @Override
    public boolean isValid(long date) {
        dateCalendar.setTimeInMillis(date);
        todayCalendar.setTimeInMillis(System.currentTimeMillis());
        long difference = todayCalendar.getTimeInMillis()-dateCalendar.getTimeInMillis();
        long maxDifference = 14*24*60*60*1000;
        return difference <= maxDifference && dateCalendar.getTimeInMillis() <= todayCalendar.getTimeInMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateValidatorWeekdays)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object[] hashedFields = {};
        return Arrays.hashCode(hashedFields);
    }
}
