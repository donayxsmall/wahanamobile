package com.wahana.wahanamobile.Ops.editSJP;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.DataSJP;
import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.PickupAirways;
import com.wahana.wahanamobile.PickupAirwaysStatus;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class updateSJPActivity extends DrawerHelper {
    private static final String TAG = "PickConclusion";
    TextView pengisi, tgl, calendar;
    Context context;
    ProgressDialog progressDialog;
    Button btnSubmitAll;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, sjp;

    List<DataSJP> dataSJPs = new ArrayList<DataSJP>();
    LinearLayout layoutForm;
    private static LayoutInflater inflater=null;
    DatabaseHandler db;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_list_ttk_sjp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(updateSJPActivity.this);
        Intent intent = getIntent();
        sjp = intent.getStringExtra("sjp");
        session = new SessionManager(updateSJPActivity.this);
        progressDialog = new ProgressDialog(updateSJPActivity.this, R.style.AppTheme_Dark_Dialog);
        user_id = session.getID();

        layoutForm = (LinearLayout) findViewById(R.id.main);
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        btnSubmitAll = (Button) findViewById(R.id.button_manifest);
        btnSubmitAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(updateSJPActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Edit SJP");
        alertDialog.setMessage("Apakah Anda Yakin ?");
        alertDialog.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submit();
                    }
                });
        alertDialog.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        layoutForm.removeAllViews();
//        getData();
    }

    public class Holder
    {
        TextView id;
        TextView label_sortir;
        TextView label_koli;
        TextView sortir;
        TextView jumlah;
        EditText inputKoli;
        EditText inputBerat;
        EditText inputBiaya;
        EditText inputTTKVendor;
        EditText inputAlasan;
        RadioGroup inputBatal;
        Button buttonInput;
        TextView beratttk;
    }

    private void tambahDaftar(final DataSJP sjp){
        final Holder holder=new Holder();
        final View view = inflater.inflate(R.layout.adapter_edit_sjp, null);
        holder.id = (TextView) view.findViewById(R.id.id);
        holder.label_sortir = (TextView)view.findViewById(R.id.kode_sortir_label);
        holder.label_koli = (TextView)view.findViewById(R.id.kode_koli_label);
        holder.sortir = (TextView)view.findViewById(R.id.kode_sortir_isi);
        holder.jumlah = (TextView)view.findViewById(R.id.jumlah);
        holder.inputBerat = (EditText) view.findViewById(R.id.input_jumlah_berat);
        holder.inputKoli = (EditText) view.findViewById(R.id.input_jumlah_koli);
        holder.inputBiaya = (EditText) view.findViewById(R.id.input_biaya);
        holder.inputTTKVendor = (EditText) view.findViewById(R.id.input_ttk_vendor);
        holder.inputAlasan = (EditText) view.findViewById(R.id.input_alasan);
        holder.inputBatal = (RadioGroup) view.findViewById(R.id.radio_batal);
        holder.buttonInput = (Button) view.findViewById(R.id.input_button);

        holder.beratttk = (TextView) view.findViewById(R.id.beratttk);

        holder.buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String formattedDate = df.format(c.getTime());


                if(holder.inputBerat.getText().toString().isEmpty() || holder.inputKoli.getText().toString().isEmpty() || holder.inputBiaya.getText().toString().isEmpty() || holder.inputTTKVendor.getText().toString().isEmpty()){

                    Toast.makeText(updateSJPActivity.this, "Data tidak boleh kosong",Toast.LENGTH_LONG).show();
                    return;

                }

                if(Integer.parseInt(holder.inputBerat.getText().toString()) > Integer.parseInt(sjp.getBeratttk())){

                    Toast.makeText(updateSJPActivity.this, "Data berat tidak boleh lebih dari "+sjp.getBeratttk()+" kg",Toast.LENGTH_LONG).show();
                    return;

                }

                String status;
                try {
                    int radio = holder.inputBatal.getCheckedRadioButtonId();
                    String hasil = ((RadioButton) view.findViewById(radio)).getText().toString();
                    if (hasil.equals("YA")){
                        status = "1";
                    }else if(hasil.equals("TIDAK")){
                        status = "";
                    }else{
                        status = "";
                    }
                }catch (Exception e){
                    Log.d("Exception", e+"");
                    status = "";
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("service", "updateSJPbyId");
                    json.put("id", sjp.getId());
                    json.put("biaya", holder.inputBiaya.getText().toString());
                    json.put("ttkvendor", holder.inputTTKVendor.getText().toString());
                    json.put("tgl", formattedDate);
                    json.put("berat", holder.inputBerat.getText().toString());
                    json.put("koli", holder.inputKoli.getText().toString());
                    json.put("batal", status);
                    json.put("alasan", holder.inputAlasan.getText().toString());
                    json.put("employeeCode", user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("hasil error", ""+e);
                }

                final ArrayList<String> parameter = new ArrayList<String>();
                parameter.add("doSSQL");
                parameter.add(session.getSessionID());
                parameter.add("apiGeneric");
                parameter.add("20");
                parameter.add("0");
                parameter.add("jsonp");
                parameter.add(""+json);
                Log.d("hasil json", ""+json);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(updateSJPActivity.this);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Update SJP");
                alertDialog.setMessage("Apakah Anda Yakin ?");
                alertDialog.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                                    Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }else {
                                            try{
                                                final String text = result.getProperty(1).toString();
                                                Log.d("hasil soap data", ""+text);
                                                if (text.equals("OK")) {
                                                    progressDialog.dismiss();
                                                    String so = result.getProperty(2).toString();

                                                    JSONObject jsonObj = new JSONObject(so);
                                                    JSONArray data = jsonObj.getJSONArray("data");
                                                    Log.d("hasil soap data", ""+data);
                                                    if (data.length()>1){
                                                        final JSONArray d = data.getJSONArray(2);
                                                        if (d.getJSONObject(1).getString("status").equals("1")){
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(updateSJPActivity.this, "Data Berhasil Diupdate",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                            layoutForm.removeAllViews();
                                                            dataSJPs.clear();
                                                            getData();
                                                        }else{
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(updateSJPActivity.this, "Mohon Cek Kembali Data Anda",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }else{
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(updateSJPActivity.this, "Mohon Cek Kembali Data Anda",Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }else{
                                                    progressDialog.dismiss();
                                                    Intent pindah = new Intent(updateSJPActivity.this, HasilError.class);
                                                    pindah.putExtra("proses","updateMA");
                                                    pindah.putExtra("no", text);
                                                    startActivity(pindah);
                                                }
                                            }catch (Exception e){
                                                Log.d("hasil error", ""+e);
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                }.execute(parameter);
                            }
                        });
                alertDialog.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();

            }
        });

        Log.d("kode ",sjp+"");
        String ttk = sjp.getTtk();
        String kodeops = sjp.getKodeops();

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");

        holder.sortir.setText(ttk);
        holder.jumlah.setText(kodeops);
        holder.inputBerat.setText(sjp.getBerat());
        holder.inputKoli.setText(sjp.getKoli());
        holder.inputBiaya.setText(sjp.getBiaya());
        holder.inputTTKVendor.setText(sjp.getTtkVendor());
        holder.inputAlasan.setText(sjp.getAlasan());
        holder.id.setText(sjp.getId());
        holder.beratttk.setText(sjp.getBeratttk()+" kg");
        int rb = 0;
        if (sjp.getBatal().equals("1")){
            rb = R.id.radioya;
            holder.inputBatal.check(rb);
        }else if(sjp.getBatal().equals("0")){
            rb = R.id.radiotidak;
            holder.inputBatal.check(rb);
        }

        layoutForm.addView(view);
    }

    private void getData(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getListTTKbySJP");
        parameter.add("0");
        parameter.add("0");
        parameter.add("sjp");
        parameter.add(sjp.trim());
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
                            Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            for (int i = 1; i<data.length(); i++){
                                final JSONArray d = data.getJSONArray(i);
                                DataSJP sjp = new DataSJP();
                                String koli = d.getString(5), berat = d.getString(6), biaya = d.getString(3), ttkVendor=d.getString(4), alasan=d.getString(8);
                                if (d.getString(5).equals("0") || d.getString(5).equals("null")){
                                    koli = null;
                                }
                                if (d.getString(6).equals("0") || d.getString(6).equals("null")){
                                    berat = null;
                                }
                                if (d.getString(3).equals("0") || d.getString(3).equals("null")){
                                    biaya = null;
                                }
                                if (d.getString(4).equals("0") || d.getString(4).equals("null")){
                                    ttkVendor = null;
                                }
                                if (d.getString(8).equals("0") || d.getString(8).equals("null")){
                                    alasan = null;
                                }
                                sjp.setId(d.getString(0));
                                sjp.setTtk(d.getString(1));
                                sjp.setKodeops(d.getString(2));
                                sjp.setBiaya(biaya);
                                sjp.setTtkVendor(ttkVendor);
                                sjp.setKoli(koli);
                                sjp.setBerat(berat);
                                sjp.setBatal(d.getString(7));
                                sjp.setAlasan(alasan);
                                sjp.setBeratttk(d.getString(9));
                                dataSJPs.add(sjp);
                                db.addDataSJP(sjp);
                            }
                            for (int i = 0 ; i < data.length() - 1; i++){
                                tambahDaftar(dataSJPs.get(i));
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(updateSJPActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil ex", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    public void submit(){
        JSONArray jsArray = new JSONArray();

        for (int i = 0; i < layoutForm.getChildCount(); i++) {
            final View parentView = layoutForm.getChildAt(i);
            String status;

            TextView beratttk=(TextView)parentView.findViewById(R.id.beratttk);
            TextView nottk=(TextView)parentView.findViewById(R.id.kode_sortir_isi);

            TextView id = (TextView) parentView.findViewById(R.id.id);
            EditText inputBerat = (EditText) parentView.findViewById(R.id.input_jumlah_berat);
            EditText inputKoli = (EditText) parentView.findViewById(R.id.input_jumlah_koli);
            EditText inputBiaya = (EditText) parentView.findViewById(R.id.input_biaya);
            EditText inputTTKVendor = (EditText) parentView.findViewById(R.id.input_ttk_vendor);
            EditText inputAlasan = (EditText) parentView.findViewById(R.id.input_alasan);
            RadioGroup inputBatal = (RadioGroup) parentView.findViewById(R.id.radio_batal);
            int radio = inputBatal.getCheckedRadioButtonId();
            try {
                String hasil = ((RadioButton) parentView.findViewById(radio)).getText().toString();
                if (hasil.equals("YA")){
                    status = "1";
                }else if(hasil.equals("TIDAK")){
                    status = "";
                }else{
                    status = "";
                }
            }catch (Exception e){
                Log.d("Exception", e+"");
                status = "";
            }

            JSONObject jsdata = new JSONObject();
            try {
                jsdata.put("id", id.getText().toString());
                jsdata.put("biaya", inputBiaya.getText().toString());
                jsdata.put("ttkvendor", inputTTKVendor.getText().toString());
                jsdata.put("koli", inputKoli.getText().toString());
                jsdata.put("berat", inputBerat.getText().toString());
                jsdata.put("batal", status);
                jsdata.put("alasan", inputAlasan.getText().toString());
                jsdata.put("beratttk", beratttk.getText().toString());
                jsdata.put("nottk", nottk.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(jsdata);
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject json = new JSONObject();
        try {
            json.put("service", "updateSJPall");
            json.put("employeeCode", session.getUsername());
            json.put("tgl", formattedDate);
            json.put("data", jsArray);
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
                            Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(updateSJPActivity.this, HasilProses.class);
                                    pindah.putExtra("proses","updateSJP");
                                    pindah.putExtra("no",sjp);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(updateSJPActivity.this, "Mohon Cek Kembali Data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(updateSJPActivity.this, "Mohon Cek Kembali Data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(updateSJPActivity.this, HasilError.class);
                            pindah.putExtra("proses","updateSJP");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(updateSJPActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }

        }.execute(parameter);

    }
}
