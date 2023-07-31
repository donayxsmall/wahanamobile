package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Destination;
import com.wahana.wahanamobile.Data.Tujuan;
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
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by Reza on 19/09/2016.
 */
public class DestinationSearchAdapter extends ArrayAdapter<Destination> implements Filterable {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    private String asal;
    ArrayList<Destination> destinationList = new ArrayList<Destination>();

    public DestinationSearchAdapter(Activity a, int resource, ArrayList<Destination> destination, String asal) {
        super(a, resource);
        activity = a;
        this.asal = asal;
        this.destinationList = destination;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        try{
            return destinationList.size();
        }catch (Exception e){
            return 0;
        }
    }

    public Destination getItem(int position) {
        return destinationList.get(position);
    }

    public long getItemId(int position) {
        return position;
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
                        ArrayList<String> parameter = new ArrayList<String>();
                        parameter.add("getTariffNodeDestination");
                        parameter.add("1");
                        parameter.add(asal);
                        if (term.contains(" ")){
                            StringTokenizer tokens = new StringTokenizer(term, " ");
                            String para = "";
                            while (tokens.hasMoreTokens()) {
                                String s = tokens.nextToken();
                                para += " +"+s;
                            }
                            parameter.add(para);
                        }else {
                            parameter.add(term);
                        }
                        destinationList = new SoapClient(){}.execute(parameter).get();
                    }catch (Exception e){
                        Log.d("Hasil","EXCEPTION "+e);
                    }
                    filterResults.values = destinationList;
                    filterResults.count = destinationList.size();
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
        View view = inflater.inflate(R.layout.test_search_listview,parent,false);

        //get Tujuan
        try{
            Destination desti = destinationList.get(position);

            TextView lokasiIsi= (TextView)view.findViewById(R.id.lokasi_isi);
            TextView lokasiId= (TextView)view.findViewById(R.id.lokasi_bps);
            Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
            Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
            lokasiIsi.setTypeface(typeSemibold);
            lokasiIsi.setText(desti.getCity()+", "+desti.getDistric());
            lokasiId.setText(desti.getProvince());
        }catch (Exception e){

        }

        return view;
    }

    public class SoapClient extends AsyncTask<ArrayList<String>, Void, ArrayList<Destination>> {
        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
        String URL = "http://ws.wahana.com/ws/mobileService/";

        @Override
        protected ArrayList<Destination> doInBackground(ArrayList<String>... params) {

            ArrayList<String> passed = params[0];
            String METHOD_NAME = passed.get(0);
            SoapObject hasil = null;
            Log.d("Method Name", METHOD_NAME);
            ArrayList<Destination> destinationList = new ArrayList<>();
            try {
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
                Request.addProperty("sessionId", passed.get(1));
                Request.addProperty("originNodeId", passed.get(2));
                Request.addProperty("destinationConstraint", passed.get(3));

                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                MarshalDouble mdo = new MarshalDouble();
                mdo.register(soapEnvelope);
                HttpTransportSE transport = new HttpTransportSE(URL);
                List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("android:silakan".getBytes())));

                transport.call(SOAP_ACTION, soapEnvelope,headerList);

                hasil = (SoapObject) soapEnvelope.bodyIn;
                if(hasil==null){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }else {
                    try {
                        final String text = hasil.getProperty(1).toString();
                        if (text.equals("OK")) {
                            for (int i = 2; i<hasil.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) hasil.getProperty(i);
                                String province = so.getProperty("nodeId").toString();
                                String city = so.getProperty("nodeNameLong").toString();
                                String distric = "";
                                Destination desti = new Destination();
                                desti.setProvince(province);
                                desti.setCity(city);
                                desti.setDistric(distric);
                                destinationList.add(desti);
                            }
                        }else{
                            activity.runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                        }
                    }catch (Exception e){}
                }
                return destinationList;

            } catch (Exception ex) {
                Log.e("SOAP", "Error: " + ex.getMessage());
                return null;
            }
        }


    }
}
