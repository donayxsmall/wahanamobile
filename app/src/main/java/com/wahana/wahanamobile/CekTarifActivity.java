package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.MarshalDouble;
import com.wahana.wahanamobile.helper.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Map;

import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import java.util.StringTokenizer;

public class CekTarifActivity extends DrawerHelper {
    private static final String TAG = "CekTarifActivity";

    Button btnInput;
    RelativeLayout tabelHasil,layoutBottom;
    TextView fromLabel, toLabel, beratLabel, satuanLabel,kotaAsalLabel, kotaAsalIsi, kotaTujuanLabel, kotaTujuanIsi,hargaLabel, hargaIsi,
    totalBeratLabel, totalBeratIsi, totalHargaLabel, totalHargaIsi, estimasiLabel, estimasiIsi, fromId, toId;
    EditText beratIsi;
    AutoCompleteTextView fromisi,  toIsi;
    SessionManager session;
    private ArrayList<Map<String, String>> mKotaList, mTujuanList;
    String from, to, berat;
    ProgressDialog progressDialog;

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
    private Filter filter;

    private final int REQUEST_FOR_ORIGIN = 1;
    private final int REQUEST_FOR_DESTINATION = 2;
    @SuppressWarnings("ResourceType")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_tarif);
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
                finish();
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        session = new SessionManager(CekTarifActivity.this);
        progressDialog = new ProgressDialog(CekTarifActivity.this, R.style.AppTheme_Dark_Dialog);
        Log.d("Hasil", session.getID()+"<<asd");

        fromLabel = (TextView) findViewById(R.id.from_label);
        toLabel  = (TextView) findViewById(R.id.to_label);
        beratLabel  = (TextView) findViewById(R.id.berat_label);
        satuanLabel  = (TextView) findViewById(R.id.satuan_label);
        kotaAsalLabel  = (TextView) findViewById(R.id.kota_asal_label);
        kotaAsalIsi  = (TextView) findViewById(R.id.kota_asal_isi);
        kotaTujuanLabel = (TextView) findViewById(R.id.kota_tujuan_label);
        kotaTujuanIsi  = (TextView) findViewById(R.id.kota_tujuan_isi);
        hargaLabel = (TextView) findViewById(R.id.harga_label);
        hargaIsi = (TextView) findViewById(R.id.harga_isi);
        totalBeratLabel = (TextView) findViewById(R.id.total_berat_label);
        totalBeratIsi = (TextView) findViewById(R.id.total_berat_isi);
        totalHargaLabel = (TextView) findViewById(R.id.total_harga_label);
        totalHargaIsi = (TextView) findViewById(R.id.total_harga_isi);
        estimasiLabel = (TextView) findViewById(R.id.estimasi_label);
        estimasiIsi = (TextView) findViewById(R.id.estimasi_isi);
        fromisi=(AutoCompleteTextView) findViewById(R.id.from_isi);
        toIsi=(AutoCompleteTextView) findViewById(R.id.to_isi);
        fromId=(TextView) findViewById(R.id.from_id);
        toId=(TextView) findViewById(R.id.to_id);
        beratIsi=(EditText) findViewById(R.id.berat_isi);
        beratIsi.setImeActionLabel(beratIsi.getText(), KeyEvent.KEYCODE_ENTER);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        Typeface typeRegular = Typeface.createFromAsset(getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemiBold = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        fromLabel.setTypeface(type);
        toLabel.setTypeface(type);
        beratLabel.setTypeface(type);
        satuanLabel.setTypeface(type);
        kotaAsalLabel.setTypeface(typeRegular);
        kotaAsalIsi.setTypeface(typeSemiBold);
        kotaTujuanLabel.setTypeface(typeRegular);
        kotaTujuanIsi.setTypeface(typeSemiBold);
        hargaLabel.setTypeface(typeRegular);
        hargaIsi.setTypeface(typeSemiBold);
        totalBeratLabel.setTypeface(typeSemiBold);
        totalBeratIsi.setTypeface(typeSemiBold);
        totalHargaLabel.setTypeface(typeSemiBold);
        totalHargaIsi.setTypeface(typeSemiBold);
        estimasiLabel.setTypeface(typeSemiBold);
        estimasiIsi.setTypeface(typeSemiBold);
        fromisi.setTypeface(type);
        toIsi.setTypeface(type);
        beratIsi.setTypeface(type);
        mKotaList = new ArrayList<Map<String, String>>();
        mTujuanList = new ArrayList<Map<String, String>>();

        btnInput = (Button) findViewById(R.id.input_button);
        tabelHasil = (RelativeLayout) findViewById(R.id.tabel_hasil);
        layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        fromisi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(CekTarifActivity.this,SearchOriginActivity.class), REQUEST_FOR_ORIGIN);
            }

        });
        toIsi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (fromisi.getText().toString().isEmpty()){
                    Toast.makeText(CekTarifActivity.this, "Mohon Isi Kota Asal", Toast.LENGTH_SHORT).show();
                }else {
//                    StringTokenizer tokens = new StringTokenizer(fromisi.getText().toString(), ",");
//                    String provinsi = tokens.nextToken();
//                    String kota = tokens.nextToken();
//                    Toast.makeText(CekTarifActivity.this, provinsi+" <> "+kota, Toast.LENGTH_SHORT).show();
                    Intent a = new Intent(CekTarifActivity.this,SearchDestinationActivity.class);
                    a.putExtra("asal", fromId.getText().toString());
                    startActivityForResult(a, REQUEST_FOR_DESTINATION);
                }
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(CekTarifActivity.this, DeliveryNote.class));
                finish();
                break;
            case 2:
                startActivity(new Intent(CekTarifActivity.this, DeliveryNoteList.class));
                finish();
                break;
            case 3:
                startActivity(new Intent(CekTarifActivity.this, OfflineDeliveryNote.class));
                finish();
                break;
            case 4:
                startActivity(new Intent(CekTarifActivity.this, AttendanceReport.class));
                finish();
                break;
            case 5:
                session.logoutUser();
                finish();
                break;
            case 6:

                break;

            default:
                break;
        }

    }

    private void submitForm() {
        if (!validate()) {
            return;
        }
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getTariff");
        parameter.add("1");
        parameter.add(fromId.getText().toString());
        parameter.add(toId.getText().toString());
        parameter.add(berat);

        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil Data Harga...");
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
                            Toast.makeText(CekTarifActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            String kg = result.getProperty(2).toString();
                            String tarif = result.getProperty(3).toString();
                            String time = result.getProperty(4).toString();

                            kotaTujuanIsi.setText(toIsi.getText().toString());
                            kotaAsalIsi.setText(fromisi.getText().toString());
                            totalBeratIsi.setText(beratIsi.getText().toString()+" KG");
                            hargaIsi.setText("Rp "+kg);
                            totalHargaIsi.setText("Rp "+tarif);
                            estimasiIsi.setText(time+" HARI");

                            tabelHasil.setVisibility(View.VISIBLE);
                            layoutBottom.setVisibility(View.VISIBLE);

                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(CekTarifActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(CekTarifActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private class submit extends AsyncTask<Void, Void, SoapObject> {
        String SOAP_ACTION = "http://wahana.sadewa.asia/MySoapServer";
        String NAMESPACE = "http://wahana.sadewa.asia/assets/wahana.wsdl";
        String URL = "http://wahana.sadewa.asia/MySoapServer";
        String METHOD_NAME = "calculateHasil";
        SoapObject hasil = null;
        @Override
        protected SoapObject doInBackground(Void... params) {

            try {
                SOAP_ACTION = URL+METHOD_NAME;
                Log.d("action", SOAP_ACTION);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                StringTokenizer oriTokens = new StringTokenizer(from, ",");
                String a = oriTokens.nextToken();
                String kota = oriTokens.nextToken();
                StringTokenizer destiTokens = new StringTokenizer(to, ",");
                String provinsi = destiTokens.nextToken();
                String city = destiTokens.nextToken();
                String distric = destiTokens.nextToken();
                Request.addProperty("asal", kota.trim());
                Request.addProperty("provinsi", provinsi.trim());
                Request.addProperty("kota", city.trim());
                Request.addProperty("district", distric.trim());
                Request.addProperty("weight", berat);
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

    public boolean validate() {
        boolean valid = true;

        from = fromisi.getText().toString();
        to = toIsi.getText().toString();
        berat = beratIsi.getText().toString();

        if(from.isEmpty())
        {
            fromisi.setError("Masukkan Kota Asal");
            valid = false;
        } else {
            fromisi.setError(null);
        }

        if (to.isEmpty()) {
            toIsi.setError("Masukkan Kota Tujuan");
            valid = false;
        } else {
            toIsi.setError(null);
        }

        if (berat.isEmpty()) {
            beratIsi.setError("Masukkan Berat");
            valid = false;
        } else {
            beratIsi.setError(null);
        }

        return valid;
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener2 implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView2(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView2(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                session.logoutUser();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_FOR_ORIGIN:
                    String origin = data.getStringExtra("origin");
                    String originId = data.getStringExtra("originId");
                    fromisi.setText(origin);
                    fromId.setText(originId);
                    break;

                case REQUEST_FOR_DESTINATION:
                    String destination = data.getStringExtra("destination");
                    String destinationId = data.getStringExtra("destinationId");
                    toIsi.setText(destination);
                    toId.setText(destinationId);
                    break;
            }
        }
    }
}
