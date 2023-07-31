package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.R;

/**
 * Created by Reza on 27/03/2017.
 */
public class SerahTerimaPickupAdapter extends BaseAdapter {
    private Activity activity;
    String[] nomorTTK;
    Context context;
    private static LayoutInflater inflater=null;


    public SerahTerimaPickupAdapter(Activity a,  String[] kodes) {
        activity = a;
        nomorTTK = kodes;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nomorTTK.length    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView label_sortir;
        TextView label_koli;
        TextView sortir;
        TextView koli;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.serah_terima_pickup_listview, null);
            holder.label_sortir = (TextView)view.findViewById(R.id.no_ttk_isi);
            Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
            //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
            holder.label_sortir.setTypeface(type);
            holder.label_sortir.setText(nomorTTK[position]);
            return view;
    }
}
