package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.AttendanceReport;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Reza on 02/07/2016.
 */
public class AttendanceReportAdapter extends BaseAdapter {
    private Activity activity;
    String [] Tanggal;
    String [] Hari;
    String [] JamMasuk;
    String [] JamPulang;
    String [] Keterangan;
    Context context;
    private static LayoutInflater inflater=null;

    public AttendanceReportAdapter(Activity a, String[] tanggal, String[] hari, String[] jamMasuk, String[]  jamPulang, String[] keterangan) {
        activity = a;
        Tanggal = tanggal;
        Hari = hari;
        JamMasuk = jamMasuk;
        JamPulang = jamPulang;
        Keterangan = keterangan;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return Tanggal.length    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tanggalIsi;
        TextView hariIsi;
        TextView jamMasukLabel;
        TextView jamMasukIsi;
        TextView jamPulangLabel;
        TextView jamPulangIsi;
        TextView keteranganLabel;
        TextView keteranganIsi;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.attendance_report_listview,null);
        holder.tanggalIsi= (TextView)view.findViewById(R.id.tanggal_isi);
        holder.hariIsi= (TextView)view.findViewById(R.id.hari_isi);
        holder.jamMasukLabel= (TextView)view.findViewById(R.id.jam_masuk_label);
        holder.jamMasukIsi= (TextView)view.findViewById(R.id.jam_masuk_isi);
        holder.jamPulangLabel= (TextView)view.findViewById(R.id.jam_pulang_label);
        holder.jamPulangIsi= (TextView)view.findViewById(R.id.jam_pulang_isi);
        holder.keteranganLabel= (TextView)view.findViewById(R.id.keterangan_label);
        holder.keteranganIsi= (TextView)view.findViewById(R.id.keterangan_isi);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.hariIsi.setTypeface(typeSemibold);
        holder.tanggalIsi.setTypeface(typeSemibold);
        holder.jamPulangLabel.setTypeface(typeSemibold);
        holder.jamMasukLabel.setTypeface(typeSemibold);
        holder.keteranganLabel.setTypeface(typeSemibold);
        holder.jamMasukIsi.setTypeface(typeRegular);
        holder.jamPulangIsi.setTypeface(typeRegular);
        holder.keteranganIsi.setTypeface(typeRegular);

        holder.hariIsi.setText(Hari[position]);
        holder.tanggalIsi.setText(Tanggal[position]);
        holder.jamMasukIsi.setText(JamMasuk[position]);
        holder.jamPulangIsi.setText(JamPulang[position]);
        holder.keteranganIsi.setText(Keterangan[position]);
        return view;
    }

}
