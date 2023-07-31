package com.wahana.wahanamobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wahana.wahanamobile.adapter.CariAgenAdapter;
import com.wahana.wahanamobile.adapter.CariAgenDetailAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.ServiceHandler;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class CariAgenActivity extends DrawerHelper implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "CariAgentActivity";

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    double lat;
    double longi;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    public ArrayAdapter<CharSequence> adapterRadius, adapterHasil;


    private ActionBarDrawerToggle mDrawerToggle;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;

    ImageView foto;
    Button btnSubmit;
    Spinner spinnerRadius, spinnerHasil;
    TextView radiusLabel, satuanLabel, hasilLabel;
//    AutoCompleteTextView lokasi_isi;
    private GoogleMap map;
    String kota;
    ListView listViewCariAgen;
    ListView listViewDetailAgen;
    CariAgenAdapter adapter;
    CariAgenDetailAdapter detailAdapter;
    List<String> nama = new ArrayList<String>();
    List<String> alamat = new ArrayList<String>();
    List<String> jarak = new ArrayList<String>();
    List<String> email = new ArrayList<String>();
    List<String> telp = new ArrayList<String>();
    List<String> latList = new ArrayList<String>();
    List<String> longList = new ArrayList<String>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_agen);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(CariAgenActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        });

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.error_message_title));
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.error_message));
            // On pressing Settings button
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent pindah = new Intent(CariAgenActivity.this, MainActivity.class);
                            startActivity(pindah);
                            finish();
                        }
                    });
        }

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.gps_violation_title));
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.gps_violation));
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            CariAgenActivity.this.startActivity(intent);
                            finish();
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(CariAgenActivity.this, R.style.AppTheme_Dark_Dialog);

        spinnerRadius = (Spinner) findViewById(R.id.spinner_radius);
        adapterRadius = ArrayAdapter.createFromResource(this, R.array.radius, R.layout.simple_spinner_list);
        adapterHasil = ArrayAdapter.createFromResource(this, R.array.hasil, R.layout.simple_spinner_list);
        adapterRadius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterHasil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRadius.setAdapter(adapterRadius);

        listViewCariAgen = (ListView) findViewById(R.id.list_agen);
        listViewDetailAgen = (ListView) findViewById(R.id.list_detail_agent);
        // Get Item Input
        listViewCariAgen.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                listViewCariAgen.setVisibility(View.GONE);
                listViewDetailAgen.setVisibility(View.VISIBLE);

            }
        });
        radiusLabel = (TextView) findViewById(R.id.radius_label);
        satuanLabel =  (TextView) findViewById(R.id.satuan_label);
        btnSubmit = (Button) findViewById(R.id.input_button);
        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        radiusLabel.setTypeface(type);
        satuanLabel.setTypeface(type);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> parameter = new ArrayList<String>();
                parameter.add("findAgent");
                parameter.add(String.valueOf(longi));
                parameter.add(String.valueOf(lat));
                parameter.add(kota);
                parameter.add(spinnerRadius.getSelectedItem().toString());

                new SoapClientMember(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Log.i(TAG, "onPreExecute");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Mengambil Data Agen...");
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
                                    Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            try{
                                final String code = result.getProperty("resCode").toString();
                                final String text = result.getProperty("resText").toString();
                                if (code.equals("1")) {
                                    nama.clear();
                                    alamat.clear();
                                    telp.clear();
                                    email.clear();
                                    jarak.clear();
                                    latList.clear();
                                    longList.clear();
                                    map.clear();
                                    map.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, longi))
                                            .title("Current Location")
                                            .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                                    Vector<SoapObject> vector = (Vector<SoapObject>) result.getProperty("list");
                                    for (SoapObject so : vector){
                                        final String agenNama = so.getProperty("agent_name").toString();
                                        String agenLat = so.getProperty("agent_lat").toString();
                                        String agenLong = so.getProperty("agent_long").toString();
                                        String agenAddress = so.getProperty("agent_address").toString();
                                        String agenEmail= null, agenPhone = null;
                                        try {
                                            agenEmail = so.getProperty("agent_email").toString();
                                        }catch (Exception e){
                                            if (agenEmail == null){
                                                agenEmail = "-";
                                            }
                                            Log.d("agen email", ""+e);
                                        }
                                        try {
                                            agenPhone = so.getProperty("agent_phone").toString();
                                            Log.d("agen phone", ""+agenPhone);
                                        }catch (Exception e){
                                            if (agenPhone == null){
                                                agenPhone = "-";
                                            }
                                            Log.d("agen phone", ""+e);
                                        }
                                        Log.d("agen phone", ""+agenPhone);
                                        String agenDistance = so.getProperty("distance").toString();
                                        nama.add(agenNama);
                                        latList.add(agenLat);
                                        longList.add(agenLong);
                                        alamat.add(agenAddress);
                                        jarak.add(agenDistance);
                                        email.add(agenEmail);
                                        telp.add(agenPhone);
                                        Log.d("hasil soap", ""+agenNama);
                                        map.addMarker(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(agenLat), Double.parseDouble(agenLong)))
                                                .title(agenNama)
                                                .snippet(agenAddress+"; Email : "+agenEmail+"; Telp : "+agenPhone+"; Jarak : "+agenDistance+";"+agenLat+";"+agenLong)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
                                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            @Override
                                            public void onInfoWindowClick(Marker marker) {
                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CariAgenActivity.this);
                                                Log.d("string", marker.getSnippet());
                                                StringTokenizer tokens = new StringTokenizer(marker.getSnippet(), ";");
                                                String alamat = tokens.nextToken();
                                                String email = tokens.nextToken();
                                                String telp = tokens.nextToken();
                                                String jarak = tokens.nextToken();
                                                final String lat = tokens.nextToken();
                                                final String longi = tokens.nextToken();
                                                alertDialog.setCancelable(false);
                                                alertDialog.setTitle(marker.getTitle());
                                                alertDialog.setMessage(Html.fromHtml(alamat+"<br>"+email+"<br>"+telp+"<br>"+jarak+" KM"));
                                                alertDialog.setPositiveButton("Direction",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String uri = "http://maps.google.com/maps?f=d&hl=en&daddr="+lat+","+longi;
                                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                                                startActivity(Intent.createChooser(intent, "Select an application"));
                                                            }
                                                        });

                                                // on pressing cancel button
                                                alertDialog.setNegativeButton("Cancel",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                // Showing Alert Message
                                                alertDialog.show();
                                            }
                                        });
                                    }
                                    adapter = new CariAgenAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email);
                                    detailAdapter = new CariAgenDetailAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email,latList,longList);
                                    listViewCariAgen.setAdapter(adapter);
                                    listViewDetailAgen.setAdapter(detailAdapter);
                                    adapter.notifyDataSetChanged();
                                    detailAdapter.notifyDataSetChanged();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(CariAgenActivity.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                }.execute(parameter);
            }
        });

        listViewDetailAgen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView latIsi = (TextView)view.findViewById(R.id.lat_label);
                TextView longIsi = (TextView)view.findViewById(R.id.long_label);
                String uri = "http://maps.google.com/maps?f=d&hl=en&daddr="+latIsi.getText().toString()+","+longIsi.getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(Intent.createChooser(intent, "Select an application"));
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        lat = location.getLatitude();
        longi = location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(13));

        getDataAgent();

//        new getKota(){
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//                Log.d("Response: ", "> " + result);
//                Log.d("Response: ", "> " + lat + longi);
//                if (result != null) {
//                    try {
//                        JSONObject jsonObj = new JSONObject(result);
//                        final String status = jsonObj.getString("status");
//                        if (status.equals("OK")){
//                            JSONArray res = jsonObj.getJSONArray("results");
//                            for (int i = 0; i < res.length(); i++) {
//                                JSONObject jo = res.getJSONObject(i);
//                                kota = jo.getString("name");
//                                kota = kota.replace("Kota ","");
//                                Log.d("Responses", ""+kota);
//                            }
//                            new getAgent(){
//
//                                @Override
//                                protected void onPostExecute(SoapObject result) {
//                                    super.onPostExecute(result);
//                                    Log.d("hasil soap", ""+result);
//                                    if(result==null){
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }else {
//                                        final String code = result.getProperty("resCode").toString();
//                                        final String text = result.getProperty("resText").toString();
//                                        if (code.equals("1")) {
//                                            Vector<SoapObject> vector = (Vector<SoapObject>) result.getProperty("list");
//                                            for (SoapObject so : vector){
//                                                String agenNama = so.getProperty("agent_name").toString();
//                                                String agenLat = so.getProperty("agent_lat").toString();
//                                                String agenLong = so.getProperty("agent_long").toString();
//                                                String agenAddress = so.getProperty("agent_address").toString();
//                                                String agenEmail= null, agenPhone = null;
//                                                try {
//                                                    agenEmail = so.getProperty("agent_email").toString();
//                                                }catch (Exception e){
//                                                    if (agenEmail == null){
//                                                        agenEmail = "-";
//                                                    }
//                                                    Log.d("agen email", ""+e);
//                                                }
//                                                try {
//                                                    agenPhone = so.getProperty("agent_phone").toString();
//                                                    Log.d("agen phone", ""+agenPhone);
//                                                }catch (Exception e){
//                                                    if (agenPhone == null){
//                                                        agenPhone = "-";
//                                                    }
//                                                    Log.d("agen phone", ""+e);
//                                                }
//                                                Log.d("agen phone", ""+agenPhone);
//                                                String agenDistance = so.getProperty("distance").toString();
//                                                nama.add(agenNama);
//                                                alamat.add(agenAddress);
//                                                jarak.add(agenDistance);
//                                                email.add(agenEmail);
//                                                telp.add(agenPhone);
//                                                latList.add(agenLat);
//                                                longList.add(agenLong);
//                                                Log.d("hasil soap", ""+agenNama);
//                                                map.addMarker(new MarkerOptions()
//                                                        .position(new LatLng(Double.parseDouble(agenLat), Double.parseDouble(agenLong)))
//                                                        .title(agenNama)
//                                                        .snippet(agenAddress+"; Email : "+agenEmail+"; Telp : "+agenPhone+"; Jarak : "+agenDistance+";"+agenLat+";"+agenLong)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
//                                                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                                                    @Override
//                                                    public void onInfoWindowClick(Marker marker) {
//                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CariAgenActivity.this);
//                                                        Log.d("string", marker.getSnippet());
//                                                        StringTokenizer tokens = new StringTokenizer(marker.getSnippet(), ";");
//                                                        String alamat = tokens.nextToken();
//                                                        String email = tokens.nextToken();
//                                                        String telp = tokens.nextToken();
//                                                        String jarak = tokens.nextToken();
//                                                        final String lat = tokens.nextToken();
//                                                        final String longi = tokens.nextToken();
//                                                        alertDialog.setCancelable(false);
//                                                        alertDialog.setTitle(marker.getTitle());
//                                                        alertDialog.setMessage(Html.fromHtml(alamat+"<br>"+email+"<br>"+telp+"<br>"+jarak+" KM"));
//                                                        alertDialog.setPositiveButton("Direction",
//                                                                new DialogInterface.OnClickListener() {
//                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                        String uri = "http://maps.google.com/maps?f=d&hl=en&daddr="+lat+","+longi;
//                                                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                                                                        startActivity(Intent.createChooser(intent, "Select an application"));
//                                                                    }
//                                                                });
//
//                                                        // on pressing cancel button
//                                                        alertDialog.setNegativeButton("Cancel",
//                                                                new DialogInterface.OnClickListener() {
//                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                        dialog.dismiss();
//                                                                    }
//                                                                });
//                                                        // Showing Alert Message
//                                                        alertDialog.show();
//                                                    }
//                                                });
//                                            }
//                                            adapter = new CariAgenAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email);
//                                            detailAdapter = new CariAgenDetailAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email,latList,longList);
//                                            listViewCariAgen.setAdapter(adapter);
//                                            listViewDetailAgen.setAdapter(detailAdapter);
//                                        }else{
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//                                                    Toast.makeText(CariAgenActivity.this, text,Toast.LENGTH_LONG).show();
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                            }.execute();
//                        }else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(CariAgenActivity.this, status, Toast.LENGTH_LONG).show();
//                                    Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.d("Responses", ""+e);
//                    }
//                } else {
//                    Log.e("JSON Data", "Didn't receive any data from server!");
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            }
//        }.execute();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(CariAgenActivity.this, LoginActivity.class));
                finish();
                break;

            default:
                break;
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class getKota extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            ServiceHandler serviceClient = new ServiceHandler();

            final String json = serviceClient.makeServiceCall("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    lat+ "," +longi+ "&type=administrative_area_level_2&key="+getString(R.string.google_api_key)+"&language=id",
                    ServiceHandler.GET);
            return json;
        }
    }

    public void getDataAgent(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("findAgent");
        parameter.add(String.valueOf(longi));
        parameter.add(String.valueOf(lat));
        parameter.add(kota);
        parameter.add(spinnerRadius.getSelectedItem().toString());

        new SoapClientMember(){
            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(CariAgenActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try {
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            Vector<SoapObject> vector = (Vector<SoapObject>) result.getProperty("list");
                            for (SoapObject so : vector){
                                String agenNama = so.getProperty("agent_name").toString();
                                String agenLat = so.getProperty("agent_lat").toString();
                                String agenLong = so.getProperty("agent_long").toString();
                                String agenAddress = so.getProperty("agent_address").toString();
                                String agenEmail= null, agenPhone = null;
                                Log.d("hasil agen", ""+agenNama);
                                try {
                                    agenEmail = so.getProperty("agent_email").toString();
                                }catch (Exception e){
                                    if (agenEmail == null){
                                        agenEmail = "-";
                                    }
                                    Log.d("agen email", ""+e);
                                }
                                try {
                                    agenPhone = so.getProperty("agent_phone").toString();
                                    Log.d("agen phone", ""+agenPhone);
                                }catch (Exception e){
                                    if (agenPhone == null){
                                        agenPhone = "-";
                                    }
                                    Log.d("agen phone", ""+e);
                                }
                                Log.d("agen phone", ""+agenPhone);
                                String agenDistance = so.getProperty("distance").toString();
                                nama.add(agenNama);
                                alamat.add(agenAddress);
                                jarak.add(agenDistance);
                                email.add(agenEmail);
                                telp.add(agenPhone);
                                latList.add(agenLat);
                                longList.add(agenLong);
                                Log.d("hasil soap", ""+agenNama);
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(agenLat), Double.parseDouble(agenLong)))
                                        .title(agenNama)
                                        .snippet(agenAddress+"; Email : "+agenEmail+"; Telp : "+agenPhone+"; Jarak : "+agenDistance+";"+agenLat+";"+agenLong)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
                                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CariAgenActivity.this);
                                        Log.d("string", marker.getSnippet());
                                        StringTokenizer tokens = new StringTokenizer(marker.getSnippet(), ";");
                                        String alamat = tokens.nextToken();
                                        String email = tokens.nextToken();
                                        String telp = tokens.nextToken();
                                        String jarak = tokens.nextToken();
                                        final String lat = tokens.nextToken();
                                        final String longi = tokens.nextToken();
                                        alertDialog.setCancelable(false);
                                        alertDialog.setTitle(marker.getTitle());
                                        alertDialog.setMessage(Html.fromHtml(alamat+"<br>"+email+"<br>"+telp+"<br>"+jarak+" KM"));
                                        alertDialog.setPositiveButton("Direction",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String uri = "http://maps.google.com/maps?f=d&hl=en&daddr="+lat+","+longi;
                                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                                        startActivity(Intent.createChooser(intent, "Select an application"));
                                                    }
                                                });

                                        // on pressing cancel button
                                        alertDialog.setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Showing Alert Message
                                        alertDialog.show();
                                    }
                                });
                            }
                            adapter = new CariAgenAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email);
                            detailAdapter = new CariAgenDetailAdapter(CariAgenActivity.this, nama,alamat,jarak,telp,email,latList,longList);
                            listViewCariAgen.setAdapter(adapter);
                            listViewDetailAgen.setAdapter(detailAdapter);
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(CariAgenActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Responses", ""+e);
                    }
                }
            }
        }.execute(parameter);
    }
}
