package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.AsymmetricGridViewAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.Data.User;
import com.wahana.wahanamobile.Ops.ApprovalSJ.ListApprovalSJ;
import com.wahana.wahanamobile.Ops.ApprovalSJ.SearchSjApprovalNik;
import com.wahana.wahanamobile.Ops.BarcodeScanner;
import com.wahana.wahanamobile.Ops.ListSubMenuActivity;
import com.wahana.wahanamobile.Ops.Pickup.PickupAgenNew;
import com.wahana.wahanamobile.Ops.ReportPageActivity;
import com.wahana.wahanamobile.Ops.SJP.InputNamaPenerus;
import com.wahana.wahanamobile.Ops.STBT.InputNikKurirSTBT;
import com.wahana.wahanamobile.Ops.STK.StkScannerActivity;
import com.wahana.wahanamobile.Ops.editSJP.inpuSJPActivity;
import com.wahana.wahanamobile.Ops.inputSMU.inputMAActivity;
import com.wahana.wahanamobile.Ops.modaAngkutan.InputKodeVia;
import com.wahana.wahanamobile.Ops.penerimaanRetur.inputTTKReturActivity;
import com.wahana.wahanamobile.Ops.rejectxray.inputttkrejectxray;
import com.wahana.wahanamobile.Ops.salahdestinasi.inputtksalahdestinasi;
import com.wahana.wahanamobile.Ops.salahtempel.inputtksalahtempel;
import com.wahana.wahanamobile.Ops.selisihBerat.inputTTKSelisihBerat;
import com.wahana.wahanamobile.Ops.stms.inputNoLabelMSActivity;
import com.wahana.wahanamobile.Ops.stpu.InputNikSupir;
import com.wahana.wahanamobile.Ops.stsm.SerahTerimaSMActivity;
import com.wahana.wahanamobile.Ops.suratJalan.InputNIKKurir;
import com.wahana.wahanamobile.Ops.tandaiRetur.inputTTKActivity;
import com.wahana.wahanamobile.adapter.AdapterPelaksanaDelivery;
import com.wahana.wahanamobile.adapter.DemoAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.GPSTracker;
import com.wahana.wahanamobile.helper.GooglePlayServicesHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataSJ;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientLogin;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class OpsMainActivityBackup2 extends DrawerHelper implements AdapterView.OnItemClickListener{
//    public class OpsMainActivity extends DrawerHelper implements AdapterView.OnItemClickListener,
//            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private static final String TAG = "OpsMainActivity";
    private Class<?> mClss;
    private static final boolean USE_CURSOR_ADAPTER = false;
    public AsymmetricGridView listView;
    private DemoAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;

    private CharSequence mTitle;

    List<String> teksMenu;
    private TypedArray navMenuIcons;

    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private final DemoUtils demoUtils = new DemoUtils();
    NavigationView navigationView;
    SessionManager session;
    public static String total;
    TextView inputTTK, nama, jabatan;
    ImageView foto;
    private static final int MAP_PERMISSION = 1;
    ProgressDialog progressDialog;
    DatabaseHandler db;
    List<OfflineTTK> offlineListSubmit = new ArrayList<OfflineTTK>();
    String encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    int jmlMenu;
    List<String> menus = new ArrayList<String>();
    List<String> icons = new ArrayList<String>();
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    List<String> menusdev = new ArrayList<String>();
    List<String> iconsdev = new ArrayList<String>();

    Button btnInput;
    ArrayList<DataSJ> sjList = new ArrayList<DataSJ>();
    String jab;
    private GPSTracker gpsTracker;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;

    GooglePlayServicesHelper geoGPS;
    final Context context = this;
    public int jmlMenustatic;
    public String jmlfix;

//    final String sd;

    String hh;
    int jml;

    int jmlmenufix;
    JSONObject json;
    double updateLoc;
    String lati,longi;


    @SuppressWarnings("ResourceType")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ops_main);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent myIntent = getIntent();

        progressDialog = new ProgressDialog(OpsMainActivityBackup2.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(OpsMainActivityBackup2.this);
        session.checkLogin();
        db = new DatabaseHandler(OpsMainActivityBackup2.this);
        jmlMenu = Integer.parseInt(db.getTotalMenu(session.getUsername()));

        jmlmenufix=jmlMenu+Integer.parseInt(session.getjmlmenustatic());

//        jml1=myIntent.getStringExtra("jml");
//
//        Log.d("jml1",""+jml);

//        Log.d("jml",""+jmlMenu);


        getMenuAndroid();

//        ceklokasi();


//        FusedLocation fusedLocation = new FusedLocation(context, new FusedLocation.Callback(){
//            @Override
//            public void onLocationResult(Location location){
//                //Do as you wish with location here
//                Toast.makeText(OpsMainActivity.this,
//                        "Latitude " +location.getLatitude() +" Longitude: " + location.getLongitude(),
//                        Toast.LENGTH_LONG).show();
//
//                updateLoc=location.getLatitude();
//
//                json = new JSONObject();
//                try {
//                    json.put("latitude", location.getLatitude());
//                    json.put("longitude", location.getLongitude());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////                hs(json);
//
//
//            }
//        });
//
//        if (!fusedLocation.isGPSEnabled()){
//            fusedLocation.showSettingsAlert();
//        }else{
//            fusedLocation.getCurrentLocation(1);
//        }


        Log.d("jml",""+jmlMenu);
        Log.d("jmlses",""+session.getjmlmenustatic());

//        statusCheck();
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        provider = locationManager.getBestProvider(criteria, false);
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        if (location != null) {
//            Toast.makeText(getApplicationContext(), location.getLatitude()+"|"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//            onLocationChanged(location);
//        } else {
//            Toast.makeText(getApplicationContext(), "not Available", Toast.LENGTH_SHORT).show();
//        }

//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(ACCESS_COARSE_LOCATION);
//
//        permissionsToRequest = findUnAskedPermissions(permissions);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//
//            if (permissionsToRequest.size() > 0)
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//        }
//
//
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        getLocation();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
//
//        checkLocation();

//        geoGPS = new GooglePlayServicesHelper(OpsMainActivity.this, true);

//        FusedLocation fusedLocation = new FusedLocation(context,true);

//        FusedLocation fusedLocation = new FusedLocation(context, new FusedLocation.Callback(){
//            @Override
//            public void onLocationResult(Location location){
//                //Do as you wish with location here
//                Toast.makeText(OpsMainActivity.this,
//                        "Latitude " +location.getLatitude() +" Longitude: " + location.getLongitude(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        if (!fusedLocation.isGPSEnabled()){
//            fusedLocation.showSettingsAlert();
//        }else{
//            fusedLocation.getCurrentLocation(1);
//        }




//        locationTrack = new LocationTrack(OpsMainActivity.this);
//
//        if (locationTrack.canGetLocation()) {
//
//
//            double longitude = locationTrack.getLongitude();
//            double latitude = locationTrack.getLatitude();
//
//            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
//        } else {
//
//            locationTrack.showSettingsAlert();
//        }



        User lastLogin =  db.getLastLogin(session.getUsername());
        String last = lastLogin.getLastLogin();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat cf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedCal = cf.format(c.getTime());
        if (last == null){
            checkAbsen();
        }else if (!last.equals(formattedCal)){
            checkAbsen();
        }

//        Log.d("staticmenu",""+hh);
//
//        Log.d("jabfix",""+jab);


//        getMenu();

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
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        Log.d("jabatan",""+jab);

//        menusdev.add("Buat SJ kurir Testing");
//        menusdev.add("Approval SJ Testing");
//
//        iconsdev.add("wahana_mydetail");
//        iconsdev.add("wahana_mydetail");
//
//        menus.addAll(menusdev);
//        icons.addAll(iconsdev);


        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        teksMenu = new ArrayList<String>();
        inputTTK = (TextView) findViewById(R.id.label_search) ;
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        inputTTK.setTypeface(type);
        inputTTK.setText("Masukkan No TTK");

        session = new SessionManager(OpsMainActivityBackup2.this);
        session.checkLogin();

        this.setTitle("");

        if (USE_CURSOR_ADAPTER) {
            if (savedInstanceState == null) {
                //adapter = new DefaultCursorAdapter(this, demoUtils.moarItems(50));
            } else {
                //adapter = new DefaultCursorAdapter(this);
            }
        } else {
            Log.d("Hasil Soap", ""+jmlMenu);
            if (savedInstanceState == null) {
                    adapter = new AdapterPelaksanaDelivery(this, demoUtils.moarItems(jmlmenufix),session.getRoleID().toString(),menus,icons);
            } else {
                    adapter = new AdapterPelaksanaDelivery(this, demoUtils.moarItems(jmlmenufix),session.getRoleID().toString(),menus,icons);
            }
        }

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
                startActivityForResult(new Intent(OpsMainActivityBackup2.this,SearchActivity.class), 0);
            }
        });
    }


    public String getlokasi(double a, double b){

        lati=String.valueOf(a);
        longi=String.valueOf(b);

        return lati+","+longi;
    }



    public void getMenuAndroid(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getMenuAndroid");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBACD");
        parameter.add(session.getUsername());
        new SoapClientMobile(){
            @Override
            protected void onPostExecute(final SoapObject result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                try {
                    if(result==null){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(OpsMainActivityBackup2.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            int total = data.length() - 1;
//
                            if (data.length()>1) {
                                for (int i = 0; i < data.length(); i++) {
                                    if (i != 0) {

                                        String menu = null, icon = null;
                                        JSONArray isi = data.getJSONArray(i);
                                        menu = isi.getString(1);
                                        icon = isi.getString(2);
                                        jab = isi.getString(3);
                                        menus.add(menu);
                                        icons.add(icon);

                                        Log.d("jabatan2", "" + jab);
                                    }
                                }

                                Log.d("jabatan1", "" + jab);

                                final String sd;

                                if (jab.equals("B112") || jab.equals("B115A") || jab.equals("B115B") || jab.equals("B114")) {
                                    menusdev.add("Buat SJ kurir Testing");
                                    menusdev.add("Pickup Account");

                                    iconsdev.add("wahana_mydetail");
                                    iconsdev.add("wahana_mydetail");

                                    menus.addAll(menusdev);
                                    icons.addAll(iconsdev);

                                    jml=2;

//                                    jmlMenustatic=jmlMenu+2;
//                                    Log.d("jmlmenu",""+jmlMenustatic);

//                                    sd = "kurir";

//                                    hitung(sd);

                                }else if(jab.equals("B104")){

                                    menusdev.add("Buat SJ kurir Testing");
                                    menusdev.add("Approval SJ Testing");
                                    menusdev.add("Pickup Account");

                                    iconsdev.add("wahana_mydetail");
                                    iconsdev.add("wahana_mydetail");
                                    iconsdev.add("wahana_mydetail");

                                    menus.addAll(menusdev);
                                    icons.addAll(iconsdev);

                                    jml=3;



                                }else if(jab.equals("B106") || jab.equals("B116") || jab.equals("B109")) {

                                    menusdev.add("STPU Account");

                                    iconsdev.add("wahana_mydetail");

                                    menus.addAll(menusdev);
                                    icons.addAll(iconsdev);

                                    jml=1;


//                                    jmlMenustatic=jmlMenu+2;
//                                    Log.d("jmlmenu",""+jmlMenustatic);
//
//                                    hitung(jmlMenustatic);

//                                    sd="proses";

                                }else{
                                    menusdev.add("Approval SJ Testing");
                                    menusdev.add("Pickup Account");
                                    menusdev.add("STPU Account");

                                    iconsdev.add("wahana_mydetail");
                                    iconsdev.add("wahana_mydetail");
                                    iconsdev.add("wahana_mydetail");

                                    menus.addAll(menusdev);
                                    icons.addAll(iconsdev);

                                    jml=3;


//                                    jmlMenustatic=jmlMenu+3;
//                                    Log.d("jmlmenu",""+jmlMenustatic);
//
//                                    hitung(jmlMenustatic);

//                                    sd="all";
                                }


//                                jmlmenufix=jml+jmlMenu;

//                                int jmlk=total+jml;
//
//                                db.updateTotalMenu(session.getUsername(), String.valueOf(jmlk));
//
//                                Log.d("jmlk",""+jmlk);

//                                hh=hitung(sd);




                            }else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(OpsMainActivityBackup2.this, "Menu Tidak Tersedia",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }else {
                            Toast.makeText(OpsMainActivityBackup2.this, ""+text, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.d("Hasil error", ""+e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(OpsMainActivityBackup2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }.execute(parameter);
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

    public void onItemClick( AdapterView<?> parent,  View view,
                             int position, long id) {
        String judul = menus.get(position);
        if (judul.equals("AKTIVITAS KARYAWAN")) {

//            ceklokasi();

//            Log.d("jsonhasil",""+json);

//            Intent pindah = new Intent(this, ListSubMenuActivity.class);
//            pindah.putExtra("menu", "AKTIVITAS KARYAWAN");
//            startActivity(pindah);


//            Intent picture_intent = new Intent(OpsMainActivity.this, ReportPageActivity.class);
//            picture_intent.putExtra("url", "https://drive.google.com/viewerng/viewer?embedded=true&url=https://mobile.wahana.com/apps/wahana/upload/peraturan_perusahaan.pdf");
//            //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
//            startActivity(picture_intent);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.wahana.com/apps/wahana/upload/peraturan_perusahaan.pdf"));
            startActivity(Intent.createChooser(intent, "Pilih aplikasi"));


//            Intent pindah = new Intent(this, SearchSjApprovalNik.class);
//            startActivity(pindah);

//            startActivityForResult(new Intent(OpsMainActivity.this,BarcodeScanner.class),ZBAR_CAMERA_PERMISSION);


//            Intent pindah = new Intent(this,InputNIKKurir.class);
//            startActivity(pindah);

//            db.deleteListSumberPickup();
//
//            DownloadTask cls = new DownloadTask(OpsMainActivity.this,btnInput, Utils.downloadcsvUrl);
//
//            db.importscv(cls.ty());

//            Intent pindah = new Intent(this, InputBBM.class);
        } else if (judul.equals("TANDAI RETUR")) {
            Intent pindah = new Intent(this, inputTTKActivity.class);
            startActivity(pindah);
        } else if (judul.equals("TTK")) {
            Intent pindah = new Intent(this, ListSubMenuActivity.class);
            pindah.putExtra("menu", "TTK");
            startActivity(pindah);
        } else if (judul.equals("KANTOR DELIVERY")) {
            Intent pindah = new Intent(this, ListSubMenuActivity.class);
            pindah.putExtra("menu", "KANTOR DELIVERY");
            startActivity(pindah);
        } else if (judul.equals("BUAT STK")) {
            Intent pindah = new Intent(this, StkScannerActivity.class);
            startActivity(pindah);
        } else if (judul.equals("BUAT MANIFEST SORTIR")) {

//            Intent pindah = new Intent(this, InputKodeKotaTujuan.class);
//            startActivity(pindah);


            Intent picture_intent = new Intent(OpsMainActivityBackup2.this, ReportPageActivity.class);
            picture_intent.putExtra("url", "http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=view/vnm.pengumumanAndroid&noframe=1");
            //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
            startActivity(picture_intent);

//            Intent pindah = new Intent(this,RecycleView.class);
//            startActivity(pindah);

//            Intent pindah = new Intent(OpsMainActivity.this, HasilProses.class);
//            pindah.putExtra("proses","sj");
//            pindah.putExtra("no", "SJ-1811-700343UU");
//            startActivity(pindah);


//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_PCT_m"));
//            startActivity(browserIntent);

        } else if (judul.equals("STSM")) {
            Intent pindah = new Intent(this, SerahTerimaSMActivity.class);
            startActivity(pindah);
        } else if (judul.equals("STMS")) {
            Intent pindah = new Intent(this, inputNoLabelMSActivity.class);
            startActivity(pindah);
        } else if (judul.equals("BUAT MA")) {
//                Intent pindah = new Intent(this, ScanMAActivity.class);
            Intent pindah = new Intent(this, InputKodeVia.class);
            startActivity(pindah);
        } else if (judul.equals("BUAT SJ")) {
            Intent pindah = new Intent(this, InputNIKKurir.class);
            startActivity(pindah);
        } else if (judul.equals("BUAT SJP")) {
            Intent pindah = new Intent(this, InputNamaPenerus.class);
            startActivity(pindah);
        } else if (judul.equals("STBT")) {
            Intent pindah = new Intent(this, InputNikKurirSTBT.class);
            startActivity(pindah);
        } else if (judul.equals("ABSENSI")) {
            launchMapActivity(Absensi.class);
        } else if (judul.equals("ABOUT US")) {
            Intent pindah = new Intent(this, AboutUsActivity.class);
            startActivity(pindah);
        } else if (judul.equals("TTK TERKIRIM")) {
            launchMapActivity(DeliveredAirways.class);
        } else if (judul.equals("TTK BELUM TERKIRIM")) {
            launchMapActivity(UndeliveredAirways.class);
        } else if (judul.equals("PICK UP")) {
            Intent pindah = new Intent(this, inputtksalahtempel.class);
            startActivity(pindah);
        } else if (judul.equals("SISA PAKET")) {
            Intent pindah = new Intent(this, MyInfo.class);
            startActivity(pindah);
        } else if (judul.equals("AGEN TERDEKAT")) {
            launchMapActivity(CariAgenActivity.class);
        } else if (judul.equals("CEK TARIF")) {
            Intent pindah = new Intent(this, CekTarifActivity.class);
            startActivity(pindah);
        } else if (judul.equals("CUSTOMER SERVICE")) {
            Intent pindah = new Intent(this, CustomerServiceActivity.class);
            startActivity(pindah);
        } else if (judul.equals("PICKUP")) {
            Intent pindah = new Intent(this, PickupActivity.class);
            startActivity(pindah);

//            Intent pindah = new Intent(this, PickupAgenNew.class);
//            startActivity(pindah);

        } else if (judul.equals("INPUT SMU")) {
            Intent pindah = new Intent(this, inputMAActivity.class);
            startActivity(pindah);
        } else if (judul.equals("EDIT SJP")) {
            Intent pindah = new Intent(this, inpuSJPActivity.class);
            startActivity(pindah);
        } else if (judul.equals("PENERIMAAN RETUR")) {
            Intent pindah = new Intent(this, inputTTKReturActivity.class);
            startActivity(pindah);
        } else if (judul.equals("REJECT X-RAY")) {
            Intent pindah = new Intent(this, inputttkrejectxray.class);
            startActivity(pindah);
        } else if (judul.equals("SELISIH BERAT")) {
            Intent pindah = new Intent(this, inputTTKSelisihBerat.class);
            startActivity(pindah);
        } else if (judul.equals("SALAH DESTINASI")) {
            Intent pindah = new Intent(this, inputtksalahdestinasi.class);
            startActivity(pindah);
        } else if (judul.equals("SALAH TEMPEL")) {
            Intent pindah = new Intent(this, inputtksalahtempel.class);
            startActivity(pindah);
        } else if (judul.equals("INPUT BBM")) {
//            Intent pindah = new Intent(this, InputBBM.class);
//            startActivity(pindah);

            Intent picture_intent = new Intent(OpsMainActivityBackup2.this, ReportPageActivity.class);
            picture_intent.putExtra("url", "http://intranet.wahana.com/web_wahana/whnbbmv5?user=" + session.getUsername());
            //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
            startActivity(picture_intent);

        } else if (judul.equals("TTK Salah Wilayah")) {
//            Intent pindah = new Intent(this, InputBBM.class);
//            startActivity(pindah);

            Intent picture_intent = new Intent(OpsMainActivityBackup2.this, ReportPageActivity.class);
            picture_intent.putExtra("url", "https://mobile.wahana.com/apps/account/cgi-bin/dw.cgi?b=mkTTKSalahWilayah;asis=1&user=" + session.getUsername());
            //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
            startActivity(picture_intent);


        } else if (judul.equals("STPU")) {

            Intent pindah = new Intent(this, InputNikSupir.class);
            startActivity(pindah);

        } else if (judul.equals("Approval SJ Kurir")) {


            AlertDialog.Builder adb = new AlertDialog.Builder(OpsMainActivityBackup2.this);
            adb.setTitle("Info");
            adb.setMessage("Pilih Metode Approval?");

            adb.setNegativeButton("Manual", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent pindah = new Intent(OpsMainActivityBackup2.this, SearchSjApprovalNik.class);
                    startActivity(pindah);
                }
            });

            adb.setPositiveButton("Scan", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent pindah = new Intent(OpsMainActivityBackup2.this, BarcodeScanner.class);
                    startActivity(pindah);
                }
            });
            adb.show();

        } else if (judul.equals("Buat SJ kurir Testing")) {

            Intent pindah = new Intent(OpsMainActivityBackup2.this, InputNIKKurir.class);
            startActivity(pindah);

        } else if (judul.equals("Approval SJ Testing")) {


            AlertDialog.Builder adb = new AlertDialog.Builder(OpsMainActivityBackup2.this);
            adb.setTitle("Info");
            adb.setMessage("Pilih Metode Approval?");

            adb.setNegativeButton("Manual", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent pindah = new Intent(OpsMainActivityBackup2.this, SearchSjApprovalNik.class);
                    startActivity(pindah);
                }
            });

            adb.setPositiveButton("Scan", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent pindah = new Intent(OpsMainActivityBackup2.this, BarcodeScanner.class);
                    startActivity(pindah);
                }
            });
            adb.show();


        } else if (judul.equals("Pickup Account")) {

            Intent pindah = new Intent(this, PickupAgenNew.class);
            startActivity(pindah);

        } else if (judul.equals("STPU Account")) {

            Intent pindah = new Intent(this, InputNikSupir.class);
            startActivity(pindah);

        }else if (judul.equals("sss")) {

            Intent pindah = new Intent(this, InputNikSupir.class);
            startActivity(pindah);

        }else {
            Toast.makeText(OpsMainActivityBackup2.this, "Menu belum bisa diakses untuk role "+session.getRole(), Toast.LENGTH_SHORT).show();
        }
    }

    public void launchMapActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, MAP_PERMISSION);
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
    protected void onStart() {
        super.onStart();

//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }

    }

    private void populate()
    {

        String role = db.getRole();
//        Toast.makeText(getApplicationContext(),""+role,Toast.LENGTH_LONG).show();
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getListMenuByUserTypeCode");
        parameter.add(role);
        new SoapClientLogin() {
            @Override
            protected void onPostExecute(final SoapObject result1) {
                super.onPostExecute(result1);
                progressDialog.dismiss();
                final String code = result1.getProperty("resCode").toString();
                final String text = result1.getProperty("resText").toString();
//                Toast.makeText(getApplicationContext(),""+code,Toast.LENGTH_LONG).show();
                if (code.equals("1")) {

                    Log.d("Hasil", "" + result1.getProperty("data"));
//                    SoapObject so = (SoapObject) result1.getProperty("data");
                    Vector<SoapObject> vector = (Vector<SoapObject>) result1.getProperty("data");
                    for (SoapObject so : vector) {
                        String teks_menu = null, menu_relation_id = null;
                        teks_menu = so.getProperty("teks_menu").toString();
                        menu_relation_id = so.getProperty("menu_relation_id").toString();
                        Log.d("Hasil: ", "" + teks_menu + menu_relation_id);
//                        Toast.makeText(getApplicationContext(),"Hasil: "+ teks_menu + menu_relation_id,Toast.LENGTH_LONG).show();
                        teksMenu.add(teks_menu);
//                        Toast.makeText(getApplicationContext(),"dalem for: "+teksMenu.size(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute(parameter);
    }


    public void ceklokasi(){
        FusedLocation fusedLocation = new FusedLocation(context, new FusedLocation.Callback(){
            @Override
            public void onLocationResult(Location location){
                //Do as you wish with location here
                Toast.makeText(OpsMainActivityBackup2.this,
                        "Latitude " +location.getLatitude() +" Longitude: " + location.getLongitude(),
                        Toast.LENGTH_LONG).show();

                json = new JSONObject();
                try {
                    json.put("latitude", location.getLatitude());
                    json.put("longitude", location.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        if (!fusedLocation.isGPSEnabled()){
            fusedLocation.showSettingsAlert();
        }else{
            fusedLocation.getCurrentLocation(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        FusedLocation fusedLocation = new FusedLocation(context, new FusedLocation.Callback(){
//            @Override
//            public void onLocationResult(Location location){
//                //Do as you wish with location here
//                Toast.makeText(OpsMainActivity.this,
//                        "Latitude " +location.getLatitude() +" Longitude: " + location.getLongitude(),
//                        Toast.LENGTH_LONG).show();
//
//                double latitude=location.getLatitude();
//                double longitude=location.getLongitude();
//
//                json = new JSONObject();
//                try {
//                    json.put("latitude", location.getLatitude());
//                    json.put("longitude", location.getLongitude());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                getlokasi(latitude,longitude);
//
//
//            }
//        });
//
//        if (!fusedLocation.isGPSEnabled()){
//            fusedLocation.showSettingsAlert();
//        }else{
//            fusedLocation.getCurrentLocation(1);
//        }

//        fusedLocation.getCurrentLocation(1);
//
//        if (!fusedLocation.isGPSEnabled()){
//            fusedLocation.showSettingsAlert();
//        }else{
//            fusedLocation.getCurrentLocation(3);
//        }

//        locationManager.requestLocationUpdates(provider, 400, 1, this);
//        if(session.getRoleID().equals("B112") || session.getRoleID().equals("B113") || session.getRoleID().equals("B104")
//                || session.getRoleID().equals("IT01") || session.getRoleID().equals("IT02") || session.getRoleID().equals("IT03")
//                || session.getRoleID().equals("IT04")){
            runOnUiThread(new Runnable() {
                public void run() {
                    ArrayList<String> parameter = new ArrayList<String>();
                    parameter.add("getActivePackageCount");
                    parameter.add(session.getSessionID());
                    parameter.add(session.getID());
                    new SoapClient(){
                        @Override
                        protected void onPostExecute(SoapObject result) {
                            super.onPostExecute(result);
                            Log.d("hasil package count", ""+result);
                            if (result != null){
                                try{
                                    final String text = result.getProperty(0).toString();
                                    if (text.equals("1")) {
                                        try {
                                            int sisa;
                                            sisa = Integer.parseInt(result.getProperty(2).toString());
                                            try {
                                                String sent = result.getProperty(3).toString();
                                                sisa = sisa-Integer.parseInt(sent);
                                            }catch (Exception e){
                                                Log.d("hasil Exception 2", e+"");
                                            }
                                            AdapterPelaksanaDelivery.textView1.setText(sisa+"");
                                        }catch(Exception e){
                                            Log.d("hasil Exception 1", e+"");
                                        }
                                        submitForm();
                                    }else if (text.equals("0")){
                                        submitForm();
                                    }
                                }catch (Exception e){}
                            }
                        }
                    }.execute(parameter);
                }
            });
//        }
    }

    private void checkAbsen(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat cf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedCal = cf.format(c.getTime());
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("attendanceList");
        parameter.add(session.getSessionID());
        parameter.add(session.getUsername());
        parameter.add(formattedCal);
        parameter.add(formattedCal);
        new SoapClient(){
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
                            Toast.makeText(OpsMainActivityBackup2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            for (int i = 2; i<result.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String in = null;
                                try {
                                    in = so.getProperty("attendanceIn").toString();
                                }catch(Exception e){
                                    e.printStackTrace();
                                    Log.d("eror", e+"");
                                }
                                if (in == null){
                                    launchMapActivity(Absensi.class);
                                }
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                    Toast.makeText(MainActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }
    int checker;
    private void submitForm() {
        offlineListSubmit = db.getAllOffline(session.getID());
        checker=1;
        for (int i = 0; i < offlineListSubmit.size(); i++){
            final OfflineTTK ttk = offlineListSubmit.get(i);
            List<String> uris = db.getAllTTKURI(ttk.getTTK());

            for (int s = 0; s < uris.size(); s++){
                String uri = uris.get(s);
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Bitmap bmp= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse("file://"+uri)));
                    bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
                    byte[] raw = out.toByteArray();
                    if (s == 0){
                        encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("hasil gambar 1", ""+encodedImage);
                        Log.d("hasil gambar", ""+uri);
                    }else if (s==1){
                        encodedImage2 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("gambar 2", ""+encodedImage2);
                        Log.d("hasil gambar", ""+uri);
                    }else if (s==2){
                        encodedImage3 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("gambar 3", ""+encodedImage3);
                        Log.d("hasil gambar", ""+uri);
                    }else if (s==3){
                        encodedImage4 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("gambar 4", ""+encodedImage4);
                        Log.d("hasil gambar", ""+uri);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Keterangan", e+"");
                }
            }

            ArrayList<String> parameter = new ArrayList<String>();
            parameter.add("setPackageDeliveryStatus");
            parameter.add(ttk.getSessionid());
            parameter.add(ttk.getRequestid());
            parameter.add(ttk.getEmployeecode());
            parameter.add(ttk.getTTK().toString());
            parameter.add(ttk.getStatusdate().toString());
            parameter.add(ttk.getPackagestatus());
            parameter.add(ttk.getReason());
            parameter.add(ttk.getReceivername());
            parameter.add(ttk.getReceivertype());
            parameter.add(ttk.getComment());
            parameter.add(encodedImage);
            parameter.add(encodedImage2);
            parameter.add(encodedImage3);
            parameter.add(encodedImage4);
            parameter.add(ttk.getLongitude());
            parameter.add(ttk.getLatitude());

            final int progress = i;
            new SoapClient(){
                @Override
                protected void onPostExecute(SoapObject result) {
                    super.onPostExecute(result);
                    Log.d("hasil soap", "" + result);
                    if (result == null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        try {
                            final String text = result.getProperty(1).toString();
                            //  final String text="NOK";
                            Log.d("hasil soap2", "" + text);
                            if (text.equals("OK")) {
                                db.updateOfflineMessage(ttk.getTTK(), text, "Terkirim");
                            } else {
                                checker=0;
                                db.updateOfflineMessage(ttk.getTTK(), text, "Gagal");
                            }
                        }catch (Exception e){
                            db.updateOfflineMessage(ttk.getTTK(), "System Error", "Gagal");
                        }
                        if (progress ==(offlineListSubmit.size()-1)) {
                            if (checker == 0) {
                                Toast.makeText(OpsMainActivityBackup2.this, "Pengiriman TTK Offline Gagal, Mohon Cek Menu TTK Offline", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }
            }.execute(parameter);
            Log.d("hasil", i+"");
        }
    }



    private void checksj(final String sj){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("getTTKSJ");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(sj);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data SJ...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(OpsMainActivityBackup2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);

//                                            Log.d("hasilsj",""+ttk);

                                            DataSJ ori = new DataSJ();
                                            ori.setTtk(ttk);
                                            sjList.add(ori);

//                                            db.addOrigin(ori);
                                        }

                                    }

                                    Intent pindah = new Intent(OpsMainActivityBackup2.this, ListApprovalSJ.class);
                                    pindah.putExtra("ttk", sj);
                                    startActivity(pindah);

//                                    adapter = new ListViewAdapterSearchKotaAsal(SearchKotaAsal.this, R.layout.listviewsearchkotaasal, originList);
//                                    listView.setAdapter(adapter);

//                                    adapter= new AdapterSJApproval(sjList,getApplicationContext());
//
//                                    listView.setAdapter(adapter);


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(OpsMainActivityBackup2.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(OpsMainActivityBackup2.this, sj+" Bukan No SJ",Toast.LENGTH_LONG).show();
//                                        Intent pindah = new Intent(ListApprovalSJ.this, MainActivity.class);
//                                        startActivity(pindah);
//                                        finish();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(OpsMainActivityBackup2.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(OpsMainActivityBackup2.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        session.updateExpired(System.currentTimeMillis()+(5*60*60*1000));
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String nilai=data.getStringExtra("ttk");
            checksj(nilai);

//            Intent pindah = new Intent(OpsMainActivity.this, ListApprovalSJ.class);
//            pindah.putExtra("ttk", nilai);
//            startActivity(pindah);

        }
    }




    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

//        Location location = manager
//                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();

        }


    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }



//LOCATION


//    @Override
//    public void onConnected(Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        startLocationUpdates();
//
//        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if(mLocation == null){
//            startLocationUpdates();
//        }
//        if (mLocation != null) {
//
//            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
//            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
//        } else {
//            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "Connection Suspended");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
//    }
////
////    @Override
////    protected void onStart() {
////        super.onStart();
////        if (mGoogleApiClient != null) {
////            mGoogleApiClient.connect();
////        }
////    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    protected void startLocationUpdates() {
//        // Create the location request
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(UPDATE_INTERVAL)
//                .setFastestInterval(FASTEST_INTERVAL);
//        // Request location updates
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                mLocationRequest, this);
//        Log.d("reque", "--->>>>");
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        String msg = "Updated Location: " +
//                location.getLatitude() + "," +
//                location.getLongitude();
//
////        String msg = "Updated Location: " +
////                Double.toString(location.getLatitude()) + "," +
////                Double.toString(location.getLongitude());
////        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
////        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        // You can now create a LatLng Object for use with maps
////        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//    }

//    private boolean checkLocation() {
//
//        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            buildAlertMessageNoGps();
//
//        }else {
//            isLocationEnabled();
//        }
//
////        if(!isLocationEnabled())
//
//        return isLocationEnabled();
//    }
//
//    private void showAlert() {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("Enable Location")
//                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
//                        "use this app")
//                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//
//                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(myIntent);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//
//                    }
//                });
//        dialog.show();
//    }

//    private boolean isLocationEnabled() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
////        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//    }

    public String hitung(String jml){

      return jml;

    }



}
