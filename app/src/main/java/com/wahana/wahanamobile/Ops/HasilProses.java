package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.SJP.ListdetailSJP;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DownloadTaskFile;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.FileDownloader;
import com.wahana.wahanamobile.helper.Qrcode;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.DownloadTaskPU;

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
import java.util.Date;
import java.util.Locale;

public class HasilProses extends DrawerHelper {
    private static final String TAG = "HasilActivity";
    ProgressDialog progressDialog;

    String proses, no,ATRBTID,kodeagen,nostm;
    TextView kode_kurir;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK,print,lihatpdf,sendemail,lihatdetailsjp,sharepdf;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id,kodesortir,niksupir,kodeverifikasi,flagsortir,urlfilepu,flagstat;
    TextView nama,jabatan;
    ImageView foto;
    TableRow label;
    DownloadTask downloadTask;
    String PATH;
    Intent myIntent;
    ImageView imageView;
    DatabaseHandler db;
    RelativeLayout layout_info;
    TextView nottk,beratsebelum,volumesebelum,beratsesudah,volumesesudah;
    String ttkfix,beratsebelumfix,volumesebelumfix,beratsesudahfix,volumesesudahfix,urlfile;



    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create", "manifest");
        super.onCreate(savedInstanceState);
        myIntent = getIntent(); // gets the previously created intent
        proses = myIntent.getStringExtra("proses");
        no = myIntent.getStringExtra("no");
        nostm = myIntent.getStringExtra("nostm");

        ATRBTID = myIntent.getStringExtra("ATRBTID");
        kodeagen = myIntent.getStringExtra("kodeagen");

        niksupir = myIntent.getStringExtra("niksupir");
        kodesortir = myIntent.getStringExtra("kodesortir");

        kodeverifikasi = myIntent.getStringExtra("kodeverifikasi");
        flagsortir = myIntent.getStringExtra("flagsortir");
        urlfilepu = myIntent.getStringExtra("urlfilepu");
        flagstat = myIntent.getStringExtra("flagstat");


        beratsebelumfix = myIntent.getStringExtra("beratsebelum");
        volumesebelumfix = myIntent.getStringExtra("volumesebelum");
        beratsesudahfix = myIntent.getStringExtra("beratsesudah");
        volumesesudahfix = myIntent.getStringExtra("volumesesudah");

        urlfile = myIntent.getStringExtra("urlfile");




        db = new DatabaseHandler(HasilProses.this);

//        proses = "ms";
//        no = "MS-1707-Z00665";
        setContentView(R.layout.activity_hasil_proses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HasilActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(HasilProses.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(HasilProses.this);
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        label = (TableRow) findViewById(R.id.label);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        manifest_label = (TextView) findViewById(R.id.manifest_label);
        manifest_isi = (TextView) findViewById(R.id.manifest_isi);
        hasil_manifest = (TextView) findViewById(R.id.hasil_manifest);
        print = (Button) findViewById(R.id.print);

        lihatpdf = (Button) findViewById(R.id.lihatpdf);
        sendemail = (Button) findViewById(R.id.sendemail);
        imageView = (ImageView) findViewById(R.id.imageView);
        lihatdetailsjp = (Button) findViewById(R.id.lihatdetailsjp);
        sharepdf = (Button) findViewById(R.id.sharepdf);


        layout_info = (RelativeLayout) findViewById(R.id.layout_info);
        nottk = (TextView)findViewById(R.id.nottk);
        beratsebelum = (TextView)findViewById(R.id.beratsebelum);
        volumesebelum = (TextView)findViewById(R.id.volumesebelum);
        beratsesudah = (TextView)findViewById(R.id.beratsesudah);
        volumesesudah = (TextView)findViewById(R.id.volumesesudah);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        manifest_label.setTypeface(type);
        manifest_isi.setTypeface(type, Typeface.BOLD);
        hasil_manifest.setTypeface(type);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        pengisi.setText(user_id);
        if (proses.equals("sj")) {
            Qrcode qr = new Qrcode();
            qr.create(imageView, no);

            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SURAT JALAN");
            hasil_manifest.setText("SURAT JALAN BERHASIL DIBUAT");

            imageView.setVisibility(View.VISIBLE);
        } else if (proses.equals("sjp")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SURAT JALAN PENERUS");
            hasil_manifest.setText("SURAT JALAN PENERUS BERHASIL DIBUAT");

            lihatdetailsjp.setVisibility(View.VISIBLE);

            lihatdetailsjp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent picture_intent = new Intent(HasilProses.this, ListdetailSJP.class);
                    picture_intent.putExtra("nosjp", no);
                    startActivity(picture_intent);
                }
            });


        } else if (proses.equals("stbt")) {
            manifest_isi.setText(no);
            manifest_label.setText("BERHASIL STBT");
            hasil_manifest.setText("BERHASIL STBT");
        } else if (proses.equals("stms")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR STMS");
            hasil_manifest.setText("STMS BERHASIL DIBUAT");
        } else if (proses.equals("stsm")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR STSM");
            hasil_manifest.setText("STSM BERHASIL DIBUAT");
        } else if (proses.equals("ma")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR MODA ANGKUTAN");
            hasil_manifest.setText("MODA ANGKUTAN BERHASIL DIBUAT");
        } else if (proses.equals("ms")) {
            final String msId = myIntent.getStringExtra("msId");
//            final String msId = "2495212276";
            PATH = Environment.getExternalStorageDirectory() + "/wahana/ms/";
            File mediaStorageDir = new File(PATH, "wa");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                print.setVisibility(View.VISIBLE);
            }
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR MANIFEST SORTIR");
            hasil_manifest.setText("MANIFEST SORTIR BERHASIL DIBUAT");
            print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadTask = new DownloadTask(HasilProses.this);
                    downloadTask.execute("http://mobile.wahana.com/apps/wahana/cgi-bin/api.cgi?t=pdf&b=view/vnm.ms_termal&ATRBTID=" + msId + "&ro=1&thermal=1");
                }
            });
        } else if (proses.equals("sm")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SURAT MUATAN");
            hasil_manifest.setText("SURAT MUATAN BERHASIL DIBUAT");
        } else if (proses.equals("stk")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL STK");
        } else if (proses.equals("updateMA")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SMU SISTEM");
            hasil_manifest.setText("BERHASIL INPUT SMU");
        } else if (proses.equals("updateSJP")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL UPDATE DATA SJP");
        } else if (proses.equals("retur")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR PENERIMAAN RETUR");
            hasil_manifest.setText("PENERIMAAN RETUR BERHASIL");
        } else if (proses.equals("selisih")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SELISIH BERAT");
            hasil_manifest.setText("BERHASIL MENAMBAHKAN SELISIH BERAT");
        } else if (proses.equals("inputbbm")) {
            manifest_isi.setText(no);
            manifest_label.setText("No Input BBM");
            hasil_manifest.setText("Data Input BBM BERHASIL DIBUAT");
        } else if (proses.equals("pu")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR PU");
            hasil_manifest.setText("PU BERHASIL DIBUAT");

            lihatpdf.setVisibility(View.GONE);
            sendemail.setVisibility(View.GONE);

//            if (flagsortir.equals("1")) {
//                db.deleteListPickup(kodeagen, kodeverifikasi, kodesortir);
//            } else {
//                db.deleteListPickupAll(kodeagen, kodeverifikasi);
//            }


            sendemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//                    String url="http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1";
//                    new DownloadTaskPU(HasilProses.this,lihatpdf,url,"aa");
//                    Intent picture_intent = new Intent(HasilProses.this, ReportPageActivity.class);
//                    picture_intent.putExtra("url", "http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1");
//                    //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
//                    startActivity(picture_intent);
                }
            });

//            lihatpdf.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String url = urlfilepu;
//
//                    String namafile = kodeagen + "_" + getDateTime() + ".pdf";
//
//                    new DownloadTaskPU(HasilProses.this, lihatpdf, url, kodeagen);
//
//                }
//            });


        }else if(proses.equals("pu-scan")){

            if(flagstat.equals("2")){
                manifest_isi.setText(no);
                manifest_label.setText("NOMOR TTK BATAL / PENDING");
                hasil_manifest.setText("BERHASIL DIBUAT");

                lihatpdf.setVisibility(View.GONE);
                sendemail.setVisibility(View.GONE);

                if (flagsortir.equals("1")) {
                    db.deleteListPickup(kodeagen, kodeverifikasi, kodesortir);
                } else {
                    db.deleteListPickupAll(kodeagen, kodeverifikasi);
                }

            }else{

                manifest_isi.setText(no);
                manifest_label.setText("NOMOR PU");
                hasil_manifest.setText("PU BERHASIL DIBUAT");

                lihatpdf.setVisibility(View.VISIBLE);
                sendemail.setVisibility(View.GONE);

                if (flagsortir.equals("1")) {
                    db.deleteListPickup(kodeagen, kodeverifikasi, kodesortir);
                } else {
                    db.deleteListPickupAll(kodeagen, kodeverifikasi);
                }

            }


            sendemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//                    String url="http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1";
//                    new DownloadTaskPU(HasilProses.this,lihatpdf,url,"aa");
//                    Intent picture_intent = new Intent(HasilProses.this, ReportPageActivity.class);
//                    picture_intent.putExtra("url", "http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1");
//                    //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
//                    startActivity(picture_intent);
                }
            });

            lihatpdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = urlfilepu;

                    String namafile = kodeagen + "_" + getDateTime() + ".pdf";

                    new DownloadTaskPU(HasilProses.this, lihatpdf, url, kodeagen);

//                    new DownloadFile().execute(url, namafile);
//
//
//                    readpdf(namafile);


//                    String url="http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1";
//                    new DownloadTaskPU(HasilProses.this,lihatpdf,url,"aa");
//                    Intent picture_intent = new Intent(HasilProses.this, ReportPageActivity.class);
//                    picture_intent.putExtra("url", "http://intranet.wahana.com/apps/wahana/cgi-bin/dw.cgi?t=pdf&b=view/vnm.prn_man&ATRBTID="+ATRBTID+"&ro=1&me=1&iswv=1");
//                    //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
//                    startActivity(picture_intent);
                }
            });

        }else if(proses.equals("stpu")) {

            manifest_isi.setText(no);
            manifest_label.setText("NOMOR STPU");
            hasil_manifest.setText("STPU BERHASIL DIBUAT");

            db.deleteListSTPU(niksupir, kodesortir);

            Log.d("stpu",""+nostm);
        }else if(proses.equals("updateSJPttk")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL UPDATE DATA SJP");
        }else if(proses.equals("USmanual")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL UPDATE TERKIRIM");
        }else if(proses.equals("ttkbermasalah")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL UPDATE TTK BERMASALAH");
        }else if(proses.equals("ttkterkirimpanelMW")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL UPDATE TTK TERKIRIM PANEL MW");
        }else if(proses.equals("ttkkeranjang")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL DITAMBAHKAN KE KERANJANG");
        }else if(proses.equals("ttkkeranjangbaru")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL DITAMBAHKAN KERANJANG BARU");
        }else if(proses.equals("ttkdestroy")) {
            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL di Tandai Destroy");
        }else if(proses.equals("retursaltem")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR PENERIMAAN RETUR");
            hasil_manifest.setText("PENERIMAAN RETUR BERHASIL");
        }else if(proses.equals("stksw")) {
            manifest_isi.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL STK SW");
        }else if(proses.equals("editberatttk")) {
            manifest_isi.setVisibility(View.GONE);
            label.setVisibility(View.GONE);

            layout_info.setVisibility(View.VISIBLE);

            nottk.setText(no);
            beratsebelum.setText(beratsebelumfix+" Kg");
            volumesebelum.setText(volumesebelumfix);
            beratsesudah.setText(beratsesudahfix+" Kg");
            volumesesudah.setText(volumesesudahfix);

            hasil_manifest.setText("BERHASIL DIUBAH");
        }else if (proses.equals("stpuma")) {

            manifest_isi.setText(no);
            manifest_label.setText("NOMOR STPU");
            hasil_manifest.setText("STPU BERHASIL DIBUAT");

            sharepdf.setVisibility(View.VISIBLE);

//            db.deleteListSTPUMA();

            String noreplace = no.replaceAll("/", "-");

            final String downloadFileName = noreplace + ".pdf";


            new DownloadTaskFile(HasilProses.this, urlfile, "STPUMA", downloadFileName);


            sharepdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    String downloadFileNamefull = urlfile.substring(urlfile.lastIndexOf('/'), urlfile.length());
//                    String downloadFileName = downloadFileNamefull.replaceAll("/", "");


                    File documentsPath = new File(Environment.getExternalStorageDirectory(), "STPUMA");
                    File file = new File(documentsPath, downloadFileName);

                    Uri uri = null;

                    try {
                        uri = FileProvider.getUriForFile(HasilProses.this,
                                "com.wahana.wahanamobile",
                                file);
                    } catch (Exception e) {

                    }

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share using"));

                }
            });


        } else if (proses.equals("directma")) {

            manifest_isi.setText(no);
            manifest_label.setText("NOMOR MA");
            hasil_manifest.setText("MA BERHASIL DIBUAT");

            sharepdf.setVisibility(View.VISIBLE);

            String noreplace = no.replaceAll("/", "-");
            final String downloadFileName = noreplace + ".pdf";

            new DownloadTaskFile(HasilProses.this, urlfile, "MA", downloadFileName);


            sharepdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    String downloadFileNamefull = urlfile.substring(urlfile.lastIndexOf('/'), urlfile.length());
//                    String downloadFileName = downloadFileNamefull.replaceAll("/", "");


                    File documentsPath = new File(Environment.getExternalStorageDirectory(), "MA");
                    File file = new File(documentsPath, downloadFileName);

                    Uri uri = null;

                    try {
                        uri = FileProvider.getUriForFile(HasilProses.this,
                                "com.wahana.wahanamobile",
                                file);
                    } catch (Exception e) {

                    }

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share using"));

                }
            });

            Log.d("stpu", "" + nostm);
        } else if (proses.equals("bongkarmafinal")) {

            manifest_isi.setText(no);
            manifest_label.setText("STMA");
            hasil_manifest.setText("BERHASIL DITERIMA");

            sharepdf.setVisibility(View.VISIBLE);

            String noreplace = no.replaceAll("/", "-");
            final String downloadFileName = noreplace + ".pdf";

            new DownloadTaskFile(HasilProses.this, urlfile, "MA", downloadFileName);


            sharepdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    String downloadFileNamefull = urlfile.substring(urlfile.lastIndexOf('/'), urlfile.length());
//                    String downloadFileName = downloadFileNamefull.replaceAll("/", "");


                    File documentsPath = new File(Environment.getExternalStorageDirectory(), "MA");
                    File file = new File(documentsPath, downloadFileName);

                    Uri uri = null;

                    try {
                        uri = FileProvider.getUriForFile(HasilProses.this,
                                "com.wahana.wahanamobile",
                                file);
                    } catch (Exception e) {

                    }

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share using"));

                }
            });

        }else if(proses.equals("stpuberat")) {

            manifest_isi.setText(no);
            manifest_label.setText("NOMOR STPU");
            hasil_manifest.setText("STPU BERHASIL DIBUAT");

            sharepdf.setVisibility(View.VISIBLE);


            String noreplace = no.replaceAll("/", "-");

            final String downloadFileName = noreplace+".pdf";


            new DownloadTaskFile(HasilProses.this, urlfile,"STPUMA",downloadFileName);


            sharepdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    String downloadFileNamefull = urlfile.substring(urlfile.lastIndexOf('/'), urlfile.length());
//                    String downloadFileName = downloadFileNamefull.replaceAll("/", "");



                    File documentsPath = new File(Environment.getExternalStorageDirectory(), "STPUMA");
                    File file = new File(documentsPath, downloadFileName);

                    Uri uri = null;

                    try {
                        uri = FileProvider.getUriForFile(HasilProses.this,
                                "com.wahana.wahanamobile",
                                file);
                    }catch (Exception e){

                    }

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share using"));

                }
            });


        }else if(proses.equals("bongkarma")){

            manifest_isi.setText(no);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("DATA BERHASIL DIBUAT");
//            db.deleteListBONGKARMA();

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, OpsMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
//

//        Intent intent = new Intent(this, OpsMainActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(this, OpsMainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        // do nothing.
    }


    public void readpdf(String namefile){
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/WahanaFolder/Pickup/PDF/" + namefile);  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(HasilProses.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private class DownloadFile extends AsyncTask<String, Void, Void>{


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
        protected Void doInBackground(String... strings) {

            progressDialog.dismiss();

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "WahanaFolder/Pickup/PDF");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }


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

                PrintManager printManager = (PrintManager) HasilProses.this.getSystemService(Context.PRINT_SERVICE);
                String jobName = HasilProses.this.getString(R.string.app_name) + " Document";
                printManager.print(jobName, pda, null);

//                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/wahana/ms/"+"MS-"+formattedDate+".pdf"));
//                Uri docUri = Uri.parse(PATH+"MS-"+formattedDate+".pdf");
//                Log.d("hasil uri", docUri+"");
//                Log.d("hasil uri 2", uri+"");
//                String path = docUri.getPath();
//                Log.d("hasil uri 3", path+"");
//                File file = new File(path);
//                if (file.exists()) {
//                    Intent printIntent = new Intent(HasilActivity.this, PrintActivity.class);
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


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}
