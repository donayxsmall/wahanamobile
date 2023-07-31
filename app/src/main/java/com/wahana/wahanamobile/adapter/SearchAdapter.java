package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wahana.wahanamobile.R;

/**
 * Created by Reza on 15/07/2016.
 */
public class SearchAdapter extends BaseAdapter {
    private Activity activity;
    String [] Waktu;
    String [] Status;
    Context context;
    private static LayoutInflater inflater=null;

    public SearchAdapter(Activity a, String[] waktu, String[] status) {
        activity = a;
        Waktu = waktu;
        Status = status;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Waktu.length    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView waktuTracking;
        TextView statusTrackinf;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.search_listview, null);
        holder.waktuTracking = (TextView) view.findViewById(R.id.waktu_tracking);
        holder.statusTrackinf = (TextView) view.findViewById(R.id.tahapan_tracking);
        //holder.kodeSortir = (TextView) view.findViewById(R.id.kode_sortir_isi);

        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
//        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.waktuTracking.setText(Waktu[position]);
        holder.statusTrackinf.setText(Status[position]);
        //holder.kodeSortir.setText(KodeSortir[0]);
        holder.waktuTracking.setTypeface(type);
        holder.statusTrackinf.setTypeface(type);
        //holder.kodeSortir.setTypeface(type);

        return view;
    }

}
