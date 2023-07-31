package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DataSJ;

import java.util.ArrayList;

/**
 * Created by team-it on 10/01/19.
 */

public class AdapterNotifHRD extends ArrayAdapter<Origin> implements View.OnClickListener{

    private ArrayList<Origin> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtttk;
        TextView nourut;
        TextView notransaksi;
        TextView link;
        TextView jenis;
    }

    public AdapterNotifHRD(ArrayList<Origin> data, Context context) {
        super(context, R.layout.row_itemsj, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataSJ dataModel=(DataSJ) object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Origin dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_itemnotif, parent, false);
            viewHolder.txtttk = (TextView) convertView.findViewById(R.id.ttk);
            viewHolder.nourut=(TextView)convertView.findViewById(R.id.nomorurut);
            viewHolder.notransaksi=(TextView)convertView.findViewById(R.id.idtransaksi);
            viewHolder.link=(TextView)convertView.findViewById(R.id.link);
            viewHolder.jenis=(TextView)convertView.findViewById(R.id.jenis);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        int no = position+1;

        viewHolder.nourut.setText(no+".");
        viewHolder.txtttk.setText(dataModel.getProvince());
        viewHolder.notransaksi.setText(dataModel.getId());
        viewHolder.link.setText(dataModel.getCity());
        viewHolder.jenis.setText(dataModel.getJenis());


//        viewHolder.txtType.setText(dataModel.getType());
//        viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
