package com.example.spomoo.sensorrecording;

/*
 * StepsDataTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for StepsData class
 */
public class StepsDataTest {

    @Test
    public void steps_getter_setter_are_correct() {
        StepsData stepsData = new StepsData();
        stepsData.setSteps(20);
        assertEquals(20, stepsData.getSteps());
    }

    @Test
    public void date_getter_setter_are_correct() {
        StepsData stepsData = new StepsData();
        stepsData.setDate("2023-07-28");
        assertEquals("2023-07-28", stepsData.getDate());
    }

    @Test
    public void userid_getter_setter_are_correct() {
        StepsData stepsData = new StepsData();
        stepsData.setUserId(43);
        assertEquals(43, stepsData.getUserId());
    }

    @Test
    public void getBeenSent() {
        StepsData stepsData = new StepsData();
        stepsData.setBeenSent(1);
        assertEquals(1, stepsData.getBeenSent());
    }

    @Test
    public void serialisation_is_correct() {
        StepsData stepsData = new StepsData(100, "2023-07-27");
        Gson gson = new Gson();
        assertEquals("{\"steps\":100,\"date\":\"2023-07-27\",\"userid\":0,\"beensent\":0}", gson.toJson(stepsData));
    }
}