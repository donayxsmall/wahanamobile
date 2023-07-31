package com.wahana.wahanamobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.adapter.StatusAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class PickupAirwaysStatus extends DrawerHelper {
    private static final String TAG = "PickupAirwaysStatus";
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
    Context context;
    ProgressDialog progressDialog;
    String agentcode, sortingcode;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id, datemin, datemax;
    TextView nama,jabatan, kode_sortir;
    ImageView foto;
    @SuppressWarnings("ResourceType")
    Button submit;
    StatusAdapter adapter;
    List<TTk> ttkList = new ArrayList<TTk>();
    DatabaseHandler db;
    int totalYa = 0;
    int totalTidak = 0;
    LinearLayout layoutForm;
    private static LayoutInflater inflater=null;
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_airways_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        super.onCreateDrawer(this);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
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
//                Intent intent = new Intent(PickupAirwaysStatus.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(PickupAirwaysStatus.this, R.style.AppTheme_Dark_Dialog);

        layoutForm = (LinearLayout) findViewById(R.id.main);
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        session = new SessionManager(PickupAirwaysStatus.this);
        user_id = session.getID();
        Bundle extras = getIntent().getExtras();
        sortingcode = extras.getString("sortingcode");

        db = new DatabaseHandler(PickupAirwaysStatus.this);
        KodeSortir text = db.getDate(sortingcode);
        datemin = text.getDatemin();
        datemax = text.getDatemax();

        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

        submit = (Button) findViewById(R.id.input_button);
        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_sortir = (TextView) findViewById(R.id.kode_sortir_isi);
        kode_sortir.setText(sortingcode);
        kode_agent.setText(agentcode);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickupAirwaysStatus.this);
                alertDialog.setCancelable(false);
                // Setting Dialog Title
                alertDialog.setTitle("Status TTK");
                // Setting Dialog Message
                alertDialog.setMessage("Apakah Anda Yakin Semua Data Sudah Sesuai ?");
                // On pressing Settings button
                alertDialog.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getAllValue();
                            }
                        });
                // on pressing cancel button
                alertDialog.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
            }
        });

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

//        listViewKoli = (ListView) findViewById(R.id.list_airway_status);

        username = session.getUsername();
        user_id = session.getID();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("pickupGetPackageListPerSortingCode");
        parameter.add(session.getSessionID());
        parameter.add(session.getID());
        parameter.add(agentcode);
        parameter.add(sortingcode);
        parameter.add(datemin);
        parameter.add(datemax);
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
                            Toast.makeText(PickupAirwaysStatus.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            for (int i = 2; i<result.getPropertyCount(); i++){
                                int noStatus;
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String nottk = so.getProperty("packageNumber").toString();
                                String status = so.getProperty("packageStatus").toString();
                                if (status.toUpperCase().equals("Perlu Diverifikasi")){
                                    noStatus = 0;
                                }else {
                                    noStatus = 1;
                                }
                                TTk ttk = new TTk();
                                ttk.setNottk(nottk);
                                ttk.setStatus(noStatus);
                                ttkList.add(ttk);
                                DatabaseHandler db = new DatabaseHandler(PickupAirwaysStatus.this);
                                db.addTTK(new TTk(nottk, noStatus, sortingcode));
                            }
//                            adapter = new StatusAdapter(PickupAirwaysStatus.this, sortingcode, ttkList);
//                            listViewKoli.setAdapter(adapter);
                            for (int i = 0 ; i < ttkList.size(); i++){
                                tambahDaftar(ttkList.get(i),i+1);
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupAirwaysStatus.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupAirwaysStatus.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
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

    public void getAllValue(){
//        View parentView = null;
        int a = 1;
        ttkList.clear();
        Log.d("listviewkoli",layoutForm.getChildCount()+"");
        for (int i = 0; i < layoutForm.getChildCount(); i++) {
//            parentView = getViewByPosition(i, listViewKoli);
            final View parentView = layoutForm.getChildAt(i);
            int status = 0;

            String nottk = ((TextView) parentView.findViewById(R.id.no_ttk_isi)).getText().toString();
            int radio = ((RadioGroup) parentView.findViewById(R.id.radio_group_label)).getCheckedRadioButtonId();
            try{
                String hasil = ((RadioButton) parentView.findViewById(radio)).getText().toString();
                if (hasil.equals("YA")){
                    status = 1;
                    totalYa = totalYa+1;
                }else if(hasil.equals("TIDAK")){
                    status = 0;
                    totalTidak = totalTidak+1;
                }
            }catch (Exception e){
                Log.d("Exception", e+"");
                a = 0;
            }
            TTk ttk = new TTk();

            ttk.setNottk(nottk);
            ttk.setStatus(status);
            Log.d("nottk", ttk.getNottk()+" "+ttk.getStatus());
            ttkList.add(ttk);
        }
        Log.d("total ya", totalYa+"");
        Log.d("total tidak", totalTidak+"");
        if (a == 0){
            ttkList.clear();
            Toast.makeText(PickupAirwaysStatus.this, "Semua Data Mohon Diisi", Toast.LENGTH_SHORT).show();
        }else {
            new status().execute();
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private class status extends AsyncTask<ArrayList<String>, Void, SoapObject> {
        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
        String URL = "http://ws.wahana.com/ws/mobileService/";

//        String SOAP_ACTION = "http://ws.wahana.com/ws/mobileService/";
//        String NAMESPACE = "http://ws.wahana.com/ws/mobileService/";
//        String URL = "http://ws.wahana.com/ws/mobileService/";

        String METHOD_NAME = "pickupVerPackage";
        SoapObject hasil = null;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        @Override
        protected SoapObject doInBackground(ArrayList<String>... params) {
            Log.i(TAG, "doInBackground");
            try {
                SOAP_ACTION = SOAP_ACTION+METHOD_NAME;
                Log.d("action", SOAP_ACTION);
                Random r = new Random();
                int requestCode = r.nextInt(9999);
                String rc = String.valueOf(requestCode);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("sessionId", session.getSessionID());
                Request.addProperty("requestId", rc);
                Request.addProperty("employeeCode", session.getID());
                Request.addProperty("agentCode", agentcode);
                Request.addProperty("sortingCode", sortingcode);
                for (int i=0; i<ttkList.size(); i++){
                    TTk ttk = ttkList.get(i);
                    SoapObject req = new SoapObject();
                    req.addProperty("packageNumber", ttk.getNottk());
                    req.addProperty("verificationStatus", ttk.getStatus());
                    Request.addProperty("verifiedPackageList", req);
                    Log.d("action", ttk.getNottk()+ttk.getStatus());
                }
                Log.d("req1",Request+"");
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("android:silakan".getBytes())));

                transport.call(SOAP_ACTION, soapEnvelope,headerList);
                hasil = (SoapObject) soapEnvelope.bodyIn;
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }

            return hasil;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            super.onPostExecute(result);
            Log.d("hasil soap", ""+result);
            if(result==null){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PickupAirwaysStatus.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }else {
                try {
                    final String text = result.getProperty(1).toString();
                    final String rescode= result.getProperty(0).toString();
                    if (rescode.equals("1")) {
                        progressDialog.dismiss();
                        Intent pindah = new Intent(PickupAirwaysStatus.this, PickupConclusion.class);
                        Toast.makeText(PickupAirwaysStatus.this, "Data Berhasil Disimpan",Toast.LENGTH_LONG).show();
                        pindah.putExtra("ttk", "tes");
                        setResult(RESULT_OK, pindah);
                        db.updateSortir(sortingcode, totalYa, totalTidak);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupAirwaysStatus.this, text,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PickupAirwaysStatus.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    public class Holder
    {
        RadioGroup radioGroup;
        RadioButton radioYa;
        RadioButton radioTidak;
        TextView nomorTTK,no;
        RelativeLayout tabelStatus;
        TextView kodeSortir;
        Button submitButton;
    }

    private void tambahDaftar(TTk ttk, int no){
        final Holder holder=new Holder();
        View view = inflater.inflate(R.layout.status_listview, null);
        holder.tabelStatus = (RelativeLayout)view.findViewById(R.id.tabel_status);
        holder.radioGroup = (RadioGroup)view.findViewById(R.id.radio_group_label);
        holder.radioYa = (RadioButton)view.findViewById(R.id.radio0);
        holder.radioTidak = (RadioButton)view.findViewById(R.id.radio1);
        holder.nomorTTK = (TextView) view.findViewById(R.id.no_ttk_isi);
        //holder.kodeSortir = (TextView) view.findViewById(R.id.kode_sortir_isi);
        holder.no = (TextView) view.findViewById(R.id.no_ttk_label);

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        if (btn.getText().toString().equals("YA")){
//                            Toast.makeText(PickupAirwaysStatus.this, "1", Toast.LENGTH_SHORT).show();
                        }else if(btn.getText().toString().equals("TIDAK")){
//                            Toast.makeText(PickupAirwaysStatus.this, "0", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        int status = ttk.getStatus();
        if (status == 1){
            holder.tabelStatus.setBackgroundColor(this.getResources().getColor(R.color.table_status));
        }
        String nottk = ttk.getNottk();
        holder.nomorTTK.setText(nottk);
        holder.no.setText(no+". NO TTK");
        Typeface type = Typeface.createFromAsset(PickupAirwaysStatus.this.getAssets(),"font/OpenSansLight.ttf");
//        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        //holder.kodeSortir.setText(KodeSortir[0]);
        holder.radioYa.setTypeface(type);
        holder.radioTidak.setTypeface(type);
        holder.nomorTTK.setTypeface(type);
        //holder.kodeSortir.setTypeface(type);

        layoutForm.addView(view);
    }
}
