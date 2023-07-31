package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.ListSubMenuAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class ListSubMenuActivity extends DrawerHelper {
    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    ListView listTTK;
    DatabaseHandler db;
    List<String> menuList, urlList;
    ListSubMenuAdapter adapter;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, submenuIsi, urlIsi;
    TextView nama,jabatan,submenu,url;
    ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sub_menu);
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
        ImageView imageButton = (ImageView) mCustomView.findViewById(R.id.wahana_logonew2018);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListSubMenuActivity.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(ListSubMenuActivity.this);
        progressDialog = new ProgressDialog(ListSubMenuActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(ListSubMenuActivity.this);
        user_id = session.getID();
        populate();

        listTTK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                submenu = (TextView) view.findViewById(R.id.no_ttk_isi);
                url = (TextView) view.findViewById(R.id.url_isi);
                submenuIsi = submenu.getText().toString();
                urlIsi = url.getText().toString();
                getUrl(urlIsi);
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

    private void populate(){
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        String getMenu = (String) bd.get("menu");
        if ("AKTIVITAS KARYAWAN".equals(getMenu) )
        {
                menuList = new ArrayList<String>();
                menuList.add("My Info");
                menuList.add("Cuti");
                menuList.add("Sakit");
                menuList.add("Ijin");
                menuList.add("Cuti Melahirkan");
                menuList.add("Cuti Keguguran");
                menuList.add("Unpaid Leave");
//                menuList.add("History Notifikasi");
                urlList = new ArrayList<String>();
                urlList.add("https://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=view/vnm.myinfo2&noframe=1&bacd="+session.getID());
                urlList.add("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_PCT_m&noframe=1&iswv=1");
//                urlList.add("https://wahana.com/hubcs4.html");
                urlList.add("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_SKT_m&noframe=1&iswv=1");
                urlList.add("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_PIJ_m&noframe=1&iswv=1");
//                urlList.add("http://intranet.wahana.com/ci-oauth2/home/historyNotif?user="+session.getUsername());
                urlList.add("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_Cuti_lahir&noframe=1&iswv=1");
                urlList.add("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_Cuti_Keguguran&noframe=1&iswv=1");
                urlList.add("https://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_UnpaidLeave&noframe=1&iswv=1");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
        }

        if ("TTK".equals(getMenu)){
            if (session.getRoleID().equals("32")) {
                menuList = new ArrayList<String>();
                menuList.add("Buat TTK Internal");
                menuList.add("Penerimaan TTK Internal");
                menuList.add("TTK Balik");
                menuList.add("Penerimaan TTK Balik");
                menuList.add("Penerimaan TTK Retur");
                menuList.add("TTK Keranjang");
                menuList.add("Penerimaan TTK Keranjang");
                urlList = new ArrayList<String>();
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else if (session.getRoleID().equals("33")) {
                menuList = new ArrayList<String>();
                menuList.add("Penerimaan TTK Internal");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        if ("KANTOR DELIVERY".equals(getMenu) ){
            if (session.getRoleID().equals("32")) {
                menuList = new ArrayList<String>();
                menuList.add("Status SJ");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        if ("SORTING CENTRE".equals(getMenu) ){
            if (session.getRoleID().equals("29")) {
                menuList = new ArrayList<String>();
                menuList.add("Serah Terima Manifest Agen/Account");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        if ("GATEWAY".equals(getMenu) ){
            if (session.getRoleID().equals("30")) {
                menuList = new ArrayList<String>();
                menuList.add("Track MS Untuk Serah Terima");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else if (session.getRoleID().equals("32")) {
                menuList = new ArrayList<String>();
                menuList.add("Track MS Untuk Serah Terima");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else if (session.getRoleID().equals("35")) {
                menuList = new ArrayList<String>();
                menuList.add("Track MS Untuk Serah Terima");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }


        if ("LAPORAN BIAYA".equals(getMenu) ){
            if (session.getRoleID().equals("33")) {
                menuList = new ArrayList<String>();
                menuList.add("Buat Laporan Biaya");
                urlList = new ArrayList<String>();
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }


        if ("MONITORING".equals(getMenu) ){
            if (session.getRoleID().equals("33")) {
                menuList = new ArrayList<String>();
                menuList.add("Monitoring MS Pickup per Kurir");
                menuList.add("Monitoring Status TTK Sudah Pickup");
                menuList.add("Monitoring Status TTK Sudah ST Pickup (by Tujuan)");
                menuList.add("Monitoring Status TTK Sudah ST Pickup (by Aktif)");
                menuList.add("Monitoring Status TTK Sudah ST Pickup (by Verifikasi)");
                menuList.add("Monitoring Status TTK Sudah ST Pickup (by Total)");
                menuList.add("Monitoring Status TTK Sudah ST Pickup (by Persentase KPI)");
                urlList = new ArrayList<String>();
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }


        if ("OPERASIONAL".equals(getMenu) ){
            if (session.getRoleID().equals("33")) {
                menuList = new ArrayList<String>();
                menuList.add("Ringkasan Proses Pickup");
                menuList.add("Update status TTK PIC");
                urlList = new ArrayList<String>();
                urlList.add("");
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        if ("INFORMASI LAPORAN".equals(getMenu) ){
            if (session.getRoleID().equals("33")) {
                menuList = new ArrayList<String>();
                menuList.add("Daftar Manifest Agen");
                menuList.add("Daftar Serah Terima Manifest");
                urlList = new ArrayList<String>();
                urlList.add("");
                urlList.add("");
                adapter = new ListSubMenuAdapter(ListSubMenuActivity.this, menuList, urlList);
                listTTK.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }




    }

    public void getUrl(String url){
        if (url.equals("")){
            Toast.makeText(this, "Menu Belum Tersedia", Toast.LENGTH_SHORT).show();
        }else{
            Intent i = new Intent(this, ReportPageActivity.class);
            i.putExtra("url", url);
            startActivity(i);
        }
    }

}