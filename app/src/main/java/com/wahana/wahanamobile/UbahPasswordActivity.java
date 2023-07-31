package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UbahPasswordActivity extends DrawerHelper {
    private static final String TAG = "Change Password";
    ProgressDialog progressDialog;
    EditText inputOldPassword, inputNewPassword, ulangiNewPassword;
    Button btnChange;
    SessionManager session;
    TextView pengisi, tgl, calendar;
    String username, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);
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
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());

        session = new SessionManager(UbahPasswordActivity.this);
        progressDialog = new ProgressDialog(UbahPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        inputOldPassword = (EditText) findViewById(R.id.input_old_password);
        inputNewPassword = (EditText) findViewById(R.id.input_new_password);
        ulangiNewPassword = (EditText) findViewById(R.id.input_confirm_password);
        btnChange = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        username = session.getUsername();
        user_id = session.getID();

        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        pengisi.setText(user_id);

        btnChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    public void changePassword() {
        if (!validate()) {
            return;
        }

        final String oldPass = inputOldPassword.getText().toString();
        final String newPass = inputNewPassword.getText().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setChangePassword");
            json.put("oldPass", oldPass);
            json.put("newPass",newPass);
            json.put("cnfNewPass",ulangiNewPassword.getText().toString());
            json.put("employeeCode", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", ""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        progressDialog.setCancelable(false);
        new SoapClientMobile(){
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
                            Toast.makeText(UbahPasswordActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        final String code = result.getProperty(0).toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(UbahPasswordActivity.this, HasilActivity.class);
                                    pindah.putExtra("proses","ubah");
                                    pindah.putExtra("no", "Berhasil mengubah password");
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(UbahPasswordActivity.this, "Mohon Cek Kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(UbahPasswordActivity.this, "Mohon Cek Kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(UbahPasswordActivity.this, HasilError.class);
                            pindah.putExtra("proses","ubah");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UbahPasswordActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    public boolean validate() {
        boolean valid = true;

        String oldPass = inputOldPassword.getText().toString();
        String newPass = inputNewPassword.getText().toString();
        String confNewPass = ulangiNewPassword.getText().toString();

        if(oldPass.isEmpty())
        {
            inputOldPassword.setError("Masukkan Password Lama Anda");
            valid = false;
        } else {
            inputOldPassword.setError(null);
        }

        if(newPass.isEmpty() || newPass.length() < 6 )
        {
            inputNewPassword.setError("Password tidak boleh kurang dari 6 karakter");
            valid = false;
        } else {
            inputNewPassword.setError(null);
        }

        if(!confNewPass.equals(newPass))
        {
            ulangiNewPassword.setError("Konfirmasi password tidak sesuai");
            valid = false;
        } else {
            ulangiNewPassword.setError(null);
        }

        return valid;
    }
}
