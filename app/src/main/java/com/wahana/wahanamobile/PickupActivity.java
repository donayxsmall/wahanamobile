package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.model.Pickup.PickupBegin;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class PickupActivity extends DrawerHelper {
    private static final String TAG = "PickupActivity";

    ProgressDialog progressDialog;

    private TextView inputLayoutKode;
    public EditText inputKode;
    private Button btnInput;

    TextView pengisi, tgl, calendar;
    RelativeLayout infoLayout;

    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;
    DatabaseHandler db;
    UserAPIService mApiInterface;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mApiInterface = RestApiWahanaOps.getClient().create(UserAPIService.class);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PickupActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(PickupActivity.this);
        db.deleteKode();
        progressDialog = new ProgressDialog(PickupActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        db.deletePickup();
        btnInput = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(username);
        infoLayout = (RelativeLayout) findViewById(R.id.info_layout);

        infoLayout.bringToFront();
        infoLayout.invalidate();
        inputLayoutKode = (TextView) findViewById(R.id.input_layout_kode_agen);
        inputKode = (EditText) findViewById(R.id.input_kode_agen);
        this.setTitle("");
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        inputKode.setTypeface(type);
        inputKode.addTextChangedListener(new MyTextWatcher(inputKode));
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

//                submitFormver2();
            }
        });

//        createfolder();
    }


    public void createfolder(){
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Pickup");
            if (!file.exists()) {
                file.mkdirs();
            }
    }



    private void submitFormver2(){
        if (!validate()) {
            return;
        }

        final String agentcode = inputKode.getText().toString();



        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);

//        final ApiPickup pickupBegin = new ApiPickup(session.getSessionID(),agentcode,session.getID(),rc);

//        Log.d("postreq",""+session.getID()+"|"+agentcode+"|"+session.getID()+"|"+rc);

        Call<ApiPickup> result = mApiInterface.pickupBegin(session.getSessionID(),agentcode,session.getID(),rc);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<ApiPickup>() {

            @Override
            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
                try {

                    progressDialog.dismiss();

                    String vrcode=response.body().getVrcode();
                    String vrmsg=response.body().getVrmesg();
                    String vurl=response.body().getvUrl();
                    String verificationcode=response.body().getVerificationCode();

                    if(vrcode.equals("1")){
                        Toast.makeText(PickupActivity.this," response version "+response.body().getVerificationCode(),Toast.LENGTH_SHORT).show();

                        Log.d("retro",""+response.body().getVerificationCode()+"|"+vurl+"|"+verificationcode);

                        DatabaseHandler db = new DatabaseHandler(PickupActivity.this);
                        db.addAgent(new AgentCode(agentcode));
                        Intent intent = new Intent(PickupActivity.this, PickupVerifikasi.class);
                        intent.putExtra("kodeagen",agentcode);
                        intent.putExtra("urllink",vurl);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    }else{
                        Toast.makeText(PickupActivity.this," error "+vrmsg,Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiPickup> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.d("error",""+t.toString());
            }
        });



    }


    //REST API NEW ANDROID OPS

    private void submitFormRest(){

        if (!validate()) {
            return;
        }

        final String agentcode = inputKode.getText().toString();

        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);

        Call<PickupBegin> result = mApiInterface.pickupBeginRest(session.getID(),agentcode,session.getSessionID(),rc);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<PickupBegin>() {

            @Override
            public void onResponse(Call<PickupBegin> call, Response<PickupBegin> response) {
                try {

                    progressDialog.dismiss();

                    String vrcode=response.body().getResCode();
                    String vrmsg=response.body().getResText();
                    String verificationcode=response.body().getVerificationCode();

                    if(vrcode.equals("1")){
                        Toast.makeText(PickupActivity.this," response version "+response.body().getVerificationCode(),Toast.LENGTH_SHORT).show();

                        DatabaseHandler db = new DatabaseHandler(PickupActivity.this);
                        db.addAgent(new AgentCode(agentcode));
                        Intent intent = new Intent(PickupActivity.this, PickupVerifikasi.class);
                        intent.putExtra("kodeagen",agentcode);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    }else{
                        Toast.makeText(PickupActivity.this," error "+vrmsg,Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PickupBegin> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.d("error",""+t.toString());
            }
        });



    }



    private void submitForm() {
        if (!validate()) {
            return;
        }
        final String agentcode = inputKode.getText().toString();
        Log.d("agent code", agentcode);
        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("pickupBegin");
        parameter.add(session.getSessionID());
        parameter.add(rc);
        parameter.add(session.getID());
        parameter.add(agentcode);
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
                            Toast.makeText(PickupActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            DatabaseHandler db = new DatabaseHandler(PickupActivity.this);
                            db.addAgent(new AgentCode(agentcode));
                            Intent intent = new Intent(PickupActivity.this, PickupVerifikasi.class);
                            intent.putExtra("kodeagen",agentcode);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PickupActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_kode_agen:
                  //  validateKode();
                    break;

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean validate() {
        boolean valid = true;

        String code = inputKode.getText().toString();

        if(code.isEmpty())
        {
            inputKode.setError("Masukkan Kode Agen");
            valid = false;
        } else {
            inputKode.setError(null);
        }
        return valid;
    }



}
