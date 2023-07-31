package com.wahana.wahanamobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.adapter.DeliveryNoteListItemAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DeliveryNoteList extends DrawerHelper {
    private static final String TAG = "DeliveryNoteList";

    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar;
    public static String [] koli;
    public static String [] berat;
    public static String [] status;
    public static String [] tanggal={"12 Juni 2016","13 Juni 2016","14 Juni 2016"};
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private SimpleDateFormat dateFormatter;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan,total_ttk,total_koli,total_berat,ttk_terkirim,ttk_belum,total_belum_update,total_belum_ST;
    ImageView foto;
    ListView listViewDeliveryNoteItem;
    DatabaseHandler db;
    List<ListTTK> listDateTTk = new ArrayList<ListTTK>();
    List<ListTTK> listTTk = new ArrayList<ListTTK>();
    String date;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_note_list);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_ttk_layout, null);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar c = Calendar.getInstance();
        date = dateFormatter.format(c.getTime());

        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        ImageView calendarButton = (ImageView) mCustomView
                .findViewById(R.id.calendar_logo);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryNoteList.this, MainActivity.class);
                startActivity(intent);
            }
        });
        calendarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog SortDatePickerDialog = new DatePickerDialog(DeliveryNoteList.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date = dateFormatter.format(newDate.getTime());
                        populateByDate();
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                SortDatePickerDialog.show();
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        session = new SessionManager(DeliveryNoteList.this);
        session.checkLogin();
        db = new DatabaseHandler(DeliveryNoteList.this);
        db.deleteListTTK();
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        total_belum_update = (TextView)findViewById(R.id.total_ttk_belum_terupdate_isi);
        total_belum_ST = (TextView)findViewById(R.id.total_belum_serah_terima_bt_isi);

        progressDialog = new ProgressDialog(DeliveryNoteList.this, R.style.AppTheme_Dark_Dialog);
        listViewDeliveryNoteItem = (ListView) findViewById(R.id.list_delivery_note_list_item);
        total_ttk = (TextView) findViewById(R.id.total_jumlah_ttk_isi);
        total_koli = (TextView) findViewById(R.id.total_jumlah_koli_isi);
        total_berat = (TextView) findViewById(R.id.total_berat_isi);
        ttk_terkirim = (TextView) findViewById(R.id.total_terkirim_isi);
        ttk_belum = (TextView) findViewById(R.id.total_ttk_belum_terkirim_isi);
        submitForm();
        username = session.getUsername();
        user_id = session.getID();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitForm() {
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("listPackageByCourier");
        parameter.add(session.getSessionID());
        parameter.add(session.getID());
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
                            Toast.makeText(DeliveryNoteList.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            ArrayList<String> noTTKList = new ArrayList<String>();
                            ArrayList<String> koliList = new ArrayList<String>();
                            ArrayList<String> beratList = new ArrayList<String>();
                            ArrayList<String> statusList = new ArrayList<String>();
                            for (int i = 2; i<result.getPropertyCount(); i++) {
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String packageStat = so.getProperty("packageStatus").toString();
                                if (packageStat.toUpperCase().equals("TERKIRIM")){
                                    packageStat = "TK";
                                }else if (packageStat.toUpperCase().equals("BELUM TERKIRIM")){
                                    packageStat = "BT";
                                }else{
                                    packageStat = "OP";
                                }
                                db.addTTKList(new ListTTK(so.getProperty("packageNumber").toString(), Integer.parseInt(so.getProperty("bag").toString()),
                                        Integer.parseInt(so.getProperty("weight").toString()), packageStat, so.getProperty("packageDate").toString()));
                            }
                            populate();
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DeliveryNoteList.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DeliveryNoteList.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private void populate(){
        listDateTTk = db.getAllListTTKDate();
        listTTk = db.getAllListTTK();
        listViewDeliveryNoteItem.setAdapter( new DeliveryNoteListItemAdapter(DeliveryNoteList.this, listDateTTk));
        total_ttk.setText(listTTk.size()+" TTK");
        int totalTTK = listTTk.size();
        int totalKoli = 0;
        int totalBerat = 0;
        int totalTerkirim = 0;
        int totalBelum = 0;
        int belumUpdate = 0;
        for (int a = 0; a < listTTk.size(); a++){
            ListTTK ttk = listTTk.get(a);
            totalKoli = totalKoli + ttk.getKoli();
            totalBerat = totalBerat + ttk.getBerat();
            String stat = ttk.getStatus();
            if (stat.equals("TK")){
                totalTerkirim = totalTerkirim + 1;
            }else if(stat.equals("BT")){
                totalBelum = totalBelum + 1;
            }else if(stat.equals("OP")){
                belumUpdate = belumUpdate +1;
            }
        }
        total_koli.setText(totalKoli+" KOLI");
        total_berat.setText(totalBerat+" Kg");
        ttk_terkirim.setText(totalTerkirim+" TTK");
        ttk_belum.setText(totalBelum+" TTK");
        total_belum_update.setText(belumUpdate+" TTK");
        total_belum_ST.setText((totalTTK-totalTerkirim-totalBelum-belumUpdate)+" TTK");
    }

    private void populateByDate(){
        listDateTTk = db.getOneTTKByDate(date);
        listTTk = db.getAllListTTKByDate(date);
        listViewDeliveryNoteItem.setAdapter( new DeliveryNoteListItemAdapter(DeliveryNoteList.this, listDateTTk));
        total_ttk.setText(listTTk.size()+" TTK");
        int totalTTK = listTTk.size();
        int totalKoli = 0;
        int totalBerat = 0;
        int totalTerkirim = 0;
        int totalBelum = 0;
        int belumUpdate = 0;
        for (int a = 0; a < listTTk.size(); a++){
            ListTTK ttk = listTTk.get(a);
            totalKoli = totalKoli + ttk.getKoli();
            totalBerat = totalBerat + ttk.getBerat();
            String stat = ttk.getStatus();
            if (stat.equals("TK")){
                totalTerkirim = totalTerkirim + 1;
            }else if(stat.equals("BT")){
                totalBelum = totalBelum + 1;
            }else if(stat.equals("OP")){
                belumUpdate = belumUpdate +1;
            }
        }
        total_koli.setText(totalKoli+" KOLI");
        total_berat.setText(totalBerat+" Kg");
        ttk_terkirim.setText(totalTerkirim+" TTK");
        ttk_belum.setText(totalBelum+" TTK");
        total_belum_update.setText(belumUpdate+" TTK");
        total_belum_ST.setText((totalTTK-totalTerkirim-totalBelum-belumUpdate)+" TTK");
    }

}
