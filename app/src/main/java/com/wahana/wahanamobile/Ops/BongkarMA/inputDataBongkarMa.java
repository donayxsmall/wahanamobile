package com.wahana.wahanamobile.Ops.BongkarMA;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2InitBongkarMA;
import com.wahana.wahanamobile.Ops.BuatMAdirect.inputDataMaDirect;
import com.wahana.wahanamobile.Ops.STPUdirectMA.inputDataSTPUdirectMa;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class inputDataBongkarMa extends AppCompatActivity {

    private static final String TAG = "InputDataBongkarMA";
    private static final int CAMERA_REQUEST = 1888;
    private static final int SCAN_MA = 1889;



    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar;
    Button btnInput,btnClickImage,scan_ma;
    EditText niksupir,kodekantor,noseal,input_ma;
    ImageView imageView;
    SessionManager session;
    String username, user_id;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;
    String encodedImage=null;
    JSONObject jsonlokasi;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_bongkar_ma);

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

        db = new DatabaseHandler(inputDataBongkarMa.this);

        progressDialog = new ProgressDialog(inputDataBongkarMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputDataBongkarMa.this);
        username = session.getUsername();
        user_id = session.getID();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        btnInput = (Button) findViewById(R.id.input_button);
        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        niksupir = (EditText)findViewById(R.id.input_nik_supir);
        kodekantor = (EditText)findViewById(R.id.input_kode_kantor);
        noseal = (EditText)findViewById(R.id.input_no_seal);
        imageView = (ImageView) findViewById(R.id.listView);
        input_ma = (EditText) findViewById(R.id.input_ma);
        scan_ma=(Button)findViewById(R.id.scan_button);

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





        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);


        scan_ma.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });


        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }

                AlertDialog.Builder adb=new AlertDialog.Builder(inputDataBongkarMa.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah Data yang di Input Sudah Benar ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitForm();

//                        Intent intent = new Intent(inputDataBongkarMa.this, QRcodeMa.class);
//                        intent.putExtra("nomorMA","MA12345");
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();


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


        Call<opv2InitBongkarMA> result = mApiInterface.opv2InitBongkarMA(
                "opv2InitBongkarMA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir.getText().toString(),
                noseal.getText().toString(),
                String.valueOf(encodedImage),
                kodekantor.getText().toString(),
                input_ma.getText().toString(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2InitBongkarMA>() {

            @Override
            public void onResponse(Call<opv2InitBongkarMA> call, retrofit2.Response<opv2InitBongkarMA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

                            String nomorMA = response.body().getData().getNomorMA();


                            Intent intent = new Intent(inputDataBongkarMa.this, QRcodeMa.class);
                            intent.putExtra("nomorMA",nomorMA);
                            intent.putExtra("nikLintas",niksupir.getText().toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputDataBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(inputDataBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(inputDataBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2InitBongkarMA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(inputDataBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
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

        if(noseal.getText().toString().isEmpty())
        {
            noseal.setError("Masukkan Nomor Seal");
            valid = false;
        } else {
            noseal.setError(null);
        }

        if(input_ma.getText().toString().isEmpty())
        {
            input_ma.setError("Masukkan Nomor MA");
            valid = false;
        } else {
            input_ma.setError(null);
        }

        if (imageView.getDrawable() == null){
            Toast.makeText(inputDataBongkarMa.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
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

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, SCAN_MA);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    imageView.setVisibility(View.VISIBLE);
                    break;

                case SCAN_MA:
                    String nilai=data.getStringExtra("ttk");
                    input_ma.setText(nilai);
                    break;
            }

        }
    }

}
