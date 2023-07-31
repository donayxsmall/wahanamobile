package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.R;

import java.util.List;

/**
 * Created by donay on 06/07/20.
 */

public class LihatPreviewAdapterSTPUMA extends BaseAdapter {

    private Activity activity;
    String kodesortir;
    Context context;
    private static LayoutInflater inflater=null;
    String[] noTTK;
    List<ListTtkSTPUMA> ttk;
    int Urut;

    public LihatPreviewAdapterSTPUMA(Activity a,  List<ListTtkSTPUMA> ttk, int urut) {
        activity = a;
        Urut = urut;
        this.ttk = ttk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        try{
            return ttk.size();
        }catch (Exception e){
            return 0;
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView no_ttk;
        TextView no_urut_label;
        Button submit;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LihatPreviewAdapterSTPUMA.Holder holder=new LihatPreviewAdapterSTPUMA.Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.lihat_preview_listview, null);
        holder.no_ttk = (TextView)view.findViewById(R.id.no_ttk_isi);
        holder.no_urut_label = (TextView)view.findViewById(R.id.no_urut_label);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.no_ttk.setTypeface(type);
        ListTtkSTPUMA sj = ttk.get(position);
        Log.d("Hasil ttk", ""+sj.getTtk());
        holder.no_ttk.setText(sj.getTtk());
        int no = position+1;
        holder.no_urut_label.setText(""+no);
        return view;
    }
}