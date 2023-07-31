package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    ProgressDialog progressDialog;

    EditText _nameText;
    EditText _emailText;
    EditText _phoneText;
    EditText _passwordText;
    EditText _confirmpasswordText;
    Button _signupButton;
    TextView _loginLink;
    String name,email,phone,password,confpass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        _nameText = (EditText) findViewById(R.id.input_nama);
        _emailText = (EditText) findViewById(R.id.input_email);
        _phoneText = (EditText) findViewById(R.id.input_phone);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _confirmpasswordText = (EditText) findViewById(R.id.input_confirm_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);



        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = _nameText.getText().toString();
                email = _emailText.getText().toString();
                phone = _phoneText.getText().toString();
                password = _passwordText.getText().toString();
                confpass = _confirmpasswordText.getText().toString();
                signupMember();
            }
        });
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() ||  phone.length() < 10 || phone.length() > 12) {
            _phoneText.setError("enter a valid phone number");
            valid = false;
        } else {
            String first = phone.substring(0,1);
            if (!first.equals("0")){
                _phoneText.setError("enter a valid phone number");
                valid = false;
            }else {
                _phoneText.setError(null);
            }
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            _passwordText.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confpass.isEmpty() || !confpass.equals(password)) {
            _confirmpasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _confirmpasswordText.setError(null);
        }

        return valid;
    }

    public void signupMember() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("registerMember1");
        parameter.add(name);
        parameter.add(email);
        parameter.add(phone);
        parameter.add(password);

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
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SignupActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            String userId = result.getProperty("userId").toString();
                            Intent intent = new Intent(SignupActivity.this, Signup2Activity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SignupActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SignupActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

}
