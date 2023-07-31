package com.wahana.wahanamobile.Ops.PanelMW;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.TTKBermasalah.tandaittkbermasalah;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateTerkirimMW extends AppCompatActivity {

    private static final String TAG = "usterkirimmanual";

    ProgressDialog progressDialog;
    SessionManager session;
    TextView pengisi, tgl, calendar;
    String username, user_id;
    private Button btnInput;
    EditText input_ttk;
    Button scan_button;
    private Class<?> mClss;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private final int REQUEST_CODE_QRCODE = 3;
    Spinner status_penerima_spinner;
    Button btn_submit_foto;
    ImageView imageView;
    private ArrayList<Origin> jenisstatuslist = new ArrayList<Origin>();
    String spinner_value,spinner_id;
    EditText input_keterangan,input_penerima;
    String encodedImage = null;

    private final int REQUEST_CODE_CLICK_IMAGE = 02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_terkirim_mw);

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
                Intent intent = new Intent(UpdateTerkirimMW.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(UpdateTerkirimMW.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(UpdateTerkirimMW.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        input_ttk=(EditText)findViewById(R.id.input_ttk);
        scan_button=(Button)findViewById(R.id.scan_button);
        status_penerima_spinner=(Spinner)findViewById(R.id.status_penerima_spinner);
        input_penerima=(EditText)findViewById(R.id.input_penerima);

        imageView = (ImageView) findViewById(R.id.listView);

        btn_submit_foto=(Button)findViewById(R.id.btn_submit_foto);
        input_keterangan=(EditText)findViewById(R.id.input_keterangan);

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

        scan_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });

        getStatusPengirim();


        status_penerima_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                spinner_value = jenisstatuslist.get(position).getCity().toString();
                spinner_id = jenisstatuslist.get(position).getId().toString();


//                idbbm = jenisbbmList.get(position).getId().toString();


//                if(!spinner_value.equals("0")){
////                    liter.setEnabled(true);
////
////                    if(!liter.getText().toString().isEmpty()){
////                        DecimalFormat formatter = new DecimalFormat("#,###,###");
////                        int as = Integer.parseInt(liter.getText().toString()) * Integer.parseInt(spinner_value);
////                        hargabbm.setText(formatter.format(as));
////                    }
//
//                    hargabbm.setEnabled(true);
//
//                    if(!hargabbm.getText().toString().isEmpty()){
//                        DecimalFormat formatter = new DecimalFormat("#,###,###");
//                        double as = Double.parseDouble(hargabbm.getText().toString()) / Double.parseDouble(spinner_value);
//                        Log.d("hasil",as+"");
//                        liter.setText(String.format("%.2f", as));
//                    }
//
//                }else{
////                    liter.setEnabled(false);
//                    hargabbm.setEnabled(false);
//                    liter.setText("");
//                    hargabbm.setText("");
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        btn_submit_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTerkirimMW.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submitForm();
                AlertDialog.Builder adb=new AlertDialog.Builder(UpdateTerkirimMW.this);
                adb.setTitle("Update Terkirim Panel MW");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        insertdata();
                    }});
                adb.show();
            }
        });

    }


    private void insertdata(){

        if (!validateKode()) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("service", "setUpdateTerkirimpanelMW");
            json.put("employeeCode", session.getID());
            json.put("ttk", input_ttk.getText().toString());
            json.put("keterangan", input_keterangan.getText().toString());
            json.put("status", spinner_value);
            json.put("namapenerima", input_penerima.getText().toString());

            if(imageView.getDrawable()==null){
                encodedImage="";
            }else{
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] raw = out.toByteArray();
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            }

            json.put("foto", String.valueOf(encodedImage));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("jkk",""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("50");
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
                            Toast.makeText(UpdateTerkirimMW.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty(0).toString();
                        final String text = result.getProperty(1).toString();
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
                                    Intent pindah = new Intent(UpdateTerkirimMW.this, HasilProses.class);
                                    pindah.putExtra("proses","ttkterkirimpanelMW");
                                    pindah.putExtra("no", input_ttk.getText().toString());
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(UpdateTerkirimMW.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(UpdateTerkirimMW.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(UpdateTerkirimMW.this, HasilError.class);
                            pindah.putExtra("proses","ttkterkirimpanelMW");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UpdateTerkirimMW.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);


    }


    private void getStatusPengirim(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getJenisStatusPengirim");
        parameter.add("0");
        parameter.add("50");
        parameter.add("0");
        parameter.add("0");
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
                            Toast.makeText(UpdateTerkirimMW.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
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
                                            String id = isi.getString(0);
                                            String nama = isi.getString(1);
//                                            String harga = isi.getString(2);
                                            Origin prov = new Origin();
                                            prov.setId(id);
                                            prov.setCity(nama);
//                                            prov.setProvince(harga);
//                                            prov.setId(""+i);
                                            jenisstatuslist.add(prov);
                                        }else{
                                            Origin prov = new Origin();
                                            prov.setId("0");
                                            prov.setCity("--Pilih--");
//                                            prov.setProvince("0");
                                            jenisstatuslist.add(prov);
                                        }
                                    }
                                    populateSpinner();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(UpdateTerkirimMW.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(UpdateTerkirimMW.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(UpdateTerkirimMW.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UpdateTerkirimMW.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < jenisstatuslist.size(); i++) {
            if (i==0){
                lables.add(jenisstatuslist.get(i).getCity());
            }else{
                lables.add(jenisstatuslist.get(i).getCity());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        status_penerima_spinner.setAdapter(spinnerAdapter);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        String filePath = null;

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_CODE_QRCODE:
                    String nilai = data.getStringExtra("ttk");
                    input_ttk.setText(nilai);
                    break;

                case REQUEST_CODE_CLICK_IMAGE:

                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView.setImageBitmap(bmp);
                    imageView.setVisibility(View.VISIBLE);

                    break;
            }
        }
    }


    private boolean validateKode() {
        if (input_ttk.getText().toString().trim().isEmpty()) {
            input_ttk.setError("Masukkan Nomor TTK");

            Toast.makeText(UpdateTerkirimMW.this,"Masukkan Nomor TTK", Toast.LENGTH_LONG).show();

            return false;
        } else {
            input_ttk.setError(null);
        }

        if (input_keterangan.getText().toString().isEmpty()) {
            input_keterangan.setError("Mohon masukkan keterangan");
            return false;
        } else {
            input_keterangan.setError(null);
        }

        if (input_penerima.getText().toString().isEmpty()) {
            input_penerima.setError("Mohon masukkan Nama Penerima");
            return false;
        } else {
            input_penerima.setError(null);
        }

        if(spinner_id.equals("0")){
            Toast.makeText(UpdateTerkirimMW.this, "Masukkan Status Penerima", Toast.LENGTH_SHORT).show();
            return false;
        }

//
//        if (imageView.getDrawable() == null){
//            Toast.makeText(UpdateTerkirimMW.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
//            return false;
//        }


        return true;
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
