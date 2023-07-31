package com.wahana.wahanamobile.webserviceClient;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by donay on 07/11/19.
 */

public class RestApiWahanaOps2019 {

    //    public static final String BASE_URL = "https://randomuser.me/";
//    public static final String BASE_URL = "http://sandbox.wahana.com:81/apps/account/cgi-bin/";
    public static final String BASE_URL = "https://ws.wahana.com/arest/";
//    public static final String BASE_URL = "http://192.168.0.110/api-dummyWahana/";
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
