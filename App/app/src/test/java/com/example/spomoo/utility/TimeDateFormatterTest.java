package com.example.spomoo.utility;

/*
 * TimeDateFormatterTest of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import static org.junit.Assert.*;

import org.junit.Test;

/*
 * Unit tests for TimeDateFormatter class
 */
public class TimeDateFormatterTest {

    @Test
    public void SQL_date_is_correct(){
        assertEquals("2023-05-24", TimeDateFormatter.toSQLDate("24.05.2023"));
    }

    @Test
    public void UI_date_is_correct(){
        assertEquals("24.05.2023", TimeDateFormatter.toUIDate("2023-05-24"));
    }

    @Test
    public void previous_date_is_correct(){
        assertEquals(TimeDateFormatter.getNextDateString(-3), TimeDateFormatter.getPrevDateString(3));
    }

    @Test
    public void next_date_is_correct(){
        assertEquals(TimeDateFormatter.getPrevDateString(-10), TimeDateFormatter.getNextDateString(10));
    }

    @Test
    public void date_from_millis_is_correct(){
        assertEquals("01.07.2023", TimeDateFormatter.getDayFromMillis(1688162400000l));
    }

    @Test
    public void millis_from_date_is_correct(){
        assertEquals(1688162400000l, TimeDateFormatter.getMillisOfDay("01.07.2023"));
    }

    @Test
    public void dayname_from_millis_is_correct(){
        assertEquals("sat", TimeDateFormatter.getDayName(1688162400000l));
    }

}