package com.wahana.wahanamobile.webserviceClient;

import android.os.AsyncTask;
import android.util.Log;

import com.wahana.wahanamobile.R;
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
 * Created by Sadewa on 01/06/2017.
 */

public class SoapClientMobile extends AsyncTask<ArrayList<String>, Void, SoapObject> {
    String SOAP_ACTION = "http://ws.wahana.com/ws/wws/";
    String NAMESPACE = "http://ws.wahana.com/ws/wws/";
    String URL = "http://ws.wahana.com/ws/wws/";

//    String SOAP_ACTION = "http://sandbox.wahana.com:81/ws/wws/";
//    String NAMESPACE = "http://sandbox.wahana.com:81/ws/wws/";
//    String URL = "http://sandbox.wahana.com:81/ws/wws/";

//    String SOAP_ACTION = "http://intranet.wahana.com/ws/wws/";
//    String NAMESPACE = "http://intranet.wahana.com/ws/wws/";
//    String URL = "http://intranet.wahana.com/ws/wws/";

    @Override
    protected SoapObject doInBackground(ArrayList<String>... params) {

        ArrayList<String> passed = params[0];
        String METHOD_NAME = passed.get(0);
        SoapObject hasil = null;
        Log.d("Method Name", METHOD_NAME);
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
            if(METHOD_NAME.equals("doSSQL")){
                SoapObject req = new SoapObject();
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("name", passed.get(2));
                Request.addProperty("limit", passed.get(3));
                Request.addProperty("offset", passed.get(4));
                req.addProperty("name", passed.get(5));
                req.addProperty("value", passed.get(6));
                Request.addProperty("parameter", req);
                if (passed.get(2).equals("cekMS")){
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }else if(passed.get(2).equals("getNikTTK")){
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }else if(passed.get(2).equals("checkMsNik")){
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }else if (passed.get(2).equals("getTTKstbtByNik")){
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }else if (passed.get(2).equals("whnGetBAKurirByNoSpv")){
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }else if (passed.get(2).equals("getjumlahcutiAndroid")) {
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);

                    SoapObject req2 = new SoapObject();
                    req2.addProperty("name", passed.get(9));
                    req2.addProperty("value", passed.get(10));
                    Request.addProperty("parameter", req2);
                }else if (passed.get(2).equals("cekAPIGetTTKInfoForMS")) {
                    SoapObject req1 = new SoapObject();
                    req1.addProperty("name", passed.get(7));
                    req1.addProperty("value", passed.get(8));
                    Request.addProperty("parameter", req1);
                }
            }
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            MarshalBase64 marshal = new MarshalBase64();
            marshal.register(soapEnvelope);
            MarshalDouble mdo = new MarshalDouble();
            mdo.register(soapEnvelope);
            int timeout = R.string.timeout;
            HttpTransportSE transport = new HttpTransportSE(URL, timeout);
            List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
            headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("android:silakan".getBytes())));

            transport.call(SOAP_ACTION, soapEnvelope,headerList);

            hasil = (SoapObject) soapEnvelope.bodyIn;
            Log.d("HASIL", "" + hasil);

        } catch (Exception ex) {
            Log.e("SOAP", "Error: " + ex.getMessage());
        }
        return hasil;
    }


}
