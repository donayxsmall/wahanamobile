package com.wahana.wahanamobile.Ops.Pickup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.wahana.wahanamobile.Ops.ReportPageActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by team-it on 01/11/18.
 */

public class PickupKodeVerifikasiNew extends DrawerHelper {
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
    UserAPIService mApiInterface;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_verifikasi);
        super.onCreateDrawer(this);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PickupKodeVerifikasiNew.this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mApiInterface = ApiClient.getClient().create(UserAPIService.class);

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



        progressDialog = new ProgressDialog(PickupKodeVerifikasiNew.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupKodeVerifikasiNew.this);
        user_id = session.getID();
        db = new DatabaseHandler(PickupKodeVerifikasiNew.this);
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

        inputVerifikasi.addTextChangedListener(new MyTextWatcher(inputVerifikasi));


        //new DownloadTask(PickupVerifikasi.this,btnInput,Utils.downloadcsvUrl);


//        db.deleteListSumberPickup();
//
//
//        new DownloadTask(PickupKodeVerifikasiNew.this,btnInput,urllink,"pu");

        //db.importscv(cls.ty());

//        rundownload();
//
//        db.importscv(rundownload());

        //Log.d("kuli",""+rundownload());

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submitForm();
                submitFormVer2();

//                coba();
            }
        });


//        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Pickup");
//
//        String uriSting = (filename.getAbsolutePath() + "/"+"testttkpickup.csv");

//        AssetManager manager = context.getAssets();

//        if (!filename.exists()) {
//            filename.mkdirs();
//        }

//        try {
//            FileReader file = new FileReader(filename+"testttkpickup.csv");
//            Log.d("location",""+file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


//        Log.d("location",""+filename);


    }


    private void coba(){
        Intent picture_intent = new Intent(PickupKodeVerifikasiNew.this, ReportPageActivity.class);
        picture_intent.putExtra("url", "https://docs.google.com/gview?embedded=true&url=http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID=3084150974&ro=1&me=1");
        //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
        startActivity(picture_intent);
    }

    private void submitFormVer2(){
        if (!validateKode()) {
            return;
        }



        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        final String verifikasi = inputVerifikasi.getText().toString();

//        final ApiPickup pickupBegin = new ApiPickup(session.getSessionID(),agentcode,session.getID(),rc);

//        Log.d("postreq",""+session.getID()+"|"+agentcode+"|"+session.getID()+"|"+rc);

        Call<ApiPickup> result = mApiInterface.pickupVerifyAccount(session.getSessionID(),session.getID(),agentcode,inputVerifikasi.getText().toString());

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<ApiPickup>() {

            @Override
            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
                try {

                    progressDialog.dismiss();

                    String vrcode=response.body().getVrcode();
                    String vrmsg=response.body().getVrmesg();

                    if(vrcode.equals("1")){
//                        Toast.makeText(PickupVerifikasi.this," response version "+response.body().getVrcode(),Toast.LENGTH_SHORT).show();

                        Log.d("retro",""+vrcode+"|"+vrmsg);

//                        int histPickup=db.checkListPickupHistory(agentcode,verifikasi);
//
//                        if(histPickup==1){
//
//                            ListTtkPickup ttk = db.getTTKhistoryMax(agentcode,verifikasi);
//
//                            int jumlahscan=db.countPUlistScan(agentcode,verifikasi);


//                            final Dialog dialog = new Dialog(context);
//
//                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//                            dialog.setContentView(R.layout.custom_dialog_pu);
//                            ImageView close = (ImageView) dialog.findViewById(R.id.close);
//                            TextView text=(TextView) dialog.findViewById(R.id.text);
//                            TextView iya=(TextView) dialog.findViewById(R.id.iya);
//                            TextView tidak=(TextView) dialog.findViewById(R.id.tidak);

//                            text.setText("Proses Pickup terakhir belum selesai , Sudah di scan \"+jumlahscan+\" ttk dengan nomor ttk terakhir \"+ttk.getTtk()+\", Apakah anda ingin melanjutkan?");

//                            dialog.show();

//                            close.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialog.dismiss();
//                                }
//                            });

//                            final AlertDialog.Builder adb=new AlertDialog.Builder(PickupKodeVerifikasiNew.this);
//                            adb.setTitle("Info");
//                            adb.setMessage("Proses Pickup terakhir belum selesai , Sudah di scan "+jumlahscan+" ttk dengan nomor ttk terakhir "+ttk.getTtk()+", Apakah anda ingin melanjutkan?");
//                            adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    db.deleteListPickup(agentcode,verifikasi);
//
//                                    Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupScanner.class);
//                                    intent.putExtra("kodeagen",agentcode);
//                                    intent.putExtra("kodeverifikasi",verifikasi);
//                                    startActivity(intent);
//                                    finish();
//
//                                }});
//                            adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //db.deleteListPickup();
//                                    Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupScanner.class);
//                                    intent.putExtra("kodeagen",agentcode);
//                                    intent.putExtra("kodeverifikasi",verifikasi);
//                                    startActivity(intent);
//                                    finish();
//                                }});
//
//
//                            adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //db.deleteListPickup();
////                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
//                                    Intent pindah = new Intent(PickupKodeVerifikasiNew.this, PreviewResultScanner.class);
//                                    pindah.putExtra("asal", "pu");
//                                    pindah.putExtra("kodeagen", kodeagen);
//                                    pindah.putExtra("kodeverifikasi", verifikasi);
//                                    startActivity(pindah);
//
//                                }});
//
//                            final AlertDialog alert = adb.create();
//                            alert.show();
//
////                            adb.show();
//
//                            // Hide after some seconds
////                            final Handler handler  = new Handler();
////                            final Runnable runnable = new Runnable() {
////                                @Override
////                                public void run() {
////                                    if (alert.isShowing()) {
////                                        alert.dismiss();
////                                    }
////                                }
////                            };
////
////                            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
////                                @Override
////                                public void onDismiss(DialogInterface dialog) {
////                                    handler.removeCallbacks(runnable);
////                                }
////                            });
////
////                            handler.postDelayed(runnable, 2000);
//
//
//
//
//
//
//                        }else{
//
//                            Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupKodeSortir.class);
//                            intent.putExtra("kodeagen",agentcode);
//                            intent.putExtra("kodeverifikasi",verifikasi);
//                            startActivity(intent);
//                            finish();
//
//                        }

                        Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupKodeSortir.class);
                        intent.putExtra("kodeagen",agentcode);
                        intent.putExtra("kodeverifikasi",verifikasi);
                        startActivity(intent);
                        finish();




                    }else{
                        Toast.makeText(PickupKodeVerifikasiNew.this,vrmsg,Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiPickup> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.d("error",""+t.toString());
            }
        });


    }

//    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
//        final String verifikasi = inputVerifikasi.getText().toString();
//        ArrayList<String> parameter = new ArrayList<String>();
//        parameter.add("pickupVerify");
//        parameter.add(session.getSessionID());
//        parameter.add(session.getID());
//        parameter.add(agentcode);
//        parameter.add(verifikasi);
//        new SoapClient(){
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                Log.i(TAG, "onPreExecute");
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Authenticating...");
//                progressDialog.show();
//            }
//
//            @Override
//            protected void onPostExecute(SoapObject result) {
//                super.onPostExecute(result);
//                Log.d("hasil soap", ""+result);
//                if(result==null){
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(PickupKodeVerifikasiNew.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    });
//                }else {
//                    try{
//                        final String text = result.getProperty(1).toString();
//                        if (text.equals("OK")) {
//                            progressDialog.dismiss();
//
////                            setDataPickup();
//
////                            String cek=db.importscv("testttkpickup8.csv");
////
////                            Log.d("location",""+cek);
//
////                            Intent intent = new Intent(PickupVerifikasi.this, PickupConclusion.class);
//
//                            int histPickup=db.checkListPickupHistory(agentcode,verifikasi);
//
//
//
//
//
//                            if(histPickup==1){
//
//                                ListTtkPickup ttk = db.getTTKhistoryMax(agentcode,verifikasi);
//
//                                int jumlahscan=db.countPUlistScan(agentcode,verifikasi);
//
//                                AlertDialog.Builder adb=new AlertDialog.Builder(PickupKodeVerifikasiNew.this);
//                                adb.setTitle("Info");
//                                adb.setMessage("Data ttk sudah pernah di input , Apakah mau melanjutkan pickup dengan jumlah yang sudah di scan "+jumlahscan+" dan ttk terakhir "+ttk.getTtk()+"?");
//                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        db.deleteListPickup(agentcode,verifikasi);
//
//                                        Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupScanner.class);
//                                        intent.putExtra("kodeagen",agentcode);
//                                        intent.putExtra("kodeverifikasi",verifikasi);
//                                        startActivity(intent);
//                                        finish();
//
//                                    }});
//                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //db.deleteListPickup();
//                                        Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupScanner.class);
//                                        intent.putExtra("kodeagen",agentcode);
//                                        intent.putExtra("kodeverifikasi",verifikasi);
//                                        startActivity(intent);
//                                        finish();
//                                    }});
//
//                                adb.show();
//                            }else{
//
//                                Intent intent = new Intent(PickupKodeVerifikasiNew.this, PickupScanner.class);
//                                intent.putExtra("kodeagen",agentcode);
//                                intent.putExtra("kodeverifikasi",verifikasi);
//                                startActivity(intent);
//                                finish();
//
//                            }
//
//
//
//                            Log.d("histpickup",""+histPickup);
//
//
//                        }else{
//                            progressDialog.dismiss();
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(PickupKodeVerifikasiNew.this, text,Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }catch (Exception e){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(PickupKodeVerifikasiNew.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute(parameter);
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
                            Toast.makeText(PickupKodeVerifikasiNew.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(PickupKodeVerifikasiNew.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupKodeVerifikasiNew.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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

