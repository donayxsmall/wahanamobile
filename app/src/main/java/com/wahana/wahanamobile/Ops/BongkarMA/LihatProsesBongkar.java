package com.wahana.wahanamobile.Ops.BongkarMA;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetBongkarMAProcessList;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListProsesBongkarMA;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataListProsesBongkarMA;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class LihatProsesBongkar extends AppCompatActivity {
    ListView listproses;
    TextView totalttk,totalttksudahscan,totalttkbelumscan;
    ProgressDialog progressDialog;
    SessionManager session;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;
    String nomorMA;
    ArrayList<DataListProsesBongkarMA> DataBongkarMAList = new ArrayList<DataListProsesBongkarMA>();
    private AdapterListProsesBongkarMA adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_proses_bongkar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        nomorMA=intent.getStringExtra("nomorMA");

        listproses = (ListView)findViewById(R.id.list_proses);
        totalttk = (TextView)findViewById(R.id.totalttk);
        totalttksudahscan = (TextView)findViewById(R.id.totalttksudahscan);
        totalttkbelumscan = (TextView)findViewById(R.id.totalttkbelumscan);

        db = new DatabaseHandler(LihatProsesBongkar.this);

        progressDialog = new ProgressDialog(LihatProsesBongkar.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(LihatProsesBongkar.this);

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);



        listbongkar();

    }


    private void listbongkar(){

        Call<opv2GetBongkarMAProcessList> result = mApiInterface.opv2GetBongkarMAProcessList(
                "opv2GetBongkarMAProcessList",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                nomorMA,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2GetBongkarMAProcessList>() {

            @Override
            public void onResponse(Call<opv2GetBongkarMAProcessList> call, retrofit2.Response<opv2GetBongkarMAProcessList> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){


                            opv2GetBongkarMAProcessList.Data data = response.body().getData();
                            List<opv2GetBongkarMAProcessList.Proses> dataList = new ArrayList<>();
                            dataList = data.getProses();

                            String totalttkjum = response.body().getData().getTarget();
                            String totalttksudahscanjum = response.body().getData().getSelesai();
                            String totalttkbelumscanjum = response.body().getData().getInproses();


                            for (int i = 0; i < dataList.size(); i++) {

                                Log.d("fit1", "" + dataList.get(i).getPetugas()+" "+dataList.get(i).getJumlah());

                                DataListProsesBongkarMA prov = new DataListProsesBongkarMA();
                                prov.setPetugas(dataList.get(i).getPetugas());
                                prov.setJumlah(dataList.get(i).getJumlah());
                                DataBongkarMAList.add(prov);
                            }

                            totalttk.setText(totalttkjum);
                            totalttksudahscan.setText(totalttksudahscanjum);
                            totalttkbelumscan.setText(totalttkbelumscanjum);

                            adapter = new AdapterListProsesBongkarMA(LihatProsesBongkar.this, R.layout.listviewbongkarma, DataBongkarMAList);
                            listproses.setAdapter(adapter);




                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(LihatProsesBongkar.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(LihatProsesBongkar.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(LihatProsesBongkar.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2GetBongkarMAProcessList> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(LihatProsesBongkar.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });
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


}
