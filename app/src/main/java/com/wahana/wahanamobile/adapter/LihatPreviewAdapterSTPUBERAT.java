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

import com.wahana.wahanamobile.Data.ListTtkSTPUBERAT;
import com.wahana.wahanamobile.R;

import java.util.List;

/**
 * Created by donay on 26/08/20.
 */

public class LihatPreviewAdapterSTPUBERAT extends BaseAdapter {

    private Activity activity;
    String kodesortir;
    Context context;
    private static LayoutInflater inflater=null;
    String[] noTTK;
    List<ListTtkSTPUBERAT> ttk;
    int Urut;

    public LihatPreviewAdapterSTPUBERAT(Activity a,  List<ListTtkSTPUBERAT> ttk, int urut) {
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
        TextView berat,dimensi;
        Button submit;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LihatPreviewAdapterSTPUBERAT.Holder holder=new LihatPreviewAdapterSTPUBERAT.Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.lihat_preview_stpuberat, null);
        holder.no_ttk = (TextView)view.findViewById(R.id.no_ttk_isi);
        holder.no_urut_label = (TextView)view.findViewById(R.id.no_urut_label);
        holder.berat = (TextView)view.findViewById(R.id.berat);
        holder.dimensi = (TextView)view.findViewById(R.id.dimensi);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
//        holder.no_ttk.setTypeface(type);
        ListTtkSTPUBERAT sj = ttk.get(position);
        Log.d("Hasil ttk", ""+sj.getTtk());
        holder.no_ttk.setText(sj.getTtk());
        int no = position+1;
        holder.no_urut_label.setText(""+no);
        holder.berat.setText(sj.getBerat());
        holder.dimensi.setText(sj.getPanjang()+"x"+sj.getLebar()+"x"+sj.getTinggi());
        return view;
    }
}
