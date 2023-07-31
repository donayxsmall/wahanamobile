package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wahana.wahanamobile.R;

/**
 * Created by Reza on 27/03/2017.
 */
public class VerifikasiSerahTerimaPickupAdapter extends BaseAdapter {
    private Activity activity;
    String kodesortir;
    Context context;
    private static LayoutInflater inflater=null;
    String[] kodeList;

    public VerifikasiSerahTerimaPickupAdapter(Activity a,  String[] kodes) {
        activity = a;
        kodeList = kodes;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kodeList.length    ;
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
        Button submit;
        TextView label_koli;
        TextView sortir;
        TextView koli;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.verifikasi_serah_terima_pickup_listview, null);
        holder.label_sortir = (TextView)view.findViewById(R.id.kode_sortir_isi);
        holder.submit = (Button)view.findViewById(R.id.input_button);
        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, SerahTerimaPickup.class);
//                activity.startActivity(intent);
//                activity.finish();
            }
        });

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
        holder.label_sortir.setTypeface(type);
        holder.label_sortir.setText(kodeList[position]);
        return view;
    }
}
