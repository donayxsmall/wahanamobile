package com.wahana.wahanamobile.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by team-it on 19/03/19.
 */

public class DownloadTaskNotifHRD {

    private static final String TAG = "Download Task";
    private Context context;
    private Button buttonText;
    private String downloadUrl = "", downloadFileName = "";
    private String kodeagen="";

//    private DownloadTask activity1;

    private Activity activity;

    private DatabaseHandler db;

    ProgressDialog progressDialog;



    public DownloadTaskNotifHRD(Context context, Button buttonText, String downloadUrl,String kodeagen) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;

        this.kodeagen=kodeagen;

//        downloadFileName = downloadUrl.replace(Utils.mainUrl, "");//Create file name by picking download file name from URL

        downloadFileName =kodeagen+"_"+getDateTime()+".pdf";
        Log.e(TAG, downloadFileName);

        Log.d("location1",""+downloadFileName);

        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);

        //Start Downloading Task
        new DownloadTaskNotifHRD.DownloadingTask().execute();

        readpdf(downloadFileName);

//        down(downloadFileName);
    }


    public void readpdf(String namefile){
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/WahanaFolder/NotifHRD/PDF/" + namefile);  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            context.startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


//    public void ff(){
//        db = new DatabaseHandler(context);
//
//        if(asal=="pu") {
//            db.importscv(downloadFileName);
//        }else if(asal=="stpu"){
//            db.importscvSTPU(downloadFileName);
//        }
//        Log.d("asal",""+asal);
//    }



    public void down(String de){
        db = new DatabaseHandler(activity);
        db.importscv("testttkpickup8.csv");
        Log.d("location",""+de);

    }

    public String ty(){
        return downloadFileName;
    }


    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            buttonText.setEnabled(false);
//            buttonText.setText(R.string.downloadStarted);//Set Button Text when download started

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Downloading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_LONG).show();

//                    buttonText.setEnabled(true);
//                    buttonText.setText(R.string.downloadCompleted);//If Download completed then change button text

//

                } else {
//                    buttonText.setText(R.string.downloadFailed);//If download failed change button text
                    progressDialog.dismiss();
                    Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            buttonText.setEnabled(true);
//                            buttonText.setText(R.string.downloadAgain);//Change button text again after 3sec
                            progressDialog.dismiss();
                            Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();

                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }


            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
//                buttonText.setText(R.string.downloadFailed);
                progressDialog.dismiss();
                Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        buttonText.setEnabled(true);
//                        buttonText.setText(R.string.downloadAgain);

                        progressDialog.dismiss();
                        Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


//                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectoryNotifPDF);
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdirs();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                Log.d("file",""+outputFile);

//                db = new DatabaseHandler(context);
//
//                String cek=db.importscv(downloadFileName);

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

//                ff();


            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }


    private String getDateTime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
