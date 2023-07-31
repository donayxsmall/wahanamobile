package com.wahana.wahanamobile.Ops.STMSRETUR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.wahana.wahanamobile.ModelApiOPS.aoSubmitPenerimaanRetur;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PenerimaanReturV2 extends DrawerHelper {
    private Button btnClickImage,btnClickImage2,btnClickImage3;
    ImageView imageView,imageView2,imageView3;
    private final int REQUEST_CODE_CLICK_IMAGE = 01;
    private final int REQUEST_CODE_CLICK_IMAGE2 = 02;
    private final int REQUEST_CODE_CLICK_IMAGE3 = 03;
    private static final String TAG = "penerimaanReturV2";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id,ttk,nokeranjang;
    TextView pengisi, tgl, calendar;
    Button submit;

    String alasan,formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public NavDrawerListAdapter drawerAdapter;
    TextView keterangan_label, foto_diri_label;
    ImageView iv;
    DatabaseHandler db;
    Uri selectedImageUri = null;
    String jeniskeranjang,alasanreturcs;

    RequestApiWahanaOps mApiInterface;

    String foto1,foto2,foto3;


    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerimaan_retur_v2);


        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PenerimaanReturV2.this));


        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);


        db = new DatabaseHandler(PenerimaanReturV2.this);
        Intent intent = getIntent();
        ttk = intent.getStringExtra("ttk");
        alasan = intent.getStringExtra("alasan");
        nokeranjang = intent.getStringExtra("nokeranjang");

        jeniskeranjang = intent.getStringExtra("jeniskeranjang");

        alasanreturcs = intent.getStringExtra("alasanreturcs");


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
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(PenerimaanReturV2.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PenerimaanReturV2.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        Random rand;
        rand = new Random();
        request_id=rand.nextInt(10000);

        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        keterangan_label = (TextView) findViewById(R.id.keterangan_label);
        foto_diri_label = (TextView) findViewById(R.id.foto_diri_label);
        submit = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        pengisi.setText(username);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        btnClickImage2 = (Button) findViewById(R.id.btn_submit_foto2);
        btnClickImage3 = (Button) findViewById(R.id.btn_submit_foto3);
        imageView = (ImageView) findViewById(R.id.listView);
        imageView2 = (ImageView) findViewById(R.id.listView2);
        imageView3 = (ImageView) findViewById(R.id.listView3);
        iv = (ImageView) findViewById(R.id.mapView);
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(PenerimaanReturV2.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });
        btnClickImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenerimaanReturV2.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE2);
            }
        });
        btnClickImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenerimaanReturV2.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE3);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(PenerimaanReturV2.this);
                adb.setTitle("Penerimaan TTK retur");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        submitForm();
                        submitFormv2();
                    }});
                adb.show();
            }
        });

        submit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

    }


    private void submitFormv2(){

        if (!validate()) {
            return;
        }


        if (imageView.getDrawable() != null){
//
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

//                Bitmap bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] raw = out.toByteArray();
            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);

            foto1 = String.valueOf(encodedImage).replace("+","?");

//            json.put("foto", String.valueOf(encodedImagefix));
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            Log.e("hasil Original", bitmap.getWidth()+" "+bitmap.getHeight());
            Log.e("hasil Compressed", decoded.getWidth()+" "+decoded.getHeight());
        }
        if (imageView2.getDrawable() != null){
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            BitmapDrawable drawable2 = (BitmapDrawable) imageView2.getDrawable();
            Bitmap bitmap2 = drawable2.getBitmap();
            bitmap2.compress(Bitmap.CompressFormat.JPEG,100,out2);
            byte[] raw2 = out2.toByteArray();
            encodedImage2 = Base64.encodeToString(raw2, Base64.DEFAULT);

            foto2 = String.valueOf(encodedImage2).replace("+","?");
//            json.put("foto2", String.valueOf(encodedImage2));
        }
        if(imageView3.getDrawable() != null){
            ByteArrayOutputStream out3 = new ByteArrayOutputStream();
            BitmapDrawable drawable3 = (BitmapDrawable) imageView3.getDrawable();
            Bitmap bitmap3 = drawable3.getBitmap();
            bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
            byte[] raw3 = out3.toByteArray();
            encodedImage3 = Base64.encodeToString(raw3, Base64.DEFAULT);
            foto3 = String.valueOf(encodedImage3).replace("+","?");
//            json.put("foto3", String.valueOf(encodedImage3));
        }

        Call<aoSubmitPenerimaanRetur> result = mApiInterface.aoSubmitPenerimaanRetur(
                "aoSubmitPenerimaanRetur",
                getString(R.string.partnerid),
                ttk,
                nokeranjang,
                session.getID(),
//                "11190569",
                formattedDate,
                alasan,
                alasanreturcs,
                "1",
                jeniskeranjang,
                foto1,
                foto2,
                foto3,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submit Data...");
        progressDialog.show();


        result.enqueue(new Callback<aoSubmitPenerimaanRetur>() {

            @Override
            public void onResponse(Call<aoSubmitPenerimaanRetur> call, Response<aoSubmitPenerimaanRetur> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        Log.d("retro",""+response.body().getCode());

                        if (response.body().getCode().equals("0")) {

                            Intent pindah = new Intent(PenerimaanReturV2.this, HasilProses.class);
                            pindah.putExtra("proses","retur");
                            pindah.putExtra("no", response.body().getNumber());
                            startActivity(pindah);
                            finish();


                        }else{

                            new SweetAlertDialog(PenerimaanReturV2.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(response.body().getAlert())
                                    .show();


                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(PenerimaanReturV2.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<aoSubmitPenerimaanRetur> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                Log.d("error2",""+t.toString());

                new SweetAlertDialog(PenerimaanReturV2.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }

    private void submitForm() {
        if (!validate()) {
            return;
        }

        JSONObject json = new JSONObject();

        try {
            json.put("service", "setSTMSRetur");
            json.put("employeeCode", session.getID());
            json.put("tgl", formattedDate);
            json.put("ttk", ttk);
            json.put("alasan", alasan);
            json.put("jumlah","1");
            json.put("nokeranjang",nokeranjang);
            json.put("jeniskeranjang",jeniskeranjang);
            json.put("alasanreturcs",alasanreturcs);

            if (imageView.getDrawable() != null){
//
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

//                Bitmap bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] raw = out.toByteArray();
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);

                String encodedImagefix = String.valueOf(encodedImage).replace("+","?");

                json.put("foto", String.valueOf(encodedImagefix));
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                Log.e("hasil Original", bitmap.getWidth()+" "+bitmap.getHeight());
                Log.e("hasil Compressed", decoded.getWidth()+" "+decoded.getHeight());
            }
            if (imageView2.getDrawable() != null){
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                BitmapDrawable drawable2 = (BitmapDrawable) imageView2.getDrawable();
                Bitmap bitmap2 = drawable2.getBitmap();
                bitmap2.compress(Bitmap.CompressFormat.JPEG,100,out2);
                byte[] raw2 = out2.toByteArray();
                encodedImage2 = Base64.encodeToString(raw2, Base64.DEFAULT);
                json.put("foto2", String.valueOf(encodedImage2));
            }
            if(imageView3.getDrawable() != null){
                ByteArrayOutputStream out3 = new ByteArrayOutputStream();
                BitmapDrawable drawable3 = (BitmapDrawable) imageView3.getDrawable();
                Bitmap bitmap3 = drawable3.getBitmap();
                bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
                byte[] raw3 = out3.toByteArray();
                encodedImage3 = Base64.encodeToString(raw3, Base64.DEFAULT);
                json.put("foto3", String.valueOf(encodedImage3));
            }

            Log.d("json ",""+json);

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Intent pindah = new Intent(PenerimaanReturV2.this, HasilProses.class);
//        pindah.putExtra("proses","retur");
//        pindah.putExtra("no", "RTE-23213-213213");
//        startActivity(pindah);
//        finish();

//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//        JSONObject json = new JSONObject();
//        try {
//            json.put("service", "setSTMSRetur");
//            json.put("employeeCode", session.getID());
//            json.put("tgl", formattedDate);
//            json.put("ttk", ttk);
//            json.put("alasan", alasan);
//            json.put("jumlah","1");
//            json.put("nokeranjang",nokeranjang);
//            json.put("jeniskeranjang",jeniskeranjang);
//
//            if (imageView.getDrawable() != null){
//
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//
////                Bitmap bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
//                Bitmap bitmap = drawable.getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
//                byte[] raw = out.toByteArray();
//                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
//                json.put("foto", String.valueOf(encodedImage));
//                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//                Log.e("hasil Original", bitmap.getWidth()+" "+bitmap.getHeight());
//                Log.e("hasil Compressed", decoded.getWidth()+" "+decoded.getHeight());
//            }
//            if (imageView2.getDrawable() != null){
//                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
//                BitmapDrawable drawable2 = (BitmapDrawable) imageView2.getDrawable();
//                Bitmap bitmap2 = drawable2.getBitmap();
//                bitmap2.compress(Bitmap.CompressFormat.JPEG,100,out2);
//                byte[] raw2 = out2.toByteArray();
//                encodedImage2 = Base64.encodeToString(raw2, Base64.DEFAULT);
//                json.put("foto2", String.valueOf(encodedImage2));
//            }
//            if(imageView3.getDrawable() != null){
//                ByteArrayOutputStream out3 = new ByteArrayOutputStream();
//                BitmapDrawable drawable3 = (BitmapDrawable) imageView3.getDrawable();
//                Bitmap bitmap3 = drawable3.getBitmap();
//                bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
//                byte[] raw3 = out3.toByteArray();
//                encodedImage3 = Base64.encodeToString(raw3, Base64.DEFAULT);
//                json.put("foto3", String.valueOf(encodedImage3));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("json2", ""+json);
//
//        ArrayList<String> parameter = new ArrayList<String>();
//        parameter.add("doSSQL");
//        parameter.add(session.getSessionID());
//        parameter.add("apiGeneric");
//        parameter.add("20");
//        parameter.add("0");
//        parameter.add("jsonp");
//        parameter.add(""+json);
//        progressDialog.setCancelable(false);
//
//        new SoapClientMobile(){
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                Log.d("adding data", "add");
//                Log.i(TAG, "onPreExecute");
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Adding Data...");
//                progressDialog.show();
//            }
//            @Override
//            protected void onPostExecute(SoapObject result) {
//                super.onPostExecute(result);
//                Log.d("Hasil", result+"");
//                if (result == null) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {progressDialog.dismiss();
//                            Toast.makeText(PenerimaanReturV2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } else {
//                    try{
//                        final String code = result.getProperty(0).toString();
//                        final String text = result.getProperty(1).toString();
//                        if (code.equals("1")) {
//                            progressDialog.dismiss();
//                            String so = result.getProperty(2).toString();
//                            JSONObject jsonObj = new JSONObject(so);
//                            JSONArray data = jsonObj.getJSONArray("data");
//                            Log.d("hasil soap data", ""+data);
//                            if (data.length()>1){
//                                final JSONArray d = data.getJSONArray(2);
////                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
//                                if (d.getJSONObject(1).getString("status").equals("1")){
//                                    Intent pindah = new Intent(PenerimaanReturV2.this, HasilProses.class);
//                                    pindah.putExtra("proses","retur");
//                                    JSONArray msd = data.getJSONArray(1);
//                                    String ms = msd.getString(0);
//                                    pindah.putExtra("no", ms);
//                                    startActivity(pindah);
//                                    finish();
//                                }else{
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(PenerimaanReturV2.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//                            }else{
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(PenerimaanReturV2.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        }else{
//                            progressDialog.dismiss();
//                            Intent pindah = new Intent(PenerimaanReturV2.this, HasilError.class);
//                            pindah.putExtra("proses","retur");
//                            pindah.putExtra("no", text);
//                            startActivity(pindah);
//                        }
//                    }catch (Exception e){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(PenerimaanReturV2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute(parameter);

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            switch(requestCode){
                case REQUEST_CODE_CLICK_IMAGE:
//                    byte[] byteArray = data.getByteArrayExtra("foto");
//                    Bitmap photo = BitmapTools.toBitmap(byteArray);
//                    photo = BitmapTools.rotate(photo, data.getIntExtra("rotation", 0), data.getIntExtra("camera", 0));
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));

                    Bitmap bmp = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView.setImageBitmap(bmp);
                    imageView.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_CLICK_IMAGE2:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp2 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView2.setImageBitmap(bmp2);
                    imageView2.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_CLICK_IMAGE3:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp3 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView3.setImageBitmap(bmp3);
                    imageView3.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        if (imageView.getDrawable() == null){
            Toast.makeText(PenerimaanReturV2.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    public boolean isInternetAvailable(Context c) {

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}
