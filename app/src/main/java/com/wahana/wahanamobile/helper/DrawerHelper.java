package com.wahana.wahanamobile.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.wahana.wahanamobile.AttendanceReport;
import com.wahana.wahanamobile.ChangePasswordActivity;
import com.wahana.wahanamobile.DeliveryNote;
import com.wahana.wahanamobile.DeliveryNoteList;
import com.wahana.wahanamobile.OfflineDeliveryNote;
import com.wahana.wahanamobile.Ops.ListTTKbyMS;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.UbahPasswordActivity;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Reza on 15/03/2017.
 */
public class DrawerHelper extends AppCompatActivity {
    private Activity activity;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    public DrawerLayout drawerLayout;
    public ListView mDrawerList;
    public NavDrawerListAdapter drawerAdapter;
    public ListView drawerList;
    public String[] layers;
    private ActionBarDrawerToggle drawerToggle;
    public ArrayList<NavDrawerItem> navDrawerItems;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView nama,jabatan,versi,kodejabatan;
    CircleImageView foto;
    SessionManager session;
    String username, user_id,session_id;



    @SuppressWarnings("ResourceType")
    public void onCreateDrawer(Activity activity) {


        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        View header =  activity.getLayoutInflater().inflate(R.layout.nav_header, null);
        mDrawerList = (ListView) activity.findViewById(R.id.list_slidermenu);
        mDrawerList.addHeaderView(header);
        navDrawerItems = new ArrayList<>();
        session = new SessionManager(this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        foto = (CircleImageView) header.findViewById(R.id.profpic);
        nama = (TextView) header.findViewById(R.id.nama_label);
        jabatan = (TextView) header.findViewById(R.id.posisi_label);
        versi = (TextView) header.findViewById(R.id.versi);
        kodejabatan = (TextView) header.findViewById(R.id.kodejabatan);
        versi.setText(getString(R.string.version)+" "+getString(R.string.versi));

//        Glide.with(this)
//                .load("https://mobile.wahana.com/apps/wahana/photograph/fotoSeluruhBadan/fotoSeluruhBadan-20210608-081033-11200052.jpg")
//                .into(foto);



        Glide.with(this)
                .load(session.getPhoto())
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(foto);

//        Toast.makeText(this,""+session.getRole(),Toast.LENGTH_LONG).show();

        if (session.getJenis().equals("sales")) {
            navMenuTitles = activity.getResources().getStringArray(R.array.nav_drawer_items_sales);
            navMenuIcons = activity.getResources().obtainTypedArray(R.array.nav_drawer_icons_sales);
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        }else if (session.getJenis().equals("member")){
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_user);
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_user);
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        }else if (session.getJenis().equals("ops")){
            if (session.getRoleID().equals("B112") || session.getRoleID().equals("B113") || session.getRole().toUpperCase().contains("PELAKSANA DELIVERY") ){
                navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
                navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//                navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
            }else if(session.getRoleID().equals("B116") || session.getRoleID().equals("B117") || session.getRoleID().equals("B109") || session.getRoleID().equals("B108") || session.getRoleID().equals("B107") ||
                    session.getRoleID().equals("B106") || session.getRoleID().equals("B105") ){
                navMenuTitles = activity.getResources().getStringArray(R.array.nav_drawer_items_ops);
                navMenuIcons = activity.getResources().obtainTypedArray(R.array.nav_drawer_icons_ops);
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
            }else if(session.getRoleID().equals("B104") || session.getRoleID().equals("IT01") || session.getRoleID().equals("IT02")  || session.getRoleID().equals("IT04") || session.getRoleID().equals("IT03")){
                navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_spvdelivery);
                navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_spvdelivery);
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
//                navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
            } else {
                navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_sales);
                navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_sales);
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
            }
        }else if (session.getJenis().equals("ar")){
            navMenuTitles = activity.getResources().getStringArray(R.array.nav_drawer_items_sales);
            navMenuIcons = activity.getResources().obtainTypedArray(R.array.nav_drawer_icons_sales);
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        }else{
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_sales);
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_sales);
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        }
        navMenuIcons.recycle();
        drawerAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.drawer_open, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        nama.setText(session.getName());
        if (session.getJenis().equals("karyawan")){
            jabatan.setText("Guest");
            kodejabatan.setText("");
        }else{
            jabatan.setText(session.getRole());
            kodejabatan.setText(session.getKodeKantor());
        }

    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            TextView txtTitle = (TextView) view.findViewById(R.id.title);
            String title = txtTitle.getText().toString().toUpperCase();
            if (title.equals("SURAT JALAN")){
                startActivity(new Intent(getApplicationContext(), DeliveryNote.class));
            }else if(title.equals("DAFTAR TTK")){
                startActivity(new Intent(getApplicationContext(), DeliveryNoteList.class));
            }else if(title.equals("OFFLINE TTK")){
                startActivity(new Intent(getApplicationContext(), OfflineDeliveryNote.class));
            }else if(title.equals("CHECK TTK BY MS")){
                startActivity(new Intent(getApplicationContext(), ListTTKbyMS.class));
            }else if(title.equals("RIWAYAT ABSENSI")){
                startActivity(new Intent(getApplicationContext(), AttendanceReport.class));
            }else if(title.equals("UBAH PASSWORD")){
                startActivity(new Intent(getApplicationContext(), UbahPasswordActivity.class));
            }else if(title.equals("LOGOUT")){
                session.logoutUser();
                finish();
            }

//            if (session.getJenis().equals("sales")) {
//                displayViewSales(position);
//            }else if (session.getJenis().equals("member")){
//                displayViewMember(position);
//            }else if (session.getJenis().equals("ops")){
//                if (session.getRoleID().equals("26")){
//                    displayViewKaryawan(position);
//                }else if(session.getRoleID().equals("28")){
//                    displayViewKaryawan(position);
//                }else if(session.getRoleID().equals("32")){
//                    displayViewSpvDelivery(position);
//                }else
//                    {
//                    displayViewOps(position);
//                }
//            }else if (session.getJenis().equals("ar")){
//                displayViewAr(position);
//            }else{
//                displayViewGuest(position);
//            }
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayViewAr(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplication(), AttendanceReport.class));
                break;
            case 2:
                session.logoutUser();
                finish();
                break;

            default:
                break;
        }
    }

    private void displayViewGuest(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplication(), AttendanceReport.class));
                break;
            case 2:
                startActivity(new Intent(getApplication(), UbahPasswordActivity.class));
                break;
            case 3:
                session.logoutUser();
                finish();
                break;

            default:
                break;
        }
    }

    private void displayViewSales(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplication(), AttendanceReport.class));
                break;
            case 2:
                session.logoutUser();
                finish();
                break;

            default:
                break;
        }
    }

    private void displayViewKaryawan(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplicationContext(), DeliveryNote.class));
                break;
            case 2:
                startActivity(new Intent(getApplicationContext(), DeliveryNoteList.class));
                break;
            case 3:
                startActivity(new Intent(getApplicationContext(), OfflineDeliveryNote.class));
                break;
            case 4:
                startActivity(new Intent(getApplicationContext(), AttendanceReport.class));
                break;
            case 5:
                startActivity(new Intent(getApplicationContext(), UbahPasswordActivity.class));
                break;
            case 6:
                session.logoutUser();
                finish();
                break;

            default:
                break;
        }
    }

    private void displayViewMember(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                intent.putExtra("LOGIN_TIPE", "member");
                startActivity(intent);
                break;
            case 2:
                session.logoutUser();
                break;

            default:
                break;
        }
    }

    private void displayViewOps(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplication(), ListTTKbyMS.class));
                break;
            case 2:
                startActivity(new Intent(getApplication(), AttendanceReport.class));
                break;
            case 3:
                startActivity(new Intent(getApplication(), UbahPasswordActivity.class));
                break;
            case 4:
                session.logoutUser();
                break;

            default:
                break;
        }
    }

    private void displayViewSpvDelivery(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(getApplicationContext(), DeliveryNote.class));
                break;
            case 2:
                startActivity(new Intent(getApplicationContext(), DeliveryNoteList.class));
                break;
            case 3:
                startActivity(new Intent(getApplicationContext(), OfflineDeliveryNote.class));
                break;
            case 4:
                startActivity(new Intent(getApplicationContext(), ListTTKbyMS.class));
                break;
            case 5:
                startActivity(new Intent(getApplicationContext(), AttendanceReport.class));
                break;
            case 6:
                startActivity(new Intent(getApplicationContext(), UbahPasswordActivity.class));
                break;
            case 7:
                session.logoutUser();
                finish();
                break;

            default:
                break;
        }
    }

    public ListView getDrawer()
    {
        return mDrawerList;
    }
}
