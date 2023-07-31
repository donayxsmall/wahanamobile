package com.wahana.wahanamobile;

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
import com.wahana.wahanamobile.adapter.TestSearchAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchOriginActivity extends AppCompatActivity {
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
    TestSearchAdapter adapter;
    DatabaseHandler db;
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
                Intent intent = new Intent(SearchOriginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        db = new DatabaseHandler(SearchOriginActivity.this);
        db.deleteOrigin();
        session = new SessionManager(SearchOriginActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        listViewTestSearch = (ListView) findViewById(R.id.list_kota);
        formLokasi = (EditText) findViewById(R.id.input_loc);
        searchLoc = (ImageButton) findViewById(R.id.search_loc);
        inputLoc = (EditText) findViewById(R.id.input_loc);
        progressDialog = new ProgressDialog(SearchOriginActivity.this, R.style.AppTheme_Dark_Dialog);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getTariffNodeOrigin");
        parameter.add("1");
        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil Data Origin...");
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
                            Toast.makeText(SearchOriginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                Origin ori = new Origin();
                                ori.setProvince(province);
                                ori.setCity(city);
                                originList.add(ori);
                                db.addOrigin(ori);
                            }
                            populate();
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchOriginActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchOriginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                Log.d("hasil", isi.getText().toString());
                Intent pindah = new Intent(SearchOriginActivity.this, CekTarifActivity.class);
                pindah.putExtra("origin", isi.getText().toString());
                pindah.putExtra("originId", lokId.getText().toString());
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
        originList.clear();
        originList = db.getAllDataOrigin(inputLoc.getText().toString());
        adapter = new TestSearchAdapter(SearchOriginActivity.this, originList);
        listViewTestSearch.setAdapter(adapter);
        listViewTestSearch.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
