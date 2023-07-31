package com.wahana.wahanamobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.adapter.SearchAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

public class SearchActivity extends DrawerHelper {
    private static final String TAG = "SearchActivityv";
    TextView inputShippingLayout, terkirimLabel,belumTerkirimLabel, labelTracking, labelCalendar, labelPengirim, labelDitujukan, diterimaLabel,
            labelDiterima, judulResult, waktuTracking, tahapanTracking, waktuTracking1, tahapanTracking1, waktuTracking2, tahapanTracking2, waktuTracking3, tahapanTracking3;
    EditText inputTracking;
    ImageButton btnTracking;
    RelativeLayout statusTerkirim, statusBelumTerkirim, statusOnProgress, statusRetur, mainTracking, trackingResult;
    ProgressDialog progressDialog;
    SessionManager session;

    public static String [] waktu_pengiriman={};
    public static String [] status_pengiriman={};
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;
    ListView listViewSearch;
    int simpan = 0;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(SearchActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(SearchActivity.this);

        inputShippingLayout = (TextView) findViewById(R.id.input_shipping_layout);
        terkirimLabel = (TextView) findViewById(R.id.terkirim_label);
        belumTerkirimLabel = (TextView) findViewById(R.id.belum_terkirim_label);
        labelTracking = (TextView) findViewById(R.id.label_tracking);
        labelCalendar = (TextView) findViewById(R.id.label_calendar);
        labelPengirim = (TextView) findViewById(R.id.label_pengirim);
        labelDitujukan = (TextView) findViewById(R.id.label_ditujukan);
        diterimaLabel = (TextView) findViewById(R.id.diterima_label);
        labelDiterima = (TextView) findViewById(R.id.label_diterima);
        judulResult = (TextView) findViewById(R.id.judul_result);
        inputTracking = (EditText) findViewById(R.id.input_tracking);
        btnTracking = (ImageButton) findViewById(R.id.btn_tracking);
        statusTerkirim = (RelativeLayout) findViewById(R.id.status_terkirim);
        statusBelumTerkirim = (RelativeLayout) findViewById(R.id.status_belum_terkirim);
        statusOnProgress = (RelativeLayout) findViewById(R.id.status_onprogress);
        statusRetur = (RelativeLayout) findViewById(R.id.status_reture);
        mainTracking = (RelativeLayout) findViewById(R.id.main_tracking);
        trackingResult = (RelativeLayout) findViewById(R.id.tracking_result);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");

        inputShippingLayout.setTypeface(type);
        terkirimLabel.setTypeface(type);
        belumTerkirimLabel.setTypeface(type);
        labelTracking.setTypeface(type);
        labelCalendar.setTypeface(type);
        labelPengirim.setTypeface(type);
        labelDitujukan.setTypeface(type);
        //diterimaLabel.setTypeface(type);
        labelDiterima.setTypeface(type);
        judulResult.setTypeface(type);
        inputTracking.setTypeface(type);

        listViewSearch = (ListView) findViewById(R.id.list_search);

        btnTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.getJenis().equals("member")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("My TTK");
                    alertDialog.setMessage("Apakah Anda Ingin Menyimpan TTK ini Sebagai TTK Anda ?");
                    alertDialog.setPositiveButton("Ya",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    simpan = 1;
                                    submitForm();
                                }
                            });

                    alertDialog.setNegativeButton("Tidak",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    submitForm();
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();
                }else {
                    submitForm();
                }
            }
        });

    }

    private void submitForm()
    {
        if (!validate()) {
            return;
        }
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("trackPackage");
        parameter.add(session.getSessionID());
        parameter.add(inputTracking.getText().toString());
        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(final SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SearchActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(0).toString();
                        String labelStatus = null;
                        if (text.equals("1")) {
                            String status = result.getProperty(2).toString();
                            String tgl = result.getProperty(4).toString();
                            String penerima = "-";
                            String pengirim = null;
                            String noPengirim = null;
                            String tujuan = null;
                            String noTujuan = null;
                            String alamatTujuan = null;
                            int as = 14;
                            if (status.equals("1")){
                                statusOnProgress.setVisibility(View.VISIBLE);
                                String txt = result.getProperty(3).toString();
                                pengirim = result.getProperty(4).toString();
                                noPengirim = result.getProperty(5).toString();
                                tujuan = result.getProperty(7).toString();
                                noTujuan = result.getProperty(8).toString();
                                alamatTujuan = result.getProperty(9).toString();
                                labelTracking.setText(inputTracking.getText().toString());
                                labelCalendar.setText(txt);
                                labelDiterima.setText(penerima);
                                labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                mainTracking.setVisibility(View.VISIBLE);
                            }else if(status.equals("2")){
                                statusRetur.setVisibility(View.VISIBLE);
                                pengirim = result.getProperty(5).toString();
                                noPengirim = result.getProperty(6).toString();
                                tujuan = result.getProperty(8).toString();
                                noTujuan = result.getProperty(9).toString();
                                alamatTujuan = result.getProperty(11).toString();
                                ArrayList<String> stringTgl = new ArrayList<String>();
                                ArrayList<String> stringStatus = new ArrayList<String>();
                                for (int i = as; i<result.getPropertyCount(); i++){
                                    SoapObject so = (SoapObject) result.getProperty(i);
                                    String date = so.getProperty("dateTime").toString();
                                    String label = so.getProperty("statusName").toString();
                                    stringTgl.add(date);
                                    stringStatus.add(label);
                                    if (i == result.getPropertyCount()-1){
                                        labelStatus = label;
                                    }
                                }
                                waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                listViewSearch.setAdapter(new SearchAdapter(SearchActivity.this, waktu_pengiriman, status_pengiriman));
                                labelTracking.setText(inputTracking.getText().toString());
                                labelCalendar.setText(tgl);
                                labelDiterima.setText(penerima);
                                labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                mainTracking.setVisibility(View.VISIBLE);
                                trackingResult.setVisibility(View.VISIBLE);
                            }else if(status.equals("3")){
                                statusBelumTerkirim.setVisibility(View.VISIBLE);
                                pengirim = result.getProperty(5).toString();
                                noPengirim = result.getProperty(6).toString();
                                tujuan = result.getProperty(8).toString();
                                noTujuan = result.getProperty(9).toString();
                                alamatTujuan = result.getProperty(11).toString();
                                as = 12;
                                ArrayList<String> stringTgl = new ArrayList<String>();
                                ArrayList<String> stringStatus = new ArrayList<String>();
                                for (int i = as; i<result.getPropertyCount(); i++){
                                    SoapObject so = (SoapObject) result.getProperty(i);
                                    String date = so.getProperty("dateTime").toString();
                                    String label = so.getProperty("statusName").toString();
                                    stringTgl.add(date);
                                    stringStatus.add(label);
                                    if (i == result.getPropertyCount()-1){
                                        labelStatus = label;
                                    }
                                }
                                waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                listViewSearch.setAdapter(new SearchAdapter(SearchActivity.this, waktu_pengiriman, status_pengiriman));
                                labelTracking.setText(inputTracking.getText().toString());
                                labelCalendar.setText(tgl);
                                labelDiterima.setText(penerima);
                                labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                mainTracking.setVisibility(View.VISIBLE);
                                trackingResult.setVisibility(View.VISIBLE);
                            }else if(status.equals("4")){
                                statusTerkirim.setVisibility(View.VISIBLE);
                                penerima = result.getProperty(5).toString();
                                String a = result.getProperty(13).toString();
                                if (a.toUpperCase().contains("ANYTYPE")){
                                    pengirim = result.getProperty(6).toString();
                                    noPengirim = result.getProperty(7).toString();
                                    tujuan = result.getProperty(9).toString();
                                    noTujuan = result.getProperty(10).toString();
                                    alamatTujuan = result.getProperty(12).toString();
                                    as = 13;
                                    ArrayList<String> stringTgl = new ArrayList<String>();
                                    ArrayList<String> stringStatus = new ArrayList<String>();
                                    for (int i = as; i<result.getPropertyCount(); i++){
                                        SoapObject so = (SoapObject) result.getProperty(i);
                                        String date = so.getProperty("dateTime").toString();
                                        String label = so.getProperty("statusName").toString();
                                        stringTgl.add(date);
                                        stringStatus.add(label);
                                        if (i == result.getPropertyCount()-1){
                                            labelStatus = label;
                                        }
                                    }
                                    waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                    status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                    listViewSearch.setAdapter(new SearchAdapter(SearchActivity.this, waktu_pengiriman, status_pengiriman));
                                    labelTracking.setText(inputTracking.getText().toString());
                                    labelCalendar.setText(tgl);
                                    labelDiterima.setText(penerima);
                                    labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                    labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                    mainTracking.setVisibility(View.VISIBLE);
                                    trackingResult.setVisibility(View.VISIBLE);
                                }else{
                                    pengirim = result.getProperty(7).toString();
                                    noPengirim = result.getProperty(8).toString();
                                    tujuan = result.getProperty(10).toString();
                                    noTujuan = result.getProperty(11).toString();
                                    alamatTujuan = result.getProperty(13).toString();
                                    as = 14;
                                    ArrayList<String> stringTgl = new ArrayList<String>();
                                    ArrayList<String> stringStatus = new ArrayList<String>();
                                    for (int i = as; i<result.getPropertyCount(); i++){
                                        SoapObject so = (SoapObject) result.getProperty(i);
                                        String date = so.getProperty("dateTime").toString();
                                        String label = so.getProperty("statusName").toString();
                                        stringTgl.add(date);
                                        stringStatus.add(label);
                                        if (i == result.getPropertyCount()-1){
                                            labelStatus = label;
                                        }
                                    }
                                    waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                    status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                    listViewSearch.setAdapter(new SearchAdapter(SearchActivity.this, waktu_pengiriman, status_pengiriman));
                                    labelTracking.setText(inputTracking.getText().toString());
                                    labelCalendar.setText(tgl);
                                    labelDiterima.setText(penerima);
                                    labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                    labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                    mainTracking.setVisibility(View.VISIBLE);
                                    trackingResult.setVisibility(View.VISIBLE);
                                }
                            }
                            if (simpan == 1){
                                ArrayList<String> parameter = new ArrayList<String>();
                                parameter.add("ttkUserSave");
                                parameter.add(session.getID());
                                parameter.add(inputTracking.getText().toString());
                                parameter.add(status);
                                if (labelStatus==null){
                                    labelStatus="TTK Baru";
                                }
                                parameter.add(labelStatus);
                                new SoapClientMember(){
                                    @Override
                                    protected void onPostExecute(final SoapObject res) {
                                        super.onPostExecute(res);
                                        progressDialog.dismiss();
                                        final String code = res.getProperty("resCode").toString();
                                        final String text = res.getProperty("resText").toString();
                                        if (code.equals("1")) {
                                            Log.d("hasil", text);
                                        }else {
                                            progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                public void run() {
//                                                    Toast.makeText(SearchActivity.this, text, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }.execute(parameter);
                            }else {
                                progressDialog.dismiss();
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SearchActivity.this, result.getProperty(1).toString() ,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil ex",e+"");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean validate()
    {
        boolean valid = true;

        String no = inputTracking.getText().toString();

        if(no.isEmpty())
        {
            inputTracking.setError("Masukkan Tanda Terima Kiriman");
            valid = false;
        } else {
            inputTracking.setError(null);
        }

        return valid;
    }
}

