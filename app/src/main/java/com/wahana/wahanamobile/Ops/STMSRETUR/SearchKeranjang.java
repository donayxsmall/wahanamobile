package com.wahana.wahanamobile.Ops.STMSRETUR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.ModelApiOPS.aoGetListKeranjang;
import com.wahana.wahanamobile.ModelApiOPS.aoGetListKeranjang_Result;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterSearchKeranjang;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.hiddensoftkeyboard;
import com.wahana.wahanamobile.model.DataKeranjang;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchKeranjang extends AppCompatActivity {

    private static final String TAG = "SearchKeranjang";

    ProgressDialog progressDialog;
    private ListView listView;
    private AdapterSearchKeranjang adapter;
    SessionManager session;
    ArrayList<DataKeranjang> DataKeranjangList = new ArrayList<DataKeranjang>();

    RequestApiWahanaOps mApiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_keranjang);

        progressDialog = new ProgressDialog(SearchKeranjang.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(SearchKeranjang.this);
        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        setData();

        setDatav2();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                hideSoftKeyboard(SearchKeranjang.this);

                TextView text = (TextView)view.findViewById(R.id.text);
                TextView idkeranjang = (TextView)view.findViewById(R.id.id);


                Intent pindah = new Intent(SearchKeranjang.this, InputTTKRetur.class);
                pindah.putExtra("textkeranjang", text.getText().toString());
                pindah.putExtra("idkeranjang", idkeranjang.getText().toString());
                setResult(RESULT_OK, pindah);

                finish();

            }
        });


    }



    private void setDatav2(){

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        Call<aoGetListKeranjang> result = mApiInterface.aoGetListKeranjang("aoGetListKeranjang",getString(R.string.partnerid),session.getKeyTokenJWT());

        result.enqueue(new Callback<aoGetListKeranjang>() {

            @Override
            public void onResponse(Call<aoGetListKeranjang> call, Response<aoGetListKeranjang> response) {

                progressDialog.dismiss();


                if(response.isSuccessful()) {

                    try {


                        Log.d("retro",""+response.body().getCode());

                        aoGetListKeranjang data = response.body();
                        List<aoGetListKeranjang_Result> dataList = new ArrayList<>();
                        dataList = data.getData();

                        for (int i = 0; i < dataList.size(); i++) {
                            Log.d("fit1", "" + dataList.get(i).getText());

                            DataKeranjang prov = new DataKeranjang();
                            prov.setId(dataList.get(i).getId());
                            prov.setText(dataList.get(i).getId());
                            DataKeranjangList.add(prov);
                        }

                        adapter = new AdapterSearchKeranjang(SearchKeranjang.this, R.layout.listviewsearchdefault, DataKeranjangList);
                        listView.setAdapter(adapter);




                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();

                    Log.d("error1",""+response.code());

                    Toast.makeText(SearchKeranjang.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();


                }



            }

            @Override
            public void onFailure(Call<aoGetListKeranjang> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                Log.d("error2",""+t.toString());

                Toast.makeText(SearchKeranjang.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setData(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getAlasanRetur");
        parameter.add("0");
        parameter.add("0");
        parameter.add("nik");
        parameter.add("");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SearchKeranjang.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String resData = result.getProperty("resData").toString();

                            JSONObject jsonObj = new JSONObject(resData);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String kode = isi.getString(0);
                                            String nama = isi.getString(1);
                                            DataKeranjang prov = new DataKeranjang();
                                            prov.setId(kode);
                                            prov.setText(nama);
                                            DataKeranjangList.add(prov);
                                        }
                                    }

                                    adapter = new AdapterSearchKeranjang(SearchKeranjang.this, R.layout.listviewsearchdefault, DataKeranjangList);
                                    listView.setAdapter(adapter);


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SearchKeranjang.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SearchKeranjang.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchKeranjang.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchKeranjang.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);


    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                // finish();
                Intent intent = new Intent(this, InputTTKRetur.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, intent);

                finish();



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }




}
