package com.wahana.wahanamobile.Ops.BuatMAdirect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.BuatMaDirect.opv2CreateDirectMA;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.STPUdirectMA.StpuScannerdirectMa;
import com.wahana.wahanamobile.Ops.STPUdirectMA.inputDataSTPUdirectMa;
import com.wahana.wahanamobile.Ops.modaAngkutan.AddDataMAActivity;
import com.wahana.wahanamobile.Ops.modaAngkutan.SearchMobilActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.Utils;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class inputDataMaDirect extends AppCompatActivity {

    private static final String TAG = "AddDataMAActivity";

    ProgressDialog progressDialog;

    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, tujuan, asal;
    Button btnClickImage;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    DatabaseHandler db;
    LinearLayout sealInfo;
    EditText km, seal;
    String encodedImage=null;
    String sealisi="1";
    AutoCompleteTextView nokendaraan,kodeopstujuan;
    private final int REQUEST_FOR_NOMOBIL = 1;
    EditText niksupir;
    RequestApiWahanaOps mApiInterface;
    JSONObject jsonlokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_ma_direct);

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
                Intent intent = new Intent(inputDataMaDirect.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

        Intent intent = getIntent();
//        noKendaraan = intent.getStringExtra("no_kendaraan");
        tujuan = intent.getStringExtra("tujuan");
        asal = intent.getStringExtra("asal");

        db = new DatabaseHandler(inputDataMaDirect.this);

        progressDialog = new ProgressDialog(inputDataMaDirect.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputDataMaDirect.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        imageView = (ImageView) findViewById(R.id.listView);
        sealInfo = (LinearLayout)findViewById(R.id.layout_seal_isi);
        km = (EditText) findViewById(R.id.input_keterangan);
        seal = (EditText) findViewById(R.id.input_seal);
        nokendaraan=(AutoCompleteTextView)findViewById(R.id.input_nokendaraan);
        niksupir =(EditText)findViewById(R.id.input_nik_supir);
        kodeopstujuan=(AutoCompleteTextView)findViewById(R.id.input_kodeops_tujuan);



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



        /// GET LOKASI
        FusedLocation fusedLocation = new FusedLocation(inputDataMaDirect.this, new FusedLocation.Callback() {
            @Override
            public void onLocationResult(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                jsonlokasi = new JSONObject();
                try {
                    jsonlokasi.put("latitude", location.getLatitude());
                    jsonlokasi.put("longitude", location.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                getlokasi(latitude,longitude);


            }
        });

        if (!fusedLocation.isGPSEnabled()) {
            fusedLocation.showSettingsAlert();
        } else {
            fusedLocation.getCurrentLocation(1);
        }

        //--END----///




        nokendaraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(inputDataMaDirect.this,SearchMobilMa.class),REQUEST_FOR_NOMOBIL);
            }
        });



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(inputDataMaDirect.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!validate()) {
                            return;
                        }
                        submitForm();
                    }});
                adb.show();
            }
        });

        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }


    private void submitForm(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        if (imageView.getDrawable() != null) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] raw = out.toByteArray();
            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
        }else{
            encodedImage=null;
        }

//        Map<String, String>  params = new HashMap<String, String>();
//        params.put("_r", "opv2CreateDirectMA");
//        params.put("partnerId", getString(R.string.partnerid));
//        params.put("authCode", session.getKeyTokenJWT());
//        params.put("specification", "0");
//        params.put("nikMA", niksupir.getText().toString());
//        params.put("mobilNo", nokendaraan.getText().toString());
//        params.put("mobilKM", km.getText().toString());
//        params.put("sealNo", seal.getText().toString());
//        params.put("sealPicture", String.valueOf(encodedImage));
//        params.put("employeeCode", session.getID());

        Call<opv2CreateDirectMA> result = mApiInterface.opv2CreateDirectMA(
                "opv2CreateDirectMA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir.getText().toString(),
                nokendaraan.getText().toString(),
                km.getText().toString(),
                seal.getText().toString(),
                String.valueOf(encodedImage),
                kodeopstujuan.getText().toString().toUpperCase(),
                jsonlokasi,
                formattedDate,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();



        result.enqueue(new Callback<opv2CreateDirectMA>() {

            @Override
            public void onResponse(Call<opv2CreateDirectMA> call, retrofit2.Response<opv2CreateDirectMA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

                            String nomorMA = response.body().getData().getNomorMA();
                            String urlMA = response.body().getData().getUrlMA();



                            Intent pindah = new Intent(inputDataMaDirect.this, HasilProses.class);
                            pindah.putExtra("proses", "directma");
                            pindah.putExtra("no", nomorMA);
                            pindah.putExtra("urlfile", urlMA);
                            startActivity(pindah);
                            finish();


                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputDataMaDirect.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(inputDataMaDirect.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(inputDataMaDirect.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2CreateDirectMA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(inputDataMaDirect.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });




    }


    private void submitFormv2() {

        if (imageView.getDrawable() != null) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] raw = out.toByteArray();
            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
        }else{
            encodedImage=null;
        }


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Utils.urlRest;

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);

                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);

                            String code = json.getString("code");
                            String text = json.getString("text");

                            if(code.equals("0")){

                                JSONObject datattk = json.getJSONObject("data");

                                String nomorMA = datattk.getString("nomorMA");
                                String urlMA = datattk.getString("urlMA");



                                Intent pindah = new Intent(inputDataMaDirect.this, HasilProses.class);
                                pindah.putExtra("proses", "directma");
                                pindah.putExtra("no", nomorMA);
                                pindah.putExtra("urlfile", urlMA);
                                startActivity(pindah);
                                finish();


                                Log.d("Response", nomorMA);



                            }else{
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("_r", "opv2CreateDirectMA");
                params.put("partnerId", getString(R.string.partnerid));
                params.put("authCode", session.getKeyTokenJWT());
                params.put("specification", "0");
                params.put("nikMA", niksupir.getText().toString());
                params.put("mobilNo", nokendaraan.getText().toString());
                params.put("mobilKM", km.getText().toString());
                params.put("sealNo", seal.getText().toString());
                params.put("sealPicture", String.valueOf(encodedImage));
                params.put("employeeCode", session.getID());
                return params;
            }
        };
        queue.add(postRequest);


    }

    public boolean validate() {
        boolean valid = true;

        if (niksupir.getText().toString().isEmpty()) {
            niksupir.setError("Masukkan Nik Supir");
            valid = false;
        } else {
            niksupir.setError(null);
        }

        if (nokendaraan.getText().toString().isEmpty()) {
            nokendaraan.setError("Masukkan Kode Kendaraan");
            valid = false;
        } else {
            nokendaraan.setError(null);
        }

        if (kodeopstujuan.getText().toString().isEmpty()) {
            kodeopstujuan.setError("Masukkan Kode SC Tujuan");
            valid = false;
        } else {
            kodeopstujuan.setError(null);
        }


        return valid;
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
//                    if (sealId.equals("2")){
//                        sealInfo.setVisibility(View.GONE);
//                    }else{
//                        sealInfo.setVisibility(View.VISIBLE);
//                    }
                    break;

                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    imageView.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }
}
