package com.example.spomoo.questionnaire;

/*
 * QuestionnaireDataTest of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

/*
 * Unit tests for QuestionnaireData class
 */
public class QuestionnaireDataTest {

    @Test
    public void serialisation_is_correct(){
        QuestionnaireData questionnaireData = new QuestionnaireData(50, 49, 48, 47, 46, 45,
                10, 15, 0, 3, "Family", "Birthday Party", 6, 5,
                4, 3, 6, 0, 3, 4 ,"Test Message", "2023-04-01",
                "11:11", 23, 323, 0);
        Gson gson = new Gson();
        assertEquals("{\"userid\":323,\"questionnaireid\":23,\"beensent\":0,\"date\":\"2023-04-01\",\"time\":\"11:11\",\"mdbf_satisfied\":50,\"mdbf_calm\":49,\"mdbf_well\":48,\"mdbf_relaxed\":47,\"mdbf_energetic\":46,\"mdbf_awake\":45,\"event_negative\":10,\"event_positive\":15,\"social_alone\":0,\"social_dislike\":3,\"social_people\":\"Family\",\"location\":\"Birthday Party\",\"rumination_properties\":6,\"rumination_rehash\":5,\"rumination_turnoff\":4,\"rumination_dispute\":3,\"selfworth_satisfied\":6,\"selfworth_dissatisfied\":0,\"impulsive\":3,\"impulsive_angry\":4,\"message\":\"Test Message\",\"expanded\":false}", gson.toJson(questionnaireData));
    }

}