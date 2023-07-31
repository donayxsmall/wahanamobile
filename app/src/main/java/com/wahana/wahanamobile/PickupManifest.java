package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class PickupManifest extends DrawerHelper {
    private static final String TAG = "PickupManifest";
    ProgressDialog progressDialog;

    String agentcode, nomorManifest;
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        nomorManifest = myIntent.getStringExtra("namaManifest"); // will return "FirstKeyValue"
        setContentView(R.layout.activity_pickup_manifest);
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
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PickupManifest.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(PickupManifest.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupManifest.this);
        DatabaseHandler db = new DatabaseHandler(PickupManifest.this);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();
        username = session.getUsername();
        user_id = session.getID();
        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_agent.setText(agentcode);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        manifest_label = (TextView) findViewById(R.id.manifest_label);
        manifest_isi = (TextView) findViewById(R.id.manifest_isi);
        hasil_manifest = (TextView) findViewById(R.id.hasil_manifest);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        kode_agent.setTypeface(type);
        manifest_label.setTypeface(type);
        manifest_isi.setTypeface(type);
        hasil_manifest.setTypeface(type);
        manifest_isi.setText(nomorManifest);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        pengisi.setText(user_id);

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

    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
        finish();
    }
    


}
