package com.wahana.wahanamobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wahana.wahanamobile.ModelApiOPS.aoCekVersiAPK;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientLogin;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Vector;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sadewa on 6/29/2016.
 */
public class SplashActivity extends Activity {
    private static final String TAG = "Splash";
    ProgressDialog progressDialog;
    SessionManager session;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        session = new SessionManager(SplashActivity.this);
        db = new DatabaseHandler(SplashActivity.this);

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);


        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 24) {
            try {
                ProviderInstaller.installIfNeeded(this);
            } catch (Exception ignored) {
            }
        }

//
//        String token = FirebaseInstanceId.getInstance().getToken();
//
//        Log.d("hasil tokenfirebase", ""+token);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            checkApk();
                            checkApkV2();

                        }
                    });
                }
            }
        };
        timerThread.start();
    }



    public void checkApkV2(){

        progressDialog = new ProgressDialog(SplashActivity.this, R.style.AppTheme_Dark_Dialog);



        Call<aoCekVersiAPK> result = mApiInterface.aoCekVersiAPK(
                "aoCekVersiAPK",
                "appMobile",
                getString(R.string.versi),
                getString(R.string.partnerid)
        );

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cek Versi Aplikasi...");
        progressDialog.show();

//        if(session.isLogin()){
//            getMenu();
//        }else {
//            progressDialog.dismiss();
//            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }


        result.enqueue(new Callback<aoCekVersiAPK>() {

            @Override
            public void onResponse(Call<aoCekVersiAPK> call, Response<aoCekVersiAPK> response) {

                Log.d("error",""+response.body().getVersion());

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();
                        final String version = response.body().getVersion();
                        final String link = response.body().getLink();

                        Log.d("error1",""+vrcode);

                        if (vrcode.equals("0")) {

                            if (version.equals(getString(R.string.versi))){
                                if(session.isLogin()){
                                    getMenu();
                                }else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                progressDialog.dismiss();
                                AlertDialog.Builder adb=new AlertDialog.Builder(SplashActivity.this);
                                adb.setTitle("Update Aplikasi");
                                adb.setMessage("Mohon update aplikasi ke versi "+version+" untuk melanjutkan ");
                                adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }});
                                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                        startActivity(browserIntent);
                                    }});
                                adb.show();
                            }



                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoCekVersiAPK> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });



    }


    public void checkApk(){
        progressDialog = new ProgressDialog(SplashActivity.this, R.style.AppTheme_Dark_Dialog);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("getApkVersion");
        new SoapClientLogin(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Cek Versi Aplikasi...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap1", ""+result);
                if(result==null){
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SplashActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            SoapObject data = (SoapObject) result.getProperty("data");
                            final String version = data.getProperty("version").toString();
                            final String link = data.getProperty("link").toString();
                            if (version.equals(getString(R.string.versi))){
                                if(session.isLogin()){
                                    getMenu();
                                }else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                progressDialog.dismiss();
                                AlertDialog.Builder adb=new AlertDialog.Builder(SplashActivity.this);
                                adb.setTitle("Update Aplikasi");
                                adb.setMessage("Mohon update aplikasi ke versi "+version+" untuk melanjutkan ");
                                adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }});
                                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                        startActivity(browserIntent);
                                    }});
                                adb.show();
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SplashActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SplashActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                        finish();
                    }
                }
            }
        }.execute(parameter);
    }
    
    public void getMenu(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getMenuAndroid");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBACD");
        parameter.add(session.getUsername());
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Cek Versi Aplikasi...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(final SoapObject result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                try {
                    if(result==null){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SplashActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                JSONArray isi = data.getJSONArray(1);
                                String roleId = isi.getString(3);
                                String role = isi.getString(4);
                                SharedPreferences pref = getSharedPreferences("WahanaPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("roleId", roleId);
                                editor.putString("Group", role);
                                editor.commit();
                                int total = data.length() - 1;
                                db.updateTotalMenu(session.getUsername(), String.valueOf(total));
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                session.logoutUser();
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(SplashActivity.this, "Menu Tidak Tersedia",Toast.LENGTH_LONG).show();
//                                    }
//                                });
                            }
                        }else {
                            session.logoutUser();
//                            Toast.makeText(SplashActivity.this, ""+text, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.d("Hasil error", ""+e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SplashActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }.execute(parameter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
