package com.wahana.wahanamobile;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;

public class CustomerServiceActivity extends DrawerHelper {
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    ImageView foto;
    public static String FACEBOOK_URL = "https://www.facebook.com/WahanaPrestasiLogistik";
    public static String FACEBOOK_PAGE_ID = "WahanaPrestasiLogistik";
    TextView alamatLabel,phoneLabel,faxLabel, facebookLabel, instagramLabel, metroLabel, wilayahJakartaLabel, waLabel, emailLabel, wilayahJawaBaratLabel,
    waLabelJawaBarat, emailLabelJawaBarat, wilayahBanteLabel,waLabelBanten, emailLabelBantan, nonMetroLabel,wilayahJawaTimurLabel,  waLabelJawaTimur,
    emailLabelJawaTimur, wilayahJawaTengahLabel, waLabelJawaTengah, emailLabelJawaTengah, wilayahSumateraLabel, waLabelSumatera, emailLabelSumatera,
    wilayahKalimantanLabel, waLabelKalimantan, emailLabelKalimantan, returLabel, waLabelRetur, emailLabelRetur1, emailLabelRetur2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
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

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerServiceActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        alamatLabel = (TextView) findViewById(R.id.alamat_label);
        phoneLabel = (TextView) findViewById(R.id.phone_label);
        faxLabel = (TextView) findViewById(R.id.fax_label);
        facebookLabel = (TextView) findViewById(R.id.facebook_label);
        instagramLabel = (TextView) findViewById(R.id.instagram_label);
        metroLabel = (TextView) findViewById(R.id.metro_label);
        wilayahJakartaLabel = (TextView) findViewById(R.id.wilayah_jakarta_label);
        waLabel = (TextView) findViewById(R.id.wa_label);
        emailLabel = (TextView) findViewById(R.id.email_label);
        wilayahJawaBaratLabel = (TextView) findViewById(R.id.wilayah_jawa_barat_label);
        waLabelJawaBarat = (TextView) findViewById(R.id.wa_label_jawa_barat);
        emailLabelJawaBarat = (TextView) findViewById(R.id.email_label_jawa_barat);
        wilayahBanteLabel = (TextView) findViewById(R.id.wilayah_banten_label);
        waLabelBanten = (TextView) findViewById(R.id.wa_label_banten);
        emailLabelBantan = (TextView) findViewById(R.id.email_label_banten);
        nonMetroLabel = (TextView) findViewById(R.id.non_metro_label);
        wilayahJawaTimurLabel = (TextView) findViewById(R.id.wilayah_jawa_timur_label);
        waLabelJawaTimur = (TextView) findViewById(R.id.wa_label_jawa_timur);
        emailLabelJawaTimur = (TextView) findViewById(R.id.email_label_jawa_timur);
        wilayahJawaTengahLabel = (TextView) findViewById(R.id.wilayah_jawa_tengah_label);
        waLabelJawaTengah = (TextView) findViewById(R.id.wa_label_jawa_tengah);
        emailLabelJawaTengah = (TextView) findViewById(R.id.email_label_jawa_tengah);
        wilayahSumateraLabel = (TextView) findViewById(R.id.wilayah_sumatera_label);
        waLabelSumatera = (TextView) findViewById(R.id.wa_label_sumatera);
        emailLabelSumatera = (TextView) findViewById(R.id.email_label_sumatera);
        wilayahKalimantanLabel = (TextView) findViewById(R.id.wilayah_kalimantan_label);
        waLabelKalimantan = (TextView) findViewById(R.id.wa_label_kalimantan);
        emailLabelKalimantan = (TextView) findViewById(R.id.email_label_kalimantan);
        returLabel = (TextView) findViewById(R.id.retur_label);
        waLabelRetur = (TextView) findViewById(R.id.wa_label_retur);
        emailLabelRetur1 = (TextView) findViewById(R.id.email1_label_retur);
        emailLabelRetur2 = (TextView) findViewById(R.id.email2_label_retur);
        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        alamatLabel.setTypeface(type);
        phoneLabel.setTypeface(type);
        faxLabel .setTypeface(type);
        facebookLabel.setTypeface(type);
        instagramLabel.setTypeface(type);
        metroLabel.setTypeface(type);
        wilayahJakartaLabel.setTypeface(type);
        waLabel.setTypeface(type);
        emailLabel.setTypeface(type);
        wilayahJawaBaratLabel.setTypeface(type);
        waLabelJawaBarat.setTypeface(type);
        emailLabelJawaBarat.setTypeface(type);
        wilayahBanteLabel.setTypeface(type);
        waLabelBanten .setTypeface(type);
        emailLabelBantan .setTypeface(type);
        nonMetroLabel.setTypeface(type);
        wilayahJawaTimurLabel.setTypeface(type);
        waLabelJawaTimur.setTypeface(type);
        emailLabelJawaTimur.setTypeface(type);
        wilayahJawaTengahLabel.setTypeface(type);
        waLabelJawaTengah.setTypeface(type);
        emailLabelJawaTengah.setTypeface(type);
        wilayahSumateraLabel.setTypeface(type);
        waLabelSumatera.setTypeface(type);
        emailLabelSumatera.setTypeface(type);
        wilayahKalimantanLabel.setTypeface(type);
        waLabelKalimantan.setTypeface(type);
        emailLabelKalimantan.setTypeface(type);
        returLabel.setTypeface(type);
        waLabelRetur.setTypeface(type);
        emailLabelRetur1.setTypeface(type);
        emailLabelRetur2 .setTypeface(type);
        facebookLabel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    Uri uri = Uri.parse(getFacebookPageURL(CustomerServiceActivity.this));
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.facebook.katana");
                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/ariqnisti")));
                    }
            }
        });



        instagramLabel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/wahana.logistik");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });
    }

    public String getFacebookPageURL(Context context) {

        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
