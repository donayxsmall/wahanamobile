package com.wahana.wahanamobile.Ops.PickupRetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VerifikasiTTKBatal extends AppCompatActivity {

    private static final String TAG = "PickupVerifikasiTTKBatal";

    ProgressDialog progressDialog;

    private TextInputLayout inputLayoutVerifikasi;
    public EditText inputVerifikasi;
    private Button btnInput;
    String agentcode;
    TextView kode_kurir, kode_agent;
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
    DatabaseHandler db;
//    Activity context;

    Context context = this;
    String kodeagen,urllink,kodesortir,kodeverifikasi,flagsortir;
    UserAPIService mApiInterface;

    LinearLayout layoutForm;
    private static LayoutInflater inflater = null;

    List<ListTtkPickup> ttklistnotfound;
    ArrayList<String> ttks = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_ttkbatal);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(VerifikasiTTKBatal.this));

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
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PickupVerifikasi.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent intent = getIntent();
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        kodesortir=intent.getStringExtra("kodesortir");
        flagsortir=intent.getStringExtra("flagsortir");


        progressDialog = new ProgressDialog(VerifikasiTTKBatal.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(VerifikasiTTKBatal.this);
        user_id = session.getID();
        db = new DatabaseHandler(VerifikasiTTKBatal.this);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_agent.setText(agentcode);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

        btnInput = (Button) findViewById(R.id.input_button);

        layoutForm = (LinearLayout) findViewById(R.id.main);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        kode_agent.setTypeface(type);

        ttklistnotfound=db.getAllDataPickupPreviewTTKtidakditemukan();


        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(VerifikasiTTKBatal.this, KodeVerifikasiTTKBatal.class);
//                startActivity(intent);
//                finish();

                submit();

            }
        });

        getdata();

    }

    private void getdata() {

        for (int i = 0; i < ttklistnotfound.size() ; i++) {
//            tambahDaftar(i);

            tambahDaftar(ttklistnotfound.get(i),i);
        }

    }

    public class Holder {
        TextView nottk;
        TextView nomor;
        TextView keteranganlabel;
        RadioGroup inputBatal;
    }

    private void tambahDaftar(final ListTtkPickup ttknotfound,int i) {

        int no = i+1;

        final Holder holder = new Holder();
        final View view = inflater.inflate(R.layout.adapter_verifikasi_ttk_batal, null);
        holder.nottk = (TextView) view.findViewById(R.id.nottk);
        holder.nomor = (TextView) view.findViewById(R.id.nomor);
        holder.keteranganlabel = (TextView) view.findViewById(R.id.keterangan_label);
        holder.inputBatal = (RadioGroup) view.findViewById(R.id.radio_batal);

        String tex="";
        if(ttknotfound.getStatus().equals("1070")){
            holder.inputBatal.check(holder.inputBatal.getChildAt(0).getId());
            for (int j = 0; j < holder.inputBatal.getChildCount(); j++) {
                holder.inputBatal.getChildAt(j).setEnabled(false);
            }

            holder.keteranganlabel.setText(" (Sudah Pernah dilakukan Pending)");
        }else{

            holder.keteranganlabel.setText("");
        }



        holder.nottk.setText(ttknotfound.getTtk());
        holder.nomor.setText(""+no);

        layoutForm.addView(view);

    }


    private void submit(){

        JSONArray jsArray = new JSONArray();
        String status;

        for (int i = 0; i < layoutForm.getChildCount(); i++) {

            final View parentView = layoutForm.getChildAt(i);
            TextView nottk=(TextView)parentView.findViewById(R.id.nottk);
            RadioGroup inputBatal = (RadioGroup) parentView.findViewById(R.id.radio_batal);
            int radio = inputBatal.getCheckedRadioButtonId();

            try {
                String hasil = ((RadioButton) parentView.findViewById(radio)).getText().toString();
                if (hasil.equals("Batal")){
                    status = "1";
                }else if(hasil.equals("Pending")){
                    status = "2";
                }else{
                    status = "";
                }
            }catch (Exception e){
                Log.d("Exception", e+"");
                status = "";
            }

            JSONObject jsdata = new JSONObject();
            try {
                jsdata.put("status", status);
                jsdata.put("nottk", nottk.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(jsdata);
        }

        int jumceklis=0;

        JSONArray jsArrayttkbatal = new JSONArray();
        JSONArray jsArrayttkpending = new JSONArray();

        for (int i=0; i<jsArray.length(); i++) {
            try {
                JSONObject isi = jsArray.getJSONObject(i);

                String stat = isi.getString("status");

                if (stat.equals("")){
                    jumceklis++;
                }else if(stat.equals("1")){
                    jsArrayttkbatal.put(isi.getString("nottk"));
                }else if (stat.equals("2")){
                    jsArrayttkpending.put(isi.getString("nottk"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (jumceklis > 0){

            new SweetAlertDialog(VerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error...")
                    .setContentText("TTK Harus di pilih semua")
                    .show();

        }else{

            if (flagsortir.equals("1")){
                ttks = db.getAllTTKPUfix(agentcode,kodeverifikasi,kodesortir);
            }else{
                ttks = db.getAllTTKPUfixAll(agentcode,kodeverifikasi);
            }


            JSONObject json = new JSONObject();
            try {
                JSONArray jsArrayttkscan = new JSONArray(ttks);
//            json.put("data", jsArray);
                json.put("ttkscan", jsArrayttkscan);
                json.put("ttkbatal", jsArrayttkbatal);
                json.put("ttkpending", jsArrayttkpending);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("hasil json", ""+json);

            Intent intent = new Intent(VerifikasiTTKBatal.this, KodeVerifikasiTTKBatal.class);
            intent.putExtra("hasilttk", json.toString());
            intent.putExtra("kodeverifikasi", kodeverifikasi);
            intent.putExtra("kodesortir", kodesortir);
            intent.putExtra("flagsortir", flagsortir);
            startActivity(intent);
            finish();

        }

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
