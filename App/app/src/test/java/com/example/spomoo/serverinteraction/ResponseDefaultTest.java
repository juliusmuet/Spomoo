package com.example.spomoo.serverinteraction;

/*
 * ResponseDefaultTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for ResponseDefault class
 */
public class ResponseDefaultTest {

    @Test
    public void constructor_and_getters_are_correct(){
        ResponseDefault responseDefault = new ResponseDefault(true, "TestError");
        assertEquals(true, responseDefault.isError());
        assertEquals("TestError", responseDefault.getMessage());
    }

    @Test
    public void serialisation_is_correct(){
        ResponseDefault responseDefault = new ResponseDefault(true, "TestError");
        Gson gson = new Gson();
        assertEquals("{\"error\":true,\"message\":\"TestError\"}", gson.toJson(responseDefault));
    }

}