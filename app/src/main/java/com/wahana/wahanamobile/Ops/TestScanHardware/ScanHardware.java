package com.wahana.wahanamobile.Ops.TestScanHardware;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.Ops.suratJalan.SuratJalanScannerActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ScanHardware extends AppCompatActivity {

    ListView listTTK;
    DatabaseHandler db;
    List<BuatSJ> ttkList;
    BuatSJAdapter adapter;
    TextView jumlahTTK,jumlah;
    Button previewTTK,submit;
    String ttkManual,kurir,via,employeeCode;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    String barcode = "";
    EditText inputscan,input_nik;
    Button input_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_hardware);


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
                Intent intent = new Intent(ScanHardware.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        input_nik=(EditText)findViewById(R.id.input_nik);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        jumlah=(TextView)findViewById(R.id.jumlah);
        db = new DatabaseHandler(ScanHardware.this);

        input_button=(Button)findViewById(R.id.input_button);

        db.deleteBuatSJ();


        populate();


//        input_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mySong = MediaPlayer.create(ScanHardware.this, R.raw.success);
//                BuatSJ sj = new BuatSJ();
//                sj.setTtk(input_nik.getText().toString());
//                db.addBuatSJ(sj);
//                populate();
//
//                input_nik.setText("");
//            }
//        });
//
        input_nik.setEnabled(false);
//        input_nik.setFocusable(true);
//
//        input_nik.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    Toast.makeText(ScanHardware.this, input_nik.getText().toString(), Toast.LENGTH_SHORT).show();
//
//                    mySong = MediaPlayer.create(ScanHardware.this, R.raw.success);
//                    BuatSJ sj = new BuatSJ();
//                    sj.setTtk(input_nik.getText().toString());
//                    db.addBuatSJ(sj);
//                    populate();
//
//                    input_nik.setText("");
//
//
//
//
//
//                    return true;
//                }
//                return false;
//            }
//        });


    }


    private void populate(){
        urut = urut +1;
//        ttkList.clear();
        ttkList = db.getAllTtkSJ();
        String textJumlah = Integer.toString(ttkList.size());
        jumlah.setText(textJumlah+"/30");
        adapter = new BuatSJAdapter(ScanHardware.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if(e.getAction()==KeyEvent.ACTION_DOWN){
            Log.i(TAG,"dispatchKeyEvent: "+e.toString());
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//            Toast.makeText(getApplicationContext(),
//                    "barcode--->>>" + barcode, Toast.LENGTH_LONG)
//                    .show();

            barcode = barcode.replaceAll("\n","");

//            Toast.makeText(getApplicationContext(), "TTK"+barcode, Toast.LENGTH_SHORT).show();

//            int ttk = db.checkSJ(barcode);

//            Toast.makeText(getApplicationContext(), "TTK1 "+ttk, Toast.LENGTH_SHORT).show();
//
            input_nik.setText(barcode);
            ttkManual=input_nik.getText().toString();
            yu(ttkManual);

////        if (!isTTK(rawResult.getContents().toUpperCase())) {
////            mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
////            mySong.start();
////            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
////        }
//            if (ttk==0){
//                submitForm(barcode);
//            }else {
//                mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
//                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                mySong.start();
//            }

            barcode="";
        }

        return super.dispatchKeyEvent(e);
    }

    public void yu(String bar){

//        int ttk = db.checkSJ(bar);

        Toast.makeText(getApplicationContext(), "TTK1 "+bar, Toast.LENGTH_SHORT).show();

//        input_nik.setText(bar);

        mySong = MediaPlayer.create(ScanHardware.this, R.raw.success);
        BuatSJ sj = new BuatSJ();
        sj.setTtk(bar);
        db.addBuatSJ(sj);
        populate();
    }
}
