package com.wahana.wahanamobile.Ops.modaAngkutan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.manifestSortir.ManifestSortirActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.Ops.modaAngkutan.FormAddKendaraanMaActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sadewa on 10/06/17.
 */

public class AddKendaraanActivity extends DrawerHelper {
    private static final String TAG = "AddKendaraanActivity";

    private Button btnInput;
    TextView pengisi, tgl, calendar,id_mobil;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id,coba;
    DatabaseHandler db;
    List<BuatSJ> kendaraanList;
    ListView lv;
    AutoCompleteTextView pilihmobil;
    BuatSJAdapter adapter;
    private final int REQUEST_FOR_VEHICLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_ma);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(AddKendaraanActivity.this);
        kendaraanList = db.getAllKendaraan();
        session = new SessionManager(AddKendaraanActivity.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);
        lv = (ListView) findViewById(R.id.list_kendaraan);
        populate();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

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


        btnInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(new Intent(AddKendaraanActivity.this,FormAddKendaraanMaActivity.class));

//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddKendaraanActivity.this);
//                alertDialog.setTitle("Input No. Kendaraan");
//                alertDialog.setMessage("Masukkan No. Kendaraan");
//
//                final EditText input = new EditText(AddKendaraanActivity.this);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                input.setLayoutParams(lp);
//                alertDialog.setView(input);
//
//                alertDialog.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                String no = input.getText().toString().toUpperCase();
//                                kendaraanList = db.getAllTtkMS();
//                                int ttk = db.checkKendaraan(no);
//                                Log.d("Hasil", ""+ttk);
//                                if (ttk==0){
//                                    BuatSJ sj = new BuatSJ();
//                                    sj.setTtk(no);
//                                    db.addKendaraan(sj);
//                                    populate();
//                                }else{
//                                    Toast.makeText(getApplicationContext(), "Kendaraan Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                alertDialog.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                input.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                alertDialog.show();

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView nottk = (TextView)view.findViewById(R.id.no_ttk_isi);
                final TextView tujuan = (TextView)view.findViewById(R.id.tujuan);
                AlertDialog.Builder adb=new AlertDialog.Builder(AddKendaraanActivity.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete kendaraan " + nottk.getText().toString());
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        String tuju = tujuan.getText().toString();
                        String tujuan1 = tuju.replaceAll("\\p{P}","");
                        db.deleteKendaraan(nottk.getText().toString(),tujuan1);
                        populate();
                    }});
                adb.show();
                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView nokendaraan = (TextView)view.findViewById(R.id.no_ttk_isi);
                final TextView tujuan = (TextView)view.findViewById(R.id.tujuan);
                Intent pindah = new Intent(AddKendaraanActivity.this, ScanMAActivity.class);
                String tuju = tujuan.getText().toString();
                String tujuan1 = tuju.replaceAll("\\p{P}","");
                pindah.putExtra("no_kendaraan", nokendaraan.getText().toString());
                pindah.putExtra("tujuan", tujuan1);
                startActivity(pindah);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }

    private void populate(){
        kendaraanList.clear();
        kendaraanList = db.getAllKendaraan();
        adapter = new BuatSJAdapter(AddKendaraanActivity.this, kendaraanList, "ma");
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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