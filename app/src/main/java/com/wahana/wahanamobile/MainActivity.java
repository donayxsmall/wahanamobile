package com.wahana.wahanamobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.AsymmetricGridViewAdapter;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.Data.User;
import com.wahana.wahanamobile.Ops.ListSubMenuActivity;
import com.wahana.wahanamobile.adapter.DefaultListAdapter;
import com.wahana.wahanamobile.adapter.DemoAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DemoItem;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends DrawerHelper implements AdapterView.OnItemClickListener  {
    private static final String TAG = "MainActivity";
    private Class<?> mClss;
    private static final boolean USE_CURSOR_ADAPTER = false;
    public AsymmetricGridView listView;
    private DemoAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
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

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(MainActivity.this);
        db = new DatabaseHandler(MainActivity.this);
//        int totalRole = db.getTotalRole();
//        if(totalRole!=0){
//            if (totalRole==1){
//                String role = db.getRole();
//                if (role.equals("1")){
//                    startActivity(new Intent(getApplication(), MainActivity.class));
//                    finish();
//                }else if(role.equals("6")){
//                    startActivity(new Intent(getApplication(), SalesMainActivity.class));
//                    finish();
//                }else if(role.equals("26")){
//                    Intent intent = new Intent(this, OpsMainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }else if(role.equals("32")){
//                    startActivity(new Intent(getApplication(), OpsMainActivity.class));
//                    finish();
//                }
//            }else {
//                startActivity(new Intent(getApplication(), SalesMainActivity.class));
//                finish();
//            }
//        }
        if(session.getJenis().equals("ops")){
            Intent intent = new Intent(this, OpsMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if(session.getJenis().equals("ar")){
            Intent intent = new Intent(this, ArMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if(session.getJenis().equals("sales")){
            Intent intent = new Intent(this, SalesMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
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
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

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
        Log.d("last login", ""+last);
        inputTTK = (TextView) findViewById(R.id.label_search) ;
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        inputTTK.setTypeface(type);
        inputTTK.setText("Masukkan No TTK");

        session = new SessionManager(MainActivity.this);
        session.checkLogin();

        this.setTitle("");

        if (USE_CURSOR_ADAPTER) {
            if (savedInstanceState == null) {
                //adapter = new DefaultCursorAdapter(this, demoUtils.moarItems(50));
            } else {
                //adapter = new DefaultCursorAdapter(this);
            }
        } else {
            if (savedInstanceState == null) {
                adapter = new DefaultListAdapter(this, demoUtils.moarItems(6));
                Log.d("Hasil adapter", ""+adapter);
            } else {
                adapter = new DefaultListAdapter(this);
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
                startActivityForResult(new Intent(MainActivity.this,SearchActivity.class), 0);
            }
        });
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


    private void setNumColumns(int numColumns) {
        listView.setRequestedColumnCount(numColumns);
        listView.determineColumns();
        listView.setAdapter(getNewAdapter());
    }

    private void setColumnWidth(int columnWidth) {
        listView.setRequestedColumnWidth(Utils.dpToPx(this, columnWidth));
        listView.determineColumns();
        listView.setAdapter(getNewAdapter());
    }


     public void onItemClick( AdapterView<?> parent,  View view,
                                      int position, long id) {
         String judul = this.getResources().getStringArray(R.array.judulutama)[position];
         if (judul.equals("AKTIVITAS KARYAWAN")) {
             Intent pindah = new Intent(this, ListSubMenuActivity.class);
             pindah.putExtra("menu", "AKTIVITAS KARYAWAN");
             startActivity(pindah);
         }else if (judul.equals("ABSENSI")) {
             launchMapActivity(Absensi.class);
         }else if (judul.equals("CEK TARIF")) {
             Intent pindah = new Intent(this, CekTarifActivity.class);
             startActivity(pindah);
         }else if (judul.equals("CARI AGEN")) {
             launchMapActivity(CariAgenActivity.class);
         }else if (judul.equals("CUSTOMER SERVICE")) {
             Intent pindah = new Intent(this, CustomerServiceActivity.class);
             startActivity(pindah);
         }else if (judul.equals("ABOUT US")) {
             Intent pindah = new Intent(this, AboutUsActivity.class);
             startActivity(pindah);
         }
    }

    public void launchMapActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MAP_PERMISSION);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        runOnUiThread(new Runnable() {
//            public void run() {
//                ArrayList<String> parameter = new ArrayList<String>();
//                parameter.add("getActivePackageCount");
//                parameter.add(session.getSessionID());
//                parameter.add(session.getID());
//                new SoapClient(){
//                    @Override
//                    protected void onPostExecute(SoapObject result) {
//                        super.onPostExecute(result);
//                        Log.d("hasil soap", ""+result);
//                        if (result != null){
//                            try{
//                                final String text = result.getProperty(0).toString();
//                                if (text.equals("1")) {
//                                    try {
//                                        int sisa;
//                                        sisa = Integer.parseInt(result.getProperty(2).toString());
//                                        try {
//                                            String sent = result.getProperty(3).toString();
//                                            sisa = sisa-Integer.parseInt(sent);
//                                        }catch (Exception e){
//                                            Log.d("Exception 2", e+"");
//                                        }
//                                        DefaultListAdapter.textView1.setText(sisa+"");
//                                    }catch(Exception e){
//                                        Log.d("Exception 1", e+"");
//                                    }
//                                    submitForm();
//                                }else if (text.equals("0")){
//                                    submitForm();
//                                }
//                            }catch (Exception e){}
//                        }
//                    }
//                }.execute(parameter);
//            }
//        });
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
                            Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(MainActivity.this, "Pengiriman TTK Offline Gagal, Mohon Cek Menu TTK Offline", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }
            }.execute(parameter);
            Log.d("hasil", i+"");
        }
    }

}
