package com.example.spomoo.serverinteraction;

/*
 * RetrofitClient of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Helper class to setup retrofit using OkHttpClient
 * Uses https
 * Supports http basic authentication
 */
public class RetrofitClient {

    //private attributes
    private static final String authKey1 = "TODO";  //key 1 of http basic authentication
    private static final String authKey2 = "TODO";  //key 1 of http basic authentication
    private static final String AUTH = "Basic " + Base64.encodeToString((authKey1+":"+authKey2).getBytes(), Base64.NO_WRAP);    //used for http basic authentication
    private static final String BASE_URL = "TODO";    //base url of the server API
    private static RetrofitClient mInstance;    //static instance of class
    private Retrofit retrofit;  //retrofit client

    //private constructor
    private RetrofitClient() {

        //needed to trust self signed https certificates
        TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };

        //initialise https with trusting self signed certificates
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { TRUST_ALL_CERTS }, new java.security.SecureRandom());    //needed to trust self signed https certificates
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        //create http client with http basic authentication for http requests
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .addHeader("Authorization", AUTH)   //add http basic authentication
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                )
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS)    //needed to trust self signed https certificates
                .hostnameVerifier((s, sslSession) -> true)  //needed to trust self signed https certificates
                .build();

        //build retrofit client
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    //singleton pattern
    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    //checks if there is a wifi or mobile data connection
    public static boolean internetIsConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }

    //use the ApiRequests with retrofit
    public ApiRequests getApi() {
        return retrofit.create(ApiRequests.class);
    }
}
