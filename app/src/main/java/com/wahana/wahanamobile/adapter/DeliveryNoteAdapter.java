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
import android.widget.EditText;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.SuratJalan;
import com.wahana.wahanamobile.DeliveryNote;
import com.wahana.wahanamobile.PickupAirwaysStatus;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 01/07/2016.
 */
public class DeliveryNoteAdapter  extends BaseAdapter {
    private Activity activity;
    String [] KodeTTK;
    String [] TanggalTTK;
    String [] JumlahTTK;
    String [] TerkirimTTK;
    String [] BelumTerkirimTTK;
    Context context;
    private static LayoutInflater inflater=null;
    List<SuratJalan> sjList = new ArrayList<SuratJalan>();

    public DeliveryNoteAdapter(Activity a, List<SuratJalan> sjList) {
        activity = a;
        this.sjList = sjList;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sjList.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView suratJalanIsi;
        TextView tanggalIsi;
        TextView jumlahTTKIsi;
        TextView terkirimIsi;
        TextView belumTerkirimIsi;
        TextView jumlahTTKLabel;
        TextView terkirimLabel;
        TextView belumTerkirimLabel;
        TextView belumUpdateIsi;
        TextView belumSerahIsi;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.delivery_note_listview, null);
        holder.suratJalanIsi= (TextView)view.findViewById(R.id.surat_jalan_isi);
        holder.tanggalIsi= (TextView)view.findViewById(R.id.tanggal_isi);
        holder.jumlahTTKIsi= (TextView)view.findViewById(R.id.jumlah_ttk_isi);
        holder.terkirimIsi= (TextView)view.findViewById(R.id.ttk_terkirim_isi);
        holder.belumTerkirimIsi= (TextView)view.findViewById(R.id.ttk_belum_terkirim_isi);
        holder.jumlahTTKLabel= (TextView)view.findViewById(R.id.jumlah_ttk_label);
        holder.terkirimLabel= (TextView)view.findViewById(R.id.ttk_terkirim_label);
        holder.belumTerkirimLabel= (TextView)view.findViewById(R.id.ttk_belum_terkirim_label);
        holder.belumUpdateIsi = (TextView)view.findViewById(R.id.ttk_belum_terupdate_isi);
        holder.belumSerahIsi = (TextView)view.findViewById(R.id.ttk_belum_serah_terima_bt_isi);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.suratJalanIsi.setTypeface(typeSemibold);
        holder.tanggalIsi.setTypeface(typeSemibold);
        holder.jumlahTTKIsi.setTypeface(typeSemibold);
        holder.terkirimIsi.setTypeface(typeSemibold);
        holder.belumTerkirimIsi.setTypeface(typeSemibold);
        holder.jumlahTTKLabel.setTypeface(typeRegular);
        holder.terkirimLabel.setTypeface(typeRegular);
        holder.belumTerkirimLabel.setTypeface(typeRegular);

        SuratJalan sj = sjList.get(position);
        holder.suratJalanIsi.setText(sj.getSJ());
        holder.tanggalIsi.setText(sj.getTgl());
        holder.jumlahTTKIsi.setText(sj.getTotal()+"");
        holder.terkirimIsi.setText(sj.getTerkirim()+"");
        holder.belumTerkirimIsi.setText(sj.getBelum()+"");
        holder.belumUpdateIsi.setText(sj.getAktif()+"");
        holder.belumSerahIsi.setText(sj.getSerah()+"");
        return view;
    }

}
