package com.wahana.wahanamobile.Ops.BBM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Absensi;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.manifestSortir.ManifestSortirActivity;
import com.wahana.wahanamobile.Ops.modaAngkutan.SearchMobilActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.BitmapTools;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by sadewa on 10/06/17.
 */

public class InputBBM extends DrawerHelper {
    private static final String TAG = "AddDataBBMActivity";

    ProgressDialog progressDialog;

    private Button btnInput;
    TextView pengisi, tgl, calendar,labelnoken,labelkm,labelfotokm,labelhargabbm,labelliter;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, tujuan, asal,spinner_value,idbbm;
    Button btnClickImage;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    DatabaseHandler db;
    LinearLayout sealInfo;
    EditText km, seal,hargabbm,liter;
    String encodedImage=null;
    String sealisi="1";
    AutoCompleteTextView nokendaraan;
    private final int REQUEST_FOR_NOMOBIL = 1;
    Uri imageUri;

    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private ArrayList<Origin> jenisbbmList = new ArrayList<Origin>();
    Spinner jenisbbm;
    Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputbbm);
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
                Intent intent = new Intent(InputBBM.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
//
//        Intent intent = getIntent();
////        noKendaraan = intent.getStringExtra("no_kendaraan");
//        tujuan = intent.getStringExtra("tujuan");
//        asal = intent.getStringExtra("asal");

//        db = new DatabaseHandler(InputBBM.this);

        progressDialog = new ProgressDialog(InputBBM.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputBBM.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        imageView = (ImageView) findViewById(R.id.listView);
        sealInfo = (LinearLayout)findViewById(R.id.layout_seal_isi);
        km = (EditText) findViewById(R.id.input_km);
        seal = (EditText) findViewById(R.id.input_seal);
        nokendaraan=(AutoCompleteTextView)findViewById(R.id.input_nokendaraan);
        hargabbm=(EditText)findViewById(R.id.input_hargabbm);
        liter=(EditText)findViewById(R.id.input_liter);
        labelnoken=(TextView)findViewById(R.id.labelnoken);
        labelkm=(TextView)findViewById(R.id.labelkm);
        labelfotokm=(TextView)findViewById(R.id.labelfotokm);
        labelhargabbm=(TextView)findViewById(R.id.labelhargabbm);
        labelliter=(TextView)findViewById(R.id.labelliter);

        String textnama = "No Kendaraan <font color=#cc0029>*</font>";
        labelnoken.setText(Html.fromHtml(textnama));

        String textkm = "KM Kendaraan <font color=#cc0029>*</font>";
        labelkm.setText(Html.fromHtml(textkm));

        String textfotokm = "Foto KM <font color=#cc0029>*</font>";
        labelfotokm.setText(Html.fromHtml(textfotokm));

        String texthargabbm = "Harga BBM <font color=#cc0029>*</font>";
        labelhargabbm.setText(Html.fromHtml(texthargabbm));

        String textliter = "Liter <font color=#cc0029>*</font>";
        labelliter.setText(Html.fromHtml(textliter));
        jenisbbm = (Spinner)findViewById(R.id.jenisbbm);
        hargabbm.setEnabled(false);
        liter.setEnabled(false);

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

        this.setTitle("");

        nokendaraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(InputBBM.this,SearchMobilBBMActivity.class),REQUEST_FOR_NOMOBIL);
            }
        });



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submitForm();
                AlertDialog.Builder adb=new AlertDialog.Builder(InputBBM.this);
                adb.setTitle("Input BBM");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkKM();
                    }});
                adb.show();
            }
        });

        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputBBM.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });

        liter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner_value.equals("0")){
                    Toast.makeText(InputBBM.this, "Mohon Pilih Jenis BBM", Toast.LENGTH_SHORT).show();
                }else{
                    liter.setEnabled(true);
                }
            }
        });



        jenisbbm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                spinner_value = jenisbbmList.get(position).getProvince().toString();
                idbbm = jenisbbmList.get(position).getId().toString();
                if(!spinner_value.equals("0")){
//                    liter.setEnabled(true);
//
//                    if(!liter.getText().toString().isEmpty()){
//                        DecimalFormat formatter = new DecimalFormat("#,###,###");
//                        int as = Integer.parseInt(liter.getText().toString()) * Integer.parseInt(spinner_value);
//                        hargabbm.setText(formatter.format(as));
//                    }

                    hargabbm.setEnabled(true);

                    if(!hargabbm.getText().toString().isEmpty()){
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        double as = Double.parseDouble(hargabbm.getText().toString()) / Double.parseDouble(spinner_value);
                        Log.d("hasil",as+"");
                        liter.setText(String.format("%.2f", as));
                    }

                }else{
//                    liter.setEnabled(false);
                    hargabbm.setEnabled(false);
                    liter.setText("");
                    hargabbm.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

//        liter.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
////                nama.setText("test agen");
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,int before, int count) {
//
//                if(s.length() > 0) {
//                    DecimalFormat formatter = new DecimalFormat("#,###,###");
//                    int as = Integer.parseInt(liter.getText().toString()) * Integer.parseInt(spinner_value);
//                    hargabbm.setText(formatter.format(as));
//                }else{
//                    hargabbm.setText("0");
//                }
//            }
//        });

        hargabbm.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {

                if(s.length() > 0) {
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    double as = Double.parseDouble(hargabbm.getText().toString()) / Double.parseDouble(spinner_value);
                    Log.d("hasil",as+"");
                    liter.setText(String.format("%.2f", as));
                }else{
                    liter.setText("0");
                }
            }
        });



    }

    private void getBBM(){
        jenisbbmList.clear();
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getJenisBBM");
        parameter.add("0");
        parameter.add("50");
        parameter.add("jenis");
        parameter.add(nokendaraan.getText().toString());
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
                            Toast.makeText(InputBBM.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
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
                                            String harga = isi.getString(2);
                                            Origin prov = new Origin();
                                            prov.setId(id);
                                            prov.setCity(nama);
                                            prov.setProvince(harga);
//                                            prov.setId(""+i);
                                            jenisbbmList.add(prov);
                                        }else{
                                            Origin prov = new Origin();
                                            prov.setId("0");
                                            prov.setCity("--Pilih--");
                                            prov.setProvince("0");
                                            jenisbbmList.add(prov);
                                        }
                                    }
                                    populateSpinner();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputBBM.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputBBM.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputBBM.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputBBM.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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

        for (int i = 0; i < jenisbbmList.size(); i++) {
            if (i==0){
                lables.add(jenisbbmList.get(i).getCity());
            }else{
                lables.add(jenisbbmList.get(i).getCity());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        jenisbbm.setAdapter(spinnerAdapter);
    }



    private void checkKM() {

        if (!validateKode()) {
            return;
        }
            ArrayList<String> parameter = new ArrayList<String>();
            parameter.add("doSSQL");
            parameter.add("12345");
            parameter.add("checkKMkendaraan");
            parameter.add("5");
            parameter.add("0");
            parameter.add("nopol");
            parameter.add(nokendaraan.getText().toString());
            progressDialog.setCancelable(false);
            new SoapClientMobile() {
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
                    Log.d("hasil soap", "" + result);
                    if (result == null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputBBM.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        try {
                            final String text = result.getProperty(1).toString();
                            Log.d("hasil soap data", "" + text);
                            if (text.equals("OK")) {
//                                progressDialog.dismiss();
                                String so = result.getProperty(2).toString();

                                JSONObject jsonObj = new JSONObject(so);
                                JSONArray data = jsonObj.getJSONArray("data");
                                if (data.length() > 1) {
                                    final JSONArray d = data.getJSONArray(1);
                                    int kmakhir=d.getInt(3);

                                    Log.d("hasil soap", "" + kmakhir);
                                    if (Integer.parseInt(km.getText().toString()) < kmakhir){
                                        progressDialog.dismiss();

                                        Toast.makeText(InputBBM.this, "KM Tidak boleh lebih kecil, km sebelumnya adalah "+kmakhir, Toast.LENGTH_LONG).show();
                                    }else{
                                        submitForm();
                                        progressDialog.dismiss();
                                    }


//                                        Toast.makeText(Daftar.this, "Berhasil Daftar", Toast.LENGTH_LONG).show()


                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            submitForm();
                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputBBM.this, text, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputBBM.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            }.execute(parameter);

    }









    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        byte[] raw = out.toByteArray();
        encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);

        String hargabbmreplace= hargabbm.getText().toString().replace(",","");

        JSONObject json = new JSONObject();
        try {
            json.put("service", "setinputBBM");
            json.put("nopol", nokendaraan.getText().toString());
            json.put("nokm", km.getText().toString());
            json.put("hargabbm", hargabbmreplace);
            json.put("liter", liter.getText().toString());
            json.put("image", String.valueOf(encodedImage));
            json.put("employeeCode", user_id);
            json.put("tgl", formattedDate);
            json.put("jenisbbm", idbbm);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("hasil error", ""+e);
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        Log.d("hasil json", ""+json);
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
//                progressDialog.show();
            }


            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(InputBBM.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                    Intent pindah = new Intent(InputBBM.this, HasilProses.class);
                                    pindah.putExtra("proses","inputbbm");
                                    JSONArray msd = data.getJSONArray(1);

                                    final JSONArray w = data.getJSONArray(1);

                                    String bbm = w.getString(0);
                                    pindah.putExtra("no",bbm);
//                                    pindah.putExtra("no","MA-123-123");
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputBBM.this, "Mohon Cek Kembali Data Input BBM Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputBBM.this, "Mohon Cek Kembali Data Input BBM Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(InputBBM.this, HasilError.class);
                            pindah.putExtra("proses","inputbbm");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (final Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputBBM.this, getString(R.string.error_message)+" "+e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }

        }.execute(parameter);
    }

    private boolean validateKode() {

        if(nokendaraan.getText().toString().trim().isEmpty()) {
            nokendaraan.setError("Masukkan No Kendaraan");
            return false;
        }


        if(km.getText().toString().trim().isEmpty()) {
            km.setError("Masukkan KM Kendaraan");
            return false;
        }else{
            km.setError(null);
        }

        if(imageView.getDrawable() == null) {
            Toast.makeText(InputBBM.this, "Mohon Masukkan Foto KM", Toast.LENGTH_SHORT).show();
            return false;

        }


        if(hargabbm.getText().toString().trim().isEmpty() || hargabbm.getText().toString().equals("0")) {
            hargabbm.setError("Masukkan Harga BBM");
            return false;
        }else{
            hargabbm.setError(null);
        }

        if(liter.getText().toString().trim().isEmpty() || liter.getText().toString().equals("0")) {
            liter.setError("Masukkan Jumlah Liter");
            return false;
        }else{
            liter.setError(null);
        }




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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_FOR_NOMOBIL:
                    String nopol = data.getStringExtra("nopol");
                    String sealId = data.getStringExtra("seal");
                    sealisi = sealId;
                    nokendaraan.setText(nopol);
                    getBBM();
//                    if (sealId.equals("2")){
//                        sealInfo.setVisibility(View.GONE);
//                    }else{
//                        sealInfo.setVisibility(View.VISIBLE);
//                    }
                    break;

                case REQUEST_CODE_CLICK_IMAGE:
//                    byte[] byteArray = data.getByteArrayExtra("foto");
//                    Bitmap bitmap2 = BitmapTools.toBitmap(byteArray);
//                    bitmap2 = BitmapTools.rotate(bitmap2, data.getIntExtra("rotation", 0), data.getIntExtra("camera", 0));
//                    imageView.setImageBitmap(bitmap2);
//                    imageView.setVisibility(View.VISIBLE);

                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView.setImageBitmap(bmp);
                    imageView.setVisibility(View.VISIBLE);


//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    imageView.setImageBitmap(photo);
//                    imageView.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }
}
