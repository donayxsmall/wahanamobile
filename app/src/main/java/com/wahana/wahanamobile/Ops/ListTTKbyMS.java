package com.wahana.wahanamobile.Ops;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.Ops.stms.inputNoLabelMSActivity;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.adapter.SearchAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sadewa on 09/06/17.
 */

public class ListTTKbyMS extends DrawerHelper {
    private static final String TAG = "InputNoLabelActivity";
    ProgressDialog progressDialog;

    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK,print;
    SessionManager session;
    String username, user_id, idMS;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button scan;
    EditText label;
    RelativeLayout tabelHasil;
    ListView list_ttk_daftar;
    String PATH;
    DownloadTask downloadTask;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listttk_byms);
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
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListTTKbyMS.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        PATH = Environment.getExternalStorageDirectory()+"/wahana/ms/";
        File mediaStorageDir = new File(PATH, "wa");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
        progressDialog = new ProgressDialog(ListTTKbyMS.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(ListTTKbyMS.this);
        username = session.getUsername();
        user_id = session.getID();
        buttonOK = (Button) findViewById(R.id.input_button);
        scan = (Button) findViewById(R.id.scan_button);
        label = (EditText) findViewById(R.id.input_ttk);
        tabelHasil = (RelativeLayout) findViewById(R.id.tabel_hasil);
        list_ttk_daftar=(ListView) findViewById(R.id.list_ttk_daftar);
        print = (Button) findViewById(R.id.print);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent printIntent = new Intent(ListTTKbyMS.this, ListTTKbyMS.class);
//                printIntent.putExtra("idMS", idMS);
//                startActivity(printIntent);
                downloadTask = new DownloadTask(ListTTKbyMS.this);
                downloadTask.execute("http://mobile.wahana.com/apps/wahana/cgi-bin/api.cgi?t=pdf&b=view/vnm.ms_termal&ATRBTID="+idMS+"&ro=1&thermal=1");

            }
        });

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });

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

    private void submitForm() {
        if (!validateKode()) {
            return;
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getTTKbyMS");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ms");
        parameter.add(label.getText().toString());

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
                            Toast.makeText(ListTTKbyMS.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
//                                    ArrayList<String> ttks = new ArrayList<String>();
//                                    ArrayList<String> berats = new ArrayList<String>();
//                                    String [] ttk_ms,berat_ttk;
                                    List<BuatSJ> ttkList = new ArrayList<BuatSJ>();
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);
                                            String berat = isi.getString(1);
//                                            ttks.add(ttk);
//                                            berats.add(berat);
                                            BuatSJ sj = new BuatSJ();
                                            sj.setTtk(ttk);
                                            sj.setTujuan(berat);
                                            ttkList.add(sj);
                                            idMS = isi.getString(2);
                                        }
                                    }
//                                    ttk_ms = ttks.toArray(new String[ttks.size()]);
//                                    berat_ttk = berats.toArray(new String[berats.size()]);
//                                    list_ttk_daftar.setAdapter(new SearchAdapter(ListTTKbyMS.this, ttk_ms, berat_ttk));

                                    list_ttk_daftar.setAdapter(new BuatSJAdapter(ListTTKbyMS.this, ttkList, "ttk_ms"));
                                    tabelHasil.setVisibility(View.VISIBLE);
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ListTTKbyMS.this, "Mohon Cek Kembali No MS Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    tabelHasil.setVisibility(View.GONE);
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ListTTKbyMS.this, "Mohon Cek Kembali No MS Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                                tabelHasil.setVisibility(View.GONE);
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ListTTKbyMS.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                            tabelHasil.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListTTKbyMS.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                        tabelHasil.setVisibility(View.GONE);
                    }
                }
            }
        }.execute(parameter);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String nilai=data.getStringExtra("ttk");
            label.setText(nilai);
        }
    }

//    @Override
//    public void onBackPressed() {
//        // do nothing.
//    }


    private boolean validateKode() {
        if (label.getText().toString().trim().isEmpty()) {
            label.setError("Masukkan Label");

            return false;
        } else {
            label.setError(null);
        }

        return true;
    }
    
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat cf = new SimpleDateFormat("ddMMyyHHmm");
        String formattedDate = cf.format(c.getTime());

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            progressDialog.dismiss();
            if (result != null){
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            }else{
//                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();

                final PrintDocumentAdapter pda = new PrintDocumentAdapter(){

                    @Override
                    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras){

                        if (cancellationSignal.isCanceled()) {
                            callback.onLayoutCancelled();
                            return;
                        }


                        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("MS-"+formattedDate+".pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                        callback.onLayoutFinished(pdi, true);
                    }

                    @Override
                    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback){
                        InputStream input = null;
                        OutputStream output = null;

                        try {

                            input = new FileInputStream(PATH+"MS-"+formattedDate+".pdf");
                            output = new FileOutputStream(destination.getFileDescriptor());

                            byte[] buf = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = input.read(buf)) > 0) {
                                output.write(buf, 0, bytesRead);
                            }

                            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                        } catch (FileNotFoundException ee){
                            //Catch exception
                        } catch (Exception e) {
                            //Catch exception
                        } finally {
                            try {
                                input.close();
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                PrintManager printManager = (PrintManager) ListTTKbyMS.this.getSystemService(Context.PRINT_SERVICE);
                String jobName = ListTTKbyMS.this.getString(R.string.app_name) + " Document";
                printManager.print(jobName, pda, null);

//                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/wahana/ms/"+"MS-"+formattedDate+".pdf"));
//                Uri docUri = Uri.parse(PATH+"MS-"+formattedDate+".pdf");
//                Log.d("hasil uri", docUri+"");
//                Log.d("hasil uri 2", uri+"");
//                String path = docUri.getPath();
//                Log.d("hasil uri 3", path+"");
//                File file = new File(path);
//                if (file.exists()) {
//                    Intent printIntent = new Intent(ListTTKbyMS.this, PrintActivity.class);
//                    printIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
//                    printIntent.putExtra("title", "tes");
//                    startActivity(printIntent);
//                    Uri pdfPath = Uri.fromFile(file);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(pdfPath, "application/pdf");
//
//                    try {
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        //if user doesn't have pdf reader instructing to download a pdf reader
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "No file", Toast.LENGTH_SHORT).show();
//                }
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(PATH+"MS-"+formattedDate+".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
