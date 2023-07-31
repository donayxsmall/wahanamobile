package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 27/08/2016.
 */
public class DeliveryNoteListItemAdapter  extends BaseAdapter {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    DatabaseHandler db;
    List<ListTTK> listTTk = new ArrayList<ListTTK>();
    List<ListTTK> listTTkByDate = new ArrayList<ListTTK>();
    LinearLayout layoutForm;

    public DeliveryNoteListItemAdapter(Activity a, List<ListTTK> listTTk) {
        activity = a;
        this.listTTk = listTTk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return listTTk.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
       TextView tanggalIsi, tanggalLabel, nottkHeader, koliHeader, beratHeader, statusHeader;
        ListView listViewDeliveryNote;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        db = new DatabaseHandler(activity);
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.delivery_note_list_item_listview, null);
        layoutForm = (LinearLayout) view.findViewById(R.id.main);
        layoutForm.removeAllViews();

        ListTTK ttk = listTTk.get(position);
        listTTkByDate = db.getAllListTTKByDate(ttk.getTgl());
//        holder.listViewDeliveryNote = (ListView) view.findViewById(R.id.list_delivery_note_list);
//        holder.listViewDeliveryNote.setAdapter(new DeliveryNoteListAdapter(activity, listTTkByDate));
        for (int i = 0 ; i < listTTkByDate.size(); i++){
            tambahDaftar(listTTkByDate.get(i));
        }

        holder.tanggalIsi= (TextView)view.findViewById(R.id.tanggal_isi);
        holder.tanggalLabel= (TextView)view.findViewById(R.id.tanggal_label);
        holder.nottkHeader= (TextView)view.findViewById(R.id.nomor_ttk_header);
        holder.koliHeader= (TextView)view.findViewById(R.id.koli_header);
        holder.beratHeader= (TextView)view.findViewById(R.id.berat_header);
        holder.statusHeader= (TextView)view.findViewById(R.id.status_header);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.tanggalIsi.setTypeface(typeSemibold);
        holder.tanggalLabel.setTypeface(typeSemibold);
        holder.nottkHeader.setTypeface(typeSemibold);
        holder.koliHeader.setTypeface(typeSemibold);
        holder.beratHeader.setTypeface(typeSemibold);
        holder.statusHeader.setTypeface(typeSemibold);
        holder.tanggalIsi.setText(ttk.getTgl());

        return view;
    }

    public class listHolder
    {
        TextView noTTKIsi;
        TextView koliIsi;
        TextView beratIsi;
        TextView statusIsi;
    }

    public void tambahDaftar(ListTTK ttk) {
        listHolder holder= new listHolder();
        View view = inflater.inflate(R.layout.delivery_note_list_listview, null);
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

        holder.noTTKIsi.setText(ttk.getNoTTK());
        holder.koliIsi.setText(ttk.getKoli()+"");
        holder.beratIsi.setText(ttk.getBerat()+"");
        holder.statusIsi.setText(ttk.getStatus());

        layoutForm.addView(view);
    }

}
