package com.wahana.wahanamobile;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;

public class AboutUsActivity extends DrawerHelper {
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    TextView judulAbout, judulPeople, judulTechnology, judulCapability,isiAbout, skilledLabel, passionateLabel, ontimeLabel,
    employLabel, internetLabel, qualityLabel, trucksLabel, operationLabel, retailLabel, staffLabel;

    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
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

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        judulAbout = (TextView) findViewById(R.id.judul_about_us);
        judulPeople= (TextView) findViewById(R.id.judul_people);
        judulTechnology= (TextView) findViewById(R.id.judul_technology);
        judulCapability= (TextView) findViewById(R.id.judul_capability);
        isiAbout= (TextView) findViewById(R.id.isi_about_us);
        skilledLabel= (TextView) findViewById(R.id.skilled_label);
        passionateLabel= (TextView) findViewById(R.id.passionate_label);
        ontimeLabel= (TextView) findViewById(R.id.on_time_label);
        employLabel= (TextView) findViewById(R.id.employ_label);
        internetLabel= (TextView) findViewById(R.id.internet_label);
        qualityLabel= (TextView) findViewById(R.id.quality_label);
        trucksLabel= (TextView) findViewById(R.id.trucks_label);
        operationLabel= (TextView) findViewById(R.id.operation_label);
        retailLabel= (TextView) findViewById(R.id.retail_label);
        staffLabel= (TextView) findViewById(R.id.staff_label);
        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        judulAbout.setTypeface(type);
        judulPeople.setTypeface(type);
        judulTechnology.setTypeface(type);
        judulCapability.setTypeface(type);
        isiAbout.setTypeface(type);
        skilledLabel.setTypeface(type);
        passionateLabel.setTypeface(type);
        ontimeLabel.setTypeface(type);
        employLabel.setTypeface(type);
        internetLabel.setTypeface(type);
        qualityLabel.setTypeface(type);
        trucksLabel.setTypeface(type);
        operationLabel.setTypeface(type);
        retailLabel.setTypeface(type);
        staffLabel.setTypeface(type);

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
