package com.wahana.wahanamobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.Data.ttkUser;
import com.wahana.wahanamobile.adapter.MemberAirwaysAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MemberAirways extends DrawerHelper {
    private static final String TAG = "List TTK Member";

    ProgressDialog progressDialog;
    TextView pengisi, tgl, calendar;
    public static String [] noTTK={"aab00001","aab10091","asw09912"};
    public static String [] status={"1","2","4"};
    public static String [] keterangan={"Menuju kota tujuan","Di kota tujuan","Terkirim"};
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private SimpleDateFormat dateFormatter;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;

    TextView nama,jabatan;
    ImageView foto;
    ListView listViewMemberAirways;
    DatabaseHandler db;
    List<ListTTK> listDateTTk = new ArrayList<ListTTK>();
    List<ListTTK> listTTk = new ArrayList<ListTTK>();
    String date;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_airways);
        super.onCreateDrawer(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        progressDialog = new ProgressDialog(MemberAirways.this, R.style.AppTheme_Dark_Dialog);

        View mCustomView = mInflater.inflate(R.layout.abs_ttk_layout, null);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar c = Calendar.getInstance();
        date = dateFormatter.format(c.getTime());

        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        ImageView calendarButton = (ImageView) mCustomView
                .findViewById(R.id.calendar_logo);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberAirways.this, UserMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        session = new SessionManager(MemberAirways.this);
        session.checkLogin();
        listTtkMember();
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        listViewMemberAirways = (ListView) findViewById(R.id.list_delivery_note_list_item);
        username = session.getUsername();
        user_id = session.getID();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void listTtkMember() {
        Log.d(TAG, "List TTK Member");

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("listTtkUser");
        parameter.add(session.getID());

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
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MemberAirways.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
//                            String userId = result.getProperty("userId").toString();
//                            Intent intent = new Intent(MemberAirways.this, Signup2Activity.class);
                            List<ttkUser> listTTk = new ArrayList<ttkUser>();
                            Vector<SoapObject> vector = (Vector<SoapObject>) result.getProperty("list");
                            for (SoapObject so : vector){
                                String noTtk = null, ket = null, status=null;
                                noTtk = so.getProperty("ttk").toString();
                                ket = so.getProperty("ket").toString();
                                status = so.getProperty("status_ttk").toString();

                                ttkUser ttk = new ttkUser();
                                if (status.equals("4")){
                                    ttk.setKet("Terkirim");
                                }else {
                                    ttk.setKet(ket);
                                }
                                ttk.setNoTtk(noTtk);
                                listTTk.add(ttk);
                            }
                            listViewMemberAirways.setAdapter(new MemberAirwaysAdapter(MemberAirways.this, listTTk));
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MemberAirways.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MemberAirways.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }
}
