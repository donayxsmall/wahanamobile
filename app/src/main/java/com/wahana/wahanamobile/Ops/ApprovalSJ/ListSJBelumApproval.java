package com.wahana.wahanamobile.Ops.ApprovalSJ;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterSJApproval;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataSJ;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ListSJBelumApproval extends AppCompatActivity {

    private static final String TAG = "sj";

    ProgressDialog progressDialog;
    SessionManager session;
    ListView listView;
    TextView judul;
    private static AdapterSJApproval adapter;
    ArrayList<DataSJ> sjList = new ArrayList<DataSJ>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sjbelum_approval);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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
                Intent intent = new Intent(ListSJBelumApproval.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(ListSJBelumApproval.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(ListSJBelumApproval.this);

        listView=(ListView)findViewById(R.id.listView);
        judul=(TextView)findViewById(R.id.judul);


        getdaftarSJ();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final TextView nottk = (TextView)v.findViewById(R.id.ttk);

                Toast.makeText(ListSJBelumApproval.this, ""+nottk.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });


    }



    private void getdaftarSJ(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("listApprovalSJKurir");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBACD");
        parameter.add(session.getID());

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data SJ...");
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
                            Toast.makeText(ListSJBelumApproval.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);

//                                            Log.d("hasilsj",""+ttk);

                                            DataSJ ori = new DataSJ();
                                            ori.setTtk(ttk);
                                            sjList.add(ori);

//                                            db.addOrigin(ori);
                                        }

                                    }

//                                    adapter = new ListViewAdapterSearchKotaAsal(SearchKotaAsal.this, R.layout.listviewsearchkotaasal, originList);
//                                    listView.setAdapter(adapter);

                                    adapter= new AdapterSJApproval(sjList,getApplicationContext());

                                    listView.setAdapter(adapter);


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ListSJBelumApproval.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ListSJBelumApproval.this,"kosong 2",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ListSJBelumApproval.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListSJBelumApproval.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
