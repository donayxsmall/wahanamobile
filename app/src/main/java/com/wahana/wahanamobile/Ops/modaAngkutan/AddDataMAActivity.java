package com.wahana.wahanamobile.Ops.modaAngkutan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import com.wahana.wahanamobile.Absensi;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.manifestSortir.ManifestSortirActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sadewa on 10/06/17.
 */

public class AddDataMAActivity extends DrawerHelper {
    private static final String TAG = "AddDataMAActivity";

    ProgressDialog progressDialog;

    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, tujuan, asal,tujuan2;
    Button btnClickImage;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    DatabaseHandler db;
    LinearLayout sealInfo;
    EditText km, seal;
    String encodedImage=null;
    String sealisi="1";
    AutoCompleteTextView nokendaraan;
    private final int REQUEST_FOR_NOMOBIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_ma);
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
                Intent intent = new Intent(AddDataMAActivity.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent intent = getIntent();
//        noKendaraan = intent.getStringExtra("no_kendaraan");
        tujuan = intent.getStringExtra("tujuan");
        asal = intent.getStringExtra("asal");
        tujuan2 = intent.getStringExtra("tujuan2");

        db = new DatabaseHandler(AddDataMAActivity.this);

        progressDialog = new ProgressDialog(AddDataMAActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(AddDataMAActivity.this);
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

        nokendaraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddDataMAActivity.this,SearchMobilActivity.class),REQUEST_FOR_NOMOBIL);
            }
        });



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(AddDataMAActivity.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

    private void submitForm() {
        if (!validateKode()) {
            return;
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ArrayList<String> ttks = db.getAllMAFix(tujuan);

        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataMA");
            json.put("kodeVia", tujuan);
            json.put("kodeAsal", asal);
            json.put("kodeMobil", nokendaraan.getText().toString());
            json.put("namaSupir", session.getName());
            JSONArray jsArray = new JSONArray(ttks);
            json.put("ms", jsArray);
            json.put("tgl", formattedDate);
            json.put("jumlah", ttks.size());
            json.put("km", km.getText().toString());
            json.put("employeeCode", user_id);
            if (sealisi.equals("1")){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] raw = out.toByteArray();
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
                json.put("noSeal", seal.getText().toString());
                json.put("fotoSeal", String.valueOf(encodedImage));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("hasil error", ""+e);
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        Log.d("hasil json", ""+json);
        new SoapClientMobile(){
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
                            Toast.makeText(AddDataMAActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
                                if (d.getJSONObject(1).getString("status").equals("1")){
//                                    db.deleteMAAll(tujuan);
                                    db.deleteMAAllFix();
                                    Intent pindah = new Intent(AddDataMAActivity.this, HasilProses.class);
                                    pindah.putExtra("proses","ma");
                                    JSONArray msd = data.getJSONArray(1);
                                    String ma = msd.getString(1);
                                    pindah.putExtra("no",ma);
//                                    pindah.putExtra("no","MA-123-123");
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(AddDataMAActivity.this, "Mohon Cek Kembali No MS Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(AddDataMAActivity.this, "Mohon Cek Kembali No MS Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(AddDataMAActivity.this, HasilError.class);
                            pindah.putExtra("proses","ma");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (final Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AddDataMAActivity.this, getString(R.string.error_message)+" "+e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }

        }.execute(parameter);
    }

    private boolean validateKode() {

        if(nokendaraan.getText().toString().trim().isEmpty()) {
            nokendaraan.setError("Masukkan No Kendaraan");

            return false;
        }

        if (sealisi.equals("1")){
            if(km.getText().toString().trim().isEmpty()) {
                km.setError("Masukkan KM Kendaraan");

                return false;
            }else if(seal.getText().toString().trim().isEmpty()){
                seal.setError("Masukkan No Seal");

                return false;
            }else if(imageView.getDrawable() == null){
                Toast.makeText(AddDataMAActivity.this, "Mohon Masukkan Foto Seal", Toast.LENGTH_SHORT).show();

                return false;
            }else{
                km.setError(null);
                seal.setError(null);
            }
        }else{
            if(km.getText().toString().trim().isEmpty()) {
                km.setError("Masukkan KM Kendaraan");

                return false;
            }else{
                km.setError(null);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_FOR_NOMOBIL:
                    String nopol = data.getStringExtra("nopol");
                    String sealId = data.getStringExtra("seal");
                    sealisi = sealId;
                    nokendaraan.setText(nopol);
                    if (sealId.equals("2")){
                        sealInfo.setVisibility(View.GONE);
                    }else{
                        sealInfo.setVisibility(View.VISIBLE);
                    }
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
