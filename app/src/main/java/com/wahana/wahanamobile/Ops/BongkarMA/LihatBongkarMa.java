package com.wahana.wahanamobile.Ops.BongkarMA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetLastSess;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class LihatBongkarMa extends AppCompatActivity {
    private static final String TAG = "LihatBongkarMA";
    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar;
    Button btnInput;
    EditText niksupir,kodekantor,nosesi;
    SessionManager session;
    String username, user_id;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_bongkar_ma);

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

        db = new DatabaseHandler(LihatBongkarMa.this);

        progressDialog = new ProgressDialog(LihatBongkarMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(LihatBongkarMa.this);
        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);
        username = session.getUsername();
        user_id = session.getID();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        btnInput = (Button) findViewById(R.id.input_button);
        niksupir = (EditText)findViewById(R.id.input_nik_supir);
        kodekantor = (EditText)findViewById(R.id.input_kode_kantor);
        nosesi = (EditText)findViewById(R.id.input_nomor_sesi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(username);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }

                submitForm();


            }
        });

    }


    private void submitForm(){

        Call<opv2GetLastSess> result = mApiInterface.opv2GetLastSess(
                "opv2GetLastSess",
                getString(R.string.partnerid),
                kodekantor.getText().toString().toUpperCase(),
                niksupir.getText().toString().toUpperCase(),
                nosesi.getText().toString().toUpperCase(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2GetLastSess>() {

            @Override
            public void onResponse(Call<opv2GetLastSess> call, retrofit2.Response<opv2GetLastSess> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

                            String nomorMA = response.body().getNomorMA();


                            Intent intent = new Intent(LihatBongkarMa.this, QRcodeMa.class);
                            intent.putExtra("nomorMA",nomorMA);
                            intent.putExtra("nikLintas",niksupir.getText().toString().toUpperCase());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(LihatBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(LihatBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(LihatBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2GetLastSess> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(LihatBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }


    public boolean validate() {
        boolean valid = true;


        if(niksupir.getText().toString().isEmpty())
        {
            niksupir.setError("Masukkan Nik Supir");
            valid = false;
        } else {
            niksupir.setError(null);
        }

        if(kodekantor.getText().toString().isEmpty())
        {
            kodekantor.setError("Masukkan Kode Kantor");
            valid = false;
        } else {
            kodekantor.setError(null);
        }

        if(nosesi.getText().toString().isEmpty())
        {
            nosesi.setError("Masukkan Nomor Sesi");
            valid = false;
        } else {
            nosesi.setError(null);
        }



        return valid;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
