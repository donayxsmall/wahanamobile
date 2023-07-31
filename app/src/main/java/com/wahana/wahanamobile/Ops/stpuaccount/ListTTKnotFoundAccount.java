package com.wahana.wahanamobile.Ops.stpuaccount;

import android.app.ProgressDialog;
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

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.RecycleViewTTKnotFound;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import java.util.List;

import com.wahana.wahanamobile.R;

public class ListTTKnotFoundAccount extends AppCompatActivity {

    private static final String TAG = "ttknotfound";
    ProgressDialog progressDialog;
    SessionManager session;
    ListView listView;
    Button inputcancel,inputbutton;
    DatabaseHandler db;
    List<ListTtkPickup> ttklistnotfound;
    RecycleViewTTKnotFound adapter;
    int urut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ttknot_found);

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

        db = new DatabaseHandler(ListTTKnotFoundAccount.this);


        progressDialog = new ProgressDialog(ListTTKnotFoundAccount.this, R.style.AppTheme_Dark_Dialog);

        session = new SessionManager(ListTTKnotFoundAccount.this);

        listView=(ListView) findViewById(R.id.listView);
//        inputbutton=(Button)findViewById(R.id.input_button);
//        inputcancel=(Button)findViewById(R.id.input_cancel);


        populate();


//        inputbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder adb=new AlertDialog.Builder(ListTTKnotFound.this);
//                adb.setTitle("Info");
//                adb.setMessage("Apakah anda yakin ingin pickup ?");
//                adb.setNegativeButton("Cancel", null);
//                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
////                        updatedata();
//                    }});
//                adb.show();
//            }
//        });

//        inputcancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent pindah = new Intent(ListTTKnotFound.this, StpuScanner.class);
////                pindah.putExtra("asal", "stpu");
////                pindah.putExtra("niksupir", niksupir);
////                pindah.putExtra("kodesortir", kodesortir);
//                startActivity(pindah);
//            }
//        });



    }



    private void populate(){

        ttklistnotfound=db.getAllDataSTPUnotFound();

        adapter = new RecycleViewTTKnotFound(ListTTKnotFoundAccount.this, ttklistnotfound, urut);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

//        adapter = new RecycleViewTTKnotFound(ttklistnotfound);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListTTKnotFound.this);
////
//        listView.setLayoutManager(layoutManager);
//
////        listView.setLayoutManager(new LinearLayoutManager(this));
//        listView.setAdapter(adapter);
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

