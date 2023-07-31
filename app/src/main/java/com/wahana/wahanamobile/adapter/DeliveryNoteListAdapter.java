package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.DeliveryNoteList;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 02/07/2016.
 */
public class DeliveryNoteListAdapter extends BaseAdapter {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    List<ListTTK> listTTK = new ArrayList<ListTTK>();

    public DeliveryNoteListAdapter(Activity a, List<ListTTK> listTTk) {
        activity = a;
        this.listTTK = listTTk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listTTK.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView noTTKIsi;
        TextView koliIsi;
        TextView beratIsi;
        TextView statusIsi;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.delivery_note_list_listview, null);
        holder.noTTKIsi= (TextView)view.findViewById(R.id.nomor_ttk_isi);
        holder.koliIsi= (TextView)view.findViewById(R.id.koli_isi);
        holder.beratIsi= (TextView)view.findViewById(R.id.berat_isi);
        holder.statusIsi= (TextView)view.findViewById(R.id.status_isi);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.noTTKIsi.setTypeface(typeSemibold);
        holder.koliIsi.setTypeface(typeSemibold);
        holder.beratIsi.setTypeface(typeSemibold);
        holder.statusIsi.setTypeface(typeSemibold);

        ListTTK ttk = listTTK.get(position);
        holder.noTTKIsi.setText(ttk.getNoTTK());
        holder.koliIsi.setText(ttk.getKoli()+"");
        holder.beratIsi.setText(ttk.getBerat()+"");
        holder.statusIsi.setText(ttk.getStatus());
        return view;
    }

}
