package com.wahana.wahanamobile.Ops.modaAngkutan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.ArmadaSearchAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientLogin;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class SearchMobilActivity extends AppCompatActivity {
    private static final String TAG = "Test Search";
    ImageButton searchLoc;
    EditText inputLoc;
    ListView listViewTestSearch;
    EditText formLokasi;
    ProgressDialog progressDialog;
    SessionManager session;
    String username, user_id;
    public static String [] judul;
    public static String [] provinsi;
    public static String [] kota;
    public static String [] distrik;
    List<Origin> originList = new ArrayList<Origin>();
    ArmadaSearchAdapter adapter;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ma_search);

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
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SearchMobilActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        db = new DatabaseHandler(SearchMobilActivity.this);
        db.deleteOrigin();
        session = new SessionManager(SearchMobilActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        listViewTestSearch = (ListView) findViewById(R.id.list_vehicle);
        formLokasi = (EditText) findViewById(R.id.input_loc);
        searchLoc = (ImageButton) findViewById(R.id.search_loc);
        inputLoc = (EditText) findViewById(R.id.input_loc);
        progressDialog = new ProgressDialog(SearchMobilActivity.this, R.style.AppTheme_Dark_Dialog);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getActiveArmada");
        parameter.add("0");
        parameter.add("0");
        parameter.add("0");
        parameter.add("0");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil Data Armada...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SearchMobilActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();
                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                for (int i=0; i<data.length(); i++){
                                    if(i!=0){
                                        JSONArray isi = data.getJSONArray(i);
                                        String nopol = isi.getString(0);
                                        String seal = isi.getString(2);
                                        Origin ori = new Origin();
                                        ori.setProvince(nopol);
                                        ori.setCity(seal);
                                        originList.add(ori);
//                                        db.addOrigin(ori);
                                    }
                                }
                                populate();
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SearchMobilActivity.this, "Data Tidak Ada",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchMobilActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchMobilActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                    populate();
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
                TextView lokId = (TextView)view.findViewById(R.id.lokasi_bps);
//                Log.d("hasil", isi.getText().toString());
                Intent pindah = new Intent(SearchMobilActivity.this, AddDataMAActivity.class);
                pindah.putExtra("nopol", isi.getText().toString());
                pindah.putExtra("seal", lokId.getText().toString());
                setResult(RESULT_OK, pindah);
                finish();
            }
        });
    }

    private void submitForm()
    {
        judul = new String [] {"Jakarta","Bandung","Semarang"};
        provinsi = new String [] {"DKI Jakarta","Bandung","Semarang"};
        kota = new String [] {"Jakarta Timur","Bandung Selatan","Semarang Utara"};
        distrik = new String [] {"Cakung","Bandung","Semarang"};
    }




    private void populate(){
//        originList.clear();
//        originList = db.getAllDataOrigin(inputLoc.getText().toString());
        adapter = new ArmadaSearchAdapter(SearchMobilActivity.this, originList);
        listViewTestSearch.setAdapter(adapter);
        listViewTestSearch.setVisibility(View.VISIBLE);
//        adapter.notifyDataSetChanged();

        formLokasi.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                SearchMobilActivity.this.adapter.filter(s.toString());

                int textlength = s.length();
                ArrayList<Origin> tempArrayList = new ArrayList<Origin>();
                for(Origin c: originList){
                    if (textlength <= c.getProvince().length()) {
                        if (c.getProvince().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(c);
                            Log.d("hasil filter", ""+s+" "+c.getProvince());
                        }
                    }
                }
//                adapter = new ContactListAdapter(activity, tempArrayList);
//                lv.setAdapter(mAdapter);

                adapter = new ArmadaSearchAdapter(SearchMobilActivity.this, tempArrayList);
                listViewTestSearch.setAdapter(adapter);
                listViewTestSearch.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
}
