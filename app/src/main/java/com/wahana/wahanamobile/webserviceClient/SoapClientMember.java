package com.wahana.wahanamobile.webserviceClient;

import android.os.AsyncTask;
import android.util.Log;

import com.wahana.wahanamobile.helper.MarshalDouble;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sadewa on 6/30/2016.
 */
public class SoapClientMember extends AsyncTask<ArrayList<String>, Void, SoapObject> {

    String SOAP_ACTION = "https://wahana.com/MemberSoapServer/";
    String NAMESPACE = "https://wahana.com/assets/wahana.wsdl";
    String URL = "https://wahana.com/MemberSoapServer/";

//    String SOAP_ACTION = "https://weblib.wahana.com/MemberSoapServer/";
//    String NAMESPACE = "https://weblib.wahana.com/assets/wahana.wsdl";
//    String URL = "https://weblib.wahana.com/MemberSoapServer/";

    @Override
    protected SoapObject doInBackground(ArrayList<String>... params) {

        ArrayList<String> passed = params[0];
        String METHOD_NAME = passed.get(0);
        SoapObject hasil = null;
        Log.d("Method Name", METHOD_NAME);
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
            if(METHOD_NAME.equals("loginMember")){
                Request.addProperty("userName", passed.get(1));
                Request.addProperty("password", passed.get(2));
            }else if(METHOD_NAME.equals("registerMember1")){
                Request.addProperty("name", passed.get(1));
                Request.addProperty("email", passed.get(2));
                Request.addProperty("phone", passed.get(3));
                Request.addProperty("password", passed.get(4));
            }else if(METHOD_NAME.equals("registerMember2")){
                Request.addProperty("userId", passed.get(1));
                Request.addProperty("province", passed.get(2));
                Request.addProperty("kabkot", passed.get(3));
                Request.addProperty("camat", passed.get(4));
                Request.addProperty("lurah", passed.get(5));
                Request.addProperty("address", passed.get(6));
                Request.addProperty("bps_code", passed.get(7));
            }else if(METHOD_NAME.equals("registerMember3")){
                Request.addProperty("userId", passed.get(1));
                Request.addProperty("date_birth", passed.get(2));
                Request.addProperty("unique_member", passed.get(3));
            }else if(METHOD_NAME.equals("getKabkot")){
                Request.addProperty("province", passed.get(1));
            }else if(METHOD_NAME.equals("getCamat")){
                Request.addProperty("province", passed.get(1));
                Request.addProperty("kabkot", passed.get(2));
            }else if(METHOD_NAME.equals("getLurah")){
                Request.addProperty("province", passed.get(1));
                Request.addProperty("kabkot", passed.get(2));
                Request.addProperty("camat", passed.get(3));
            }else if(METHOD_NAME.equals("gcmUserLogin")){
                Request.addProperty("user_id", passed.get(1));
                Request.addProperty("token_firebase", passed.get(2));
            }else if(METHOD_NAME.equals("gcmUserLogout")){
                Request.addProperty("user_id", passed.get(1));
            }else if(METHOD_NAME.equals("ttkUserSave")){
                Request.addProperty("user_id", passed.get(1));
                Request.addProperty("ttk", passed.get(2));
                Request.addProperty("status_ttk", passed.get(3));
                Request.addProperty("ket", passed.get(4));
            }else if(METHOD_NAME.equals("listTtkUser")){
                Request.addProperty("user_id", passed.get(1));
            }else if(METHOD_NAME.equals("deleteTtkUser")){
                Request.addProperty("user_id", passed.get(1));
                Request.addProperty("ttk", passed.get(2));
            }else if(METHOD_NAME.equals("forgetPassword")){
                Request.addProperty("email", passed.get(1));
            }else if(METHOD_NAME.equals("changePassword")){
                Request.addProperty("user_id", passed.get(1));
                Request.addProperty("oldPass", passed.get(2));
                Request.addProperty("newPass", passed.get(3));
            }else if(METHOD_NAME.equals("findAgent")){
                Request.addProperty("lat", passed.get(1));
                Request.addProperty("long", passed.get(2));
                Request.addProperty("kabkot", passed.get(3));
                Request.addProperty("radius", passed.get(4));
            }

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            MarshalDouble mdo = new MarshalDouble();
            mdo.register(soapEnvelope);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            hasil = (SoapObject) soapEnvelope.getResponse();

        } catch (Exception ex) {
            Log.e("SOAP", "Error: " + ex.getMessage());
        }
        return hasil;
    }


}
