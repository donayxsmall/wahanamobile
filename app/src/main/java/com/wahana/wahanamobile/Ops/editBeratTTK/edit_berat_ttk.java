package com.wahana.wahanamobile.Ops.editBeratTTK;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK.aoSubmitEditBeratTTK;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.Camerates2;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_berat_ttk extends AppCompatActivity {

    private Button btnClickImage,btnClickImage2,btnClickImage3,btnClickImage4,btnClickImage5;
    ImageView imageView,imageView2,imageView3,imageView4,imageView5;
    private final int REQUEST_CODE_CLICK_IMAGE = 01;
    private final int REQUEST_CODE_CLICK_IMAGE2 = 02;
    private final int REQUEST_CODE_CLICK_IMAGE3 = 03;
    private final int REQUEST_CODE_CLICK_IMAGE4 = 04;
    private final int REQUEST_CODE_CLICK_IMAGE5 = 05;
    private static final String TAG = "EDITBERAT";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id,ttk;
    TextView pengisi, tgl, calendar;
    Button submit;

    String formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null,encodedImage5=null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public NavDrawerListAdapter drawerAdapter;
    TextView keterangan_label, foto_diri_label;
    DatabaseHandler db;
    EditText berattxt, panjangtxt, lebartxt, tinggitxt;
    Uri selectedImageUri = null;

    TextView nottkinfo,beratinfo,pengiriminfo,volumeinfo;
    String pengirim,berat,dimensi;

    String foto1,foto2,foto3,foto4,foto5;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_berat_ttk);

        db = new DatabaseHandler(edit_berat_ttk.this);

        Intent intent = getIntent();
        ttk = intent.getStringExtra("ttk");
        pengirim = intent.getStringExtra("pengirim");
        berat = intent.getStringExtra("berat");
        dimensi = intent.getStringExtra("dimensi");


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

        progressDialog = new ProgressDialog(edit_berat_ttk.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(edit_berat_ttk.this);
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


        nottkinfo = (TextView)findViewById(R.id.nottkinfo);
        beratinfo = (TextView) findViewById(R.id.beratinfo);
        pengiriminfo = (TextView) findViewById(R.id.pengiriminfo);
        volumeinfo = (TextView) findViewById(R.id.volumeinfo);



        nottkinfo.setText(ttk);
        beratinfo.setText(berat+" Kg");
        pengiriminfo.setText(pengirim);
        volumeinfo.setText(dimensi);

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        btnClickImage2 = (Button) findViewById(R.id.btn_submit_foto2);
        btnClickImage3 = (Button) findViewById(R.id.btn_submit_foto3);
        btnClickImage4 = (Button) findViewById(R.id.btn_submit_foto4);
        btnClickImage5 = (Button) findViewById(R.id.btn_submit_foto5);
        imageView = (ImageView) findViewById(R.id.listView);
        imageView2 = (ImageView) findViewById(R.id.listView2);
        imageView3 = (ImageView) findViewById(R.id.listView3);
        imageView4 = (ImageView) findViewById(R.id.listView4);
        imageView5 = (ImageView) findViewById(R.id.listView5);
        berattxt = (EditText) findViewById(R.id.berat);
        panjangtxt = (EditText) findViewById(R.id.panjang);
        lebartxt = (EditText) findViewById(R.id.lebar);
        tinggitxt = (EditText) findViewById(R.id.tinggi);
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_berat_ttk.this, Camerates2.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });
        btnClickImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_berat_ttk.this, Camerates2.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE2);
            }
        });
        btnClickImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_berat_ttk.this, Camerates2.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE3);
            }
        });
        btnClickImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_berat_ttk.this, Camerates2.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE4);
            }
        });
        btnClickImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_berat_ttk.this, Camerates2.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE5);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {
                    return;
                }

                AlertDialog.Builder adb=new AlertDialog.Builder(edit_berat_ttk.this);
                adb.setTitle("Ubah Berat TTK");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitForm();
                    }});
                adb.show();
            }
        });

        submit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

    }


    private void submitForm(){



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

        if(imageView4.getDrawable() != null){
            ByteArrayOutputStream out3 = new ByteArrayOutputStream();
            BitmapDrawable drawable3 = (BitmapDrawable) imageView4.getDrawable();
            Bitmap bitmap3 = drawable3.getBitmap();
            bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
            byte[] raw3 = out3.toByteArray();
            encodedImage4 = Base64.encodeToString(raw3, Base64.DEFAULT);
            foto4 = String.valueOf(encodedImage4).replace("+","?");
//            json.put("foto3", String.valueOf(encodedImage3));
        }

        if(imageView5.getDrawable() != null){
            ByteArrayOutputStream out3 = new ByteArrayOutputStream();
            BitmapDrawable drawable3 = (BitmapDrawable) imageView5.getDrawable();
            Bitmap bitmap3 = drawable3.getBitmap();
            bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
            byte[] raw3 = out3.toByteArray();
            encodedImage5 = Base64.encodeToString(raw3, Base64.DEFAULT);
            foto5 = String.valueOf(encodedImage5).replace("+","?");
//            json.put("foto3", String.valueOf(encodedImage3));
        }

        Call<aoSubmitEditBeratTTK> result = mApiInterface.aoSubmitEditBeratTTK(
                "aoSubmitEditBeratTTK",
                getString(R.string.partnerid),
                session.getID(),
                ttk,
                berattxt.getText().toString(),
                panjangtxt.getText().toString(),
                lebartxt.getText().toString(),
                tinggitxt.getText().toString(),
                foto1,
                foto2,
                foto3,
                foto4,
                foto5,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<aoSubmitEditBeratTTK>() {

            @Override
            public void onResponse(Call<aoSubmitEditBeratTTK> call, Response<aoSubmitEditBeratTTK> response) {


                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    Log.d("hasil",""+response.body().getCode());


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {


                            Intent pindah = new Intent(edit_berat_ttk.this, HasilProses.class);
                            pindah.putExtra("proses","editberatttk");
                            pindah.putExtra("no",ttk);
                            pindah.putExtra("beratsebelum",response.body().getBeratsebelum());
                            pindah.putExtra("volumesebelum",response.body().getDimensisebelum());
                            pindah.putExtra("beratsesudah",response.body().getBeratsesudah());
                            pindah.putExtra("volumesesudah",response.body().getDimensisesudah());
                            startActivity(pindah);
                            finish();



                        } else {

//

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(edit_berat_ttk.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();


                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(edit_berat_ttk.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(edit_berat_ttk.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoSubmitEditBeratTTK> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(edit_berat_ttk.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });






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

                case REQUEST_CODE_CLICK_IMAGE4:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp4 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView4.setImageBitmap(bmp4);
                    imageView4.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_CLICK_IMAGE5:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp5 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView5.setImageBitmap(bmp5);
                    imageView5.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }



    public boolean validate() {
        boolean valid = true;

        if (berattxt.getText().toString().isEmpty()){
            berattxt.setError("Masukkan Berat Aktual");
            valid = false;
        }

        if (panjangtxt.getText().toString().isEmpty()){
            panjangtxt.setError("Masukkan Panjang");
            valid = false;
        }

        if (lebartxt.getText().toString().isEmpty()){
            lebartxt.setError("Masukkan Lebar");
            valid = false;
        }

        if (tinggitxt.getText().toString().isEmpty()){
            tinggitxt.setError("Masukkan Tinggi");
            valid = false;
        }

        if (imageView.getDrawable() == null){
            Toast.makeText(edit_berat_ttk.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (imageView2.getDrawable() == null){
            Toast.makeText(edit_berat_ttk.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (imageView3.getDrawable() == null){
            Toast.makeText(edit_berat_ttk.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (imageView4.getDrawable() == null){
            Toast.makeText(edit_berat_ttk.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (imageView5.getDrawable() == null){
            Toast.makeText(edit_berat_ttk.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }






}
