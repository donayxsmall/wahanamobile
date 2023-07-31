package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Reza on 19/09/2016.
 */
public class PenerusSearchAdapter extends BaseAdapter {
    private Activity activity;
    String [] Judul;
    String [] Provinsi;
    String [] Kota;
    String [] Distrik;
    Context context;
    private static LayoutInflater inflater=null;
    List<Origin> originList = new ArrayList<Origin>();
    List<Origin> searchList = new ArrayList<Origin>();

    public PenerusSearchAdapter(Activity a, List<Origin> origin) {
        activity = a;
        this.originList = origin;
        this.searchList.addAll(originList);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return originList.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView lokasiIsi,lokasiId;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.test_search_listview,null);
        holder.lokasiIsi= (TextView)view.findViewById(R.id.lokasi_isi);
        holder.lokasiId= (TextView)view.findViewById(R.id.lokasi_bps);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.lokasiIsi.setTypeface(typeSemibold);
        Origin ori = originList.get(position);
//        holder.lokasiIsi.setText(ori.getProvince()+", "+ori.getCity());
        holder.lokasiId.setText(ori.getId());
        holder.lokasiIsi.setText(ori.getCity()+": "+ori.getProvince());
        return view;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase();
        originList.clear();
        Log.d("text",""+charText);
        if (charText.length() == 0) {
            originList.addAll(searchList);
        } else {
            for (Origin s : searchList) {
                if (s.getProvince().toLowerCase().contains(charText)) {
                    originList.add(s);
                    Log.d("hasil filter", ""+charText+" "+s.getProvince());
                }
            }
        }
        notifyDataSetChanged();
    }
}
