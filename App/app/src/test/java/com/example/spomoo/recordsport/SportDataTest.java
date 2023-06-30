package com.example.spomoo.recordsport;

/*
 * SportDataTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for SportData class
 */
public class SportDataTest {

    @Test
    public void type_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setType("Badminton");
        assertEquals("Badminton", sportData.getType());
    }

    @Test
    public void date_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setDate("2023-12-31");
        assertEquals("2023-12-31", sportData.getDate());
    }

    @Test
    public void start_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setStart("09:31");
        assertEquals("09:31", sportData.getStart());
    }

    @Test
    public void duration_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setDuration("09:31");
        assertEquals("09:31", sportData.getDuration());
    }

    @Test
    public void intensity_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setIntensity(99);
        assertEquals(99, sportData.getIntensity());
    }

    @Test
    public void sportid_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setSportId(99);
        assertEquals(99, sportData.getSportId());
    }

    @Test
    public void userid_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setUserId(99);
        assertEquals(99, sportData.getUserId());
    }

    @Test
    public void beenSent_getter_setter_are_correct(){
        SportData sportData = new SportData();
        sportData.setBeenSent(1);
        assertEquals(1, sportData.getBeenSent());
    }

    @Test
    public void serialisation_is_correct(){
        SportData sportData = new SportData("Badminton", "02:30", "2023-08-31", "10:12");
        Gson gson = new Gson();
        assertEquals("{\"type\":\"Badminton\",\"date\":\"2023-08-31\",\"start\":\"10:12\",\"duration\":\"02:30\",\"intensity\":-1,\"sportid\":0,\"userid\":0,\"beensent\":0}", gson.toJson(sportData));
    }

}