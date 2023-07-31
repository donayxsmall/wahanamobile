package com.wahana.wahanamobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.wahana.wahanamobile.Data.SuratJalan;
import com.wahana.wahanamobile.adapter.DeliveryNoteAdapter;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DeliveryNote extends DrawerHelper {
    private static final String TAG = "DeliveryNote";

    ProgressDialog progressDialog;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan,total_sj,total_ttk,total_terkirim,total_belum,total_belum_update,total_belum_ST;
    ImageView foto;
    ListView listViewDeliveryNote;
    DatabaseHandler db;
    List<SuratJalan> sjList = new ArrayList<SuratJalan>();
    private SimpleDateFormat dateFormatter;
    String date;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_note);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        session = new SessionManager(DeliveryNote.this);
        session.checkLogin();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        db = new DatabaseHandler(DeliveryNote.this);
        db.deleteSJ();

        View mCustomView = mInflater.inflate(R.layout.abs_ttk_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        ImageView calendarButton = (ImageView) mCustomView
                .findViewById(R.id.calendar_logo);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryNote.this, MainActivity.class);
                startActivity(intent);
            }
        });
        calendarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog SortDatePickerDialog = new DatePickerDialog(DeliveryNote.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date = dateFormatter.format(newDate.getTime());
                        populatePerDay();
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                SortDatePickerDialog.show();
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(DeliveryNote.this, R.style.AppTheme_Dark_Dialog);
        username = session.getUsername();
        user_id = session.getID();

        listViewDeliveryNote = (ListView) findViewById(R.id.list_delivery_note);
        total_sj = (TextView) findViewById(R.id.total_surat_jalan_isi);
        total_ttk = (TextView) findViewById(R.id.total_ttk_isi);
        total_terkirim = (TextView) findViewById(R.id.total_terkirim_isi);
        total_belum = (TextView) findViewById(R.id.total_ttk_belum_terkirim_isi);
        total_belum_update = (TextView)findViewById(R.id.total_ttk_belum_terupdate_isi);
        total_belum_ST = (TextView)findViewById(R.id.total_belum_serah_terima_bt_isi);

        submitForm();
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
        parameter.add("listDeliveryOrder");
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
                            Toast.makeText(DeliveryNote.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            for (int i = 2; i<result.getPropertyCount(); i++) {
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String noSurat = so.getProperty("deliveryOrderNumber").toString();
                                String tgl = so.getProperty("deliveryOrderDate").toString();
                                String TTKterkirim = so.getProperty("sentCount").toString();
                                String TTKtidak = so.getProperty("notSentCount").toString();
                                String TTKtotal = so.getProperty("totalCount").toString();
                                String TTKonprogress = so.getProperty("activeCount").toString();
                                int belumST = Integer.parseInt(TTKtotal)-Integer.parseInt(TTKterkirim)-Integer.parseInt(TTKtidak)-Integer.parseInt(TTKonprogress);
                                db.addSJ(new SuratJalan(noSurat, tgl, Integer.parseInt(TTKtotal), Integer.parseInt(TTKterkirim),
                                        Integer.parseInt(TTKtidak), Integer.parseInt(TTKonprogress), belumST));
                            }
                            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar cal = GregorianCalendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.DAY_OF_YEAR, -5);
                            Date daysBeforeDate = cal.getTime();
                            date = inFormat.format(daysBeforeDate);
                            populate();
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DeliveryNote.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DeliveryNote.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private void populate(){
        sjList = db.getAllDataSJ(date);
        listViewDeliveryNote.setAdapter(new DeliveryNoteAdapter(DeliveryNote.this, sjList));
        total_sj.setText(sjList.size()+" SJ");
        int totalSJ = 0;
        int terkirim = 0;
        int tidakTerkirim = 0;
        int belumUpdate = 0;
        int belumST = 0;
        for (int a = 0; a < sjList.size(); a++){
            SuratJalan sj = sjList.get(a);
            totalSJ = totalSJ + sj.getTotal();
            terkirim = terkirim + sj.getTerkirim();
            tidakTerkirim = tidakTerkirim + sj.getBelum();
            belumUpdate = belumUpdate + sj.getAktif();
            belumST = belumST +sj.getSerah();
        }
        total_ttk.setText(totalSJ+" TTK");
        total_terkirim.setText(terkirim+" TTK");
        total_belum.setText(tidakTerkirim+" TTK");
        total_belum_update.setText(belumUpdate+" TTK");
        total_belum_ST.setText(belumST+" TTK");
    }

    private void populatePerDay(){
        sjList = db.getSJPerDay(date);
        listViewDeliveryNote.setAdapter(new DeliveryNoteAdapter(DeliveryNote.this, sjList));
        total_sj.setText(sjList.size()+" SJ");
        int totalSJ = 0;
        int terkirim = 0;
        int tidakTerkirim = 0;
        int belumUpdate = 0;
        int belumST = 0;
        for (int a = 0; a < sjList.size(); a++){
            SuratJalan sj = sjList.get(a);
            totalSJ = totalSJ + sj.getTotal();
            terkirim = terkirim + sj.getTerkirim();
            tidakTerkirim = tidakTerkirim + sj.getBelum();
            belumUpdate = belumUpdate + sj.getAktif();
            belumST = belumST +sj.getSerah();
        }
        total_ttk.setText(totalSJ+" TTK");
        total_terkirim.setText(terkirim+" TTK");
        total_belum.setText(tidakTerkirim+" TTK");
        total_belum_update.setText(belumUpdate+" TTK");
        total_belum_ST.setText(belumST+" TTK");
    }

}
