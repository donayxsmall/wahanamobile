package com.wahana.wahanamobile;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.AsymmetricGridViewAdapter;
import com.wahana.wahanamobile.CustomerRelation.InputNomorAgenDailyReport;
import com.wahana.wahanamobile.CustomerRelation.InputNomorAgenUpdate;
import com.wahana.wahanamobile.CustomerRelation.PengajuanAgenAR;
import com.wahana.wahanamobile.adapter.DefaultListAdapter2;
import com.wahana.wahanamobile.adapter.DefaultListAdapter5;
import com.wahana.wahanamobile.adapter.DemoAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.member.PendaftaranCalonAgen;
import com.wahana.wahanamobile.member.RincianSaldo;
import com.wahana.wahanamobile.model.DemoItem;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class ArMainActivity extends DrawerHelper implements AdapterView.OnItemClickListener {
        private static final String TAG = "MainActivity";
        private Class<?> mClss;
        private static final boolean USE_CURSOR_ADAPTER = false;
        public AsymmetricGridView listView;
        private DemoAdapter adapter;
        // nav drawer title
        private CharSequence mDrawerTitle;
        // used to store app title
        private CharSequence mTitle;
        // slide menu items
        private String[] navMenuTitles;
        private TypedArray navMenuIcons;

        private ActionBarDrawerToggle mDrawerToggle;
        public ArrayList<NavDrawerItem> navDrawerItems;
        public NavDrawerListAdapter drawerAdapter;
        private final DemoUtilsUser demoUtils = new DemoUtilsUser();
                NavigationView navigationView;
                SessionManager session;
        public static String total;
                TextView inputTTK, nama, jabatan;
                ImageView foto;
        private static final int MAP_PERMISSION = 1;
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_main);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.open_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        session = new SessionManager(ArMainActivity.this);
        mDrawerList = getDrawer();
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        DatabaseHandler db = new DatabaseHandler(this);
        inputTTK = (TextView) findViewById(R.id.label_search) ;
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        inputTTK.setTypeface(type);
        inputTTK.setText("Masukkan No TTK");

        this.setTitle("");

        if (USE_CURSOR_ADAPTER) {
            if (savedInstanceState == null) {
                //adapter = new DefaultCursorAdapter(this, demoUtils.moarItems(50));
            } else {
                //adapter = new DefaultCursorAdapter(this);
            }
        } else {
            if (savedInstanceState == null) {
                adapter = new DefaultListAdapter5(this, demoUtils.moarItems(6));
                Log.d("Hasil adapter", ""+adapter);
            } else {
                adapter = new DefaultListAdapter5(this);
                Log.d("Hasil adapter", ""+adapter);
            }
        }
//
        listView = (AsymmetricGridView) findViewById(R.id.listView);
        listView.setRequestedColumnCount(4);
        listView.setAdapter(getNewAdapter());
        listView.setDebugging(true);
        listView.setOnItemClickListener(this);
        // Setting to true will move items up and down to better use the space
        // Defaults to false.
        listView.setAllowReordering(false);
        listView.isAllowReordering(); // true
        Log.d("Hasil adapter 1", ""+adapter);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.search_ttk);
        relative.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(ArMainActivity.this,SearchActivity.class), 0);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String judul = this.getResources().getStringArray(R.array.judular)[position];
        if (judul.equals("UPDATE AGEN"))
        {
            Intent pindah = new Intent(this, InputNomorAgenUpdate.class);
            startActivity(pindah);

        }
        if (judul.equals("ABSENSI"))
        {
            launchMapActivity(Absensi.class);
//             Intent pindah = new Intent(this, Absensi.class);
//             startActivity(pindah);
        }
        if (judul.equals("VISIT AGEN"))
        {
            launchMapActivity(InputNomorAgenDailyReport.class);

        }
        if (judul.equals("CEK TARIF"))
        {
            Intent pindah = new Intent(this, CekTarifActivity.class);
            startActivity(pindah);
        }
        if (judul.equals("CUSTOMER SERVICE"))
        {
            Intent pindah = new Intent(this, CustomerServiceActivity.class);
            startActivity(pindah);
//             Intent pindah = new Intent(this, DeliveredAirways.class);
//             startActivity(pindah);
        }
        if (judul.equals("ABOUT US"))
        {
            Intent pindah = new Intent(this, AboutUsActivity.class);
            startActivity(pindah);
//             Intent pindah = new Intent(this, UndeliveredAirways.class);
//             startActivity(pindah);
        }
    }

    public void launchMapActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MAP_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MAP_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                drawerLayout.openDrawer(mDrawerList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = drawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private AsymmetricGridViewAdapter getNewAdapter() {
        return new AsymmetricGridViewAdapter(this, listView, adapter);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentOffset", demoUtils.currentOffset);
        outState.putInt("itemCount", adapter.getCount());
        for (int i = 0; i < adapter.getCount(); i++) {
            outState.putParcelable("item_" + i, (Parcelable) adapter.getItem(i));
        }
    }

    @Override protected void onRestoreInstanceState( Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        demoUtils.currentOffset = savedInstanceState.getInt("currentOffset");
        int count = 6;
        List<DemoItem> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add((DemoItem) savedInstanceState.getParcelable("item_" + i));
        }
        adapter.setItems(items);
    }

}

