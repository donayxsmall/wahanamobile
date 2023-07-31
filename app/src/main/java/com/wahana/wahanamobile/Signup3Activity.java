package com.wahana.wahanamobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Signup3Activity extends AppCompatActivity {
    private static final String TAG = "SignupActivity3";
    Button _signupButton;
    EditText _ttlText;
    EditText _nopilText;
    String userId,ttl,nopil,date;
    ProgressDialog progressDialog;
    private SimpleDateFormat dateFormatter,newDateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        progressDialog = new ProgressDialog(Signup3Activity.this, R.style.AppTheme_Dark_Dialog);
        _ttlText = (EditText) findViewById(R.id.input_tanggal_lahir);
        _nopilText = (EditText) findViewById(R.id.input_no_pilihan);
        _signupButton = (Button) findViewById(R.id.btn_signup);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        _ttlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog SortDatePickerDialog = new DatePickerDialog(Signup3Activity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date = dateFormatter.format(newDate.getTime());
                        _ttlText.setText(date);
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                SortDatePickerDialog.show();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttl = _ttlText.getText().toString();
                nopil = _nopilText.getText().toString();
                signupMember();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        if (ttl.isEmpty()) {
            _ttlText.setError("at least 3 characters ");
            valid = false;
        } else {
            _ttlText.setError(null);
        }

        if (nopil.isEmpty()) {
            _nopilText.setError("enter a valid email address");
            valid = false;
        } else {
            _nopilText.setError(null);
        }

        return valid;
    }

    public void signupMember() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }
        Date tgl;
        String newttl = null;
        newDateFormatter = new SimpleDateFormat("ddMMyy", Locale.US);
        try {
            tgl = dateFormatter.parse(ttl);
            newttl = newDateFormatter.format(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("registerMember3");
        parameter.add(userId);
        parameter.add(newttl);
        parameter.add(nopil);

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
                            Toast.makeText(Signup3Activity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            String username = result.getProperty("username").toString();
                            Intent intent = new Intent(Signup3Activity.this, SignupFinishActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Signup3Activity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Signup3Activity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }
}
