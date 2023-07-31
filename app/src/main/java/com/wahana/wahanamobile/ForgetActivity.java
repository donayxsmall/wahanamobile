package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ForgetActivity extends AppCompatActivity {
    private static final String TAG = "forgetPassword";
    ProgressDialog progressDialog;
    TextView forgotInfo;
    EditText inputEmail;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        progressDialog = new ProgressDialog(ForgetActivity.this, R.style.AppTheme_Dark_Dialog);
        inputEmail = (EditText) findViewById(R.id.input_username);
        btnLogin = (Button) findViewById(R.id.btn_login);
        forgotInfo = (TextView) findViewById(R.id.forgot_info);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
    }

    public void forgetPassword() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }

        final String username = inputEmail.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("forgetPassword");
        parameter.add(username);

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
                            Toast.makeText(ForgetActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            Intent intent = new Intent(ForgetActivity.this, ThanksActivity.class);
                            startActivity(intent);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ForgetActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ForgetActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError("masukkan email anda");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        return valid;
    }
}
