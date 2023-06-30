package com.example.spomoo.utility;

/*
 * UserDataTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import org.junit.Test;

/*
 * Unit tests for UserData class
 */
public class UserDataTest {

    @Test
    public void empty_id_is_correct(){
        UserData userData = new UserData();
        assertEquals(-1, userData.getId());
    }

    @Test
    public void empty_username_is_correct(){
        UserData userData = new UserData();
        assertEquals("", userData.getUsername());
    }

    @Test
    public void empty_email_is_correct(){
        UserData userData = new UserData();
        assertEquals("", userData.getEmail());
    }

    @Test
    public void empty_gender_is_correct(){
        UserData userData = new UserData();
        assertEquals("", userData.getGender());
    }

    @Test
    public void empty_birthday_is_correct(){
        UserData userData = new UserData();
        assertEquals("", userData.getBirthday());
    }

    @Test
    public void empty_height_is_correct(){
        UserData userData = new UserData();
        assertEquals(-1, userData.getHeight());
    }

    @Test
    public void empty_weight_is_correct(){
        UserData userData = new UserData();
        assertEquals(-1, userData.getWeight());
    }

    @Test
    public void birthday_from_UI_is_correct(){
        UserData userData = new UserData();
        userData.setBirthdayFromUI("01.07.2002");
        assertEquals("2002-07-01", userData.getBirthdaySQL());
    }

    @Test
    public void birthday_to_UI_is_correct(){
        UserData userData = new UserData();
        userData.setBirthday("2002-07-01");
        assertEquals("01.07.2002", userData.getBirthday());
    }

    @Test
    public void constructor_is_correct(){
        UserData userData = new UserData(1 ,"TestName", "test@email.com", "Männlich", "2002-07-01", 180, 70);
        assertEquals(1, userData.getId());
        assertEquals("TestName", userData.getUsername());
        assertEquals("test@email.com", userData.getEmail());
        assertEquals("Männlich", userData.getGender());
        assertEquals("2002-07-01", userData.getBirthdaySQL());
        assertEquals(180, userData.getHeight());
        assertEquals(70, userData.getWeight());
    }
}