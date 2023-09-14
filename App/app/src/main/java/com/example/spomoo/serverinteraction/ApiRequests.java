package com.example.spomoo.serverinteraction;

/*
 * ApiRequests of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/*
 * Interface for the http requests
 */
public interface ApiRequests {

    //used to register a user temporarily
    @FormUrlEncoded
    @POST("registeruser")
    Call<ResponseDefault> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("gender") String gender,
            @Field("birthday") String birthday,
            @Field("height") int height,
            @Field("weight") int weight
    );

    //used to verify a temporarily registered user
    @FormUrlEncoded
    @POST("registeruser/verifyuser")
    Call<ResponseLogin> verifyUser(
            @Field("email") String email,
            @Field("temppassword") String tempPassword,
            @Field("password") String password
    );

    //send email with temporary password again
    @FormUrlEncoded
    @POST("registeruser/sendtemppassword")
    Call<ResponseDefault> sendTempPassword(
            @Field("email") String email
    );

    //used to login a user
    @FormUrlEncoded
    @POST("userlogin")
    Call<ResponseLogin> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    //used to updated personal data
    @FormUrlEncoded
    @POST("updateuser/personaldata")
    Call<ResponseLogin> updateUser(
            @Field("id") int id,
            @Field("username") String username,
            @Field("email") String email,
            @Field("gender") String gender,
            @Field("birthday") String birthday,
            @Field("height") int height,
            @Field("weight") int weight,
            @Field("password") String password
    );

    //used to request email with temporary password
    @FormUrlEncoded
    @POST("updateuser/sendtemppassword")
    Call<ResponseDefault> updatePasswordGetTemporary(
            @Field("email") String email
    );

    //used to update password
    @FormUrlEncoded
    @POST("updateuser/updatepassword")
    Call<ResponseDefault> updatePassword(
            @Field("email") String email,
            @Field("temppassword") String temppassword,
            @Field("newpassword") String newpassword
    );

    //used to send accelerometer data
    @FormUrlEncoded
    @POST("senddata/accelerometer")
    Call<ResponseDefault> sendAccelerometerData(
            @Field("accelerometer") String accelerometerData
    );

    //used to send rotation data
    @FormUrlEncoded
    @POST("senddata/rotation")
    Call<ResponseDefault> sendRotationData(
            @Field("rotation") String rotationData
    );

    //used to send steps data
    @FormUrlEncoded
    @POST("senddata/steps")
    Call<ResponseDefault> sendStepsData(
            @Field("steps") String stepsData
    );

    //used to send sport data
    @FormUrlEncoded
    @POST("senddata/sport")
    Call<ResponseDefault> sendSportData(
            @Field("sport") String sportData
    );

    //used to send questionnaire data
    @FormUrlEncoded
    @POST("senddata/questionnaire")
    Call<ResponseDefault> sendQuestionnaireData(
            @Field("questionnaire") String questionnaireData
    );

    /*
    @GET("allusers")
    Call<UsersResponse> getUsers();

    @FormUrlEncoded
    @PUT("updateuser/{id}")
    Call<ResponseLogin> updateUser(
            @Path("id") int id,
            @Field("email") String email,
            @Field("name") String name,
            @Field("school") String school
    );

        @DELETE("deleteuser/{id}")
    Call<ResponseDefault> deleteUser(@Path("id") int id);
    */
}
