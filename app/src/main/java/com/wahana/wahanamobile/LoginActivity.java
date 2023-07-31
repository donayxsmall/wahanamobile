package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.wahana.wahanamobile.Data.Role;
import com.wahana.wahanamobile.Data.User;
import com.wahana.wahanamobile.ModelApiOPS.aoAuthUser;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.service.Config;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientLogin;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import android.content.SharedPreferences.Editor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    String METHOD_NAME = "login";

    ProgressDialog progressDialog;
    SessionManager session;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    Button _aboutButton;
    TextView _signupLink, _forgetPassword;
    SharedPreferences pref;
    Editor editor;
    DatabaseHandler db;
    private Class<?> mClss;
    private static final int MAP_PERMISSION = 1;
    String jab;
    int jml;
    String atasan;

    RequestApiWahanaOps mApiInterface;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("hasil time",""+System.currentTimeMillis());

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);


        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(LoginActivity.this);
        db = new DatabaseHandler(LoginActivity.this);
        pref = this.getSharedPreferences("WahanaPref", 0);
        if(session.isLogin())
        {
            if(session.isExpired(System.currentTimeMillis()))
            {
                Toast.makeText(LoginActivity.this, "Sesi Anda Telah Kadaluarsa", Toast.LENGTH_SHORT).show();
                session.clearSessionData();
            }else{
//                if (session.getJenis().equals("karyawan")){
//                    Intent inten = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(inten);
//                    this.finish();
//                }else if(session.getJenis().equals("member")){
//                    SharedPreferences shared = getSharedPreferences(Config.SHARED_PREF, 0);
//                    String token = (shared.getString("regId", ""));
//                    if(token.equals(session.getToken())){
//                        Intent inten = new Intent(getApplicationContext(), UserMainActivity.class);
//                        startActivity(inten);
//                        this.finish();
//                    }
//                }else if(session.getJenis().equals("sales")){
//                    Intent inten = new Intent(getApplicationContext(), SalesMainActivity.class);
//                    startActivity(inten);
//                    this.finish();
//                }else if(session.getJenis().equals("ops")){
                    Intent inten = new Intent(getApplicationContext(), OpsMainActivity.class);
                    startActivity(inten);
                    this.finish();
//                }else if(session.getJenis().equals("ar")){
//                    Intent inten = new Intent(getApplicationContext(), ArMainActivity.class);
//                    startActivity(inten);
//                    this.finish();
//                }
            }
        }

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _aboutButton = (Button) findViewById(R.id.btn_about);
//        _forgetPassword = (TextView) findViewById(R.id.link_forgot);
//
//        _signupLink = (TextView) findViewById(R.id.link_signup);
        Typeface type = Typeface.createFromAsset(this.getAssets(),"font/OpenSansLight.ttf");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = _emailText.getText().toString();
                if(email.length() == 8){
//                    loginNew();
                    login();
                }else if(email.length() == 9){
//                    loginMember();
                    login();
                }else if(email.length() == 5){
//                    loginSales();
                    login();
                }else if(email.length() == 3){
//                    loginOps();
                    login();
                }else if(email.length() == 2){
//                    loginAr();
                    login();
                }else {
//                    loginNew();
                    login();
                }
            }
        });

        _aboutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(intent);
            }
        });
//        _signupLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivity(intent);
//            }
//        });
//        _forgetPassword.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
//                startActivity(intent);
//            }
//        });
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("login");
        parameter.add(username+"@"+getString(R.string.versinew));
//        parameter.add(username);
        parameter.add(password);

        new SoapClient(){
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
                            Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            final String id = result.getProperty(2).toString();
                            ArrayList<String> parameter = new ArrayList<String>();
                            parameter.add("doSSQL");
                            parameter.add(id);
                            parameter.add("getRoleUser");
                            parameter.add("0");
                            parameter.add("0");
                            parameter.add("nik");
                            parameter.add(_emailText.getText().toString());
                            new SoapClientMobile(){
                                @Override
                                protected void onPostExecute(final SoapObject result1) {
                                    super.onPostExecute(result1);
                                    progressDialog.dismiss();
                                    try {
                                        if(result1==null){
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }else {
                                            final String code = result1.getProperty("resCode").toString();
                                            final String text = result1.getProperty("resMessage").toString();
                                            if (text.equals("OK")) {
                                                String so = result1.getProperty(2).toString();

                                                JSONObject jsonObj = new JSONObject(so);
                                                JSONArray data = jsonObj.getJSONArray("data");
                                                if (data.length()>1){
                                                    final JSONArray d = data.getJSONArray(1);
                                                    JSONArray isi = data.getJSONArray(1);
                                                    String nama = isi.getString(0);
                                                    String kdkantor = isi.getString(1);
                                                    String nmkantor = isi.getString(2);
                                                    String nik = isi.getString(3);
                                                    String kdrole = isi.getString(4);
                                                    String nmrole = isi.getString(5);
                                                    session.createLoginSession("", _emailText.getText().toString(), "", kdrole, "", id, "ops",kdkantor);
                                                    db.addUser(new User(_emailText.getText().toString(), nama, kdrole, session.getSessionID(), nik));
                                                    editor = pref.edit();
                                                    editor.putString("id", nik);
                                                    editor.putString("Name", nama);
                                                    editor.putString("Group", nmrole);
                                                    editor.commit();

                                                    Role role = new Role();
                                                    role.setCode(kdrole);
                                                    role.setName(nmrole);
                                                    db.addRole(role);


                                                    insertFirebaseId(_emailText.getText().toString());
                                                    gettokenjwt(_emailText.getText().toString(),_passwordText.getText().toString());

                                                    getMenu();



//                                                    Toast.makeText(LoginActivity.this, ""+data,Toast.LENGTH_SHORT).show();
                                                }else{
                                                    progressDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(LoginActivity.this, "Data tidak ada", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }


                                    }catch (Exception e){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }.execute(parameter);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }


    private void gettokenjwt(String nik,String password){

        Call<aoAuthUser> result = mApiInterface.aoAuthUser(
                "aoAuthUser",
                getString(R.string.partnerid),
                nik,
                password
//                "11190569",
//                "11190569"
        );

        result.enqueue(new Callback<aoAuthUser>() {

            @Override
            public void onResponse(Call<aoAuthUser> call, Response<aoAuthUser> response) {

                if(response.isSuccessful()) {

                    try {


                        Log.d("retro",""+response.body().getData().getBenm());
                        session.tokenJWT(response.body().getData().getJwt(), response.body().getData().getPhoto(), response.body().getData().getVaksin());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{


                    Log.d("error1",""+response.code());

                    Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();


                }



            }

            @Override
            public void onFailure(Call<aoAuthUser> call, Throwable t) {
                t.printStackTrace();

                Log.d("error2",""+t.toString());

                Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

            }
        });



    }








    public void loginNew() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("login");
        parameter.add(username);
        parameter.add(password);

        new SoapClient(){
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
                            Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            final String id = result.getProperty(2).toString();
                            ArrayList<String> parameter = new ArrayList<String>();
                            parameter.add("getLoginInfo");
                            parameter.add(id);
                            parameter.add(_emailText.getText().toString());
                            new SoapClient(){
                                @Override
                                protected void onPostExecute(final SoapObject res) {
                                    super.onPostExecute(res);
                                    if(res==null){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }else {
                                        final String text = res.getProperty(1).toString();
                                        if (text.equals("OK")) {
                                            SoapObject so = (SoapObject) res.getProperty(2);
                                            final String name = so.getProperty("employeeName").toString();
                                            final String group = so.getProperty("mainGroup").toString();
                                            final String employeeID = _emailText.getText().toString();

                                            ArrayList<String> parameter = new ArrayList<String>();
                                            parameter.add("login");
                                            parameter.add(employeeID);
                                            new SoapClientLogin(){
                                                @Override
                                                protected void onPostExecute(final SoapObject result1) {
                                                    super.onPostExecute(result1);
                                                    progressDialog.dismiss();
                                                    try {
                                                        if(result1==null){
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        }else {
                                                            final String code = result1.getProperty("resCode").toString();
                                                            final String text = result1.getProperty("resText").toString();
                                                            if (code.equals("1")) {
                                                                SoapObject so = (SoapObject) result1.getProperty("data");
                                                                String tipe_name = null, tipe_code = null;
                                                                tipe_name = so.getProperty("tipe_name").toString();
                                                                tipe_code = so.getProperty("tipe_code").toString();
                                                                Log.d("Hasil Soap", ""+tipe_name+tipe_code);
                                                                session.createLoginSession("", _emailText.getText().toString(), "", tipe_code, "", id, "ops","");
                                                                db.addUser(new User(_emailText.getText().toString(), name, tipe_code, session.getSessionID(), employeeID));
                                                                editor = pref.edit();
                                                                editor.putString("id", employeeID);
                                                                editor.putString("Name", name);
                                                                editor.putString("Group", tipe_name);
                                                                editor.commit();

                                                                Role role = new Role();
                                                                role.setCode(tipe_code);
                                                                role.setName(tipe_name);
                                                                db.addRole(role);

                                                                launchMapActivity(MainActivity.class);
                                                                finish();
                                                            }else {
                                                                session.createLoginSession("", _emailText.getText().toString(), "", "","", id, "karyawan","");
                                                                db.addUser(new User(_emailText.getText().toString(), name, group, session.getSessionID(), employeeID));
                                                                editor = pref.edit();
                                                                editor.putString("id", employeeID);
                                                                editor.putString("Name", name);
                                                                editor.putString("Group", group);
                                                                editor.commit();
                                                                launchMapActivity(MainActivity.class);
                                                                finish();
                                                            }
                                                        }


                                                    }catch (Exception e){
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                }
                                            }.execute(parameter);
                                        }else {
                                            progressDialog.dismiss();

                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            }.execute(parameter);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    public void loginMember() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("loginMember");
//        parameter.add(username);
        parameter.add(username+"@"+getString(R.string.versinew));
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
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            String name = result.getProperty("name").toString();
                            String userId = result.getProperty("userId").toString();
                            Log.d("Hasil Soap", ""+name);
                            session.createLoginSession(userId, username, name, "","Member", "", "member","");
                            SharedPreferences shared = getSharedPreferences(Config.SHARED_PREF, 0);
                            String token = (shared.getString("regId", ""));
                            session.tokenSession(token);
                            db.addUser(new User(username, name, "member", session.getSessionID(), userId));
                            ArrayList<String> parameter = new ArrayList<String>();
                            parameter.add("gcmUserLogin");
                            parameter.add(userId);
                            parameter.add(token);
                            new SoapClientMember(){
                                @Override
                                protected void onPostExecute(final SoapObject res) {
                                    super.onPostExecute(res);
                                    progressDialog.dismiss();
                                    final String code = res.getProperty("resCode").toString();
                                    final String text = res.getProperty("resText").toString();
                                    if (code.equals("1")) {
                                        launchMapActivity(UserMainActivity.class);
                                        finish();
                                    }else {
                                        progressDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }.execute(parameter);
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LoginActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

//

    public void loginSales() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        session.createLoginSession("testSales", username, username, "","Sales", "", "sales","");
        db.addUser(new User(_emailText.getText().toString(), username, username, session.getSessionID(), username));
        SharedPreferences shared = getSharedPreferences(Config.SHARED_PREF, 0);
        String token = (shared.getString("regId", ""));
        session.tokenSession(token);

        launchMapActivity(SalesMainActivity.class);
        finish();


    }

    public void loginOps() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        session.createLoginSession("testOps", username, username, "Ops","Ops", "", "ops","");
        db.addUser(new User(_emailText.getText().toString(), username, username, session.getSessionID(), username));
        Role role = new Role();
        role.setCode("26");
        role.setName("ops");
        db.addRole(role);

        SharedPreferences shared = getSharedPreferences(Config.SHARED_PREF, 0);
        String token = (shared.getString("regId", ""));
        session.tokenSession(token);

        launchMapActivity(OpsMainActivity.class);
        finish();
    }

    public void loginAr() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        session.createLoginSession("testAr", username, username, "","Ar", "", "ar","");
        db.addUser(new User(_emailText.getText().toString(), username, username, session.getSessionID(), username));
        SharedPreferences shared = getSharedPreferences(Config.SHARED_PREF, 0);
        String token = (shared.getString("regId", ""));
        session.tokenSession(token);

        launchMapActivity(ArMainActivity.class);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
       finish();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if(email.isEmpty())
        {
            _emailText.setError("masukkan username anda");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 16) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void launchMapActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, MAP_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MAP_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
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
                progressDialog.setMessage("Mengambil data menu...");
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
                                Toast.makeText(LoginActivity.this, "Cek Koneksi Anda", Toast.LENGTH_LONG).show();
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
                                int total = data.length() - 1;

                                for (int i = 0; i < data.length(); i++) {
                                    if (i != 0) {
                                        String menu = null, icon = null;
                                        JSONArray isi = data.getJSONArray(i);
                                        jab = isi.getString(3);
                                        atasan=isi.getString(6);
                                        Log.d("jabatan2", "" + jab);
                                    }
                                }

                                Log.d("jablogin",""+jab);

//
//                    if (atasan.equals("COO") || atasan.equals("CTO")) {
//
//                        if (jab.equals("B112") || jab.equals("B115A") || jab.equals("B115B") || jab.equals("B114")) {
//
//                            jml = 2;
//
//                        } else if (jab.equals("B104")) {
//                            jml = 3;
//                        } else if (jab.equals("B106") || jab.equals("B116") || jab.equals("B109")) {
//                            jml = 1;
//                        } else {
//                            jml = 3;
//                        }
//                    }else{
//                        jml=0;
//                    }
                                //hitung total menu static
//                                if()



//                                int jmlfix=total+jml;
//                                Log.d("hasilr",""+jmlfix);


                                db.updateTotalMenu(session.getUsername(), String.valueOf(total));


//                                session.jmlmenustatic(jml);


                                launchMapActivity(OpsMainActivity.class);

                                finish();
                            }else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Menu Tidak Tersedia",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }else {
                            Toast.makeText(LoginActivity.this, ""+text, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.d("Hasil error", ""+e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }.execute(parameter);
    }




    public void insertFirebaseId(String nik){
        JSONObject json = new JSONObject();

        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            json.put("service", "setFirebaseIdAndroidOps");
            json.put("nik",nik);
            json.put("firebaseId",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", "" + json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("1");
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add("" + json);
        progressDialog.setCancelable(false);
        new SoapClientMobile() {
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
                Log.d("hasil soap", "" + result);
                if (result == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    try {
                        final String code = result.getProperty(0).toString();
                        final String text = result.getProperty(1).toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", "" + data);
                            if (data.length() > 1) {
                                final JSONArray d = data.getJSONArray(2);
                                if (d.getJSONObject(1).getString("status").equals("1")) {
                                    Log.d("hasil soap data", "" + data);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LoginActivity.this, R.string.error_connection, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.d("hasil error", "" + e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }







}
