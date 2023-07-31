package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class Signup2Activity extends AppCompatActivity {
    private static final String TAG = "SignupActivity2";
    EditText _provinsiText;
    EditText _kabkotText;
    EditText _kecamatanText;
    EditText _kelurahanText;
    EditText _alamatText;
    Button _signupButton;
    String userId,provinsi,kabkot,kecamatan,kelurahan,alamat,kodeBps;
    ProgressDialog progressDialog;
    private final int REQUEST_FOR_PROVINCE = 1;
    private final int REQUEST_FOR_KABKOT = 2;
    private final int REQUEST_FOR_KECAMATAN = 3;
    private final int REQUEST_FOR_KELURAHAN = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        progressDialog = new ProgressDialog(Signup2Activity.this, R.style.AppTheme_Dark_Dialog);
        _provinsiText = (EditText) findViewById(R.id.input_provinsi);
        _kabkotText = (EditText) findViewById(R.id.input_kabkot);
        _kecamatanText = (EditText) findViewById(R.id.input_kecamatan);
        _kelurahanText = (EditText) findViewById(R.id.input_kelurahan);
        _alamatText = (EditText) findViewById(R.id.input_alamat);
        _signupButton = (Button) findViewById(R.id.btn_signup);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provinsi = _provinsiText.getText().toString();
                kabkot = _kabkotText.getText().toString();
                kecamatan = _kecamatanText.getText().toString();
                kelurahan = _kelurahanText.getText().toString();
                alamat = _alamatText.getText().toString();
                Log.d("hasil", kodeBps);
                signupMember();
            }
        });

        _provinsiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup2Activity.this, AddressSearchActivity.class);
                intent.putExtra("type", "provinsi");
                startActivityForResult(intent, REQUEST_FOR_PROVINCE);
            }
        });
        _kabkotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_provinsiText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Provinsi", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(Signup2Activity.this, AddressSearchActivity.class);
                    intent.putExtra("type", "kabkot");
                    intent.putExtra("provinsi", _provinsiText.getText().toString());
                    startActivityForResult(intent, REQUEST_FOR_KABKOT);
                }
            }
        });
        _kecamatanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_provinsiText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Provinsi", Toast.LENGTH_SHORT).show();
                }else if (_kabkotText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Kota/Kabupaten", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Signup2Activity.this, AddressSearchActivity.class);
                    intent.putExtra("type", "kecamatan");
                    intent.putExtra("provinsi", _provinsiText.getText().toString());
                    intent.putExtra("kabkot", _kabkotText.getText().toString());
                    startActivityForResult(intent, REQUEST_FOR_KECAMATAN);
                }
            }
        });
        _kelurahanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_provinsiText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Provinsi", Toast.LENGTH_SHORT).show();
                }else if (_kabkotText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Kota/Kabupaten", Toast.LENGTH_SHORT).show();
                }else if (_kecamatanText.getText().toString().isEmpty()){
                    Toast.makeText(Signup2Activity.this, "Mohon Isi Kecamatan", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Signup2Activity.this, AddressSearchActivity.class);
                    intent.putExtra("type", "kelurahan");
                    intent.putExtra("provinsi", _provinsiText.getText().toString());
                    intent.putExtra("kabkot", _kabkotText.getText().toString());
                    intent.putExtra("kecamatan", _kecamatanText.getText().toString());
                    startActivityForResult(intent, REQUEST_FOR_KELURAHAN);
                }
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        if (provinsi.isEmpty()) {
            _provinsiText.setError("at least 3 characters ");
            valid = false;
        } else {
            _provinsiText.setError(null);
        }

        if (kabkot.isEmpty()) {
            _kabkotText.setError("enter a valid email address");
            valid = false;
        } else {
            _kabkotText.setError(null);
        }

        if (kecamatan.isEmpty()) {
            _kecamatanText.setError("enter a valid phone number");
            valid = false;
        } else {
            _kecamatanText.setError(null);
        }

        if (kelurahan.isEmpty()) {
            _kelurahanText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _kelurahanText.setError(null);
        }

        if (alamat.isEmpty()) {
            _alamatText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _alamatText.setError(null);
        }

        return valid;
    }

    public void signupMember() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("registerMember2");
        parameter.add(userId);
        parameter.add(provinsi);
        parameter.add(kabkot);
        parameter.add(kecamatan);
        parameter.add(kelurahan);
        parameter.add(alamat);
        parameter.add(kodeBps);

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
                            Toast.makeText(Signup2Activity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            String userId = result.getProperty("userId").toString();
                            Intent intent = new Intent(Signup2Activity.this, Signup3Activity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Signup2Activity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Signup2Activity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_FOR_PROVINCE:
                    String provinsi = data.getStringExtra("hasil");
                    _provinsiText.setText(provinsi);
                    break;

                case REQUEST_FOR_KABKOT:
                    String kabkot = data.getStringExtra("hasil");
                    _kabkotText.setText(kabkot);
                    break;

                case REQUEST_FOR_KECAMATAN:
                    String kecamatan = data.getStringExtra("hasil");
                    _kecamatanText.setText(kecamatan);
                    break;

                case REQUEST_FOR_KELURAHAN:
                    String kelurahan = data.getStringExtra("hasil");
                    kodeBps = data.getStringExtra("kodeBps");
                    _kelurahanText.setText(kelurahan);
                    break;
            }
        }
    }
}
