package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.TtkKeranjangRetur;
import com.wahana.wahanamobile.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by team-it on 13/03/19.
 */

public class AdapterTTKKeranjang extends ArrayAdapter<TtkKeranjangRetur> {

    private Activity activity;
    private ArrayList<TtkKeranjangRetur> friendList;
    private ArrayList<TtkKeranjangRetur> searchList;

    // View lookup cache
    private static class ViewHolder {
        TextView txtid;
        TextView txtttk;
        TextView txttglmasuk;
    }


    public AdapterTTKKeranjang(Activity context, int resource, ArrayList<TtkKeranjangRetur> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.friendList = objects;
        this.searchList = new ArrayList<>();
        this.searchList.addAll(friendList);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TtkKeranjangRetur dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listviewttkkeranjangdestroy, parent, false);
            viewHolder.txtid = (TextView) convertView.findViewById(R.id.idttkker);
            viewHolder.txtttk = (TextView) convertView.findViewById(R.id.text);
            viewHolder.txttglmasuk = (TextView) convertView.findViewById(R.id.tglmasuk);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(activity, (position > lastPosition) ? R.layout.up_from_bottom : R.layout.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtid.setText(dataModel.getId());
        viewHolder.txtttk.setText(dataModel.getTtk());
        viewHolder.txttglmasuk.setText(dataModel.getTglmasuk());

        // Return the completed view to render on screen
        return convertView;
    }


    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        friendList.clear();
        if (charText.length() == 0) {
            friendList.addAll(searchList);
        } else {
            for (TtkKeranjangRetur s : searchList) {
                if (s.getTtk().toLowerCase(Locale.getDefault()).contains(charText)) {
                    friendList.add(s);
                    Log.d("hasil filter", ""+charText+" "+s.getTtk());
                }
            }
        }
        notifyDataSetChanged();
    }


}
