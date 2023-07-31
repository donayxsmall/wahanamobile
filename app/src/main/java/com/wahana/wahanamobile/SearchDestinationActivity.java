package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.Destination;
import com.wahana.wahanamobile.adapter.DestinationSearchAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class SearchDestinationActivity extends AppCompatActivity {
    private static final String TAG = "Test Search";
    ImageButton searchLoc;
    EditText inputLoc;
    ListView listViewTestSearch;
    EditText formLokasi;
    ProgressDialog progressDialog;
    SessionManager session;
    String username, user_id;
    ArrayList<Destination> destinationList = new ArrayList<Destination>();
    DestinationSearchAdapter adapter;
    DatabaseHandler db;
    String asal, tujuan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDestinationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        Bundle extras = getIntent().getExtras();
        asal = extras.getString("asal");
        tujuan = "Jakarta";
        db = new DatabaseHandler(SearchDestinationActivity.this);
        db.deleteOrigin();
        session = new SessionManager(SearchDestinationActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        listViewTestSearch = (ListView) findViewById(R.id.list_kota);
        formLokasi = (EditText) findViewById(R.id.input_loc);
        searchLoc = (ImageButton) findViewById(R.id.search_loc);
        inputLoc = (EditText) findViewById(R.id.input_loc);
        progressDialog = new ProgressDialog(SearchDestinationActivity.this, R.style.AppTheme_Dark_Dialog);
        searchLoc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

            }
        });
        submitForm();
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getTariffNodeDestination");
        parameter.add("1");
        parameter.add(asal);
        parameter.add(tujuan);
        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil Data Tujuan...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SearchDestinationActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            for (int i = 2; i<result.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String province = so.getProperty("nodeId").toString();
                                String city = so.getProperty("nodeNameLong").toString();
                                String distric = "";
                                Destination desti = new Destination();
                                desti.setProvince(province);
                                desti.setCity(city);
                                desti.setDistric(distric);
                                destinationList.add(desti);
                            }
                            populate();
                            adapter = new DestinationSearchAdapter(SearchDestinationActivity.this, android.R.layout.simple_dropdown_item_1line,  destinationList, asal);
                            listViewTestSearch.setAdapter(adapter);
                            listViewTestSearch.setVisibility(View.VISIBLE);
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchDestinationActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchDestinationActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    try{
                        destinationList.clear();
                        adapter.getFilter().filter(inputLoc.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Log.d("Error", e+"");
                    }
                }
            }

            private boolean filterLongEnough() {
                return inputLoc.getText().toString().trim().length() > 1;
            }
        };
        inputLoc.addTextChangedListener(fieldValidatorTextWatcher);
        listViewTestSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView isi = (TextView)view.findViewById(R.id.lokasi_isi);
                TextView isiId = (TextView)view.findViewById(R.id.lokasi_bps);
                Log.d("hasil", isi.getText().toString());
                Intent pindah = new Intent(SearchDestinationActivity.this, CekTarifActivity.class);
                pindah.putExtra("destination", isi.getText().toString());
                pindah.putExtra("destinationId", isiId.getText().toString());
                setResult(RESULT_OK, pindah);
                finish();
            }
        });
    }

    private void submitForm()
    {
    }

    private void populate(){
//        destinationList.clear();
//        destinationList = db.getAllDataOrigin(inputLoc.getText().toString());
//        adapter = new TestSearchAdapter(SearchDestinationActivity.this, destinationList);
//        listViewTestSearch.setAdapter(adapter);
//        listViewTestSearch.setVisibility(View.VISIBLE);
//        adapter.notifyDataSetChanged();
    }

    private class getOrigin extends AsyncTask<Void, Void, SoapObject> {
        String SOAP_ACTION = "http://wahana.sadewa.asia/MySoapServer";
        String NAMESPACE = "http://wahana.sadewa.asia/assets/wahana.wsdl";
        String URL = "http://wahana.sadewa.asia/MySoapServer";
        String METHOD_NAME = "calculateDestination";
        SoapObject hasil = null;
        @Override
        protected SoapObject doInBackground(Void... params) {

            try {
                SOAP_ACTION = URL+METHOD_NAME;
                Log.d("action", SOAP_ACTION);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("asal", asal);
                Request.addProperty("tujuan", tujuan);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                MarshalDouble mdo = new MarshalDouble();
                mdo.register(soapEnvelope);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                hasil = (SoapObject) soapEnvelope.getResponse();
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }

            return hasil;
        }
    }
}
