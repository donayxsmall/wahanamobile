package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.adapter.AddressSearchAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AddressSearchActivity extends AppCompatActivity {
    private static final String TAG = "Test Search";
    ImageButton searchLoc;
    EditText inputLoc;
    ListView listViewTestSearch;
    EditText formLokasi;
    ProgressDialog progressDialog;
    SessionManager session;
    String username, user_id, type, provinsi, kabkot, kecamatan;
    List<Origin> originList = new ArrayList<Origin>();
    AddressSearchAdapter adapter;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("kabkot")){
            provinsi = intent.getStringExtra("provinsi");
        }else if(type.equals("kecamatan")){
            provinsi = intent.getStringExtra("provinsi");
            kabkot = intent.getStringExtra("kabkot");
        }else if(type.equals("kelurahan")){
            provinsi = intent.getStringExtra("provinsi");
            kabkot = intent.getStringExtra("kabkot");
            kecamatan = intent.getStringExtra("kecamatan");
        }
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
                Intent intent = new Intent(AddressSearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        db = new DatabaseHandler(AddressSearchActivity.this);
        db.deleteOrigin();
        session = new SessionManager(AddressSearchActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        listViewTestSearch = (ListView) findViewById(R.id.list_kota);
        formLokasi = (EditText) findViewById(R.id.input_loc);
        searchLoc = (ImageButton) findViewById(R.id.search_loc);
        inputLoc = (EditText) findViewById(R.id.input_loc);
        progressDialog = new ProgressDialog(AddressSearchActivity.this, R.style.AppTheme_Dark_Dialog);

        ArrayList<String> parameter = new ArrayList<String>();
        if (type.equals("provinsi")){
            parameter.add("getProvince");
        }else if (type.equals("kabkot")){
            parameter.add("getKabkot");
            parameter.add(provinsi);
        }else if (type.equals("kecamatan")){
            parameter.add("getCamat");
            parameter.add(provinsi);
            parameter.add(kabkot);
        }else if (type.equals("kelurahan")){
            parameter.add("getLurah");
            parameter.add(provinsi);
            parameter.add(kabkot);
            parameter.add(kecamatan);
        }

        new SoapClientMember(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil Data...");
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
                            Toast.makeText(AddressSearchActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            Vector<SoapObject> vector = (Vector<SoapObject>) result.getProperty("list");
                            for (SoapObject so : vector){
                                String province = null, bps_code = null;
                                if (type.equals("provinsi")){
                                    province = so.getProperty("province").toString();
                                }else if (type.equals("kabkot")){
                                    province = so.getProperty("kabkot").toString();
                                }else if (type.equals("kecamatan")){
                                    province = so.getProperty("camat").toString();
                                }else if (type.equals("kelurahan")){
                                    province = so.getProperty("lurah").toString();
                                    bps_code = so.getProperty("bps_code").toString();
                                }
                                Origin ori = new Origin();
                                ori.setProvince(province);
                                ori.setCity(bps_code);
                                originList.add(ori);
                                db.addOrigin(ori);
                            }
                            populate();
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(AddressSearchActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AddressSearchActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                TextView bps = (TextView)view.findViewById(R.id.lokasi_bps);
                Log.d("hasil", isi.getText().toString());
                Intent pindah = new Intent(AddressSearchActivity.this, CekTarifActivity.class);
                pindah.putExtra("hasil", isi.getText().toString());
                if (type.equals("kelurahan")){
                    pindah.putExtra("kodeBps", bps.getText().toString());
                }
                setResult(RESULT_OK, pindah);
                finish();
            }
        });
    }

    private void populate(){
        originList.clear();
        originList = db.getAllDataOrigin(inputLoc.getText().toString());
        adapter = new AddressSearchAdapter(AddressSearchActivity.this, originList, type);
        listViewTestSearch.setAdapter(adapter);
        listViewTestSearch.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
