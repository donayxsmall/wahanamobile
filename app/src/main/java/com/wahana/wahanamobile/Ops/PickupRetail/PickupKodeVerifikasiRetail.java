package com.wahana.wahanamobile.Ops.PickupRetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupBeginOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupVerifyOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupVerifyOpsRetail_Result;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.ReportPageActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupKodeVerifikasiRetail extends AppCompatActivity {
    private static final String TAG = "PickupKodeVerifikasiNew";

    ProgressDialog progressDialog;

    private TextInputLayout inputLayoutVerifikasi;
    public EditText inputVerifikasi;
    private Button btnInput;
    String agentcode;
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
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
//    Activity context;

    Context context = this;
    String kodeagen,urllink;
//    UserAPIService mApiInterface;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_kode_verifikasi_retail);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PickupKodeVerifikasiRetail.this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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
//                Intent intent = new Intent(PickupVerifikasi.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent myIntent = getIntent();
        kodeagen=myIntent.getStringExtra("kodeagen");
        urllink=myIntent.getStringExtra("urllink");



        Log.d("kodeagen",""+kodeagen);



        progressDialog = new ProgressDialog(PickupKodeVerifikasiRetail.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupKodeVerifikasiRetail.this);
        user_id = session.getID();
        db = new DatabaseHandler(PickupKodeVerifikasiRetail.this);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

//        db.deleteListSumberPickup();

        btnInput = (Button) findViewById(R.id.input_button);

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


        //inputLayoutVerifikasi = (TextInputLayout) findViewById(R.id.input_layout_verifikasi);
        inputVerifikasi = (EditText) findViewById(R.id.input_verifikasi);
        this.setTitle("");

        inputVerifikasi.setTypeface(type);

        inputVerifikasi.addTextChangedListener(new PickupKodeVerifikasiRetail.MyTextWatcher(inputVerifikasi));


        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submitFormVer2();

                if (!validateKode()) {
                    return;
                }

                AlertDialog.Builder adb = new AlertDialog.Builder(PickupKodeVerifikasiRetail.this);
                adb.setTitle("Info");
                adb.setMessage("Pickup Scan Barcode ?");

                adb.setNegativeButton("Per Kode Sortir", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent pindah = new Intent(PickupKodeVerifikasiRetail.this, PickupKodeSortirRetail.class);
//                        pindah.putExtra("cara","manual");
//                        startActivity(pindah);

                        submitFormVer3("1");
                    }
                });

                adb.setPositiveButton("Semua", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent pindah = new Intent(PickupKodeVerifikasiRetail.this, PickupScannerRetail.class);
//                        pindah.putExtra("cara","alat");
//                        startActivity(pindah);

                        submitFormVer3("2");
                    }
                });
                adb.show();

//                Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupKodeSortirRetail.class);
//                intent.putExtra("kodeagen",agentcode);
//                intent.putExtra("kodeverifikasi","234");
//                startActivity(intent);
//                finish();

            }
        });


    }

    private void coba(){
        Intent picture_intent = new Intent(PickupKodeVerifikasiRetail.this, ReportPageActivity.class);
        picture_intent.putExtra("url", "https://docs.google.com/gview?embedded=true&url=http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID=3084150974&ro=1&me=1");
        //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
        startActivity(picture_intent);
    }


    private void submitFormVer3(final String flagsortir){


        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        final String verifikasi = inputVerifikasi.getText().toString();

        Call<aoPickupVerifyOpsRetail> result = mApiInterface.aoPickupVerifyOpsRetail(
                "aoPickupVerifyOpsRetail",
                getString(R.string.partnerid),
                session.getID(),
                kodeagen,
                verifikasi,
                flagsortir,
                session.getSessionID(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<aoPickupVerifyOpsRetail>() {

            @Override
            public void onResponse(Call<aoPickupVerifyOpsRetail> call, Response<aoPickupVerifyOpsRetail> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();


                        Log.d("error1",""+vrcode);
                        Log.d("error2",""+response.body().getText());

                        if (vrcode.equals("0")) {

                            aoPickupVerifyOpsRetail data = response.body();
                            List<aoPickupVerifyOpsRetail_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String vurl = dataList.get(0).getUrlfile();
                            String vFile = dataList.get(0).getFilename();

                            Log.d("retro1", "" + dataList.get(0).getUrlfile());

                            db.deleteListSumberPickup();

                            if (flagsortir.equals("1")){
                                Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupKodeSortirRetail.class);
                                intent.putExtra("kodeagen",agentcode);
                                intent.putExtra("kodeverifikasi",verifikasi);
                                intent.putExtra("flagsortir",flagsortir);
                                startActivity(intent);
                                finish();

                            }else{


                                new DownloadTask(PickupKodeVerifikasiRetail.this, btnInput, vurl, "pu",vFile);

                                int histPickup = db.checkListPickupHistoryAll(agentcode, verifikasi);

                                if (histPickup == 1) {

                                    ListTtkPickup ttk = db.getTTKhistoryMaxAll(agentcode, verifikasi);
                                    int jumlahscan = db.countPUlistScanAll(agentcode, verifikasi);

                                    final AlertDialog.Builder adb = new AlertDialog.Builder(PickupKodeVerifikasiRetail.this);
                                    adb.setTitle("Info");
                                    adb.setMessage("Proses Pickup terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                    adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.deleteListPickupAll(agentcode, verifikasi);

                                            Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupScannerRetail.class);
                                            intent.putExtra("kodeagen", agentcode);
                                            intent.putExtra("kodeverifikasi", verifikasi);
                                            intent.putExtra("flagsortir",flagsortir);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });
                                    adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //db.deleteListPickup();
                                            Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupScannerRetail.class);
                                            intent.putExtra("kodeagen", agentcode);
                                            intent.putExtra("kodeverifikasi", verifikasi);
                                            intent.putExtra("flagsortir",flagsortir);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });


                                    adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent pindah = new Intent(PickupKodeVerifikasiRetail.this, PreviewResultScanner.class);
                                            pindah.putExtra("asal", "pu");
                                            pindah.putExtra("kodeagen", agentcode);
                                            pindah.putExtra("kodeverifikasi", verifikasi);
                                            pindah.putExtra("flagsortir",flagsortir);
                                            startActivity(pindah);

                                        }
                                    });

                                    final AlertDialog alert = adb.create();
                                    alert.show();

                                }else{

                                    Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupScannerRetail.class);
                                    intent.putExtra("kodeagen", agentcode);
                                    intent.putExtra("kodeverifikasi", verifikasi);
                                    intent.putExtra("flagsortir",flagsortir);
                                    startActivity(intent);
                                    finish();
                                }


                            }



                        } else {

//                            Log.d("error3",""+response.body().getText());

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(PickupKodeVerifikasiRetail.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(PickupKodeVerifikasiRetail.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(PickupKodeVerifikasiRetail.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoPickupVerifyOpsRetail> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(PickupKodeVerifikasiRetail.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });


    }

//    private void submitFormVer2(){
//        if (!validateKode()) {
//            return;
//        }
//
//
//
//        Random r = new Random();
//        int requestCode = r.nextInt(9999);
//        String rc = String.valueOf(requestCode);
//        final String verifikasi = inputVerifikasi.getText().toString();
//
////        final ApiPickup pickupBegin = new ApiPickup(session.getSessionID(),agentcode,session.getID(),rc);
//
////        Log.d("postreq",""+session.getID()+"|"+agentcode+"|"+session.getID()+"|"+rc);
//
//        Call<ApiPickup> result = mApiInterface.pickupVerify(session.getSessionID(),session.getID(),agentcode,inputVerifikasi.getText().toString());
//
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//
//        result.enqueue(new Callback<ApiPickup>() {
//
//            @Override
//            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
//                try {
//
//                    progressDialog.dismiss();
//
//                    String vrcode=response.body().getVrcode();
//                    String vrmsg=response.body().getVrmesg();
//
//                    if(vrcode.equals("1")){
////                        Toast.makeText(PickupVerifikasi.this," response version "+response.body().getVrcode(),Toast.LENGTH_SHORT).show();
//
//                        Log.d("retro",""+vrcode+"|"+vrmsg);
//
//                        Intent intent = new Intent(PickupKodeVerifikasiRetail.this, PickupKodeSortirRetail.class);
//                        intent.putExtra("kodeagen",agentcode);
//                        intent.putExtra("kodeverifikasi",verifikasi);
//                        startActivity(intent);
//                        finish();
//
//
//
//
//                    }else{
//                        Toast.makeText(PickupKodeVerifikasiRetail.this,vrmsg,Toast.LENGTH_SHORT).show();
//                    }
//
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ApiPickup> call, Throwable t) {
//                progressDialog.dismiss();
//                t.printStackTrace();
//                Log.d("error",""+t.toString());
//            }
//        });
//
//
//    }


    private void setDataPickup(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("TestgetTTKpickup");
        parameter.add("0");
        parameter.add("0");
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
                            Toast.makeText(PickupKodeVerifikasiRetail.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            db.deleteListSumberPickup();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){

                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);
                                            String noref = isi.getString(1);

                                            //Log.d("pickup",""+ttk+":"+noref);
                                            ListTtkPickup ori = new ListTtkPickup();
                                            ori.setTtk(ttk);
                                            ori.setNoref(noref);

                                            boolean e=db.addTTKPickupSumber(ori);

                                            Log.d("insert",""+e);
                                        }

                                    }


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                        }
                                    });
                                }

                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupKodeVerifikasiRetail.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupKodeVerifikasiRetail.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private boolean validateKode() {
        if (inputVerifikasi.getText().toString().trim().isEmpty()) {
            inputVerifikasi.setError("Masukkan Kode Verifikasi");

            return false;
        } else {
            inputVerifikasi.setError(null);
        }

        return true;
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
                    validateKode();
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




}
