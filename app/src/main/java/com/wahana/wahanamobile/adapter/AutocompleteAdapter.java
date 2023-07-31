package com.wahana.wahanamobile.adapter;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Tujuan;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SessionManager;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteAdapter extends ArrayAdapter<Tujuan> implements Filterable {
    private ArrayList<Tujuan> mTujuan;
    private Activity activity;
    private String asal;
    private SessionManager session;

    public AutocompleteAdapter(Activity a, int resource, String asal) {
        super(a, resource);
        activity = a;
        this.asal = asal;
        mTujuan = new ArrayList<>();
        session = new SessionManager(a);
    }

    @Override
    public int getCount() {
        return mTujuan.size();
    }

    @Override
    public Tujuan getItem(int position) {
        return mTujuan.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                Log.d("hasil", constraint+" "+asal);
                if(constraint != null){
                    try{
                        //get data from the web
                        String term = constraint.toString();
//                        mTujuan = new DownloadTujuan().execute(term).get();
                        ArrayList<String> parameter = new ArrayList<String>();
                        parameter.add("getTariffNodeDestination");
                        parameter.add(session.getSessionID());
                        parameter.add(asal);
                        parameter.add(term);
                        mTujuan = new SoapClient(){}.execute(parameter).get();
                    }catch (Exception e){
                        Log.d("Hasil","EXCEPTION "+e);
                    }
                    filterResults.values = mTujuan;
                    filterResults.count = mTujuan.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.adapter_kota,parent,false);

        //get Tujuan
        Tujuan tujuan = mTujuan.get(position);

        TextView id = (TextView) view.findViewById(R.id.textView);
        TextView TujuanName = (TextView) view.findViewById(R.id.textView2);

        TujuanName.setText(tujuan.getName());
        id.setText(tujuan.getId());

        return view;
    }

    public class SoapClient extends AsyncTask<ArrayList<String>, Void, ArrayList<Tujuan>> {
        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
        String URL = "http://ws.wahana.com/ws/mobileService/";

        @Override
        protected ArrayList<Tujuan> doInBackground(ArrayList<String>... params) {

            ArrayList<String> passed = params[0];
            String METHOD_NAME = passed.get(0);
            SoapObject hasil = null;
            Log.d("Method Name", METHOD_NAME);
            try {
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("originNodeId", passed.get(2));
                Request.addProperty("destinationConstraint", passed.get(3));

                Log.d("Method", passed.get(0) + "<>" + SOAP_ACTION);

                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                // soapEnvelope.encodingStyle=SoapSerializationEnvelope.ENC2003;
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                MarshalBase64 marshal = new MarshalBase64();
                marshal.register(soapEnvelope);
                MarshalDouble mdo = new MarshalDouble();
                mdo.register(soapEnvelope);
                HttpTransportSE transport = new HttpTransportSE(URL);
                List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("android:silakan".getBytes())));

                transport.call(SOAP_ACTION, soapEnvelope,headerList);
                hasil = (SoapObject) soapEnvelope.bodyIn;
                ArrayList<Tujuan> tujuanList = new ArrayList<>();
                if(hasil==null){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }else {
                    try {
                        final String text = hasil.getProperty(1).toString();
                        if (text.equals("OK")) {
                            mTujuan.clear();
                            hasil.getPropertyCount();
                            for (int i=2; i < hasil.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) hasil.getProperty(i);
                                String id = so.getProperty("nodeId").toString();
                                String name = so.getProperty("nodeName").toString();
                                String longname = so.getProperty("nodeNameLong").toString();
                                Tujuan tujuan = new Tujuan();
                                tujuan.setId(id);
                                tujuan.setName(name+", "+longname);
                                tujuanList.add(tujuan);
                            }
                        }else{
                            activity.runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                        }
                    }catch (Exception e){}
                }
                return tujuanList;

            } catch (Exception ex) {
                Log.e("SOAP", "Error: " + ex.getMessage());
                return null;
            }
        }


    }
}
