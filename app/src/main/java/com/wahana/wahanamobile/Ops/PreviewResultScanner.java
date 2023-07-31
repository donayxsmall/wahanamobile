package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.Data.ListMsSTSM;
import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Data.ListTtkSTPUBERAT;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.Data.TtkKeranjangRetur;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.LihatPreviewAdapter;
import com.wahana.wahanamobile.adapter.LihatPreviewAdapterBongkarMA;
import com.wahana.wahanamobile.adapter.LihatPreviewAdapterPU;
import com.wahana.wahanamobile.adapter.LihatPreviewAdapterSTPUBERAT;
import com.wahana.wahanamobile.adapter.LihatPreviewAdapterSTPUMA;
import com.wahana.wahanamobile.adapter.LihatPreviewTtkKeranjangRetur;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataListProsesBongkarMA;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class PreviewResultScanner extends DrawerHelper {
    ProgressDialog progressDialog;

    private TextInputLayout inputLayoutVerifikasi;
    public EditText inputVerifikasi;
    private Button btnInput;
    ListView listTTK;
    DatabaseHandler db;
    List<BuatSJ> ttkList;
    List<ListMsSTSM> ttkListSTSM;

    LihatPreviewAdapter adapter;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;

    List<ListTtkPickup> ttkListPU;
    LihatPreviewAdapterPU adapterPu;

    List<ListTtkPickup> ttklistnotfound;

    List<TtkKeranjangRetur> ttkListkeranjang;
    LihatPreviewTtkKeranjangRetur adapterKeranjang;

    List<ListTtkSTPUMA> ttkListstpuma;
    LihatPreviewAdapterSTPUMA adapterStpuMA;

    List<ListTtkBongkarMA> ttkListbongkarma;
    LihatPreviewAdapterBongkarMA adapterBongkarMA;

    List<ListTtkSTPUBERAT> ttkListstpuberat;
    LihatPreviewAdapterSTPUBERAT adapterStpuBERAT;

    SessionManager session;
    String username, user_id, asal,noKendaraan,niksupir,kodeverifikasi,kodeagen,kodesortir,flagsortir,ma,ms,nomorMA;
    TextView nama,jabatan;
    ImageView foto;
    int urut;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_result_scanner);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(PreviewResultScanner.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        intent = getIntent();
        asal = intent.getStringExtra("asal");
        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(PreviewResultScanner.this);

        niksupir=intent.getStringExtra("niksupir");
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        kodeagen=intent.getStringExtra("kodeagen");
        kodesortir=intent.getStringExtra("kodesortir");
        flagsortir=intent.getStringExtra("flagsortir");

        ma=intent.getStringExtra("ma");
        ms=intent.getStringExtra("ms");

        nomorMA=intent.getStringExtra("nomorMA");





        progressDialog = new ProgressDialog(PreviewResultScanner.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PreviewResultScanner.this);
        user_id = session.getID();

        populate();

        Log.d("hy",""+session.getID()+" "+nomorMA);




        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");



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
    private void populate() {


        if (asal.equals("stmsv2")) {
            ttkList = db.getAllTtkSTMSV2(ms);
        } else if (asal.equals("sj")) {
            ttkList = db.getAllTtkSJ();
        } else if (asal.equals("sjp")) {
            ttkList = db.getAllTtkSJP();
        } else if (asal.equals("stsm")) {
            ttkList = db.getAllTtkSTSMV2(ma);
        } else if (asal.equals("ms")) {
            ttkList = db.getAllTtkMS();
        } else if (asal.equals("sm")) {
            ttkList = db.getAllTtkSM();
        } else if (asal.equals("stbt")) {
            ttkList = db.getAllTtkSTBT();
        } else if (asal.equals("stk")) {
            ttkList = db.getAllTtkSTK();
        }else if (asal.equals("sw")) {
            ttkList = db.getAllTtkSTMS();
        } else if (asal.equals("ma")) {
//            noKendaraan = intent.getStringExtra("noKendaraan");
            String tujuan = intent.getStringExtra("tujuan");
            ttkList = db.getAllMA(tujuan);
        } else if (asal.equals("pu")) {

            if (flagsortir.equals("1")) {
                ttkListPU = db.getAllDataPickupPreview(kodeagen, kodeverifikasi, kodesortir);
            } else {
                ttkListPU = db.getAllDataPickupPreviewAll(kodeagen, kodeverifikasi);
            }


//            ttklistnotfound = db.getAllDataPickupPreviewTTKtidakditemukan();
        } else if (asal.equals("stpu")) {
            ttkListPU = db.getAllDataSTPUPreview(niksupir, kodesortir);
        } else if (asal.equals("ttkreturkeranjang")) {
            ttkListkeranjang = db.getAllTTKKeranjangRetur();
        } else if (asal.equals("stpuma")) {
            ttkListstpuma = db.getAllDataSTPUMAPreview(niksupir, kodesortir);
        } else if (asal.equals("bongkarma")) {
            ttkListbongkarma = db.getAllDataBONGKARMAPreview(user_id, nomorMA);
        } else if (asal.equals("stpuberat")) {
            ttkListstpuberat = db.getAllDataSTPUBERATPreview(niksupir, kodesortir);
        }


        if (asal.equals("pu") || asal.equals("stpu")) {
            Log.d("Hasil", "" + ttkListPU);
            adapterPu = new LihatPreviewAdapterPU(PreviewResultScanner.this, ttkListPU, urut);
            listTTK.setAdapter(adapterPu);
            adapterPu.notifyDataSetChanged();
        } else if (asal.equals("ttkreturkeranjang")) {
            adapterKeranjang = new LihatPreviewTtkKeranjangRetur(PreviewResultScanner.this, ttkListkeranjang, urut);
            listTTK.setAdapter(adapterKeranjang);
            adapterKeranjang.notifyDataSetChanged();

        } else if (asal.equals("stpuma")) {
            adapterStpuMA = new LihatPreviewAdapterSTPUMA(PreviewResultScanner.this, ttkListstpuma, urut);
            listTTK.setAdapter(adapterStpuMA);
            adapterStpuMA.notifyDataSetChanged();
        } else if (asal.equals("bongkarma")) {
            adapterBongkarMA = new LihatPreviewAdapterBongkarMA(PreviewResultScanner.this, ttkListbongkarma, urut);
            listTTK.setAdapter(adapterBongkarMA);
            adapterBongkarMA.notifyDataSetChanged();

        }else if (asal.equals("stpuberat")) {
            adapterStpuBERAT = new LihatPreviewAdapterSTPUBERAT(PreviewResultScanner.this, ttkListstpuberat, urut);
            listTTK.setAdapter(adapterStpuBERAT);
            adapterStpuBERAT.notifyDataSetChanged();

        }else {
            Log.d("Hasil", "" + ttkList);
            adapter = new LihatPreviewAdapter(PreviewResultScanner.this, ttkList, urut);
            listTTK.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


    }

}

