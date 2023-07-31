package com.wahana.wahanamobile.Ops.PickupRetail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupBeginOpsRetail;
import com.wahana.wahanamobile.Ops.Pickup.PickupAgenNew;
import com.wahana.wahanamobile.Ops.Pickup.PickupKodeVerifikasiNew;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupAgenRetail extends AppCompatActivity {

    private static final String TAG = "PickupAgenNew";

    ProgressDialog progressDialog;

    private TextView inputLayoutKode;
    public EditText inputKode;
    private Button btnInput;

    TextView pengisi, tgl, calendar;
    RelativeLayout infoLayout;

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
    DatabaseHandler db;
//    UserAPIService mApiInterface;
    RequestApiWahanaOps mApiInterface;
    JSONObject jsonlokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agen_retail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PickupAgenRetail.this));


        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

//        mApiInterface = ApiClient.getClient().create(UserAPIService.class);

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PickupActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(PickupAgenRetail.this);
        db.deleteKode();
        progressDialog = new ProgressDialog(PickupAgenRetail.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupAgenRetail.this);
        username = session.getUsername();
        user_id = session.getID();
        db.deletePickup();
        btnInput = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(username);
        infoLayout = (RelativeLayout) findViewById(R.id.info_layout);

        infoLayout.bringToFront();
        infoLayout.invalidate();
        inputLayoutKode = (TextView) findViewById(R.id.input_layout_kode_agen);
        inputKode = (EditText) findViewById(R.id.input_kode_agen);
        this.setTitle("");
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        inputKode.setTypeface(type);
        inputKode.addTextChangedListener(new PickupAgenRetail.MyTextWatcher(inputKode));




        FusedLocation fusedLocation = new FusedLocation(PickupAgenRetail.this, new FusedLocation.Callback() {
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













        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Log.d("klikbut1",""+"sdadas");
//                submitFormver2();

                  submitFormver3();

//                DatabaseHandler db = new DatabaseHandler(PickupAgenRetail.this);
//                db.addAgent(new AgentCode("12123"));
//                Intent intent = new Intent(PickupAgenRetail.this, PickupKodeVerifikasiRetail.class);
//                intent.putExtra("kodeagen", "12123");
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();


            }
        });
    }

    public void createfolder(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Pickup");
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    private void tesdialog(){

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Connecting");
        progress.setMessage("Please wait while we connect to devices...");

//        progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        btnInput.setEnabled(false);

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progress.cancel();
                btnInput.setEnabled(true);
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 10000);
    }


    private void submitFormver3(){
        if (!validate()) {
            return;
        }

        final String agentcode = inputKode.getText().toString();



        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);


        Call<aoPickupBeginOpsRetail> result = mApiInterface.aoPickupBeginOpsRetail(
                "aoPickupBeginOpsRetail",
                getString(R.string.partnerid),
                session.getID(),
                agentcode,
                session.getSessionID(),
                jsonlokasi,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        btnInput.setEnabled(false);


        result.enqueue(new Callback<aoPickupBeginOpsRetail>() {

            @Override
            public void onResponse(Call<aoPickupBeginOpsRetail> call, Response<aoPickupBeginOpsRetail> response) {

//                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();
                btnInput.setEnabled(true);

                if(response.isSuccessful()) {



                    try {

                        String vrcode = response.body().getCode();
//                        String vrmsg = response.body().getVrmesg();
//                        String vurl = response.body().getvUrl();
//                        String verificationcode = response.body().getVerificationCode();

                        Log.d("error1",""+vrcode);
//                        Log.d("error2",""+response.body().getText());

                        if (vrcode.equals("0")) {
//                            Toast.makeText(PickupAgenRetail.this, " response version " + response.body().getVerificationCode(), Toast.LENGTH_SHORT).show();

//                            Log.d("retro", "" + response.body().getVerificationCode() + "|" + vurl + "|" + verificationcode);

                            DatabaseHandler db = new DatabaseHandler(PickupAgenRetail.this);
                            db.addAgent(new AgentCode(agentcode));
                            Intent intent = new Intent(PickupAgenRetail.this, PickupKodeVerifikasiRetail.class);
                            intent.putExtra("kodeagen", agentcode);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                        } else {

//                            Log.d("error3",""+response.body().getText());

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(PickupAgenRetail.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                            ///sementara
//                            DatabaseHandler db = new DatabaseHandler(PickupAgenRetail.this);
//                            db.addAgent(new AgentCode(agentcode));
//                            Intent intent = new Intent(PickupAgenRetail.this, PickupKodeVerifikasiRetail.class);
//                            intent.putExtra("kodeagen", agentcode);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(PickupAgenRetail.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(PickupAgenRetail.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();



                }



            }

            @Override
            public void onFailure(Call<aoPickupBeginOpsRetail> call, Throwable t) {
                progressDialog.dismiss();
                btnInput.setEnabled(true);
                t.printStackTrace();

                new SweetAlertDialog(PickupAgenRetail.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });



    }


//    private void submitFormver2(){
//        if (!validate()) {
//            return;
//        }
//
//        final String agentcode = inputKode.getText().toString();
//
//
//
//        Random r = new Random();
//        int requestCode = r.nextInt(9999);
//        String rc = String.valueOf(requestCode);
//
//
//        Call<aoPickupBeginOpsRetail> result = mApiInterface.aoPickupBeginOpsRetail(session.getSessionID(),agentcode,session.getID(),rc);
//
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//
//        btnInput.setEnabled(false);
//
//        result.enqueue(new Callback<ApiPickup>() {
//
//            @Override
//            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
//
//                Log.d("error",""+response.code());
//
//                if(response.isSuccessful()) {
//
//                    btnInput.setEnabled(true);
//
//                    try {
//
//                        progressDialog.dismiss();
//
//                        String vrcode = response.body().getVrcode();
//                        String vrmsg = response.body().getVrmesg();
//                        String vurl = response.body().getvUrl();
//                        String verificationcode = response.body().getVerificationCode();
//
//                        if (vrcode.equals("1")) {
//                            Toast.makeText(PickupAgenRetail.this, " response version " + response.body().getVerificationCode(), Toast.LENGTH_SHORT).show();
//
//                            Log.d("retro", "" + response.body().getVerificationCode() + "|" + vurl + "|" + verificationcode);
//
//                            DatabaseHandler db = new DatabaseHandler(PickupAgenRetail.this);
//                            db.addAgent(new AgentCode(agentcode));
//                            Intent intent = new Intent(PickupAgenRetail.this, PickupKodeVerifikasiRetail.class);
//                            intent.putExtra("kodeagen", agentcode);
//                            intent.putExtra("urllink", vurl);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
//
//
//                        } else {
//                            Toast.makeText(PickupAgenRetail.this, " error " + vrmsg, Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        btnInput.setEnabled(true);
//                    }
//
//
//                }else{
//
//                    progressDialog.dismiss();
//                    btnInput.setEnabled(true);
//
////                    try {
////                        Toast.makeText(PickupAgenRetail.this, " error " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
////                    } catch (IOException e) {
////                        Toast.makeText(PickupAgenRetail.this, " unknown error", Toast.LENGTH_SHORT).show();
////                        e.printStackTrace();
////                    }
//
//                    Log.d("error1",""+response.code());
//
//                    Toast.makeText(PickupAgenRetail.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
//
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ApiPickup> call, Throwable t) {
//                progressDialog.dismiss();
//                btnInput.setEnabled(true);
//                t.printStackTrace();
//
//                Log.d("error2",""+t.toString());
//
//                Toast.makeText(PickupAgenRetail.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
////
////                if (t instanceof NoConnectivityException) {
////                    Toast.makeText(PickupAgenRetail.this, "Error Internet connection", Toast.LENGTH_SHORT).show();
////                }
//
//
//            }
//        });
//
//
//
//    }

    private void submitForm() {
        if (!validate()) {
            return;
        }
        final String agentcode = inputKode.getText().toString();
        Log.d("agent code", agentcode);
        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("pickupBegin");
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
                            Toast.makeText(PickupAgenRetail.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            DatabaseHandler db = new DatabaseHandler(PickupAgenRetail.this);
                            db.addAgent(new AgentCode(agentcode));
                            Intent intent = new Intent(PickupAgenRetail.this, PickupKodeVerifikasiNew.class);
                            intent.putExtra("kodeagen",agentcode);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupAgenRetail.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupAgenRetail.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_kode_agen:
                    //  validateKode();
                    break;

            }
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

    public boolean validate() {
        boolean valid = true;

        String code = inputKode.getText().toString();

        if(code.isEmpty())
        {
            inputKode.setError("Masukkan Kode Agen");
            valid = false;
        } else {
            inputKode.setError(null);
        }
        return valid;
    }



}
