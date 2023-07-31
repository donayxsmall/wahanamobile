package com.wahana.wahanamobile.webserviceClient;

import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.mkSTMASingle;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetBongkarMAProcessList;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetLastSess;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetTTKMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2InitBongkarMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2MkBongkarMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2MkBongkarMAFinal;
import com.wahana.wahanamobile.ModelApiOPS.BuatMaDirect.opv2CreateDirectMA;
import com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK.aoCekTTK;
import com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK.aoSubmitEditBeratTTK;
import com.wahana.wahanamobile.ModelApiOPS.ModaAngkutan.cekMsforMA;
import com.wahana.wahanamobile.ModelApiOPS.STMS.getTtkFromMs;
import com.wahana.wahanamobile.ModelApiOPS.STMS.setDataSTMS;
import com.wahana.wahanamobile.ModelApiOPS.STSM.getMsFromMa;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoCreateMSPUOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoGenTTKPickupOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupBeginOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupKodeVerTTKBatal;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupVerifyOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.STSM.setDataSTSM;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2CekDimensiSTPU;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2GetTTKSTPUv2;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2STPUv2;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2CreateSTPU2MA;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2GetTTKSTPU;
import com.wahana.wahanamobile.ModelApiOPS.aoAuthUser;
import com.wahana.wahanamobile.ModelApiOPS.aoCekAlasanRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoCekVersiAPK;
import com.wahana.wahanamobile.ModelApiOPS.aoCheckSTMRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoGetAlasanRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoGetJenisKeranjang;
import com.wahana.wahanamobile.ModelApiOPS.aoGetListKeranjang;
import com.wahana.wahanamobile.ModelApiOPS.aoSubmitPenerimaanRetur;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by donay on 07/11/19.
 */

public interface RequestApiWahanaOps {



    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoAuthUser> aoAuthUser(@Field("_r") String _r,
                                 @Field("partnerId") String partnerId,
                                 @Field("nik") String nik,
                                 @Field("password") String password );


    ///----STMS PENERIMAAN RETUR----///

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoCekAlasanRetur> aoCekAlasanRetur(@Field("_r") String _r,
                                            @Field("partnerId") String partnerId,
                                            @Field("ttkno") String ttkno,
                                            @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoGetListKeranjang> aoGetListKeranjang(@Field("_r") String _r,
                                                @Field("partnerId") String partnerId,
                                                @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoGetJenisKeranjang> aoGetJenisKeranjang(@Field("_r") String _r,
                                                  @Field("partnerId") String partnerId,
                                                  @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoGetAlasanRetur> aoGetAlasanRetur(@Field("_r") String _r,
                                            @Field("partnerId") String partnerId,
                                            @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoCheckSTMRetur> aoCheckSTMRetur(@Field("_r") String _r,
                                          @Field("partnerId") String partnerId,
                                          @Field("ttkno") String ttkno,
                                          @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoSubmitPenerimaanRetur> aoSubmitPenerimaanRetur(@Field("_r") String _r,
                                                  @Field("partnerId") String partnerId,
                                                  @Field("nottk") String nottk,
                                                  @Field("nokeranjang") String nokeranjang,
                                                  @Field("employeeCode") String employeeCode,
                                                  @Field("tgldibuat") String tgldibuat,
                                                  @Field("idalasanreturops") String idalasanreturops,
                                                  @Field("alasanreturcs") String alasanreturcs,
                                                  @Field("jumlah") String jumlah,
                                                  @Field("idjeniskeranjang") String idjeniskeranjang,
                                                  @Field("foto1") String foto1,
                                                  @Field("foto2") String foto2,
                                                  @Field("foto3") String foto3,
                                                  @Field("authCode") String authCode );

    /////--------------END--------------////



    ////--------PICKUP SCAN--------////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoPickupBeginOpsRetail> aoPickupBeginOpsRetail(@Field("_r") String _r,
                                                        @Field("partnerId") String partnerId,
                                                        @Field("employeeCode") String employeeCode,
                                                        @Field("agenCode") String agenCode,
                                                        @Field("sessionId") String sessionId,
                                                        @Field("lokasi") JSONObject lokasi,
                                                        @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoPickupVerifyOpsRetail> aoPickupVerifyOpsRetail(@Field("_r") String _r,
                                                          @Field("partnerId") String partnerId,
                                                          @Field("employeeCode") String employeeCode,
                                                          @Field("agenCode") String agenCode,
                                                          @Field("verificationCode") String verificationCode,
                                                          @Field("flagSortir") String flagSortir,
                                                          @Field("sessionId") String sessionId,
                                                          @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoGenTTKPickupOpsRetail> aoGenTTKPickupOpsRetail(@Field("_r") String _r,
                                                          @Field("partnerId") String partnerId,
                                                          @Field("employeeCode") String employeeCode,
                                                          @Field("agenCode") String agenCode,
                                                          @Field("kodeSortir") String kodeSortir,
                                                          @Field("sessionId") String sessionId,
                                                          @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoPickupKodeVerTTKBatal> aoPickupKodeVerTTKBatal(@Field("_r") String _r,
                                                          @Field("partnerId") String partnerId,
                                                          @Field("employeeCode") String employeeCode,
                                                          @Field("agenCode") String agenCode,
                                                          @Field("verificationCode") String verificationCode,
                                                          @Field("sessionId") String sessionId,
                                                          @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoCreateMSPUOpsRetail> aoCreateMSPUOpsRetail(@Field("_r") String _r,
                                                      @Field("partnerId") String partnerId,
                                                      @Field("employeeCode") String employeeCode,
                                                      @Field("agenCode") String agenCode,
                                                      @Field("ttk") JSONObject ttk,
                                                      @Field("lokasi") JSONObject lokasi,
                                                      @Field("versionpu") String versionpu,
                                                      @Field("authCode") String authCode );


    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoCekVersiAPK> aoCekVersiAPK(@Field("_r") String _r,
                                      @Field("jenis") String jenis,
                                      @Field("version") String version,
                                      @Field("partnerId") String partnerId);



    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoCekTTK> aoCekTTK(@Field("_r") String _r,
                            @Field("partnerId") String partnerId,
                            @Field("employeeCode") String employeeCode,
                            @Field("ttk") String ttk,
                            @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<aoSubmitEditBeratTTK> aoSubmitEditBeratTTK(@Field("_r") String _r,
                                                    @Field("partnerId") String partnerId,
                                                    @Field("employeeCode") String employeeCode,
                                                    @Field("ttk") String ttk,
                                                    @Field("weight") String weight,
                                                    @Field("length") String length,
                                                    @Field("width") String width,
                                                    @Field("height") String height,
                                                    @Field("fotopaket") String fotopaket,
                                                    @Field("fotoberat") String fotoberat,
                                                    @Field("fotopanjang") String fotopanjang,
                                                    @Field("fotolebar") String fotolebar,
                                                    @Field("fototinggi") String fototinggi,
                                                    @Field("authCode") String authCode );




    ////----------END--------------////






    /////------------------------------------MODA ANGKUTAN-----------------------------------//////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<cekMsforMA> cekMsforMA(@Field("_r") String _r,
                                @Field("partnerId") String partnerId,
                                @Field("employeeCode") String employeeCode,
                                @Field("noMs") String noMs,
                                @Field("via") String via,
                                @Field("kodeTujuan") String kodeTujuan,
                                @Field("authCode") String authCode );


    /////--------------------------------------END-------------------------------------------//////


    /////------------------------------------STSM-----------------------------------//////
    @FormUrlEncoded
    @POST("apws.cgi")
    Call<getMsFromMa> getMsFromMa(@Field("_r") String _r,
                                  @Field("partnerId") String partnerId,
                                  @Field("employeeCode") String employeeCode,
                                  @Field("noMA") String noMA,
                                  @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<setDataSTSM> setDataSTSM(@Field("_r") String _r,
                                  @Field("partnerId") String partnerId,
                                  @Field("employeeCode") String employeeCode,
                                  @Field("noMs") JSONObject noMs,
                                  @Field("authCode") String authCode );



    /////--------------------------------------END-------------------------------------------//////



    /////------------------------------------STMS-----------------------------------//////
    @FormUrlEncoded
    @POST("apws.cgi")
    Call<getTtkFromMs> getTtkFromMs(@Field("_r") String _r,
                                    @Field("partnerId") String partnerId,
                                    @Field("employeeCode") String employeeCode,
                                    @Field("noMs") String noMs,
                                    @Field("authCode") String authCode );


    @FormUrlEncoded
    @POST("apws.cgi")
    Call<setDataSTMS> setDataSTMS(@Field("_r") String _r,
                                  @Field("partnerId") String partnerId,
                                  @Field("employeeCode") String employeeCode,
                                  @Field("noTtk") JSONObject noTtk,
                                  @Field("jumlah") String jumlah,
                                  @Field("noMs") String noMs,
                                  @Field("authCode") String authCode );




    /////--------------------------------------END-------------------------------------------//////

    /////------------------------------------STPU MA-----------------------------------//////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2GetTTKSTPU> opv2GetTTKSTPU(@Field("_r") String _r,
                                        @Field("partnerId") String partnerId,
                                        @Field("employeeCode") String employeeCode,
                                        @Field("specification") String specification,
                                        @Field("nikPU") String nikPU,
                                        @Field("nikMA") String nikMA,
                                        @Field("sortingCode") String sortingCode,
                                        @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2CreateSTPU2MA> opv2CreateSTPU2MA(@Field("_r") String _r,
                                              @Field("partnerId") String partnerId,
                                              @Field("employeeCode") String employeeCode,
                                              @Field("specification") String specification,
                                              @Field("nikPU") String nikPU,
                                              @Field("nikMA") String nikMA,
                                              @Field("sortingCode") String sortingCode,
                                              @Field("ttk") JSONObject ttk,
                                              @Field("geolocation") JSONObject geolocation,
                                              @Field("devicetime") String devicetime,
                                              @Field("authCode") String authCode );

    /////--------------------------------------END-------------------------------------------//////


    /////------------------------------------DIRECT MA-----------------------------------//////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2CreateDirectMA> opv2CreateDirectMA(@Field("_r") String _r,
                                                @Field("partnerId") String partnerId,
                                                @Field("employeeCode") String employeeCode,
                                                @Field("specification") String specification,
                                                @Field("nikMA") String nikMA,
                                                @Field("mobilNo") String mobilNo,
                                                @Field("mobilKM") String mobilKM,
                                                @Field("sealNo") String sealNo,
                                                @Field("sealPicture") String sealPicture,
                                                @Field("kodeopstujuan") String kodeopstujuan,
                                                @Field("geolocation") JSONObject geolocation,
                                                @Field("devicetime") String devicetime,
                                                @Field("authCode") String authCode );

    /////--------------------------------------END-------------------------------------------//////


    /////------------------------------------BONGKAR MA-----------------------------------//////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2GetBongkarMAProcessList> opv2GetBongkarMAProcessList(@Field("_r") String _r,
                                                                  @Field("partnerId") String partnerId,
                                                                  @Field("nikBongkar") String nikBongkar,
                                                                  @Field("specification") String specification,
                                                                  @Field("nomorMA") String nomorMA,
                                                                  @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2GetTTKMA> opv2GetTTKMA(@Field("_r") String _r,
                                    @Field("partnerId") String partnerId,
                                    @Field("nikPenerima") String nikPenerima,
                                    @Field("specification") String specification,
                                    @Field("nomorMA") String nomorMA,
                                    @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2InitBongkarMA> opv2InitBongkarMA(@Field("_r") String _r,
                                              @Field("partnerId") String partnerId,
                                              @Field("nikBongkar") String nikBongkar,
                                              @Field("specification") String specification,
                                              @Field("nikLintas") String nikLintas,
                                              @Field("sealNo") String sealNo,
                                              @Field("sealPicture") String sealPicture,
                                              @Field("kodeKantor") String kodeKantor,
                                              @Field("nomorMA") String nomorMA,
                                              @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2MkBongkarMA> opv2MkBongkarMA(@Field("_r") String _r,
                                          @Field("partnerId") String partnerId,
                                          @Field("nikPenerima") String nikPenerima,
                                          @Field("specification") String specification,
                                          @Field("nomorMA") String nomorMA,
                                          @Field("sessionId") String sessionId,
                                          @Field("ttk") JSONArray ttk,
                                          @Field("authCode") String authCode );


    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2MkBongkarMAFinal> opv2MkBongkarMAFinal(@Field("_r") String _r,
                                                    @Field("partnerId") String partnerId,
                                                    @Field("nikBongkar") String nikBongkar,
                                                    @Field("specification") String specification,
                                                    @Field("nomorMA") String nomorMA,
                                                    @Field("geolocation") JSONObject geolocation,
                                                    @Field("devicetime") String devicetime,
                                                    @Field("authCode") String authCode );


    @FormUrlEncoded
    @POST("apws.cgi")
    Call<mkSTMASingle> mkSTMASingle(@Field("_r") String _r,
                                    @Field("partnerId") String partnerId,
                                    @Field("employeeCode") String employeeCode,
                                    @Field("session") String session,
                                    @Field("ttk") String ttk,
                                    @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2GetLastSess> opv2GetLastSess(@Field("_r") String _r,
                                          @Field("partnerId") String partnerId,
                                          @Field("kodeKantor") String kodeKantor,
                                          @Field("nikLintas") String nikLintas,
                                          @Field("sessNo") String sessNo,
                                          @Field("authCode") String authCode );


    /////--------------------------------------END-------------------------------------------//////



    /////------------------------------------STPU BERAT-----------------------------------//////

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2GetTTKSTPUv2> opv2GetTTKSTPUv2(@Field("_r") String _r,
                                            @Field("partnerId") String partnerId,
                                            @Field("employeeCode") String employeeCode,
                                            @Field("specification") String specification,
                                            @Field("nikPU") String nikPU,
                                            @Field("sortingCode") String sortingCode,
                                            @Field("authCode") String authCode );

    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2CekDimensiSTPU> opv2CekDimensiSTPU(@Field("_r") String _r,
                                                @Field("partnerId") String partnerId,
                                                @Field("employeeCode") String employeeCode,
                                                @Field("specification") String specification,
                                                @Field("ttk") String ttk,
                                                @Field("berat") String berat,
                                                @Field("panjang") String panjang,
                                                @Field("lebar") String lebar,
                                                @Field("tinggi") String tinggi,
                                                @Field("authCode") String authCode );


    @FormUrlEncoded
    @POST("apws.cgi")
    Call<opv2STPUv2> opv2STPUv2(@Field("_r") String _r,
                                @Field("partnerId") String partnerId,
                                @Field("employeeCode") String employeeCode,
                                @Field("specification") String specification,
                                @Field("nikPU") String nikPU,
                                @Field("sortingCode") String sortingCode,
                                @Field("ttk") JSONObject ttk,
                                @Field("geolocation") JSONObject geolocation,
                                @Field("devicetime") String devicetime,
                                @Field("authCode") String authCode );


    /////--------------------------------------END-------------------------------------------//////



//    @FormUrlEncoded
//    @POST("Testing")
//    Call<aoCekTTK> aoCekTTK(@Field("_r") String _r,
//                            @Field("partnerId") String partnerId,
//                            @Field("employeeCode") String employeeCode,
//                            @Field("ttk") String ttk,
//                            @Field("authCode") String authCode );
//
//    @FormUrlEncoded
//    @POST("Testing")
//    Call<aoSubmitEditBeratTTK> aoSubmitEditBeratTTK(@Field("_r") String _r,
//                                                    @Field("partnerId") String partnerId,
//                                                    @Field("employeeCode") String employeeCode,
//                                                    @Field("ttk") String ttk,
//                                                    @Field("weight") String weight,
//                                                    @Field("length") String length,
//                                                    @Field("width") String width,
//                                                    @Field("height") String height,
//                                                    @Field("fotopaket") String fotopaket,
//                                                    @Field("fotoberat") String fotoberat,
//                                                    @Field("fotopanjang") String fotopanjang,
//                                                    @Field("fotolebar") String fotolebar,
//                                                    @Field("fototinggi") String fototinggi,
//                                                    @Field("authCode") String authCode );

    ////---------API DUMMY-----------////



}
