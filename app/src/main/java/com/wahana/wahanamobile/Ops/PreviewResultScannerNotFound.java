package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.RecycleViewTTKnotFound;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import java.util.List;

public class PreviewResultScannerNotFound extends AppCompatActivity {

    private static final String TAG = "ttknotfound";
    ProgressDialog progressDialog;
    SessionManager session;
    ListView listView;
    Button inputcancel,inputbutton;
    DatabaseHandler db;
    List<ListTtkPickup> ttklistnotfound;
    RecycleViewTTKnotFound adapter;
    int urut;
    String asal,noma,ms;
    Intent intent;
    TextView judul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_result_scanner_not_found);

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
//                Intent intent = new Intent(ListTTKnotFound.this, OpsMainActivity.class);
//                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        intent = getIntent();
        asal = intent.getStringExtra("asal");
        noma = intent.getStringExtra("ma");
        ms = intent.getStringExtra("ms");

        db = new DatabaseHandler(PreviewResultScannerNotFound.this);


        progressDialog = new ProgressDialog(PreviewResultScannerNotFound.this, R.style.AppTheme_Dark_Dialog);

        session = new SessionManager(PreviewResultScannerNotFound.this);

        listView=(ListView) findViewById(R.id.listView);
        judul = (TextView) findViewById(R.id.judul);

//        inputbutton=(Button)findViewById(R.id.input_button);
//        inputcancel=(Button)findViewById(R.id.input_cancel);


        populate();

    }

    private void populate(){

        if(asal.equals("stsm")){
            ttklistnotfound=db.getAllDataPickupPreviewMStidakditemukan();
            judul.setText("MS Tidak di Scan");
        }else if(asal.equals("stms")){
            ttklistnotfound=db.getAllDataPreviewTTKSTMStidakditemukan(ms);
            judul.setText("TTK Tidak di Scan");
        }


        adapter = new RecycleViewTTKnotFound(PreviewResultScannerNotFound.this, ttklistnotfound, urut);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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


}
