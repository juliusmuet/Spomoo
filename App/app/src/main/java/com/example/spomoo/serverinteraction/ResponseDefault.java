package com.example.spomoo.serverinteraction;

/*
 * ResponseDefault of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import com.google.gson.annotations.SerializedName;

/*
 * Default response for http responses
 */
public class ResponseDefault {

    //private attributes
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    //constructor
    public ResponseDefault(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    //getters
    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
