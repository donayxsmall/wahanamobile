package com.wahana.wahanamobile.Hrd;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.AttendanceReport;
import com.wahana.wahanamobile.Data.Ijin1;
import com.wahana.wahanamobile.Ops.penerimaanRetur.inputTTKReturActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.DatabaseHandler;
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
import java.util.List;
import java.util.Locale;

public class Ijin extends AppCompatActivity {

    private static final String TAG = "PengajuanCuti";
    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar,labelnoken,labelkm,labelfotokm,labelhargabbm,labelliter;
    String username,sisacuti;
    String user_id;
    String tujuan;
    String asal;
    String spinner_value,spinner_value_jenis,tglhariini,spinner_value_jumlah;
    String idbbm;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    DatabaseHandler db;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Date newDate1;
    Button input_button,buttonfoto;
    Spinner jenisijin;
    ImageView imageview;
    EditText input_kontak,input_dari,input_sampaitanggal,input_jumlahhari;
    ImageButton changeDate1;
    RelativeLayout layoutfoto;
    private ArrayList<Ijin1> ijinList = new ArrayList<Ijin1>();
    private final int REQUEST_CODE_CLICK_IMAGE = 01;
    Uri selectedImageUri = null;
    final Calendar myCalender = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijin);
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
                Intent intent = new Intent(Ijin.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        progressDialog = new ProgressDialog(Ijin.this, R.style.AppTheme_Dark_Dialog);


        session = new SessionManager(Ijin.this);
        user_id = session.getID();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        input_dari=(EditText)findViewById(R.id.input_dari);
        jenisijin=(Spinner)findViewById(R.id.jenisijin);
        layoutfoto=(RelativeLayout)findViewById(R.id.layoutfoto);
        imageview=(ImageView)findViewById(R.id.listView);
        buttonfoto=(Button)findViewById(R.id.btn_submit_foto);
        input_sampaitanggal=(EditText)findViewById(R.id.input_sampaitanggal);
        input_jumlahhari=(EditText)findViewById(R.id.input_jumlahhari);
        changeDate1=(ImageButton)findViewById(R.id.change_date1);

        input_jumlahhari.setEnabled(false);
        input_sampaitanggal.setEnabled(false);

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


        Calendar d = Calendar.getInstance();
        String formattedDate1 = dateFormatter.format(d.getTime());
        input_dari.setText(formattedDate1);

        buttonfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(Ijin.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });

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
                String strCurrentDate = input_dari.getText().toString();
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

                new DatePickerDialog(Ijin.this, date, year, month, date1).show();
            }
        });








        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("plvv_suratijinAndroid");
        parameter.add("0");
        parameter.add("0");
        parameter.add("nik");
        parameter.add("");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Ijin.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
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
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String id = isi.getString(0);
                                            String jenis = isi.getString(1);
                                            String type = isi.getString(2);
                                            String jumlah = isi.getString(3);
                                            Ijin1 prov = new Ijin1();
                                            prov.setId(id);
                                            prov.setJenis(jenis);
                                            prov.setType(type);
                                            prov.setJumlah(jumlah);
                                            ijinList.add(prov);
                                        }else{
                                            Ijin1 prov = new Ijin1();
                                            prov.setId("0");
                                            prov.setJenis("--Pilih--");
                                            prov.setType("");
                                            prov.setJumlah("");
                                            ijinList.add(prov);
                                        }
                                    }
                                    populateSpinner();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Ijin.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Ijin.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Ijin.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Ijin.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);







        jenisijin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                spinner_value_jenis = ijinList.get(position).getType().toString();
                spinner_value_jumlah=ijinList.get(position).getJumlah().toString();

                // /add 7 day
                Calendar plus_seven_day = Calendar.getInstance();
                plus_seven_day.add(Calendar.DATE, 7);
                String hasilplus_seven_day = dateFormatter.format(plus_seven_day.getTime());

                //add 30 day
                Calendar plus_thirty_day = Calendar.getInstance();
                plus_thirty_day.add(Calendar.DATE, 30);
                String hasilplus_thirty_day = dateFormatter.format(plus_thirty_day.getTime());

                Calendar plus_hitung_tgljumlah = Calendar.getInstance();
                Calendar datetgl = Calendar.getInstance();
                Calendar datetglskrng = Calendar.getInstance();
                String tglplusjumlahijin=null;

                try {

                    if (spinner_value_jenis.equals("1")) {
                        plus_hitung_tgljumlah.add(Calendar.DATE, 7 + Integer.parseInt(spinner_value_jumlah)-1);
                        tglplusjumlahijin = dateFormatter.format(plus_hitung_tgljumlah.getTime());
                        layoutfoto.setVisibility(View.VISIBLE);
                        input_dari.setText(hasilplus_seven_day);

                    } else {
                        datetgl.add(Calendar.DATE, Integer.parseInt(spinner_value_jumlah) - 1);
                        tglplusjumlahijin = dateFormatter.format(datetgl.getTime());
                        layoutfoto.setVisibility(View.GONE);
                        input_dari.setText(dateFormatter.format(datetglskrng.getTime()));
                    }

                }catch (Exception e){

                }
                input_sampaitanggal.setText(tglplusjumlahijin);
                input_jumlahhari.setText(spinner_value_jumlah);

                Log.d("hasil spinner", ""+hasilplus_seven_day+" "+hasilplus_thirty_day+" "+dateFormatter.format(datetgl.getTime())+" "+tglplusjumlahijin);

//                if(spinner_value_jenis.equals("0") || spinner_value_jenis.equals("null"))
//                {
////                    cityList.clear();
////                    getCity();
//                    layoutfoto.setVisibility(View.GONE);
//                }else{
//                    layoutfoto.setVisibility(View.VISIBLE);
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




    }


    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < ijinList.size(); i++) {
            if (i==0){
                lables.add(ijinList.get(i).getJenis());
            }else{
                lables.add(ijinList.get(i).getJenis());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        jenisijin.setAdapter(spinnerAdapter);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            switch(requestCode){
                case REQUEST_CODE_CLICK_IMAGE:
//                    byte[] byteArray = data.getByteArrayExtra("foto");
//                    Bitmap photo = BitmapTools.toBitmap(byteArray);
//                    photo = BitmapTools.rotate(photo, data.getIntExtra("rotation", 0), data.getIntExtra("camera", 0));
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageview.setImageBitmap(bmp);
                    imageview.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        input_dari.setText(sdf.format(myCalender.getTime()));
        String tglpilih=sdf.format(myCalender.getTime());

        // /add 7 day
        Calendar plus_seven_day = Calendar.getInstance();
        plus_seven_day.add(Calendar.DATE, 7);
        String hasilplus_seven_day = dateFormatter.format(plus_seven_day.getTime());

        //add 30 day
        Calendar plus_thirty_day = Calendar.getInstance();
        plus_thirty_day.add(Calendar.DATE, 30);
        String hasilplus_thirty_day = dateFormatter.format(plus_thirty_day.getTime());


        Calendar datetglskrng = Calendar.getInstance();

        Calendar plus_jumlahdayseven = Calendar.getInstance();
        Calendar plus_jumlahdayskrng = Calendar.getInstance();

        String tglawalfix=null;
        String tglfixjumlah=null;
        if(spinner_value_jenis.equals("1")){
            tglawalfix=hasilplus_seven_day;
            myCalender.add(Calendar.DATE,Integer.parseInt(spinner_value_jumlah)-1);
            tglfixjumlah=dateFormatter.format(myCalender.getTime());
            Log.d("hasil jumlah",""+tglfixjumlah);
        }else{
            tglawalfix=dateFormatter.format(datetglskrng.getTime());
            myCalender.add(Calendar.DATE,Integer.parseInt(spinner_value_jumlah)-1);
            tglfixjumlah=dateFormatter.format(myCalender.getTime());
            Log.d("hasil jumlah",""+tglfixjumlah);
        }

        if(tglawalfix.compareTo(tglpilih) > 0){
            Toast.makeText(Ijin.this, "Range tanggal kurang dari yang ditentukan",Toast.LENGTH_LONG).show();
            input_dari.setText(tglawalfix);
            input_sampaitanggal.getText().clear();
            input_jumlahhari.getText().clear();
        }else if(hasilplus_thirty_day.compareTo(tglpilih) < 0){
            Toast.makeText(Ijin.this, "Range tanggal melebihi dari yang ditentukan",Toast.LENGTH_LONG).show();
            input_dari.setText(tglawalfix);
            input_sampaitanggal.getText().clear();
            input_jumlahhari.getText().clear();
        }else{
            input_sampaitanggal.setText(tglfixjumlah);
        }

        Log.d("hasil tglfix",""+tglpilih+" "+spinner_value_jumlah+" "+tglawalfix+" "+tglfixjumlah);






    }









}
