package com.wahana.wahanamobile.Ops.SJP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterSJPDetail;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataSJP;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ListdetailSJP extends AppCompatActivity {

    private static final String TAG = "sj";
    ListView listView;
    ProgressDialog progressDialog;

    TextView nosjp;
    Button input_button;
    SessionManager session;
    String sjp;
    ArrayList<DataSJP> sjpList = new ArrayList<DataSJP>();
    private static AdapterSJPDetail adapter;
    private static Activity context1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listdetail_sjp);

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
                Intent intent = new Intent(ListdetailSJP.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(ListdetailSJP.this, R.style.AppTheme_Dark_Dialog);

        session = new SessionManager(ListdetailSJP.this);

        listView=(ListView)findViewById(R.id.listView);
        nosjp=(TextView)findViewById(R.id.judul);
        input_button=(Button)findViewById(R.id.input_button);

        Intent intent = getIntent();
        sjp=intent.getStringExtra("nosjp");
        nosjp.setText(sjp);

        setData();


        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ListdetailSJP.this, "Berhasil dibuat", Toast.LENGTH_LONG).show();

                writeexcel();


            }
        });
    }


    private void setData(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("listDetailSJP");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(sjp);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data SJP...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ListdetailSJP.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);

                                            DataSJP sjp = new DataSJP();
                                            sjp.setTtk(isi.getString(0));
                                            sjp.setNamapengirim(isi.getString(1));
                                            sjp.setNamapenerima(isi.getString(2));
                                            sjp.setAlamattujuan(isi.getString(3));
                                            sjp.setBeratvendor(isi.getString(4));
                                            sjp.setKolivendor(isi.getString(5));
                                            sjp.setBiayavendor(isi.getString(6));
                                            sjp.setNoresivendor(isi.getString(7));
                                            sjpList.add(sjp);

                                        }

                                    }

//                                    adapter = new ListViewAdapterSearchKotaAsal(SearchKotaAsal.this, R.layout.listviewsearchkotaasal, originList);
//                                    listView.setAdapter(adapter);

                                    adapter= new AdapterSJPDetail(sjpList,getApplicationContext());

                                    listView.setAdapter(adapter);


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ListdetailSJP.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ListdetailSJP.this, "Bukan No SJP",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ListdetailSJP.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListdetailSJP.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private void writeexcel(){
        String Fnamexls=sjp  + ".xls";

        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Excel");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file1 = new File(dir, Fnamexls);

        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            int a = 0;

//            List<Bean> listdata = new ArrayList<>();
//
//            listdata.add(new Bean("mr", "firstName1", "middleName1", "lastName1"));
//            listdata.add(new Bean("mr", "firstName2", "middleName2", "lastName2"));
//            listdata.add(new Bean("mr", "firstName3", "middleName3", "lastName3"));


            workbook = Workbook.createWorkbook(file1, wbSettings);
            WritableSheet sheet = workbook.createSheet(sjp, 0);

//            Label label01 = new Label(0, 0, "No");
//            Label label02 = new Label(1, 0, "No TTK Wahana");
//            Label label03 = new Label(2, 0, "Nama Pengirim");
//            Label label04 = new Label(3, 0, "Nama Penerima");
//            Label label05 = new Label(4, 0, "Alamat Tujuan");
//            Label label06 = new Label(5, 0, "Berat Vendor");
//            Label label07 = new Label(6, 0, "Koli Vendor");
//            Label label08 = new Label(7, 0, "Biaya Vendor");
//            Label label09 = new Label(8, 0, "No Resi Vendor");


            try {
                sheet.addCell(new Label(0, 0, "No"));
                sheet.addCell(new Label(1, 0, "No TTK Wahana"));
                sheet.addCell(new Label(2, 0, "Nama Pengirim"));
                sheet.addCell(new Label(3, 0, "Nama Penerima"));
                sheet.addCell(new Label(4, 0, "Alamat Tujuan"));
                sheet.addCell(new Label(5, 0, "Berat Vendor"));
                sheet.addCell(new Label(6, 0, "Koli Vendor"));
                sheet.addCell(new Label(7, 0, "Biaya Vendor"));
                sheet.addCell(new Label(8, 0, "No Resi Vendor"));


                for (int i=0; i < sjpList.size(); i++){
                    a++;

                    sheet.addCell(new Label(0, i + 1, ""+a));
                    sheet.addCell(new Label(1, i + 1, sjpList.get(i).getTtk()));
                    sheet.addCell(new Label(2, i + 1, sjpList.get(i).getNamapengirim()));
                    sheet.addCell(new Label(3, i + 1, sjpList.get(i).getNamapenerima()));
                    sheet.addCell(new Label(4, i + 1, sjpList.get(i).getAlamattujuan()));
                    sheet.addCell(new Label(5, i + 1, sjpList.get(i).getBeratvendor()));
                    sheet.addCell(new Label(6, i + 1, sjpList.get(i).getKolivendor()));
                    sheet.addCell(new Label(7, i + 1, sjpList.get(i).getBiayavendor()));
                    sheet.addCell(new Label(8, i + 1, sjpList.get(i).getNoresivendor()));

//                    String ttk=sjpList.get(i).getTtk();
//                    Log.d("tty",""+ttk);

                }


//                for (int i = 0; i < listdata.size(); i++) {
//                    sheet.addCell(new Label(0, i + 1, listdata.get(i).getInitial()));
//                    sheet.addCell(new Label(1, i + 1, listdata.get(i).getFirstName()));
//                    sheet.addCell(new Label(2, i + 1, listdata.get(i).getMiddleName()));
//                    sheet.addCell(new Label(3, i + 1, listdata.get(i).getLastName()));
//                }

            }catch (RowsExceededException e) {
                e.printStackTrace();

            }catch (WriteException e) {
                e.printStackTrace();
            }


//            Label label = new Label(0, 2, "SECOND");
//            Label label0 = new Label(0,0,"HEADING");
//            Label label1 = new Label(0,1,"first");
//            Label label3 = new Label(1,0,"Heading2");
//            Label label4 = new Label(1,1,String.valueOf(a));

//            try {
////                sheet.addCell(label01);
////                sheet.addCell(label02);
////                sheet.addCell(label03);
////                sheet.addCell(label04);
////                sheet.addCell(label05);
////                sheet.addCell(label06);
////                sheet.addCell(label07);
////                sheet.addCell(label08);
////                sheet.addCell(label09);
//
//                for (int i = 0; i < listdata.size(); i++) {
//                    sheet.addCell(new Label(0, i + 1, listdata.get(i).getInitial()));
//                    sheet.addCell(new Label(1, i + 1, listdata.get(i).getFirstName()));
//                    sheet.addCell(new Label(2, i + 1, listdata.get(i).getMiddleName()));
//                    sheet.addCell(new Label(3, i + 1, listdata.get(i).getLastName()));
//                }
//
//            } catch (RowsExceededException e) {
//                e.printStackTrace();
//            } catch (WriteException e) {
//                e.printStackTrace();
//            }
            workbook.write();

            try {
                workbook.close();
            } catch (WriteException e) {

                e.printStackTrace();
            }


//            File outputFile = new File(Environment.getExternalStoragePublicDirectory
//                    (Environment.DIRECTORY_DOWNLOADS), "example.pdf");

            File outputFile = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Excel/"+Fnamexls);

            Uri uri = Uri.fromFile(outputFile);

            Log.d("excel",""+uri);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
//            share.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(share, "Share File"));
//            context1.startActivity(share);

//            ListdetailSJP.startActivity(share);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
