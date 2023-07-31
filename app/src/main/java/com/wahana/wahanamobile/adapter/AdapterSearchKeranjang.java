package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DataKeranjang;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by donay on 06/11/19.
 */

public class AdapterSearchKeranjang extends ArrayAdapter<DataKeranjang> {

    private Activity activity;
    private ArrayList<DataKeranjang> friendList=new ArrayList<>();
    private ArrayList<DataKeranjang> searchList=new ArrayList<>();
    AdapterSearchKeranjang adapter;
    ListView list;

    public AdapterSearchKeranjang(Activity context, int resource, ArrayList<DataKeranjang> objects) {
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
    public DataKeranjang getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View v = convertView;
        final DataKeranjang cell = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.listviewsearchdefault, null);
            TextView text = (TextView) v.findViewById(R.id.text);
            TextView id = (TextView) v.findViewById(R.id.id);

            text.setText(cell.getText());
            id.setText(cell.getId());


        return v;
    }


    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        friendList.clear();
        if (charText.length() == 0) {
            friendList.addAll(searchList);
        } else {
            for (DataKeranjang s : searchList) {
                if (s.getText().toLowerCase().contains(charText)) {
                    friendList.add(s);
                    Log.d("hasil filter", ""+charText+" "+s.getText());
                }
            }
        }
        notifyDataSetChanged();
    }


}
