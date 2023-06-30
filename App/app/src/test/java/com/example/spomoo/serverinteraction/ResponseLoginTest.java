package com.example.spomoo.serverinteraction;

/*
 * ResponseLoginTest of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import static org.junit.Assert.*;

import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;
import com.example.spomoo.utility.UserData;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;

/*
 * Unit tests for ResponseLogin class
 */
public class ResponseLoginTest {

    @Test
    public void constructor_and_getters_are_correct(){
        UserData userData = new UserData();
        ArrayList<AccelerometerData> accelerometerDataArrayList = new ArrayList<>();
        accelerometerDataArrayList.add(new AccelerometerData());
        ArrayList<RotationData> rotationDataArrayList = new ArrayList<>();
        rotationDataArrayList.add(new RotationData());
        ArrayList<StepsData> stepsDataArrayList = new ArrayList<>();
        stepsDataArrayList.add(new StepsData());
        ArrayList<SportData> sportDataArrayList = new ArrayList<>();
        sportDataArrayList.add(new SportData());
        ArrayList<QuestionnaireData> questionnaireDataArrayList = new ArrayList<>();
        questionnaireDataArrayList.add(new QuestionnaireData());
        ResponseLogin responseLogin = new ResponseLogin(true, "TestError", userData, accelerometerDataArrayList, rotationDataArrayList,
                stepsDataArrayList,sportDataArrayList, questionnaireDataArrayList);

        assertEquals(true, responseLogin.isError());
        assertEquals("TestError", responseLogin.getMessage());
        assertEquals(new AccelerometerData().getDate(), responseLogin.getAccelerometer().get(0).getDate());
        assertEquals(new RotationData().getDate(), responseLogin.getRotation().get(0).getDate());
        assertEquals(new StepsData().getSteps(), responseLogin.getSteps().get(0).getSteps());
        assertEquals(new SportData().getType(), responseLogin.getSport().get(0).getType());
        assertEquals(new QuestionnaireData().getMessage(), responseLogin.getQuestionnaire().get(0).getMessage());
    }

    @Test
    public void serialisation_is_correct(){
        ResponseLogin responseLogin = new ResponseLogin(true, "TestError", null, null, null, null, null, null);
        Gson gson = new Gson();
        assertEquals("{\"error\":true,\"message\":\"TestError\"}", gson.toJson(responseLogin));
    }

}