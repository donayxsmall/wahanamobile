package com.wahana.wahanamobile.Ops.stpuretail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputNikSupirRetail extends AppCompatActivity {

    private static final String TAG = "InputNikSupir";

    ProgressDialog progressDialog;
    private TextView inputLayoutKode;
    public EditText input_nik_supir;
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
    UserAPIService mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_nik_supir_retail);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(InputNikSupirRetail.this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
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

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(InputNikSupirRetail.this);

        progressDialog = new ProgressDialog(InputNikSupirRetail.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNikSupirRetail.this);
        username = session.getUsername();
        user_id = session.getID();

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

        Log.d("jam",""+getDateTime());

        input_nik_supir = (EditText) findViewById(R.id.input_nik_supir);
        this.setTitle("");
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        input_nik_supir.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        mApiInterface = ApiClient.getClient().create(UserAPIService.class);
    }

    private void submitForm() {
        if (!validate()) {
            return;
        }






//        db.deleteListSumberSTPU();
//
//        new DownloadTask(InputNikSupir.this,btnInput,Utils.downloadcsvUrl,"stpu");

//        Intent intent = new Intent(InputNikSupir.this, StpuVerifikasi.class);
//        intent.putExtra("niksupir",input_nik_supir.getText().toString());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();



//        rundownload();
//        db.importscvSTPU(rundownload());

//        DownloadTask cls = new DownloadTask(InputNikSupir.this,btnInput, Utils.downloadcsvUrl);
//        db.importscvSTPU(cls.ty());



        Call<ApiPickup> result = mApiInterface.genTTKstpu(input_nik_supir.getText().toString());

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
                    String vurl=response.body().getvUrl();
                    String verificationcode=response.body().getVerificationCode();

//                    db.deleteListSumberSTPU();
//
//                    new DownloadTask(InputNikSupir.this,btnInput,vurl,"stpu");

                    if(vrcode.equals("1")){

//                        int histPickup=db.checkListSTPUHistory(input_nik_supir.getText().toString());
//
//                        int jumlahscan=db.countSTPUlistScan(input_nik_supir.getText().toString());
//
//                        if(histPickup==1){
//
//                            ListTtkPickup ttk = db.getTTKhistoryMaxSTPU(input_nik_supir.getText().toString());
//
//                            AlertDialog.Builder adb=new AlertDialog.Builder(InputNikSupir.this);
//                            adb.setTitle("Info");
//                            adb.setMessage("Proses Pickup terakhir belum selesai , Sudah di scan "+jumlahscan+" ttk dengan nomor ttk terakhir "+ttk.getTtk()+", Apakah anda ingin melanjutkan?");
//                            adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    db.deleteListSTPU(input_nik_supir.getText().toString());
//
//                                    Intent intent = new Intent(InputNikSupir.this, StpuScanner.class);
//                                    intent.putExtra("niksupir",input_nik_supir.getText().toString());
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//
//                                }});
//                            adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //db.deleteListPickup();
//                                    Intent intent = new Intent(InputNikSupir.this, StpuScanner.class);
//                                    intent.putExtra("niksupir",input_nik_supir.getText().toString());
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//                                }});
//
//                            adb.show();
//                        }else{
//
//                            Intent intent = new Intent(InputNikSupir.this, StpuKodeSortir.class);
//                            intent.putExtra("niksupir",input_nik_supir.getText().toString());
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
//
//                        }

                        Intent intent = new Intent(InputNikSupirRetail.this, StpuKodeSortirRetail.class);
                        intent.putExtra("niksupir",input_nik_supir.getText().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();






//
//                        Toast.makeText(InputNikSupir.this," response version "+response.body().getVrmesg(),Toast.LENGTH_SHORT).show();
//
////                        Log.d("retro",""+response.body().getVerificationCode()+"|"+vurl+"|"+verificationcode);
////
////                        DatabaseHandler db = new DatabaseHandler(PickupActivity.this);
////                        db.addAgent(new AgentCode(agentcode));
//
//                        db.deleteListSumberSTPU();
//
//                        new DownloadTask(InputNikSupir.this,btnInput,vurl,"stpu");
//
//                        Intent intent = new Intent(InputNikSupir.this, StpuScanner.class);
//                        intent.putExtra("niksupir",input_nik_supir.getText().toString());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//
//
//
//                        Log.d("retro",""+vurl);


                    }else{
                        Toast.makeText(InputNikSupirRetail.this," error "+vrmsg,Toast.LENGTH_SHORT).show();
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

//    private String rundownload(){
//        DownloadTask cls = new DownloadTask(InputNikSupir.this,btnInput,Utils.downloadcsvUrl);
//        return cls.ty();
//    }


    public boolean validate() {
        boolean valid = true;

        String code = input_nik_supir.getText().toString();

        if(code.isEmpty())
        {
            input_nik_supir.setError("Masukkan Nik Supir");
            valid = false;
        } else {
            input_nik_supir.setError(null);
        }
        return valid;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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

