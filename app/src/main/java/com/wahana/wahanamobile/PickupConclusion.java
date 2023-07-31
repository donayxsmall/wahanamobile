package com.wahana.wahanamobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class PickupConclusion extends DrawerHelper {
    private static final String TAG = "PickConclusion";
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
    Context context;
    ProgressDialog progressDialog;
    String agentcode;
    Button btnSubmitAll,btnCancel;
    public static String [] kodeSortir;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;

    @SuppressWarnings("ResourceType")

    DatabaseHandler db;
    List<KodeSortir> kodeList = new ArrayList<KodeSortir>();
    LinearLayout layoutForm;
    private static LayoutInflater inflater=null;
    List<TTk> ttkList = new ArrayList<TTk>();
    private final int REQUEST_FOR_ACTIVITY_CODE = 1;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_pickup_conclusion);
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
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PickupConclusion.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        session = new SessionManager(PickupConclusion.this);
        progressDialog = new ProgressDialog(PickupConclusion.this, R.style.AppTheme_Dark_Dialog);
        user_id = session.getID();

        layoutForm = (LinearLayout) findViewById(R.id.main);
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = new DatabaseHandler(PickupConclusion.this);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_agent.setText(agentcode);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(user_id);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        kode_agent.setTypeface(type);
        btnSubmitAll = (Button) findViewById(R.id.button_manifest);
        btnCancel = (Button) findViewById(R.id.button_cancel_pickup);
        btnSubmitAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickupConclusion.this);
                alertDialog.setCancelable(false);
                // Setting Dialog Title
                alertDialog.setTitle("PickUp Airway");
                // Setting Dialog Message
                alertDialog.setMessage("Apakah Anda Yakin Cancel Pickup ?");
                // On pressing Settings button
                alertDialog.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cancelForm();
                            }
                        });
                // on pressing cancel button
                alertDialog.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
            }
        });

        username = session.getUsername();
        user_id = session.getID();
        getData();
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
    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
//        pickUp task = new pickUp();
//        task.execute();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickupConclusion.this);
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle("PickUp Airway");
        // Setting Dialog Message
        alertDialog.setMessage("Apakah Anda Yakin Semua Data Sudah Sesuai ?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int total = db.getTotalUnsubmit();
                        Log.d("total", ""+total);
                        if (total != 0){
                            Toast.makeText(PickupConclusion.this, "Data Belum Sesuai", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(PickupConclusion.this, PickupAirways.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        // on pressing cancel button
        alertDialog.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
    private void cancelForm(){
        ArrayList<String> parameter = new ArrayList<String>();
        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        parameter.add("pickupCancel");
        parameter.add(session.getSessionID());
        parameter.add(rc);
        parameter.add(session.getID());
        parameter.add(agentcode);
        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Requesting...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(PickupConclusion.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(PickupConclusion.this, "Pickup telah dicancel",Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupConclusion.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        layoutForm.removeAllViews();
        kodeList.clear();
        db.deleteKode();
        getData();
//        List<KodeSortir> kodes = db.getAllDataSortir();
//        for (int i = 0 ; i < kodes.size(); i++){
//            tambahDaftar(kodes.get(i));
//        }
    }

    public class Holder
    {
        TextView label_sortir;
        TextView label_koli;
        TextView sortir;
        TextView jumlah;
        EditText inputKoli;
        Button buttonInput;
    }

    private void tambahDaftar(final KodeSortir kode){
        final Holder holder=new Holder();
        View view = inflater.inflate(R.layout.conclusion_listview, null);
        holder.label_sortir = (TextView)view.findViewById(R.id.kode_sortir_label);
        holder.label_koli = (TextView)view.findViewById(R.id.kode_koli_label);
        holder.sortir = (TextView)view.findViewById(R.id.kode_sortir_isi);
        holder.jumlah = (TextView)view.findViewById(R.id.jumlah);
        holder.inputKoli = (EditText) view.findViewById(R.id.input_jumlah_koli);
        holder.buttonInput = (Button) view.findViewById(R.id.input_button);

        holder.buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new SessionManager(PickupConclusion.this);
                username = session.getUsername();
                user_id = session.getID();
                if(holder.inputKoli.getText().toString().isEmpty())
                {
                    holder.inputKoli.setError("Masukkan Jumlah Koli");
                } else {
                    final KodeSortir hasil = db.getTotal(holder.sortir.getText().toString());
                    int total = hasil.getJumlahttk();
                    int verify = hasil.getVerify();
                    final int input = Integer.parseInt(holder.inputKoli.getText().toString());
                    holder.inputKoli.setError(null);
                    Log.d("jumlah verify3","jumlah "+total+" ver "+verify);
                    KodeSortir text = db.getDate(hasil.getKodesortir());
                    if (input == total){
                        ArrayList<String> parameter = new ArrayList<String>();
                        parameter.add("pickupGetPackageListPerSortingCode");
                        parameter.add(session.getSessionID());
                        parameter.add(session.getID());
                        parameter.add(agentcode);
                        parameter.add(hasil.getKodesortir());
                        parameter.add(text.getDatemin());
                        parameter.add(text.getDatemax());

                        new SoapClient(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Log.i("Conclusion Adapter", "onPreExecute");
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
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
                                            Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }else {
                                    try {
                                        final String text = result.getProperty(1).toString();
                                        if (text.equals("OK")) {
                                            progressDialog.dismiss();
                                            for (int i = 2; i<result.getPropertyCount(); i++){
                                                SoapObject so = (SoapObject) result.getProperty(i);
                                                String nottk = so.getProperty("packageNumber").toString();
                                                TTk ttk = new TTk();
                                                ttk.setNottk(nottk);
                                                ttk.setStatus(1);
                                                ttkList.add(ttk);
                                                db.addTTK(new TTk(nottk, 1, hasil.getKodesortir()));
                                            }
                                            holder.inputKoli.setVisibility(View.GONE);
                                            holder.buttonInput.setVisibility(View.GONE);
                                            holder.jumlah.setText(""+input);
                                            int a = db.updateStatusSortir(hasil.getKodesortir(), input);
                                            ArrayList<String> parameter = new ArrayList<String>();
                                            Random r = new Random();
                                            int requestCode = r.nextInt(9999);
                                            String rc = String.valueOf(requestCode);
                                            parameter.add("pickupVerPackage");
                                            parameter.add(session.getSessionID());
                                            parameter.add(rc);
                                            parameter.add(session.getID());
                                            parameter.add(agentcode);
                                            parameter.add(hasil.getKodesortir());
                                            new status(){
                                                @Override
                                                protected void onPreExecute() {
                                                    super.onPreExecute();
                                                    Log.i(TAG, "onPreExecute");
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.setCanceledOnTouchOutside(false);
                                                    progressDialog.setIndeterminate(true);
                                                    progressDialog.setMessage("Requesting...");
                                                    progressDialog.show();
                                                }

                                                @Override
                                                protected void onPostExecute(SoapObject result) {
                                                    super.onPostExecute(result);
                                                    Log.d("hasil soap", ""+result);
                                                    if(result==null){
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                    }else {
                                                        try {
                                                            final String text = result.getProperty(1).toString();
                                                            final String rescode= result.getProperty(0).toString();
                                                            if (rescode.equals("1")) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(PickupConclusion.this, "Data Berhasil Disimpan",Toast.LENGTH_LONG).show();
                                                                db.updateSortir(holder.sortir.getText().toString(), ttkList.size(), 0);
                                                            }else{
                                                                progressDialog.dismiss();
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(PickupConclusion.this, text,Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        }catch (Exception e){
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }.execute(parameter);
                                            Log.d("jumlah", input+"<<"+a+kode);
                                        }else{
                                            progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(PickupConclusion.this, text,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }catch (Exception e){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        }.execute(parameter);
                    }else if(total== verify) {
                        holder.inputKoli.setVisibility(View.GONE);
                        holder.buttonInput.setVisibility(View.GONE);
                        holder.jumlah.setText(total + "");

                    }else{
                        String totalInput = db.getTotalInput(holder.sortir.getText().toString());
                        if (Integer.parseInt(totalInput) < 2){
                            int sisa = 3 - (Integer.parseInt(totalInput)+1);
                            Toast.makeText(PickupConclusion.this, "Jumlah Koli Anda Masukkan Salah, Sisa Percobaan "+sisa, Toast.LENGTH_SHORT).show();
                            db.updateTotalInput(holder.sortir.getText().toString(), String.valueOf(Integer.parseInt(totalInput)+1));
                        }else{
                            Intent intent = new Intent(PickupConclusion.this, PickupAirwaysStatus.class);
                            intent.putExtra("agentcode", agentcode);
                            intent.putExtra("sortingcode", holder.sortir.getText().toString());
                            startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                        }
                    }
                }
            }
        });

        Log.d("kode ",kode+"");
        String status = kode.getStatus();
        String kodesortir = kode.getKodesortir();
        int verify=kode.getVerify();
        Log.d("jumlah verify"," ver "+verify);
        if (status.equals("submit")){
            //verify = kode.getVerify();
            holder.inputKoli.setVisibility(View.GONE);
            holder.buttonInput.setVisibility(View.GONE);
            holder.jumlah.setText(""+verify);
        }
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.label_sortir.setTypeface(type);
        holder.sortir.setTypeface(type);
        holder.inputKoli.setTypeface(type);
        holder.sortir.setText(kodesortir);

        layoutForm.addView(view);
    }

    private class status extends AsyncTask<ArrayList<String>, Void, SoapObject> {
        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
        String URL = "http://ws.wahana.com/ws/mobileService/";

//        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
//        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
//        String URL = "http://ws.wahana.com/ws/mobileService/";

        SoapObject hasil = null;

        @Override
        protected SoapObject doInBackground(ArrayList<String>... params) {
            Log.i(TAG, "doInBackground");
            ArrayList<String> passed = params[0];
            String METHOD_NAME = passed.get(0);
            try {
                SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
                Log.d("action", SOAP_ACTION);
                Random r = new Random();
                int requestCode = r.nextInt(9999);
                String rc = String.valueOf(requestCode);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("sessionId", session.getSessionID());
                Request.addProperty("requestId", rc);
                Request.addProperty("employeeCode", session.getID());
                Request.addProperty("agentCode", agentcode);
                Request.addProperty("sortingCode", passed.get(5));
                for (int i=0; i<ttkList.size(); i++){
                    TTk ttk = ttkList.get(i);
                    SoapObject req = new SoapObject();
                    req.addProperty("packageNumber", ttk.getNottk());
                    req.addProperty("verificationStatus", ttk.getStatus());
                    Request.addProperty("verifiedPackageList", req);
                    Log.d("action", ttk.getNottk()+ttk.getStatus());
                }
                Log.d("req1",Request+"");
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("android:silakan".getBytes())));

                transport.call(SOAP_ACTION, soapEnvelope,headerList);
                hasil = (SoapObject) soapEnvelope.bodyIn;
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }

            return hasil;
        }

    }

    private void getData(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("pickupGetBagCountPerSortingCode");
        parameter.add(session.getSessionID());
        parameter.add(session.getID());
        parameter.add(agentcode);
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
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            for (int i = 2; i<result.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) result.getProperty(i);
                                int verify = Integer.parseInt(so.getProperty("bagCountVerified").toString());
                                int noferify = Integer.parseInt(so.getProperty("bagCountNoVerify").toString());
                                int unferivy = Integer.parseInt(so.getProperty("bagCountUnverified").toString());
                                String kode = so.getProperty("sortingCode").toString();
                                String total = so.getProperty("bagCountTotal").toString();
                                String datemin = so.getProperty("postingDateMin").toString();
                                String datemax = so.getProperty("postingDateMax").toString();
                                KodeSortir ks = new KodeSortir();
                                ks.setKodesortir(kode);
                                ks.setVerify(verify);
                                ks.setJumlahttk(Integer.parseInt(total));
                                String status="unsubmit";
                                if(verify==Integer.parseInt(total)){
                                    ks.setStatus("submit");
                                    status="submit";
                                }else {
                                    ks.setStatus("unsubmit");
                                }
                                kodeList.add(ks);
                                db.addKodeSortir(new KodeSortir(kode, verify, noferify, unferivy, datemin, datemax, Integer.parseInt(total), status, "0"));

                            }
                            for (int i = 0 ; i < kodeList.size(); i++){
                                tambahDaftar(kodeList.get(i));
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupConclusion.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupConclusion.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

}
