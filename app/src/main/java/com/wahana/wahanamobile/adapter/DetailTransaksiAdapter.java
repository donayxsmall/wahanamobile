package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ttkUser;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 29/03/2017.
 */
public class DetailTransaksiAdapter  extends BaseAdapter {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    String[] listwaktu, listketerangan, listsaldo, listnominal;
    public DetailTransaksiAdapter(Activity a, String[] ListWaktu, String[] ListKeterangan, String[] ListNominal , String[] ListSaldo) {
        activity = a;
        this.listwaktu = ListWaktu;
        this.listketerangan = ListKeterangan;
        this.listsaldo = ListSaldo;
        this.listnominal = ListNominal;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return listwaktu.length    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView txtWaktu, txtKeterangan, txtNominalLabel, txtNominalIsi, txtSaldoLabel, txtSaldoIsi;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.detail_transaksi_listview, null);
            holder.txtWaktu= (TextView)view.findViewById(R.id.label_waktu);
            holder.txtKeterangan= (TextView) view.findViewById(R.id.label_keterangan);
            holder.txtNominalLabel = (TextView) view.findViewById(R.id.nominal_label);
            holder.txtNominalIsi = (TextView) view.findViewById(R.id.nominal_isi);
            holder.txtSaldoLabel = (TextView) view.findViewById(R.id.saldo_label);
            holder.txtSaldoIsi = (TextView) view.findViewById(R.id.saldo_isi);
            Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
            Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
            holder.txtWaktu.setTypeface(typeSemibold);
            holder.txtKeterangan.setTypeface(typeSemibold);
            holder.txtNominalLabel.setTypeface(typeSemibold);
            holder.txtNominalIsi.setTypeface(typeSemibold);
            holder.txtSaldoLabel.setTypeface(typeSemibold);
            holder.txtSaldoIsi.setTypeface(typeSemibold);
            holder.txtWaktu.setText(listwaktu[position]);
            holder.txtKeterangan.setText(listketerangan[position]);
            holder.txtNominalIsi.setText(listnominal[position]);
            holder.txtSaldoIsi.setText(listsaldo[position]);

            return view;
    }
}
