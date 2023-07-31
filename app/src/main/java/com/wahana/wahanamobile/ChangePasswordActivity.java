package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ChangePasswordActivity extends DrawerHelper {
    private static final String TAG = "Change Password";
    ProgressDialog progressDialog;
    String tipeLogin;
    EditText inputOldPassword, inputNewPassword, ulangiNewPassword;
    Button btnChange;
    TextView changeInfo;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        super.onCreateDrawer(this);
        session = new SessionManager(ChangePasswordActivity.this);
        progressDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        tipeLogin = getIntent().getStringExtra("LOGIN_TIPE");
        inputOldPassword = (EditText) findViewById(R.id.input_old_password);
        inputNewPassword = (EditText) findViewById(R.id.input_new_password);
        ulangiNewPassword = (EditText) findViewById(R.id.ulangi_new_password);
        btnChange = (Button) findViewById(R.id.btn_change);
        changeInfo = (TextView) findViewById(R.id.change_info);
        btnChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tipeLogin.equals("member"))
                {
                    forgetPassword();
                }
                else
                {
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    public void forgetPassword() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }

        final String oldPass = inputOldPassword.getText().toString();
        final String newPass = inputNewPassword.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("changePassword");
        parameter.add(session.getID());
        parameter.add(oldPass);
        parameter.add(newPass);

        new SoapClientMember(){
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
                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ChangePasswordActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                            Intent intent = new Intent(ChangePasswordActivity.this, UserMainActivity.class);
                            startActivity(intent);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ChangePasswordActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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

        if(newPass.isEmpty() || newPass.length() < 6 || newPass.length() > 10)
        {
            inputNewPassword.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputNewPassword.setError(null);
        }

        if(confNewPass.isEmpty() || !confNewPass.equals(newPass))
        {
            ulangiNewPassword.setError("Ulangi Password Baru Anda");
            valid = false;
        } else {
            ulangiNewPassword.setError(null);
        }

        return valid;
    }
}
