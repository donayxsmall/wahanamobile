package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.CariAgenActivity;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 25/07/2016.
 */
public class CariAgenDetailAdapter extends BaseAdapter {
    private Activity activity;
    List<String> nama;
    List<String> alamat;
    List<String> jarak;
    List<String> phone;
    List<String> email;
    List<String> lat;
    List<String> longi;
    String [] Nama;
    String [] Alamat;
    String [] Jarak;
    String [] Phone;
    String [] Email;
    Context context;
    private static LayoutInflater inflater=null;
    public CariAgenDetailAdapter(Activity a, String[] nama, String[] alamat, String[] jarak, String[] phone, String[] email) {
        activity = a;
        Nama = nama;
        Alamat = alamat;
        Jarak = jarak;
        Phone = phone;
        Email = email;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CariAgenDetailAdapter(Activity a, List<String> nama, List<String> alamat, List<String> jarak,
                                 List<String> telp, List<String> email, List<String> lat, List<String> longi) {
        activity = a;
        this.nama = nama;
        this.alamat = alamat;
        this.jarak = jarak;
        this.phone = telp;
        this.email = email;
        this.lat = lat;
        this.longi = longi;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView phone_label;
        TextView email_label;
        TextView phone_isi;
        TextView email_isi;
        TextView lat_isi;
        TextView long_isi;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.cari_agen_detail_listview, null);
        holder.nama_label = (TextView)view.findViewById(R.id.nama_label);
        holder.alamat_label = (TextView)view.findViewById(R.id.alamat_label);
        holder.jarak_label = (TextView)view.findViewById(R.id.jarak_label);
        holder.phone_label = (TextView)view.findViewById(R.id.phone_label);
        holder.phone_isi = (TextView)view.findViewById(R.id.phone_isi);
        holder.email_label = (TextView)view.findViewById(R.id.email_label);
        holder.email_isi = (TextView)view.findViewById(R.id.email_isi);
        holder.lat_isi = (TextView)view.findViewById(R.id.lat_label);
        holder.long_isi = (TextView)view.findViewById(R.id.long_label);

        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        holder.nama_label.setTypeface(type);
        holder.alamat_label.setTypeface(type);
        holder.jarak_label.setTypeface(type);
        holder.phone_label.setTypeface(type);
        holder.phone_isi.setTypeface(type);
        holder.email_label.setTypeface(type);
        holder.email_isi.setTypeface(type);

        holder.nama_label.setText(nama.get(position));
        holder.alamat_label.setText(alamat.get(position));
        holder.jarak_label.setText(jarak.get(position)+" KM");
        holder.phone_isi.setText(phone.get(position));
        holder.email_isi.setText(email.get(position));
        holder.lat_isi.setText(lat.get(position));
        holder.long_isi.setText(longi.get(position));

        return view;
    }


}
