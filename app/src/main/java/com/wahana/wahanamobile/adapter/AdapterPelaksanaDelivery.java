package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DemoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lazuardy on 29/05/2017.
 */

public class AdapterPelaksanaDelivery extends ArrayAdapter<DemoItem> implements DemoAdapter {
    private static final String TAG = "SalesMainActivity";

    private final LayoutInflater layoutInflater;
    public ImageView imageView;
    public ImageView imageView_odd;
    public TextView textview;
    public ImageView layout_kotak;
    public static TextView textView1;
    public String roleId, judul;
    int imageId;
    List<String> menus = new ArrayList<String>();
    List<String> icons = new ArrayList<String>();

    public AdapterPelaksanaDelivery(Context context, List<DemoItem> items, String roleId) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
        this.roleId = roleId;
    }

    public AdapterPelaksanaDelivery(Context context, List<DemoItem> items, String roleId, List<String> menus, List<String>icons) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
        this.roleId = roleId;
        this.menus = menus;
        this.icons = icons;
//        Log.d("hasil api",""+menus+"  "+icons);
    }

    public AdapterPelaksanaDelivery(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;

        DemoItem item = getItem(position);

        String planet = icons.get(position);
        judul = menus.get(position);
        imageId =  getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                "drawable",  getContext().getPackageName());
//        Log.d("hasil api",""+planet+"  "+judul+" "+position);

        if (convertView == null) {
            if (judul.equals("SISA PAKET"))
            {
                v = layoutInflater.inflate(
                        R.layout.adapter_item , parent, false);
//            }else if (roleId.equals("28")  && position ==1 ){
//                v = layoutInflater.inflate(
//                        R.layout.adapter_item , parent, false);
            }else{
                v = layoutInflater.inflate(
                        R.layout.adapter_item_odd , parent, false);
            }
        } else {
            v = convertView;
        }

//        if (roleId.equals("26")){
//            String planet = getContext().getResources().getStringArray(R.array.menupelaksanates)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulpelaksanates)[position];
//        }else if(roleId.equals("28")){
//            String planet = getContext().getResources().getStringArray(R.array.menupelaksanapickup)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulpelaksanapickup)[position];
//            imageId =  getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable",  getContext().getPackageName());
//        }else if(roleId.equals("29")) {
//            String planet = getContext().getResources().getStringArray(R.array.menupelaksanaproseschecker)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulpelaksanaproseschecker)[position];
//            imageId = getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getContext().getPackageName());
//        }else if(roleId.equals("30")) {
//            String planet = getContext().getResources().getStringArray(R.array.menupelaksanaprosessortir)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulpelaksanaprosessortir)[position];
//            imageId = getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getContext().getPackageName());
//        }else if(roleId.equals("32")) {
//            String planet = getContext().getResources().getStringArray(R.array.menusupervisordelivery)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulsupervisordelivery)[position];
//            imageId = getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getContext().getPackageName());
//        }else if(roleId.equals("33")) {
//            String planet = getContext().getResources().getStringArray(R.array.menusupervisorpickup)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulsupervisorpickup)[position];
//            imageId = getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getContext().getPackageName());
//        }else if(roleId.equals("35")) {
//            String planet = getContext().getResources().getStringArray(R.array.menusupervisorproses)[position];
//            judul = getContext().getResources().getStringArray(R.array.judulsupervisorproses)[position];
//            imageId = getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getContext().getPackageName());
//        }

        for (int i=0;i<=position;i++)
        {
            if (judul.equals("SISA PAKET")){
                TextView textView = (TextView) v.findViewById(R.id.textview_odd);
                textView1 = (TextView) v.findViewById(R.id.textview_content);
                Typeface type = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansLight.ttf");
                Typeface typeContent = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansRegular.ttf");
                textView.setTypeface(type);
                textView1.setTypeface(typeContent);

                textView.setText(judul);
                textView1.setText("0");
                continue;
//            }else if (roleId.equals("28") && position == 1) {
//                TextView textView = (TextView) v.findViewById(R.id.textview_odd);
//                textView1 = (TextView) v.findViewById(R.id.textview_content);
//                Typeface type = Typeface.createFromAsset(getContext().getAssets(), "font/OpenSansLight.ttf");
//                Typeface typeContent = Typeface.createFromAsset(getContext().getAssets(), "font/OpenSansRegular.ttf");
//                textView.setTypeface(type);
//                textView1.setTypeface(typeContent);
//
//                textView.setText(judul);
//                textView1.setText("0");
//                continue;
            }

            TextView textView = (TextView) v.findViewById(R.id.textview_odd);
            layout_kotak = (ImageView) v.findViewById(R.id.imageview_content);
            layout_kotak.setBackgroundDrawable(getContext().getResources().getDrawable(imageId));
            Typeface type = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansLight.ttf");
            textView.setTypeface(type);

            textView.setText(judul);
        }


        return v;
    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    public void appendItems(List<DemoItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<DemoItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
}
