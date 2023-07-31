package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 19/09/2016.
 */
public class AddressSearchAdapter extends BaseAdapter {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    List<Origin> originList = new ArrayList<Origin>();
    String type;

    public AddressSearchAdapter(Activity a, List<Origin> origin, String type) {
        activity = a;
        this.originList = origin;
        this.type = type;
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
        TextView lokasiIsi;
        TextView lokasiBps;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.test_search_listview,null);
        holder.lokasiIsi= (TextView)view.findViewById(R.id.lokasi_isi);
        holder.lokasiBps= (TextView)view.findViewById(R.id.lokasi_bps);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.lokasiIsi.setTypeface(typeSemibold);
        Origin ori = originList.get(position);
        holder.lokasiIsi.setText(ori.getProvince());
        if (type.equals("kelurahan")){
            holder.lokasiBps.setText(ori.getCity());
        }
        return view;
    }
}
