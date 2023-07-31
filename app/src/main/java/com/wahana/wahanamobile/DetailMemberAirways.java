package com.wahana.wahanamobile;

import android.app.ProgressDialog;
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

import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.adapter.SearchAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class DetailMemberAirways extends DrawerHelper {

    private static final String TAG = "DetailMemberAirways";
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
    String username, user_id, no_ttk;
    TextView nama,jabatan;
    ImageView foto;
    ListView listViewSearch;
    int simpan = 0;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_member_airways);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(DetailMemberAirways.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(DetailMemberAirways.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(DetailMemberAirways.this);

        Intent intent = getIntent();
        no_ttk = intent.getStringExtra("noTtk");
        Log.d("hasil String Extra", no_ttk);
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

        statusTerkirim = (RelativeLayout) findViewById(R.id.status_terkirim);
        statusBelumTerkirim = (RelativeLayout) findViewById(R.id.status_belum_terkirim);
        statusOnProgress = (RelativeLayout) findViewById(R.id.status_onprogress);
        statusRetur = (RelativeLayout) findViewById(R.id.status_reture);
        mainTracking = (RelativeLayout) findViewById(R.id.main_tracking);
        trackingResult = (RelativeLayout) findViewById(R.id.tracking_result);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");


        terkirimLabel.setTypeface(type);
        belumTerkirimLabel.setTypeface(type);
        labelTracking.setTypeface(type);
        labelCalendar.setTypeface(type);
        labelPengirim.setTypeface(type);
        labelDitujukan.setTypeface(type);
        //diterimaLabel.setTypeface(type);
        labelDiterima.setTypeface(type);
        judulResult.setTypeface(type);
//        inputTracking.setTypeface(type);

        listViewSearch = (ListView) findViewById(R.id.list_search);
        submitForm();
    }

    private void submitForm()
    {
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("trackPackage");
        parameter.add("123123");
        parameter.add(no_ttk);
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
                            Toast.makeText(DetailMemberAirways.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    try {
                        final String text = result.getProperty(0).toString();
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
                                labelTracking.setText(no_ttk);
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
                                }
                                waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                listViewSearch.setAdapter(new SearchAdapter(DetailMemberAirways.this, waktu_pengiriman, status_pengiriman));
                                labelTracking.setText(no_ttk);
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
                                }
                                waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                listViewSearch.setAdapter(new SearchAdapter(DetailMemberAirways.this, waktu_pengiriman, status_pengiriman));
                                labelTracking.setText(no_ttk);
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
                                    }
                                    waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                    status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                    listViewSearch.setAdapter(new SearchAdapter(DetailMemberAirways.this, waktu_pengiriman, status_pengiriman));
                                    labelTracking.setText(no_ttk);
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
                                    }
                                    waktu_pengiriman = stringTgl.toArray(new String[stringTgl.size()]);
                                    status_pengiriman = stringStatus.toArray(new String[stringStatus.size()]);
                                    listViewSearch.setAdapter(new SearchAdapter(DetailMemberAirways.this, waktu_pengiriman, status_pengiriman));
                                    labelTracking.setText(no_ttk);
                                    labelCalendar.setText(tgl);
                                    labelDiterima.setText(penerima);
                                    labelPengirim.setText(pengirim.replaceAll("\\B\\w\\B", "*")+" ("+noPengirim.replaceAll("\\B\\w\\B", "*")+")");
                                    labelDitujukan.setText(tujuan.replaceAll("\\B\\w\\B", "*")+" ("+noTujuan.replaceAll("\\B\\w\\B", "*")+") "+alamatTujuan);
                                    mainTracking.setVisibility(View.VISIBLE);
                                    trackingResult.setVisibility(View.VISIBLE);
                                }
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DetailMemberAirways.this, result.getProperty(1).toString() ,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil ex",e+"");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DetailMemberAirways.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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

}
