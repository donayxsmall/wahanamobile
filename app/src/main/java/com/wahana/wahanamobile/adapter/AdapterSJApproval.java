package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DataSJ;

import java.util.ArrayList;

/**
 * Created by team-it on 30/10/18.
 */


public class AdapterSJApproval extends ArrayAdapter<DataSJ> implements View.OnClickListener{

    private ArrayList<DataSJ> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtttk;
        TextView nourut;
    }

    public AdapterSJApproval(ArrayList<DataSJ> data, Context context) {
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
        DataSJ dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_itemsj, parent, false);
            viewHolder.txtttk = (TextView) convertView.findViewById(R.id.ttk);
            viewHolder.nourut=(TextView)convertView.findViewById(R.id.nomorurut);
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
        viewHolder.txtttk.setText(dataModel.getTtk());


//        viewHolder.txtType.setText(dataModel.getType());
//        viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

