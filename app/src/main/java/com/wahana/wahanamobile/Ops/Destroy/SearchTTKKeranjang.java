package com.wahana.wahanamobile.Ops.Destroy;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.TtkKeranjangRetur;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterTTKKeranjang;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class SearchTTKKeranjang extends AppCompatActivity {

    private static final String TAG = "cekTarif";
    ListView listView;
    ProgressDialog progressDialog;
    private ArrayList<TtkKeranjangRetur> ttkKerList = new ArrayList<TtkKeranjangRetur>();
    private AdapterTTKKeranjang adapter;
    TextView namakeranjang;
    String nokeranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ttkkeranjang);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent=getIntent();
        nokeranjang=intent.getStringExtra("nokeranjang");

        progressDialog = new ProgressDialog(SearchTTKKeranjang.this, R.style.AppTheme_Dark_Dialog);

        listView=(ListView)findViewById(R.id.list_item);
        namakeranjang=(TextView)findViewById(R.id.namakeranjang);

        namakeranjang.setText(nokeranjang);


        getTTKkeranjang();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView isi = (TextView)view.findViewById(R.id.text);

                Toast.makeText(SearchTTKKeranjang.this, ""+isi.getText().toString(), Toast.LENGTH_LONG).show();

                Intent pindah = new Intent(SearchTTKKeranjang.this, TandaiDestroy.class);
                pindah.putExtra("nottk",isi.getText().toString());
                startActivity(pindah);
                finish();
            }
         });


    }


    private void getTTKkeranjang(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("getTTKkeranjang");
        parameter.add("0");
        parameter.add("0");
        parameter.add("idkeranjang");
        parameter.add(nokeranjang);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data Propinsi...");
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
                            Toast.makeText(SearchTTKKeranjang.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i>0) {
                                            JSONArray isi = data.getJSONArray(i);
                                            String code = isi.getString(0);
                                            String nama = isi.getString(2);
                                            String tglmasuk = isi.getString(3);
                                            TtkKeranjangRetur prov = new TtkKeranjangRetur();
                                            prov.setId(code);
                                            prov.setTtk(nama);
                                            prov.setTglmasuk(tglmasuk);
                                            ttkKerList.add(prov);

                                        }
                                    }

//                                    provinsiList1=db.getAllDataProvinsi();
//                                    provinsiList.addAll(provinsiList);

                                    adapter = new AdapterTTKKeranjang(SearchTTKKeranjang.this, R.layout.listviewttkkeranjangdestroy, ttkKerList);

                                    listView.setAdapter(adapter);



                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SearchTTKKeranjang.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SearchTTKKeranjang.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchTTKKeranjang.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchTTKKeranjang.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
