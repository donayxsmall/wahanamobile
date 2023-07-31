package com.wahana.wahanamobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.adapter.AttendanceReportAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AttendanceReport extends DrawerHelper implements View.OnClickListener {
    private static final String TAG = "AttendanceReport";
    ProgressDialog progressDialog;
    ImageButton changeDate1;
    ImageButton changeDate2;
    public static String [] tanggal;
    public static String [] hari;
    public static String [] jamMasuk;
    public static String [] jamPulang;
    public static String [] keterangan;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;
    TextView jabatan, nama, dari, sampai;
    ImageView foto;
    Button submit;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ArrayList<Map<String, String>> mAbsenList;
    ListView listViewAttendanceReport;
    TextView totalMasuk, totalAbsen;
    @SuppressWarnings("ResourceType")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
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
                Intent intent = new Intent(AttendanceReport.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        progressDialog = new ProgressDialog(AttendanceReport.this, R.style.AppTheme_Dark_Dialog);
        mAbsenList = new ArrayList<Map<String, String>>();

        listViewAttendanceReport = (ListView) findViewById(R.id.list_attendance_report);
//        listViewAttendanceReport.setAdapter(new AttendanceReportAdapter(this, tanggal,hari,jamMasuk,jamPulang,keterangan));

        totalMasuk = (TextView) findViewById(R.id.total_masuk_isi);
        totalAbsen = (TextView) findViewById(R.id.total_absen_isi);
        submit = (Button) findViewById(R.id.submit_buton);
        dari = (EditText) findViewById(R.id.input_dari);
        //dari.setImeActionLabel(dari.getText(), KeyEvent.KEYCODE_ENTER);
        sampai = (EditText) findViewById(R.id.input_sampai);
        //sampai.setImeActionLabel(sampai.getText(), KeyEvent.KEYCODE_ENTER);
        changeDate1 = (ImageButton) findViewById(R.id.change_date1);
        changeDate2 = (ImageButton) findViewById(R.id.change_date2);
        InputMethodManager mgr_dari = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr_dari.hideSoftInputFromWindow(dari.getWindowToken(), 0);
        InputMethodManager mgr_sampai = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr_sampai.hideSoftInputFromWindow(sampai.getWindowToken(), 0);

        setDateTimeField();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        session = new SessionManager(AttendanceReport.this);
        session.checkLogin();
        username = session.getUsername();
        user_id = session.getID();


        Calendar c = Calendar.getInstance();
        String formattedDate = dateFormatter.format(c.getTime());
        dari.setText(formattedDate);
        sampai.setText(formattedDate);
    }

    @Override
    public void onClick(View v) {
        if(v == changeDate1) {
            fromDatePickerDialog.show();
        } else if(v == changeDate2) {
            toDatePickerDialog.show();
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

    public boolean validate() {
        boolean valid = true;

        if(dari.getText().toString().isEmpty())
        {
            dari.setError("Masukkan Tanggal");
            valid = false;
        } else {
            dari.setError(null);
        }

        if(sampai.getText().toString().isEmpty())
        {
            sampai.setError("Masukkan Tanggal");
            valid = false;
        } else {
            sampai.setError(null);
        }
        return valid;
    }

    private void setDateTimeField() {
        changeDate1.setOnClickListener(this);
        changeDate2.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dari.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sampai.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void submitForm() {
        if (!validate()) {
            return;
        }
        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("attendanceList");
        parameter.add(session.getSessionID());
        parameter.add(session.getID());
        parameter.add(dari.getText().toString());
        parameter.add(sampai.getText().toString());
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
                            Toast.makeText(AttendanceReport.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            ArrayList<String> tanggalList = new ArrayList<String>();
                            ArrayList<String> hariList = new ArrayList<String>();
                            ArrayList<String> masukList = new ArrayList<String>();
                            ArrayList<String> pulangList = new ArrayList<String>();
                            ArrayList<String> ketList = new ArrayList<String>();
                            for (int i = 2; i<result.getPropertyCount(); i++){
                                SoapObject so = (SoapObject) result.getProperty(i);
                                String date = so.getProperty("date").toString();
                                String in = null;
                                String out = null;
                                try {
                                    in = so.getProperty("attendanceIn").toString();
                                    masukList.add(in);
                                }catch(Exception e){
                                    e.printStackTrace();
                                    Log.d("eror", e+"");
                                    if (in == null || out == null){
                                        masukList.add("-");
                                    }
                                }
                                try {
                                    out = so.getProperty("attendanceOut").toString();
                                    pulangList.add(out);
                                }catch(Exception e){
                                    e.printStackTrace();
                                    Log.d("eror", e+"");
                                    if (in == null || out == null){
                                        pulangList.add("-");
                                    }
                                }
                                String comment = so.getProperty("comment").toString();
                                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date tgl = new Date();
                                try {
                                    tgl = inFormat.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (comment.contains("any")){
                                    comment = "-";
                                }
                                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                                String day = outFormat.format(tgl);
                                Log.d("track", date+" <> "+day+"<"+tgl);
                                tanggalList.add(date);
                                hariList.add(day);
                                ketList.add(comment);
//                            Map<String, String> absen = new HashMap<String, String>();
//                            absen.put("date", date);
//                            absen.put("day", day);
//                            absen.put("attendanceIn", "");
//                            absen.put("attendanceOut", "");
//                            absen.put("comment", comment);
//                            mAbsenList.add(absen);
                            }
                            tanggal = tanggalList.toArray(new String[tanggalList.size()]);
                            hari = hariList.toArray(new String[hariList.size()]);
                            jamMasuk = masukList.toArray(new String[masukList.size()]);
                            jamPulang = pulangList.toArray(new String[pulangList.size()]);
                            keterangan = ketList.toArray(new String[ketList.size()]);
                            listViewAttendanceReport.setAdapter(new AttendanceReportAdapter(AttendanceReport.this, tanggal,hari,jamMasuk,jamPulang,keterangan));
                            listViewAttendanceReport.setVisibility(View.VISIBLE);
                            int masuk = 0;
                            int absen = 0;
                            for (int z = 0; z < jamMasuk.length; z++){
                                String jam = jamMasuk[z];
                                if (jam.equals("-")){
                                    String ket = keterangan[z];
                                    if (ket.toUpperCase().equals("N/A")){
                                        absen = absen + 1;
                                    }else {
                                        absen = absen + 0;
                                    }
                                }else {
                                    masuk = masuk + 1;
                                }
                            }
                            totalMasuk.setText(masuk+" Hari");
                            totalAbsen.setText(absen+" Hari");
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(AttendanceReport.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AttendanceReport.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

}
