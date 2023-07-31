package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.R;

import java.util.List;

/**
 * Created by Reza on 21/04/2017.
 */
public class BuatSJAdapter extends BaseAdapter {
    private Activity activity;
    String asal="";
    Context context;
    private static LayoutInflater inflater=null;
    String[] noTTK;
    List<BuatSJ> ttk;
    int Urut;

//    public BuatSJAdapter(Activity a, String[] kodes, int urut) {
//        Urut = urut;
//        activity = a;
//        noTTK = kodes;
//        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

    public BuatSJAdapter(Activity a,  List<BuatSJ> ttk, int urut) {
        activity = a;
        Urut = urut;
        this.ttk = ttk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public BuatSJAdapter(Activity a,  List<BuatSJ> ttk, String asal) {
        activity = a;
        this.ttk = ttk;
        this.asal = asal;
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
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.buat_sj_listview, null);
        holder.no_ttk = (TextView)view.findViewById(R.id.no_ttk_isi);
        holder.no_urut_label = (TextView)view.findViewById(R.id.no_urut_label);
        TextView tujuan = (TextView) view.findViewById(R.id.tujuan);
        TableLayout tabel = (TableLayout) view.findViewById(R.id.tabel_detail_koli);
        TextView titik = (TextView) view.findViewById(R.id.pembandingurut);
        TableRow list = (TableRow) view.findViewById(R.id.row);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.no_ttk.setTypeface(type);
        BuatSJ sj = ttk.get(position);
        holder.no_ttk.setText(sj.getTtk());
        int no = position+1;
        holder.no_urut_label.setText(""+no);
        if (asal.equals("ma")){
            int size = 18;
            tabel.getLayoutParams().height = 100;
            list.setPadding(10,20,10,10);
            holder.no_ttk.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            holder.no_urut_label.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            tujuan.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            titik.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            holder.no_ttk.setGravity(Gravity.CENTER);
            holder.no_urut_label.setGravity(Gravity.CENTER);
            tujuan.setGravity(Gravity.CENTER);
            titik.setGravity(Gravity.CENTER);
            tujuan.setText("("+sj.getTujuan()+")");
            tujuan.setVisibility(View.VISIBLE);
        }else if(asal.equals("ttk_ms")){
            int size = 20;
            tujuan.setText(sj.getTujuan()+" kg");
            tujuan.setVisibility(View.VISIBLE);
            holder.no_ttk.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            holder.no_urut_label.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            tujuan.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            titik.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }
        return view;
    }
}