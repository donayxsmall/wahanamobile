package com.wahana.wahanamobile.webserviceClient;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by team-it on 09/10/18.
 */

public class ApiClient {

    //    public static final String BASE_URL = "https://randomuser.me/";
    public static final String BASE_URL = "http://intranet.wahana.com/ci-oauth2/Api/";
    private static Retrofit retrofit = null;
    private static Context context;




    public static Retrofit getClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(false)
//                .addInterceptor(new ConnectivityInterceptor(context))
                .build();


        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

