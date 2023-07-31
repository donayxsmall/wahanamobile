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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.Kode;
import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.PickupAirwaysStatus;
import com.wahana.wahanamobile.PickupConclusion;
import com.wahana.wahanamobile.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Reza on 29/06/2016.
 */
public class KoliAdapter extends BaseAdapter {
    private Activity activity;
    String agentcode;
    Context context;
    private static LayoutInflater inflater=null;
    List<KodeSortir> kodeList;

    public KoliAdapter(Activity a, List<KodeSortir> kodes) {
        activity = a;
        kodeList = kodes;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kodeList.size()    ;
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
             view = inflater.inflate(R.layout.koli_listview, null);
             holder.label_sortir = (TextView)view.findViewById(R.id.kode_sortir_label);
             holder.label_koli = (TextView)view.findViewById(R.id.kode_koli_label);
             holder.sortir = (TextView)view.findViewById(R.id.kode_sortir_isi);
             holder.koli = (TextView)view.findViewById(R.id.kode_koli_isi);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(activity, PickupConclusion.class);
//                intent.putExtra("agentcode", agentcode);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                activity.startActivity(intent);
//            }
//        });
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.label_koli.setTypeface(type);
        holder.label_sortir.setTypeface(type);
        holder.sortir.setTypeface(type);
        holder.koli.setTypeface(type);
        KodeSortir kode = kodeList.get(position);
        String kodesortir = kode.getKodesortir();
        int jumlah = kode.getVerify();
        holder.sortir.setText(kodesortir);
        Log.d("jumlah", ""+jumlah);
        holder.koli.setText(""+jumlah);
        return view;
    }
}
