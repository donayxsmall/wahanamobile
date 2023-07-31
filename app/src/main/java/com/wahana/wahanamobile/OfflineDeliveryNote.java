package com.wahana.wahanamobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.adapter.OfflineDeliveryAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OfflineDeliveryNote extends DrawerHelper {
    private static final String TAG = "DeliveredAirways";

    ProgressDialog progressDialog;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan,total_sj,total_ttk,total_terkirim,total_belum;
    ImageView foto;
    ListView listViewOffline;
    Button sendButton;
    DatabaseHandler db;
    List<OfflineTTK> offlineList = new ArrayList<OfflineTTK>();
    List<OfflineTTK> offlineListSubmit = new ArrayList<OfflineTTK>();
    String encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    private SimpleDateFormat dateFormatter;
    String date;
    OfflineDeliveryAdapter adapter;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_delivery_note);
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
        session = new SessionManager(OfflineDeliveryNote.this);
        progressDialog = new ProgressDialog(OfflineDeliveryNote.this, R.style.AppTheme_Dark_Dialog);
        session.checkLogin();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        db = new DatabaseHandler(OfflineDeliveryNote.this);
        offlineList = db.getAllOfflineTTK(session.getID());

        View mCustomView = mInflater.inflate(R.layout.abs_ttk_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        ImageView calendarButton = (ImageView) mCustomView
                .findViewById(R.id.calendar_logo);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfflineDeliveryNote.this, MainActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        username = session.getUsername();
        user_id = session.getID();
        sendButton=(Button) findViewById(R.id.input_button);
//        if(listViewOffline!=null) {
        listViewOffline = (ListView) findViewById(R.id.list_offline_delivery_note);
        adapter = new OfflineDeliveryAdapter(OfflineDeliveryNote.this, offlineList);
        listViewOffline.setAdapter(adapter);
//        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OfflineDeliveryNote.this);
                alertDialog.setCancelable(false);
                // Setting Dialog Title
                alertDialog.setTitle("Update Status TTK");
                // Setting Dialog Message
                alertDialog.setMessage("Apakah Anda Yakin ?");
                // On pressing Settings button
                alertDialog.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                submitForm();
                            }
                        });
                // on pressing cancel button
                alertDialog.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
            }
        });
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

    private void submitForm() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate=dateTime.format(c.getTime());
        offlineListSubmit = db.getAllOfflineTTKSend(session.getID());
        for (int i = 0; i < offlineListSubmit.size(); i++){
            final OfflineTTK ttk = offlineListSubmit.get(i);
            List<String> uris = db.getAllTTKURI(ttk.getTTK());
            for (int s = 0; s < uris.size(); s++){
                String uri = uris.get(s);
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Bitmap bmp= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse("file://"+uri)));
                    bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
                    byte[] raw = out.toByteArray();
                    if (s == 0){
                        encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("hasil gambar 1", ""+uri);
                    }else if (s==1){
                        encodedImage2 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("hasil gambar 2", ""+uri);
                    }else if (s==2){
                        encodedImage3 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("hasil gambar 3", ""+uri);
                    }else if (s==3){
                        encodedImage4 = Base64.encodeToString(raw, Base64.DEFAULT);
                        Log.d("hasil gambar 4", ""+uri);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Keterangan", e+"");
                }
            }

            ArrayList<String> parameter = new ArrayList<String>();
            parameter.add("setPackageDeliveryStatus");
            parameter.add(ttk.getSessionid());
            parameter.add(ttk.getRequestid());
            parameter.add(ttk.getEmployeecode());
            parameter.add(ttk.getTTK().toString());
            parameter.add(formattedDate);
            parameter.add(ttk.getPackagestatus());
            parameter.add(ttk.getReason());
            parameter.add(ttk.getReceivername());
            parameter.add(ttk.getReceivertype());
            parameter.add(ttk.getComment());
            parameter.add(encodedImage);
            parameter.add(encodedImage2);
            parameter.add(encodedImage3);
            parameter.add(encodedImage4);
            parameter.add(ttk.getLongitude());
            parameter.add(ttk.getLatitude());
            Log.d("hasil gambar kirim", ""+encodedImage);

            final int progress = i;
            new SoapClient(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.d("adding data", "add");
                    Log.i(TAG, "onPreExecute");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Adding Data...");
                    progressDialog.show();
                }
                @Override
                protected void onPostExecute(SoapObject result) {
                    super.onPostExecute(result);
                    Log.d("hasil soap", "" + result);
                    if (result == null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(OfflineDeliveryNote.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        try {
                            final String text = result.getProperty(1).toString();
                            //  final String text="NOK";
                            Log.d("hasil soap2", "" + text);
                            if (text.equals("OK")) {
                                Toast.makeText(OfflineDeliveryNote.this, getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                                db.updateOfflineMessage(ttk.getTTK(), text, "Terkirim");
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(OfflineDeliveryNote.this, text, Toast.LENGTH_LONG).show();
                                    }
                                });
                                db.updateOfflineMessage(ttk.getTTK(), text, "Gagal");
                            }
                        }catch (Exception e){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(OfflineDeliveryNote.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                }
                            });
                            db.updateOfflineMessage(ttk.getTTK(), "System Error", "Gagal");
                        }
                        if (progress ==(offlineListSubmit.size()-1)){
                            progressDialog.dismiss();
                            offlineList.clear();
                            List<OfflineTTK> kodes = db.getAllOfflineTTK(session.getID());
                            adapter = new OfflineDeliveryAdapter(OfflineDeliveryNote.this, kodes);
                            listViewOffline.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }.execute(parameter);
            Log.d("hasil", i+"");
        }
    }



}
