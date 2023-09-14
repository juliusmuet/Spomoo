package com.example.spomoo.sensorrecording;

/*
 * RotationDataTest of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for RotationData class
 */
public class RotationDataTest {

    @Test
    public void xRotation_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setxRotation(3.1f);
        assertEquals(3.1f, rotationData.getxRotation(), 0);
    }

    @Test
    public void yRotation_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setyRotation(3.1f);
        assertEquals(3.1f, rotationData.getyRotation(), 0);
    }

    @Test
    public void zRotation_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setzRotation(3.1f);
        assertEquals(3.1f, rotationData.getzRotation(), 0);
    }

    @Test
    public void scalar_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setScalar(3.1f);
        assertEquals(3.1f, rotationData.getScalar(), 0);
    }

    @Test
    public void date_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setDate("2023-12-24");
        assertEquals("2023-12-24", rotationData.getDate());
    }

    @Test
    public void time_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setTime("13:24");
        assertEquals("13:24", rotationData.getTime());
    }

    @Test
    public void userid_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setUserId(34);
        assertEquals(34, rotationData.getUserId());
    }

    @Test
    public void beenSent_getter_setter_are_correct(){
        RotationData rotationData = new RotationData();
        rotationData.setBeenSent(0);
        assertEquals(0, rotationData.getBeenSent());
    }

    @Test
    public void serialisation_is_correct(){
        RotationData rotationData = new RotationData(3, 5, 2, 10, "2023-12-31", "15:37", 1043, 0);
        Gson gson = new Gson();
        assertEquals("{\"xrotation\":3.0,\"yrotation\":5.0,\"zrotation\":2.0,\"scalar\":10.0,\"date\":\"2023-12-31\",\"time\":\"15:37\",\"userid\":1043,\"beensent\":0}", gson.toJson(rotationData));
    }

}