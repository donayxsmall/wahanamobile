package com.wahana.wahanamobile.webserviceClient;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.HeaderProperty;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Sadewa on 6/30/2016.
 */
public class SoapClient extends AsyncTask<ArrayList<String>, Void, SoapObject> {

    String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
    String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
    String URL = "http://ws.wahana.com/ws/mobileService/";

//    String SOAP_ACTION = "http://intranet.wahana.com/ws/mobileService/";
//    String NAMESPACE = "http://intranet.wahana.com/ws/mobileService/";
//    String URL = "http://intranet.wahana.com/ws/mobileService/";

//    String SOAP_ACTION = "http://sandbox.wahana.com:81/ws/mobileService/";
//    String NAMESPACE = "http://sandbox.wahana.com:81/ws/mobileService/";
//    String URL = "http://sandbox.wahana.com:81/ws/mobileService/";

    @Override
    protected SoapObject doInBackground(ArrayList<String>... params) {

        ArrayList<String> passed = params[0];
        String METHOD_NAME = passed.get(0);
        SoapObject hasil = null;
        Log.d("Method Name", METHOD_NAME);
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
            if(METHOD_NAME.equals("login")){
                Request.addProperty("userName", passed.get(1));
                Request.addProperty("password", passed.get(2));
            }else if(METHOD_NAME.equals("getActivePackageCount")){
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
            }else if(METHOD_NAME.equals("attandanceSet")){
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("userName", passed.get(3));
                Request.addProperty("attendanceType", passed.get(4));
                Request.addProperty("dateTime", passed.get(5));
                Request.addProperty("photo", passed.get(6));
                SoapObject req = new SoapObject();
                req.addProperty("longitude", passed.get(7));
                req.addProperty("latitude", passed.get(8));
                Request.addProperty("location",req);
                Log.d("requset",Request+"");
                Log.d("requset2",req+"");
            }else if (METHOD_NAME.equals("getLoginInfo")){
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("userName", passed.get(2));
            }else if (METHOD_NAME.equals("getTariffNodeOrigin")){
                Request.addProperty("sessionId", passed.get(1));
            }else if (METHOD_NAME.equals("getTariff")){
                Request.addProperty("sessionID", passed.get(1));
                Request.addProperty("originNodeId", passed.get(2));
                Request.addProperty("destinationNodeId", passed.get(3));
                Request.addProperty("weight", passed.get(4));
                Log.d("method", METHOD_NAME);
            }else if (METHOD_NAME.equals("pickupBegin")){
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("agentCode", passed.get(4));
            }else if (METHOD_NAME.equals("pickupVerify")){
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
                Request.addProperty("agentCode", passed.get(3));
                Request.addProperty("verificationCode", passed.get(4));
            }else if (METHOD_NAME.equals("trackPackage")){
                Log.d("hasil ex packageNumber", passed.get(2)+"");
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("packageNumber", passed.get(2));
            }else if(METHOD_NAME.equals("setPackageDeliveryStatus")){
                SoapObject req = new SoapObject();
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("packageNumber", passed.get(4));
                Request.addProperty("statusDate", passed.get(5));
                Request.addProperty("packageStatus", passed.get(6));
                Request.addProperty("reason",passed.get(7));
                Request.addProperty("receiverName", passed.get(8));
                Request.addProperty("receiverType", passed.get(9));
                Request.addProperty("comment", passed.get(10));
                Request.addProperty("photo1", passed.get(11));
                Request.addProperty("photo2", passed.get(12));
                Request.addProperty("photo3", passed.get(13));
                Request.addProperty("photo4", passed.get(14));
                req.addProperty("longitude", passed.get(15));
                req.addProperty("latitude", passed.get(16));
                Request.addProperty("location",req);
            }else if(METHOD_NAME.equals("attendanceSet")){
                SoapObject req = new SoapObject();
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("attendanceType", passed.get(4));
                Request.addProperty("dateTime", passed.get(5));
                Request.addProperty("photo", passed.get(6));
                req.addProperty("longitude", passed.get(7));
                req.addProperty("latitude", passed.get(8));
                Request.addProperty("location",req);
            }else if (METHOD_NAME.equals("attendanceList")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
                Request.addProperty("dateBegin", passed.get(3));
                Request.addProperty("dateEnd", passed.get(4));
            }else if (METHOD_NAME.equals("listDeliveryOrder")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
            }else if (METHOD_NAME.equals("listPackageByCourier")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
            }else if (METHOD_NAME.equals("pickupGetBagCountPerSortingCode")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
                Request.addProperty("agentCode", passed.get(3));
            }else if (METHOD_NAME.equals("pickupGetPackageListPerSortingCode")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
                Request.addProperty("agentCode", passed.get(3));
                Request.addProperty("sortingCode", passed.get(4));
                Request.addProperty("postingDateMin", passed.get(5));
                Request.addProperty("postingDateMax", passed.get(6));
            }else if (METHOD_NAME.equals("pickupVerPackage")) {
                SoapObject req = new SoapObject();
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("agentCode", passed.get(4));
                Request.addProperty("sortingCode", passed.get(5));
                Request.addProperty("verifiedPackageList", passed.get(5));
            }else if (METHOD_NAME.equals("pickupGenManifest")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("agentCode", passed.get(4));
            }else if (METHOD_NAME.equals("pickupCancel")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("agentCode", passed.get(4));
            }else if (METHOD_NAME.equals("getTariffNodeDestination")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("originNodeId", passed.get(2));
                Request.addProperty("destinationConstraint", passed.get(3));
            }else if (METHOD_NAME.equals("getMyInfo")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("employeeCode", passed.get(2));
                Request.addProperty("dateBegin", passed.get(3));
                Request.addProperty("dateEnd", passed.get(4));
            }else if (METHOD_NAME.equals("pickupVerBagCountPerSortingCode")) {
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("requestId", passed.get(2));
                Request.addProperty("employeeCode", passed.get(3));
                Request.addProperty("agentCode", passed.get(4));
                Request.addProperty("sortingCode", passed.get(5));
                Request.addProperty("bagCount", passed.get(6));
            }else if (METHOD_NAME.equals("getPackageInfo")){
                Log.d("hasil ex packageNumber", passed.get(2)+"");
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("packageNumber", passed.get(2));
            }

            Log.d("Method", passed.get(0) + "<>" + SOAP_ACTION);
            StringBuffer buf = new StringBuffer("android");
            buf.append(':').append("ujicoba");
            byte[] raw = buf.toString().getBytes();
            buf.setLength(0);
            buf.append("Basic ");
            org.kobjects.base64.Base64.encode(raw, 0, raw.length, buf);
        //    ServiceConnection
        //    setRequestProperty("Authorization", buf.toString());

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
           // soapEnvelope.encodingStyle=SoapSerializationEnvelope.ENC2003;
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

        } catch (Exception ex) {
            Log.e("SOAP", "Error: " + ex.getMessage());
        }
        return hasil;
    }


}
