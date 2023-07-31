package com.wahana.wahanamobile.Ops.SJP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.Role;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InputNamaPenerus extends DrawerHelper {
    private static final String TAG = "InputNamaPenerus";

    ProgressDialog progressDialog;
    public ArrayAdapter<CharSequence> adapter;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
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
    AutoCompleteTextView spinner;
    private final int REQUEST_FOR_PENERUS = 1;
    EditText idPenerus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_namapenerus_sjp);
//        super.onCreateDrawer(this);
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
                Intent intent = new Intent(InputNamaPenerus.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);


        progressDialog = new ProgressDialog(InputNamaPenerus.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNamaPenerus.this);
        user_id = session.getID();

        spinner = (AutoCompleteTextView) findViewById(R.id.spinner_namapenerus);
//        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
//                R.layout.simple_spinner_list, new Role[] {
//                new Role( "101", "JNE"),
//                new Role( "102", "JNT")
//
//        });
//        adapter = ArrayAdapter.createFromResource(this, R.array.SJP, R.layout.simple_spinner_list);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerAdapter);
        btnInput = (Button) findViewById(R.id.input_button);
        idPenerus = (EditText) findViewById(R.id.code_penerus);

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

                if (!validateKode()) {
                    return;
                }

                submitForm();
            }
        });

        spinner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(InputNamaPenerus.this,SearchPenerusActivity.class),REQUEST_FOR_PENERUS);
            }

        });

    }

    private void submitForm() {
        Intent pindah = new Intent(this, SuratJalanPenerusScanner.class);
//        int via = spinner.getSelectedItemPosition();
        pindah.putExtra("via",idPenerus.getText().toString());
        startActivity(pindah);
        finish();
    }

    private boolean validateKode() {
        if (spinner.getText().toString().trim().isEmpty()) {
            spinner.setError("Masukkan Nama Penerus");

            Toast.makeText(InputNamaPenerus.this, "Masukkan Nama Penerus",Toast.LENGTH_LONG).show();

            return false;
        } else {
            spinner.setError(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, OpsMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){

            String penerus = data.getStringExtra("penerus");
            String id = data.getStringExtra("id");
            spinner.setText(penerus);
            idPenerus.setText(id);
        }
    }
}

