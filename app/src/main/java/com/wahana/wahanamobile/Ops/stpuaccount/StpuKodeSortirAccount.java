package com.wahana.wahanamobile.Ops.stpuaccount;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.Pickup.PickupAgenNew;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StpuKodeSortirAccount extends AppCompatActivity {

    private static final String TAG = "StpuKodeSortir";
    ProgressDialog progressDialog;
    SessionManager session;
    DatabaseHandler db;
    public EditText inputkodesortir;
    private Button btnInput;
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
    String username, user_id,agentcode;
    UserAPIService mApiInterface;
    String kodeverifikasi,niksupir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stpu_kode_sortir_account);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(StpuKodeSortirAccount.this));

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

        Intent intent = getIntent();
        niksupir=intent.getStringExtra("niksupir");

        db = new DatabaseHandler(StpuKodeSortirAccount.this);

        mApiInterface = ApiClient.getClient().create(UserAPIService.class);

        progressDialog = new ProgressDialog(StpuKodeSortirAccount.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(StpuKodeSortirAccount.this);
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


        inputkodesortir = (EditText) findViewById(R.id.input_kodesortir);
        this.setTitle("");
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        inputkodesortir.setTypeface(type);


        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });



    }


    public void submitForm(){
        if (!validate()) {
            return;
        }

        Call<ApiPickup> result = mApiInterface.genTTKwithKodeSortirSTPUAccount(niksupir,inputkodesortir.getText().toString());

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
                    String vFile=response.body().getvFile();

                    Log.d("retro",""+vurl);



                    if(vrcode.equals("1")){

                        db.deleteListSumberSTPU();

                        new DownloadTask(StpuKodeSortirAccount.this,btnInput,vurl,"stpuAccount",vFile);

                        int histPickup=db.checkListSTPUHistory(niksupir,inputkodesortir.getText().toString());

                        int jumlahscan=db.countSTPUlistScan(niksupir,inputkodesortir.getText().toString());

                        if(histPickup==1){

                            ListTtkPickup ttk = db.getTTKhistoryMaxSTPU(niksupir,inputkodesortir.getText().toString());

                            AlertDialog.Builder adb=new AlertDialog.Builder(StpuKodeSortirAccount.this);
                            adb.setTitle("Info");
                            adb.setMessage("Proses STPU terakhir belum selesai , Sudah di scan "+jumlahscan+" ttk dengan nomor ttk terakhir "+ttk.getTtk()+", Apakah anda ingin melanjutkan?");
                            adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteListSTPU(niksupir,inputkodesortir.getText().toString());

                                    Intent intent = new Intent(StpuKodeSortirAccount.this, StpuScannerHardwareAccount.class);
                                    intent.putExtra("niksupir",niksupir);
                                    intent.putExtra("kodesortir",inputkodesortir.getText().toString());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }});
                            adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //db.deleteListPickup();
                                    Intent intent = new Intent(StpuKodeSortirAccount.this, StpuScannerHardwareAccount.class);
                                    intent.putExtra("niksupir",niksupir);
                                    intent.putExtra("kodesortir",inputkodesortir.getText().toString());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }});

                            adb.show();
                        }else{

                            Intent intent = new Intent(StpuKodeSortirAccount.this, StpuScannerHardwareAccount.class);
                            intent.putExtra("niksupir",niksupir);
                            intent.putExtra("kodesortir",inputkodesortir.getText().toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }


                    }else{
                        Toast.makeText(StpuKodeSortirAccount.this,vrmsg,Toast.LENGTH_SHORT).show();
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


    public boolean validate() {
        boolean valid = true;

        String code = inputkodesortir.getText().toString();

        if(code.isEmpty())
        {
            inputkodesortir.setError("Masukkan Kode Sortir");
            valid = false;
        } else {
            inputkodesortir.setError(null);
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





}
