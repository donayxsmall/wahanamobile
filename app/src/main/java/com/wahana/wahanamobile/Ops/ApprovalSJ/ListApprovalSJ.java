package com.wahana.wahanamobile.Ops.ApprovalSJ;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.manifestSortir.InputKodeKotaTujuan;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterSJApproval;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataSJ;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ListApprovalSJ extends AppCompatActivity {
    private static final String TAG = "sj";
    ArrayList<DataSJ> dataModels;
    ListView listView;
    private static AdapterSJApproval adapter;
    ProgressDialog progressDialog;

    ArrayList<DataSJ> sjList = new ArrayList<DataSJ>();
    String sj;
    TextView nosj;
    Button input_button;
    SessionManager session;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_approval_sj);

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
                Intent intent = new Intent(ListApprovalSJ.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(ListApprovalSJ.this, R.style.AppTheme_Dark_Dialog);

        session = new SessionManager(ListApprovalSJ.this);

        listView=(ListView)findViewById(R.id.listView);
        nosj=(TextView)findViewById(R.id.judul);
        input_button=(Button)findViewById(R.id.input_button);

        Intent intent = getIntent();
        sj=intent.getStringExtra("ttk");


        nosj.setText(sj);

//        dataModels= new ArrayList<>();
//
//        dataModels.add(new DataSJ("Apple Pie"));
//        dataModels.add(new DataSJ("Banana Bread"));
//        dataModels.add(new DataSJ("Cupcake"));
//        dataModels.add(new DataSJ("Donut"));
//        dataModels.add(new DataSJ("Eclair"));
//        dataModels.add(new DataSJ("Froyo"));
//        dataModels.add(new DataSJ("Gingerbread"));
//        dataModels.add(new DataSJ("Honeycomb"));
//        dataModels.add(new DataSJ("Ice Cream Sandwich"));
//        dataModels.add(new DataSJ("Jelly Bean"));
//        dataModels.add(new DataSJ("Kitkat"));
//        dataModels.add(new DataSJ("Lollipop"));
//        dataModels.add(new DataSJ("Marshmallow"));
//
//        adapter= new AdapterSJApproval(dataModels,getApplicationContext());
//
//        listView.setAdapter(adapter);
//        listView.setDivider(getDrawable(R.drawable.divider));

        setData();




        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ListApprovalSJ.this);
                adb.setTitle("Info");
                adb.setMessage("Apakah anda yakin ingin Approval SJ ini ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updatedata();
                    }});
                adb.show();
            }
        });



    }


    private void updatedata(){


        JSONObject json = new JSONObject();
        try {
            json.put("service", "setUpdateSjApproval");
            json.put("sjno", nosj.getText().toString());
            json.put("employeeCode", session.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", ""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        progressDialog.setCancelable(false);
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ListApprovalSJ.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty(0).toString();
                        text = result.getProperty(1).toString();

                        Log.d("codesj",""+code+"|"+text);
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Toast.makeText(ListApprovalSJ.this, "Berhasil Approval SJ",Toast.LENGTH_LONG).show();
                                    Intent pindah = new Intent(ListApprovalSJ.this, MainActivity.class);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ListApprovalSJ.this, "Mohon Cek Kembali SJ NYA",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ListApprovalSJ.this, "Mohon Cek Kembali SJ NYA",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ListApprovalSJ.this, text, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListApprovalSJ.this, text, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private void setData(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("getTTKSJ");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(sj);

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
                            Toast.makeText(ListApprovalSJ.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                            Toast.makeText(ListApprovalSJ.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ListApprovalSJ.this, sj+" Bukan No SJ",Toast.LENGTH_LONG).show();
                                        Intent pindah = new Intent(ListApprovalSJ.this, MainActivity.class);
                                        startActivity(pindah);
                                        finish();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ListApprovalSJ.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListApprovalSJ.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            String nilai=data.getStringExtra("ttk");
//            ttk.setText(nilai);
            Log.d("scan",""+nilai);
        }
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
