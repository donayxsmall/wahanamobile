package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wahana.wahanamobile.R;

import java.util.List;

/**
 * Created by Lazuardy on 30/05/2017.
 */

public class ListSubMenuAdapter extends BaseAdapter {
    private Activity activity;
    String kodesortir;
    Context context;
    private static LayoutInflater inflater=null;
    String[] noTTK;
    List<String> Menu, urls;
    String Tipe;

    public ListSubMenuAdapter(Activity a, List<String> menu, List<String> urls) {
        activity = a;
        this.Menu = menu;
        this.urls = urls;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        try{
            return Menu.size();
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
        TextView url;
        Button submit;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ListSubMenuAdapter.Holder holder=new ListSubMenuAdapter.Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.lihat_submenu_listview, null);
        holder.no_ttk = (TextView)view.findViewById(R.id.no_ttk_isi);
        holder.url = (TextView)view.findViewById(R.id.url_isi);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.no_ttk.setTypeface(type);
        String sj = Menu.get(position);
        String url = urls.get(position);
        holder.no_ttk.setText(sj);
        holder.url.setText(url);
        int size = 20;
        holder.no_ttk.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        return view;
    }
}

