package com.wahana.wahanamobile.webserviceClient;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.security.ProviderInstaller;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SSLConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Lazuardy on 27/05/2017.
 */

public class SoapClientLogin extends AsyncTask<ArrayList<String>, Void, SoapObject> {
//    String SOAP_ACTION = "https://wahana.com/new/MySoapServer/";
//    String NAMESPACE = "https://wahana.com/new/assets/login.wsdl";
//    String URL = "https://wahana.com/new/MySoapServer/";

    String SOAP_ACTION = "https://m.wahana.com/MySoapServer/";
    String NAMESPACE = "https://m.wahana.com/assets/login.wsdl";
    String URL = "https://m.wahana.com/MySoapServer/";


    @Override
    protected SoapObject doInBackground(ArrayList<String>... params) {

        ArrayList<String> passed = params[0];
        String METHOD_NAME = passed.get(0);
        SoapObject hasil = null;
        Log.d("Method Name", METHOD_NAME);
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
            if(METHOD_NAME.equals("getUserById")){
                Request.addProperty("userId", passed.get(1));
            }else if(METHOD_NAME.equals("login")){
                Request.addProperty("username", passed.get(1));
            }else if(METHOD_NAME.equals("getListMenuByUserTypeCode")){
                Request.addProperty("typeCode", passed.get(1));
            }else if(METHOD_NAME.equals("getListSubMenuByMenuRelationId")){
                Request.addProperty("menuRelationId", passed.get(1));
            }else if(METHOD_NAME.equals("getRoleMenu")){
                Request.addProperty("role", passed.get(1));
            }
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            MarshalDouble mdo = new MarshalDouble();
            mdo.register(soapEnvelope);

//            SSLConnection.allowAllSSL();
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            hasil = (SoapObject) soapEnvelope.getResponse();
            Log.d("HASIL", "HASILL: " + hasil);

        } catch (Exception ex) {
            Log.e("Hasil error", "Error: " + ex.getMessage());
        }
        return hasil;
    }


}
