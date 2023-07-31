package com.wahana.wahanamobile.member;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.adapter.DetailTransaksiAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import org.ksoap2.serialization.SoapObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DetailTransaksi extends DrawerHelper  implements View.OnClickListener{
    ProgressDialog progressDialog;
    ImageButton changeDate1;
    ImageButton changeDate2;
    Button submitButton;
    TextView dariTanggalLabel,sampaiTangalLabel, ttkListLabel, ttkListIsi,tidakTerkirimLabel,tidakTerkirimIsi,
            terkirimLabel,terkirimIsi,beratTotalLabel,beratTotalIsi,beratTerkirimLabel,beratTerkirimIsi,
            belumUpdate,belumST;
    //    TextView totalPoinLabel, totalPoinIsi,poinKGPLabel,poinKGPIsi,poinKGSLabel,poinKGSIsi;
    EditText inputDari,inputSampai;
    RelativeLayout myInfoResult;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;
    EditText dari, sampai;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    RelativeLayout hasil;
    ListView listViewDetailTransaksi;
    String listWaktu[]={"12/03/2017 22:24","08/03/2017 01:00","06/03/2017 14:28"};
    String listKeterangan[]={"Withdrawal Deposito (PT.BCA(BANK CENTRAL ASIA)TBK-64080424688-alzamendi qatryany)(processed)*13/03/2017 10:39:43:Completed","Transaksi Penjualan Berhasil - INV/20170303/XVII/III/72258957","Withdrawal Deposito (PT.BCA(BANK CENTRAL ASIA)TBK-64080424688-alzamendi qatryany)(proccesed) * 07/13/2017 07:51:46:Completed"};
    String listNominal[]={"-Rp 1.497.900","Rp 1.497.900", "-Rp 500.000"};
    String listSaldo[]={"Rp. 0","Rp 1.497.900", "Rp. 0"};
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(DetailTransaksi.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        hasil = (RelativeLayout) findViewById(R.id.myinfo_result);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        myInfoResult = (RelativeLayout) findViewById (R.id.myinfo_result);
        changeDate1 = (ImageButton) findViewById(R.id.change_date1);
        changeDate2 = (ImageButton) findViewById(R.id.change_date2);
        dari = (EditText) findViewById(R.id.input_dari);
        sampai = (EditText) findViewById(R.id.input_sampai);
        submitButton = (Button) findViewById(R.id.submit_buton);
        dariTanggalLabel = (TextView) findViewById(R.id.dari_tanggal_label);
        sampaiTangalLabel= (TextView) findViewById(R.id.sampai_tanggal_label);
//        totalPoinLabel= (TextView) findViewById(R.id.total_poin_label);
//        totalPoinIsi= (TextView) findViewById(R.id.total_poin_isi);
        listViewDetailTransaksi = (ListView) findViewById(R.id.detail_transaksi_list);
        listViewDetailTransaksi.setAdapter(new DetailTransaksiAdapter(DetailTransaksi.this, listWaktu, listKeterangan, listNominal, listSaldo));
        ttkListLabel= (TextView) findViewById(R.id.ttk_list_label);
        ttkListIsi= (TextView) findViewById(R.id.ttk_list_isi);
        tidakTerkirimLabel= (TextView) findViewById(R.id.tidak_terkirim_label);
        tidakTerkirimIsi= (TextView) findViewById(R.id.tidak_terkirim_isi);
        terkirimLabel= (TextView) findViewById(R.id.terkirim_label);
        terkirimIsi= (TextView) findViewById(R.id.terkirim_isi);
        beratTotalLabel= (TextView) findViewById(R.id.berat_total_label);
        beratTotalIsi= (TextView) findViewById(R.id.berat_total_isi);
        beratTerkirimLabel= (TextView) findViewById(R.id.berat_terkirim_label);
        beratTerkirimIsi= (TextView) findViewById(R.id.berat_terkirim_isi);
//        poinKGPLabel= (TextView) findViewById(R.id.poin_kgp_label);
//        poinKGPIsi= (TextView) findViewById(R.id.poin_kgp_isi);
//        poinKGSLabel= (TextView) findViewById(R.id.poin_kgs_label);
//        poinKGSIsi= (TextView) findViewById(R.id.poin_kgs_isi);
        inputDari= (EditText) findViewById(R.id.input_dari);
        inputSampai= (EditText) findViewById(R.id.input_sampai);
        belumUpdate = (TextView) findViewById(R.id.ttk_belum_terupdate_isi);
        belumST = (TextView) findViewById(R.id.ttk_belum_serah_terima_bt_isi);
        setDateTimeField();

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        Typeface typeRegular = Typeface.createFromAsset(getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemiBold = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        dariTanggalLabel.setTypeface(typeSemiBold);
        sampaiTangalLabel.setTypeface(typeSemiBold);

        inputDari.setTypeface(typeSemiBold);
        inputSampai.setTypeface(typeSemiBold);

        session = new SessionManager(DetailTransaksi.this);
        progressDialog = new ProgressDialog(DetailTransaksi.this, R.style.AppTheme_Dark_Dialog);
        username = session.getUsername();
        user_id = session.getID();

        Calendar c = Calendar.getInstance();
        String formattedDate = dateFormatter.format(c.getTime());
        dari.setText(formattedDate);
        sampai.setText(formattedDate);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == changeDate1) {
            fromDatePickerDialog.show();
        } else if(v == changeDate2) {
            toDatePickerDialog.show();
        }
    }

    private void submitForm()
    {
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getMyInfo");
        parameter.add(session.getSessionID());
        parameter.add(session.getID());
        parameter.add(dari.getText().toString());
        parameter.add(sampai.getText().toString());
        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(final SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetailTransaksi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(0).toString();
                        if (text.equals("1")) {
                            progressDialog.dismiss();
                            hasil.setVisibility(View.VISIBLE);
                            myInfoResult.setVisibility(View.VISIBLE);
                            String totalTTK = result.getProperty(2).toString();
                            String terkirimTTK = result.getProperty(3).toString();
                            String belumTTK = result.getProperty(4).toString();
                            String aktifTTK = result.getProperty(5).toString();
                            int belumSTBT = Integer.parseInt(totalTTK)-Integer.parseInt(terkirimTTK)-Integer.parseInt(belumTTK)-Integer.parseInt(aktifTTK);
                            String totalBerat = result.getProperty(6).toString();
                            String totalBeratTerkirim = result.getProperty(7).toString();
                            int KGP = Integer.parseInt(result.getProperty(8).toString());
                            int KGS = Integer.parseInt(result.getProperty(9).toString());
                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            String totalPoin;
                            try{
                                String asd = result.getProperty(11).toString();
//                                try {
                                int total = Integer.parseInt(result.getProperty(10).toString());
                                totalPoin = "Rp. "+formatter.format(total);
//                                }catch (Exception e){
//                                    Log.d("hasil excep", e+"");
//                                    totalPoin = "Rp. 0";
//                                }
//                                poinKGPIsi.setText("Rp. "+formatter.format(KGP));
//                                poinKGSIsi.setText("Rp. "+formatter.format(KGS));
//                                totalPoinIsi.setText(totalPoin);
                            }catch (Exception e){
                                Log.d("hasil ex", e+"");
//                                try {
                                totalPoin = "Rp. "+formatter.format(KGP);
//                                }catch (Exception ex){
//                                    Log.d("hasil excep", ex+"");
//                                    totalPoin = "Rp. 0";
//                                }
//                                totalPoinIsi.setText(totalPoin);
//                                poinKGPIsi.setText("Rp. 0");
//                                poinKGSIsi.setText("Rp. 0");
                                Log.d("hasil total", totalPoin+" aass");
                            }
                            ttkListIsi.setText(totalTTK);
                            terkirimIsi.setText(terkirimTTK);
                            tidakTerkirimIsi.setText(belumTTK);
                            belumUpdate.setText(aktifTTK);
                            belumST.setText(belumSTBT+"");
                            beratTotalIsi.setText(totalBerat);
                            beratTerkirimIsi.setText(totalBeratTerkirim);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DetailTransaksi.this, result.getProperty(1).toString() ,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DetailTransaksi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}
