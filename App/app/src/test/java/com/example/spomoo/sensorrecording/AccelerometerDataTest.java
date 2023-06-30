package com.example.spomoo.sensorrecording;

/*
 * AccelerometerDataTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for AccelerometerData class
 */
public class AccelerometerDataTest {

    @Test
    public void xAxis_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setxAxis(3.1f);
        assertEquals(3.1f, accelerometerData.getxAxis(), 0);
    }

    @Test
    public void yAxis_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setyAxis(3.1f);
        assertEquals(3.1f, accelerometerData.getyAxis(), 0);
    }

    @Test
    public void zAxis_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setzAxis(3.1f);
        assertEquals(3.1f, accelerometerData.getzAxis(), 0);
    }

    @Test
    public void acceleration_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setAcceleration(3.1f);
        assertEquals(3.1f, accelerometerData.getAcceleration(), 0);
    }

    @Test
    public void date_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setDate("2023-12-31");
        assertEquals("2023-12-31", accelerometerData.getDate());
    }

    @Test
    public void time_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setTime("16:45");
        assertEquals("16:45", accelerometerData.getTime());
    }

    @Test
    public void userid_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setUserId(365);
        assertEquals(365, accelerometerData.getUserId());
    }

    @Test
    public void beenSent_getter_setter_are_correct(){
        AccelerometerData accelerometerData = new AccelerometerData();
        accelerometerData.setBeenSent(0);
        assertEquals(0, accelerometerData.getBeenSent());
    }

    @Test
    public void serialisation_is_correct(){
        AccelerometerData accelerometerData = new AccelerometerData(3.0f, 2.0f, 4.0f, 6.0f, "2023-12-25", "20:15", 400, 0);
        Gson gson = new Gson();
        assertEquals("{\"xaxis\":3.0,\"yaxis\":2.0,\"zaxis\":4.0,\"acceleration\":6.0,\"date\":\"2023-12-25\",\"time\":\"20:15\",\"userid\":400,\"beensent\":0}", gson.toJson(accelerometerData));
    }

}