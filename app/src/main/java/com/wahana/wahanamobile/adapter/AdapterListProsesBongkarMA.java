package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DataListProsesBongkarMA;

import java.util.ArrayList;

/**
 * Created by donay on 21/07/20.
 */

public class AdapterListProsesBongkarMA extends ArrayAdapter<DataListProsesBongkarMA> {

    private Activity activity;
    private ArrayList<DataListProsesBongkarMA> friendList=new ArrayList<>();
    private ArrayList<DataListProsesBongkarMA> searchList=new ArrayList<>();
    AdapterListProsesBongkarMA adapter;
    ListView list;

    public AdapterListProsesBongkarMA(Activity context, int resource, ArrayList<DataListProsesBongkarMA> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.friendList = objects;
        this.searchList.addAll(friendList);
        list=(ListView)activity.findViewById(R.id.list_item);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public DataListProsesBongkarMA getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View v = convertView;
        final DataListProsesBongkarMA cell = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.listviewbongkarma, null);
        TextView petugas = (TextView) v.findViewById(R.id.petugas);
        TextView jumlah = (TextView) v.findViewById(R.id.jumlah);

        petugas.setText(cell.getPetugas());
        jumlah.setText(cell.getJumlah());


        return v;
    }


    // Filter method
//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        friendList.clear();
//        if (charText.length() == 0) {
//            friendList.addAll(searchList);
//        } else {
//            for (DataAlasanReturCS s : searchList) {
//                if (s.getText().toLowerCase().contains(charText)) {
//                    friendList.add(s);
//                    Log.d("hasil filter", ""+charText+" "+s.getText());
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }


}

