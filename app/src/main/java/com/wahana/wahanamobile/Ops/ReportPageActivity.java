package com.wahana.wahanamobile.Ops;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportPageActivity extends DrawerHelper {
    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, url;
    TextView nama,jabatan;
    ImageView foto;
    WebView browser;
    ProgressBar progressBar;
   // private ValueCallback<Uri[]> mUploadMessage;
    //private final static int FILECHOOSER_RESULTCODE = 1;
    private String LOG_TAG = "DREG";
    private Uri[] results;
    private FilePickerDialog dialog;

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private ValueCallback<Uri> mUploadMessage;

    private static final String TAG = ReportPageActivity.class.getSimpleName();
    private Uri mCapturedImageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ReportPageActivity.this, OpsMainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(ReportPageActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(ReportPageActivity.this);
        user_id = session.getID();
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
//        url = "http://wahana.com/new/Android/tabel/2";
//        url = "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=seek/fnm.whnTTKList&ttkty=101";

//        browser = (WebView) findViewById(R.id.webview);
//        browser.getSettings().setJavaScriptEnabled(true);
//        browser.getSettings().setPluginState(WebSettings.PluginState.ON);
//        browser.getSettings().setJavaScriptEnabled(true);
//        browser.getSettings().setDomStorageEnabled(true);
//        browser.getSettings().setLoadWithOverviewMode(true);
//        browser.getSettings().setUseWideViewPort(true);
//        browser.getSettings().setSupportZoom(true);
//        browser.getSettings().setAllowFileAccess(true);
//        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        browser.setWebViewClient(new myWebClient());
//        //browser.setWebChromeClient(new WebChromeClient());
//
//        String cookieString = "dwsession=u&"+session.getUsername()+"&s&"+session.getSessionID()+"; domain=mobile.wahana.com";
//        CookieManager cm = CookieManager.getInstance();
//        cm.setCookie(url, cookieString);
//        browser.loadUrl(url);

        browser = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
//        browser.setWebViewClient(new PQClient());
//        browser.setWebChromeClient(new PQChromeClient());
//        if (Build.VERSION.SDK_INT >= 19) {
//            browser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            browser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }






        browser.setWebChromeClient(new ChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            browser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
            browser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
//
//        String urlString="https://www.youtube.com/watch?v=miomuSGoPzIe";
//        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
//
//        intent.setPackage("com.android.chrome");
//
//        startActivity(intent);


        browser.setWebViewClient(new myWebClient());

//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeSessionCookie(); // problem
//        CookieSyncManager.getInstance().sync(); // maybe problem

        String cookieString = "dwsession=u&"+session.getUsername()+"&s&"+session.getSessionID()+"; domain=mobile.wahana.com";

        Log.d("hasil cookies",""+cookieString);
        CookieManager cm = CookieManager.getInstance();

        browser.clearCache(true);
        browser.clearHistory();
        browser.reload();

//        cm.clearCookies(getActivity());



        cm.setCookie(url, cookieString);

        browser.loadUrl(url);

        Log.d("session",""+cookieString);


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public class ChromeClient extends WebChromeClient {
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
//            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//            contentSelectionIntent.setType("image/*");
//            Intent[] intentArray;
//            if (takePictureIntent != null) {
//                intentArray = new Intent[]{takePictureIntent};
//            } else {
//                intentArray = new Intent[0];
//            }
//            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(takePictureIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }


        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_OPENABLE);
//            i.setType("image/*");
//            // Create file chooser intent
//            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//            // Set camera intent to file chooser
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(captureIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }

    }


//    private void openFileSelectionDialog() {
//
//        if (null != dialog && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//
//        //Create a DialogProperties object.
//        final DialogProperties properties = new DialogProperties();
//
//        //Instantiate FilePickerDialog with Context and DialogProperties.
//        dialog = new FilePickerDialog(ReportPageActivity.this, properties);
//        dialog.setTitle("Select a File");
//        dialog.setPositiveBtnName("Select");
//        dialog.setNegativeBtnName("Cancel");
//        properties.selection_mode = DialogConfigs.MULTI_MODE; // for multiple files
////        properties.selection_mode = DialogConfigs.SINGLE_MODE; // for single file
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//
//        //Method handle selected files.
//        dialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                results = new Uri[files.length];
//                for (int i = 0; i < files.length; i++) {
//                    String filePath = new File(files[i]).getAbsolutePath();
//                    if (!filePath.startsWith("file://")) {
//                        filePath = "file://" + filePath;
//                    }
//                    results[i] = Uri.parse(filePath);
//                    Log.d(LOG_TAG, "file path: " + filePath);
//                    Log.d(LOG_TAG, "file uri: " + String.valueOf(results[i]));
//                }
//                mUploadMessage.onReceiveValue(results);
//                mUploadMessage = null;
//            }
//        });
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                if (null != mUploadMessage) {
//                    if (null != results && results.length >= 1) {
//                        mUploadMessage.onReceiveValue(results);
//                    } else {
//                        mUploadMessage.onReceiveValue(null);
//                    }
//                }
//                mUploadMessage = null;
//            }
//        });
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if (null != mUploadMessage) {
//                    if (null != results && results.length >= 1) {
//                        mUploadMessage.onReceiveValue(results);
//                    } else {
//                        mUploadMessage.onReceiveValue(null);
//                    }
//                }
//                mUploadMessage = null;
//            }
//        });
//
//        dialog.show();
//
//    }
//
//    public class PQChromeClient extends WebChromeClient {
//
//        @Override
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//            // Double check that we don't have any existing callbacks
//            if (mUploadMessage != null) {
//                mUploadMessage.onReceiveValue(null);
//            }
//            mUploadMessage = filePathCallback;
//
//            openFileSelectionDialog();
//
//            return true;
//        }
//
//    }
//
//    public class PQClient extends WebViewClient {
//        ProgressBar progressDialog;
//
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//            // If url contains mailto link then open Mail Intent
//            if (url.contains("mailto:")) {
//
//                // Could be cleverer and use a regex
//                //Open links in new browser
//                view.getContext().startActivity(
//                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//
//                // Here we can open new activity
//
//                return true;
//
//            } else {
//                // Stay within this webview and load url
//                view.loadUrl(url);
//                return true;
//            }
//        }
//    }





    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
            browser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

//    @Override
//    public void onBackPressed()
//    {
//        super.onBackPressed();
//        Intent i = new Intent(this, ListSubMenuActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//        finish();
//    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

}
