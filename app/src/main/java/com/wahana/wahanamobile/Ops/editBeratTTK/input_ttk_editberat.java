package com.wahana.wahanamobile.Ops.editBeratTTK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK.aoCekTTK;
import com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK.aoCekTTK_Result;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class input_ttk_editberat extends AppCompatActivity {

    private static final String TAG = "input ttk edit berat";

    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String user_id;
    Button scan;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ttk_editberat);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(input_ttk_editberat.this));

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

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        progressDialog = new ProgressDialog(input_ttk_editberat.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(input_ttk_editberat.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        scan = (Button) findViewById(R.id.scan_button);

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

        inputVerifikasi = (EditText) findViewById(R.id.input_ttk);
        this.setTitle("");

        inputVerifikasi.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });

    }


    private void submitForm(){

        if (!validateKode()) {
            return;
        }


        Call<aoCekTTK> result = mApiInterface.aoCekTTK(
                "aoCekTTK",
                getString(R.string.partnerid),
                session.getID(),
                inputVerifikasi.getText().toString(),
                session.getKeyTokenJWT()
//                "213213213213"
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<aoCekTTK>() {

            @Override
            public void onResponse(Call<aoCekTTK> call, Response<aoCekTTK> response) {


                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    Log.d("hasil",""+response.body().getCode());


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {

//                            Log.d("code",""+response.body().getText());

                            aoCekTTK data = response.body();
                            List<aoCekTTK_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String nottk = dataList.get(0).getTtk();
                            String pengirim = dataList.get(0).getPengirim();
                            String berat = dataList.get(0).getBerat();
                            String dimensi = dataList.get(0).getDimensi();

                            Intent pindah = new Intent(input_ttk_editberat.this, edit_berat_ttk.class);
                            pindah.putExtra("ttk",nottk);
                            pindah.putExtra("pengirim",pengirim);
                            pindah.putExtra("berat",berat);
                            pindah.putExtra("dimensi",dimensi);
                            startActivity(pindah);
                            finish();


                        } else {

//

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(input_ttk_editberat.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();


                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(input_ttk_editberat.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(input_ttk_editberat.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoCekTTK> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(input_ttk_editberat.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });










    }


    private boolean validateKode() {
        if (inputVerifikasi.getText().toString().trim().isEmpty()) {
            inputVerifikasi.setError("Masukkan No. TTK");

            return false;
        } else {
            inputVerifikasi.setError(null);
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
        if(resultCode == RESULT_OK){
            String nilai=data.getStringExtra("ttk");
            inputVerifikasi.setText(nilai);
        }
    }
}
