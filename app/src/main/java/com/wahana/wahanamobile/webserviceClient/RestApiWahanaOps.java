package com.wahana.wahanamobile.webserviceClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by team-it on 09/11/18.
 */

public class RestApiWahanaOps {

    //    public static final String BASE_URL = "https://randomuser.me/";
    public static final String BASE_URL = "http://intranet.wahana.com/ci-oauth2/Api/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
