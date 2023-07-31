package com.wahana.wahanamobile.Hrd;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Ops.BBM.InputBBM;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.format;

public class Cuti extends DrawerHelper {

    private static final String TAG = "PengajuanCuti";
    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar,labelnoken,labelkm,labelfotokm,labelhargabbm,labelliter;
    String username,sisacuti;
    String user_id;
    String tujuan;
    String asal;
    String spinner_value;
    String idbbm;
    EditText dari,sampaitanggal,jumlahharicuti,input_kontak,visiblejmlcuti;
    ImageButton changeDate1;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    DatabaseHandler db;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Date newDate1;
    final Calendar myCalender = Calendar.getInstance();
    Button input_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti);
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
                Intent intent = new Intent(Cuti.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(Cuti.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(Cuti.this);
        user_id = session.getID();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        dari = (EditText) findViewById(R.id.input_dari);
        changeDate1 = (ImageButton) findViewById(R.id.change_date1);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sampaitanggal=(EditText)findViewById(R.id.input_samapaitanggal);
        jumlahharicuti=(EditText)findViewById(R.id.input_jumlahcuti);
        input_button=(Button)findViewById(R.id.input_button);
        input_kontak=(EditText) findViewById(R.id.input_kontak);
        visiblejmlcuti=(EditText)findViewById(R.id.visiblejmlcuti);

        sampaitanggal.setEnabled(false);

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

        this.setTitle("");


        InputMethodManager mgr_dari = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr_dari.hideSoftInputFromWindow(dari.getWindowToken(), 0);

//        setDateTimeField();

        Calendar d = Calendar.getInstance();
        d.add(Calendar.DATE, 7);
        String formattedDate1 = dateFormatter.format(d.getTime());
        dari.setText(formattedDate1);



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();

            }
        };


        changeDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCurrentDate = dari.getText().toString();
                Log.d("hasil select tglselect",""+strCurrentDate);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);

                try {
                    newDate1 = format.parse(strCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("Hasil new date",""+newDate1);

                DateFormat formating = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                String datei = formating.format(newDate1);

                String strDate = datei;
                Log.d("Hasil new datei",""+datei);
                String[] items1 = strDate.split("-");
                final int year= Integer.parseInt(items1[0]);
                final int month= Integer.parseInt(items1[1])-1;
                final int date1= Integer.parseInt(items1[2]);

                new DatePickerDialog(Cuti.this, date, year, month, date1).show();
            }
        });




        jumlahharicuti.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {



                String text = s.toString();

                // you can call or do what you want with your EditText here


                if (text.equals("")) {
                    jumlahharicuti.getText().clear();
                    sampaitanggal.getText().clear();
                } else if (Integer.parseInt(text) > 3) {
                    Log.d("Hasil sisa", "" + "lebih");
                    Log.d("Hasil cuti", "" + sisacuti);
                    Toast.makeText(Cuti.this, "Jumlah hari cuti maksimal 3 hari", Toast.LENGTH_LONG).show();
//                  umlahharicuti.getText().clear();
                }else{
                    cekjumlahcuti();
                }

//                if(s.length()==0 || s==null){
//                    cekjumlahcuti();
//                }else{
//
//                    if(Integer.parseInt(text) > 3){
//                        Log.d("Hasil sisa cuti",""+sisacuti);
//                        Toast.makeText(Cuti.this, "Jumlah hari cuti maksimal 3 hari",Toast.LENGTH_LONG).show();
//                        jumlahharicuti.getText().clear();
//                    }else if(Integer.parseInt(text) > Integer.parseInt(sisacuti)){
//                        Log.d("Hasil sisa cuti",""+sisacuti);
//                        Toast.makeText(Cuti.this, "Jumlah cuti melebihi sisa cuti, Sisa cuti : "+sisacuti,Toast.LENGTH_LONG).show();
//                    }else{
//                        cekjumlahcuti();
//                    }
//                }



//                cekjumlahcuti();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cekjumlahcuti();
                Log.d("Hasil s", "" + s);
                Log.d("Hasil cuti", "" + visiblejmlcuti.getText().toString());

            }
        });





        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateKode()) {
                    return;
                }
            }
        });


    }


//    public void onClick(View v) {
//        if(v == changeDate1) {
//            fromDatePickerDialog.show();
//        }
//    }




//    private void setDateTimeField() {
//        changeDate1.setOnClickListener(this);
//
//        String strCurrentDate = dari.getText().toString();
//        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy");
//        try {
//            newDate1 = format.parse(strCurrentDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("Hasil new date",""+newDate1);
//
////
////        Calendar newCalendar = Calendar.getInstance();
////        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
////
////            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
////                Calendar newDate = Calendar.getInstance();
////                newDate.set(year, monthOfYear, dayOfMonth);
////                dari.setText(dateFormatter.format(newDate.getTime()));
////
////                Log.d("tanggal", dateFormatter.format(dari.getText().toString())+"");
////
////                cekjumlahcuti();
////            }
////
////        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//    }


    private void cekjumlahcuti(){

        Log.d("hasil soap", ""+jumlahharicuti.getText().toString());

        username = session.getUsername();


        //String jumlahharifix=jumlahharicuti.getText().toString().trim().isEmpty()?"0":jumlahharicuti.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getjumlahcutiAndroid");
        parameter.add("50");
        parameter.add("0");
        parameter.add("jumlahhari");
        parameter.add(jumlahharicuti.getText().toString());
        parameter.add("dtsa");
        parameter.add(dari.getText().toString());
        parameter.add("ATRBACD");
        parameter.add(username);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Cek Data Cuti...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
//                Log.d("hasil soap", ""+result);
                if(result==null){
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Cuti.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String resData = result.getProperty("resData").toString();

                            JSONObject jsonObj = new JSONObject(resData);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                progressDialog.dismiss();

                                for (int i=0; i<data.length(); i++){
                                    if(i!=0){
                                        JSONArray isi = data.getJSONArray(i);
                                        sampaitanggal.setText(isi.getString(0));
                                        sisacuti=isi.getString(1);

                                        visiblejmlcuti.setText(sisacuti);

//                                        if(Integer.parseInt(jumlahharicuti.getText().toString()) > Integer.parseInt(sisacuti)){
//                                            Toast.makeText(Cuti.this, "Jumlah cuti melebihi sisa cuti, Sisa cuti : "+sisacuti,Toast.LENGTH_LONG).show();
//                                        }else{
//                                            Toast.makeText(Cuti.this, "Jumlah cuti melebihi sisa cuti, Sisa cuti : "+sisacuti,Toast.LENGTH_LONG).show();
//                                        }


                                    }
                                }
                                Log.d("hasil soap", ""+result);


                            }else{
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Cuti.this, "Data gagal mengambil cuti",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Cuti.this, "Data gagal mengambil cuti",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Cuti.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);


    }


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dari.setText(sdf.format(myCalender.getTime()));

        String tglpilih=sdf.format(myCalender.getTime());

        Calendar d = Calendar.getInstance();
        d.add(Calendar.DATE, 7);
        String tglfix = dateFormatter.format(d.getTime());

        Calendar e = Calendar.getInstance();
        e.add(Calendar.DATE, 30);
        String tglsebulan = dateFormatter.format(e.getTime());

        if(tglfix.compareTo(tglpilih) > 0 ){
//            Log.d("hasil", ""+"tidakbisa");
            Toast.makeText(Cuti.this, "Range tanggal kurang dari yang ditentukan",Toast.LENGTH_LONG).show();
            dari.setText(tglfix);
            jumlahharicuti.getText().clear();
            sampaitanggal.getText().clear();


        }else if(tglsebulan.compareTo(tglpilih) < 0){
//            Log.d("hasil", ""+"bisa");
            Toast.makeText(Cuti.this, "Range tanggal melebihi dari yang ditentukan",Toast.LENGTH_LONG).show();
            dari.setText(tglfix);
            jumlahharicuti.getText().clear();
            sampaitanggal.getText().clear();
        }else{
            cekjumlahcuti();
        }

//        Log.d("hasil tglfix", ""+tglfix);
//        Log.d("hasil tglpilih", ""+sdf.format(myCalender.getTime()));
//        Log.d("hasil tglsebulan", ""+tglsebulan);
//
//        cekjumlahcuti();
    }


    private boolean validateKode() {
        if (jumlahharicuti.getText().toString().trim().isEmpty()) {
            jumlahharicuti.setError("Masukkan Jumlah Hari");

            return false;
        } else {
            jumlahharicuti.setError(null);
        }

        if (sampaitanggal.getText().toString().trim().isEmpty()) {
            sampaitanggal.setError("Tanggal Sampai tidak boleh kosong");

            return false;
        } else {
            sampaitanggal.setError(null);
        }

        if (input_kontak.getText().toString().trim().isEmpty()) {
            input_kontak.setError("Masukkan kontak");

            return false;
        } else {
            input_kontak.setError(null);
        }

        return true;
    }
}
