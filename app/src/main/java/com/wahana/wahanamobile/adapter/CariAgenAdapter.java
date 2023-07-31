package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.PickupAirwaysStatus;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 22/07/2016.
 */
public class CariAgenAdapter extends BaseAdapter {
    private Activity activity;
    List<String> nama;
    List<String> alamat;
    List<String> jarak;
    List<String> phone;
    List<String> email;
    String [] Nama;
    String [] Alamat;
    String [] Jarak;
    Context context;
    private static LayoutInflater inflater=null;
    public CariAgenAdapter(Activity a, String[] nama, String[] alamat, String[] jarak) {
        activity = a;
        Nama = nama;
        Alamat = alamat;
        Jarak = jarak;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public CariAgenAdapter(Activity a, List<String> nama, List<String> alamat, List<String> jarak, List<String> telp, List<String> email) {
        activity = a;
        this.nama = nama;
        this.alamat = alamat;
        this.jarak = jarak;
        this.phone = phone;
        this.email = email;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return nama.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView nama_label;
        TextView alamat_label;
        TextView jarak_label;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.cari_agen_listview, null);
        holder.nama_label = (TextView)view.findViewById(R.id.nama_label);
        holder.alamat_label = (TextView)view.findViewById(R.id.alamat_label);
        holder.jarak_label = (TextView)view.findViewById(R.id.jarak_label);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        holder.nama_label.setTypeface(type);
        holder.alamat_label.setTypeface(type);
        holder.jarak_label.setTypeface(type);
        holder.nama_label.setText(nama.get(position));
        holder.alamat_label.setText(alamat.get(position));
        holder.jarak_label.setText(jarak.get(position)+" KM");
        return view;
    }
}
