package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Reza on 29/06/2016.
 */
public class StatusAdapter extends BaseAdapter {
    private Activity activity;
    String  KodeSortir;
    private static LayoutInflater inflater=null;
    List<TTk> tTkList;
    int selectedPosition = 0;

    public StatusAdapter(Activity a, String kodeSortir, List<TTk> nottk) {
        activity = a;
        KodeSortir = kodeSortir;
        tTkList = nottk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tTkList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        RadioGroup radioGroup;
        RadioButton radioYa;
        RadioButton radioTidak;
        TextView nomorTTK;
        TextView kodeSortir;
        Button submitButton;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
        view = inflater.inflate(R.layout.status_listview, null);
        holder.radioGroup = (RadioGroup)view.findViewById(R.id.radio_group_label);
        holder.radioYa = (RadioButton)view.findViewById(R.id.radio0);
        holder.radioTidak = (RadioButton)view.findViewById(R.id.radio1);
        holder.nomorTTK = (TextView) view.findViewById(R.id.no_ttk_isi);
        //holder.kodeSortir = (TextView) view.findViewById(R.id.kode_sortir_isi);

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        if (btn.getText().toString().equals("YA")){
                            Toast.makeText(activity, "1", Toast.LENGTH_SHORT).show();
                        }else if(btn.getText().toString().equals("TIDAK")){
                            Toast.makeText(activity, "0", Toast.LENGTH_SHORT).show();
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
        TTk ttk = tTkList.get(position);
        String nottk = ttk.getNottk();
        holder.nomorTTK.setText(nottk);
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
//        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        //holder.kodeSortir.setText(KodeSortir[0]);
        holder.radioYa.setTypeface(type);
        holder.radioTidak.setTypeface(type);
        holder.nomorTTK.setTypeface(type);
        //holder.kodeSortir.setTypeface(type);

        return view;
    }

}
