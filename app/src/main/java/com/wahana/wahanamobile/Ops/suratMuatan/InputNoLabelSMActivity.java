package com.wahana.wahanamobile.Ops.suratMuatan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sadewa on 09/06/17.
 */

public class InputNoLabelSMActivity extends DrawerHelper {
    private static final String TAG = "InputNoLabelActivity";
    ProgressDialog progressDialog;

    String proses, no;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
    SessionManager session;
    String username, user_id;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button scan;
    EditText label;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        no = myIntent.getStringExtra("no");
        setContentView(R.layout.activity_scan_label_ms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InputNoLabelSMActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(InputNoLabelSMActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNoLabelSMActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        manifest_label = (TextView) findViewById(R.id.manifest_label);
        manifest_isi = (TextView) findViewById(R.id.manifest_isi);
        hasil_manifest = (TextView) findViewById(R.id.hasil_manifest);
        scan = (Button) findViewById(R.id.scan_button);
        label = (EditText) findViewById(R.id.input_ttk);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        manifest_label.setTypeface(type);
        manifest_isi.setTypeface(type,Typeface.BOLD);
        hasil_manifest.setTypeface(type);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        pengisi.setText(user_id);
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });

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

    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
        finish();
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
            label.setText(nilai);
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
