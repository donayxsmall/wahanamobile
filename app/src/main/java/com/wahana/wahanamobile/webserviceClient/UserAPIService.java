package com.wahana.wahanamobile.webserviceClient;


import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.Pickup.PickupBegin;
import com.wahana.wahanamobile.model.Pickup.PickupVerify;
import com.wahana.wahanamobile.model.Tracking;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by team-it on 09/10/18.
 */

public interface UserAPIService {

//    @GET("trackingNew?access_token=093a64444fa19f591682f7087a5e5a08febd9e43&ttk=ALS96291")
//    Call<Tracking> getResultInfo();

    @GET("trackingNew?access_token=093a64444fa19f591682f7087a5e5a08febd9e43&ttk={input}")
    Call<Tracking> getResultInfo(@Path(value = "input", encoded = true) String input);

    @GET("trackingNew?access_token=093a64444fa19f591682f7087a5e5a08febd9e43&ttk=ALS96291")
    Call<ResponseBody> getResultAsJSON();

//    @POST("pickupBegin?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    @FormUrlEncoded
//    Call<ApiPickup> pickupBegin(@Field("employeeCode") String employeeCode,
//                             @Field("agenCode") String agenCode,
//                             @Field("sessionId") String sessionId,
//                             @Field("requestId") String requestId
//                                );


    //API SERVICE INTRANET

//    @FormUrlEncoded
//    @POST("pickupBegin?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  pickupBegin(@Field("employeeCode") String employeeCode,
//                                 @Field("agenCode") String agenCode,
//                                 @Field("sessionId") String sessionId,
//                                 @Field("requestId") String requestId );
//
//
//
//    @FormUrlEncoded
//    @POST("pickupVerify?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  pickupVerify(@Field("sessionId") String sessionId,
//                                  @Field("employeeCode") String employeeCode,
//                                  @Field("agentCode") String agentCode,
//                                  @Field("verificationCode") String verificationCode );
//
//    @FormUrlEncoded
//    @POST("createMSPU?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  CreatePU(@Field("employeeCode") String employeeCode,
//                                 @Field("agenCode") String agenCode,
//                                 @Field("ttk") JSONObject ttk,
//                                 @Field("sessionId") String sessionId,
//                                 @Field("requestId") JSONObject requestId );
//
//    @FormUrlEncoded
//    @POST("genTTKpickup?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  createttkPickup(@Field("agenCode") String agenCode,
//                              @Field("kodesortir") String kodesortir);
//
//
//    @FormUrlEncoded
//    @POST("genTTKstpu?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  genTTKstpu(@Field("employeeCode") String employeeCode);
//
//
//    @FormUrlEncoded
//    @POST("genTTKwithKodeSortirSTPU?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  genTTKwithKodeSortirSTPU(@Field("employeeCode") String employeeCode,
//                                                @Field("kodesortir") String kodesortir);
//
//    @FormUrlEncoded
//    @POST("createSTM?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  createSTM(@Field("employeeCode") String employeeCode,
//                               @Field("kurir") String kurir,
//                               @Field("ttk") JSONObject ttk);




//API SERVICE OPERASIONAL

//    @FormUrlEncoded
//    @POST("pickupBeginOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  pickupBegin(@Field("employeeCode") String employeeCode,
//                                 @Field("agenCode") String agenCode,
//                                 @Field("sessionId") String sessionId,
//                                 @Field("requestId") String requestId );
////    Call<ApiPickup> pickupBegin(@Body ApiPickup pickupBegin);
//
//
//    @FormUrlEncoded
//    @POST("pickupVerifyOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  pickupVerify(@Field("sessionId") String sessionId,
//                                  @Field("employeeCode") String employeeCode,
//                                  @Field("agentCode") String agentCode,
//                                  @Field("verificationCode") String verificationCode );
//
//    @FormUrlEncoded
//    @POST("createMSPUOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  CreatePU(@Field("employeeCode") String employeeCode,
//                              @Field("agenCode") String agenCode,
//                              @Field("ttk") JSONObject ttk,
//                              @Field("sessionId") String sessionId,
//                              @Field("requestId") JSONObject requestId );
//
//    @FormUrlEncoded
//    @POST("genTTKpickupOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  createttkPickup(@Field("agenCode") String agenCode,
//                                     @Field("kodesortir") String kodesortir);
//
//    @FormUrlEncoded
//    @POST("genTTKstpuOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  genTTKstpu(@Field("employeeCode") String employeeCode);
//
//    @FormUrlEncoded
//    @POST("genTTKwithKodeSortirSTPUOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  genTTKwithKodeSortirSTPU(@Field("employeeCode") String employeeCode,
//                                              @Field("kodesortir") String kodesortir);
//
//    @FormUrlEncoded
//    @POST("createSTMOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
//    Call<ApiPickup>  createSTM(@Field("employeeCode") String employeeCode,
//                               @Field("kurir") String kurir,
//                               @Field("ttk") JSONObject ttk);




    //TESTING TTK RETAIL


    @FormUrlEncoded
    @POST("pickupBeginOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  pickupBegin(@Field("employeeCode") String employeeCode,
                                 @Field("agenCode") String agenCode,
                                 @Field("sessionId") String sessionId,
                                 @Field("requestId") String requestId );



    @FormUrlEncoded
    @POST("pickupVerifyOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  pickupVerify(@Field("sessionId") String sessionId,
                                  @Field("employeeCode") String employeeCode,
                                  @Field("agentCode") String agentCode,
                                  @Field("verificationCode") String verificationCode );

    @FormUrlEncoded
    @POST("createMSPUOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  CreatePU(@Field("employeeCode") String employeeCode,
                              @Field("agenCode") String agenCode,
                              @Field("ttk") JSONObject ttk,
                              @Field("sessionId") String sessionId,
                              @Field("requestId") JSONObject requestId );

    @FormUrlEncoded
    @POST("genTTKpickupOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  createttkPickup(@Field("agenCode") String agenCode,
                                     @Field("kodesortir") String kodesortir);


    @FormUrlEncoded
    @POST("genTTKstpuOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  genTTKstpu(@Field("employeeCode") String employeeCode);


    @FormUrlEncoded
    @POST("genTTKwithKodeSortirSTPUOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  genTTKwithKodeSortirSTPU(@Field("employeeCode") String employeeCode,
                                              @Field("kodesortir") String kodesortir);

    @FormUrlEncoded
    @POST("createSTMOpsRetail?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  createSTM(@Field("employeeCode") String employeeCode,
                               @Field("kurir") String kurir,
                               @Field("ttk") JSONObject ttk);




    //API SERVICE OPERASIONAL PU DAN STPU ACCOUNT

    @FormUrlEncoded
    @POST("pickupBeginOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  pickupBeginAccount(@Field("employeeCode") String employeeCode,
                                 @Field("agenCode") String agenCode,
                                 @Field("sessionId") String sessionId,
                                 @Field("requestId") String requestId );
//    Call<ApiPickup> pickupBegin(@Body ApiPickup pickupBegin);


    @FormUrlEncoded
    @POST("pickupVerifyOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  pickupVerifyAccount(@Field("sessionId") String sessionId,
                                  @Field("employeeCode") String employeeCode,
                                  @Field("agentCode") String agentCode,
                                  @Field("verificationCode") String verificationCode );

    @FormUrlEncoded
    @POST("createMSPUOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  CreatePUAccount(@Field("employeeCode") String employeeCode,
                              @Field("agenCode") String agenCode,
                              @Field("ttk") JSONObject ttk,
                              @Field("sessionId") String sessionId,
                              @Field("requestId") JSONObject requestId );

    @FormUrlEncoded
    @POST("genTTKpickupOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  createttkPickupAccount(@Field("agenCode") String agenCode,
                                     @Field("kodesortir") String kodesortir);

    @FormUrlEncoded
    @POST("genTTKstpuOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  genTTKstpuAccount(@Field("employeeCode") String employeeCode);

    @FormUrlEncoded
    @POST("genTTKwithKodeSortirSTPUOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  genTTKwithKodeSortirSTPUAccount(@Field("employeeCode") String employeeCode,
                                              @Field("kodesortir") String kodesortir);

    @FormUrlEncoded
    @POST("createSTMOps?access_token=093a64444fa19f591682f7087a5e5a08febd9e43")
    Call<ApiPickup>  createSTMAccount(@Field("employeeCode") String employeeCode,
                               @Field("kurir") String kurir,
                               @Field("ttk") JSONObject ttk);









    //ANDROID OPS REST

    @FormUrlEncoded
    @POST("pickupBegin")
    Call<PickupBegin>  pickupBeginRest(@Field("employeeCode") String employeeCode,
                                   @Field("agenCode") String agenCode,
                                   @Field("sessionId") String sessionId,
                                   @Field("requestId") String requestId );

    @FormUrlEncoded
    @POST("pickupVerify")
    Call<PickupVerify>  pickupVerifyRest(@Field("sessionId") String sessionId,
                                         @Field("employeeCode") String employeeCode,
                                         @Field("agentCode") String agentCode,
                                         @Field("verificationCode") String verificationCode );







}
