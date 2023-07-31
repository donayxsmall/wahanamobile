package com.wahana.wahanamobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.dataRecycleview;

import java.util.ArrayList;

/**
 * Created by team-it on 12/11/18.
 */

public class AdapterRecycleView extends RecyclerView.Adapter<AdapterRecycleView.AdapterRecycleViewViewHolder> {


    private ArrayList<dataRecycleview> dataList;

    public AdapterRecycleView(ArrayList<dataRecycleview> dataList) {
        this.dataList = dataList;
    }

    @Override
    public AdapterRecycleViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_recycleview, parent, false);
        return new AdapterRecycleViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterRecycleViewViewHolder holder, int position) {
        holder.txtNama.setText(dataList.get(position).getNama());
        holder.txtNpm.setText(dataList.get(position).getNpm());
        holder.txtNoHp.setText(dataList.get(position).getNohp());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class AdapterRecycleViewViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtNpm, txtNoHp;

        public AdapterRecycleViewViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.txt_nama_mahasiswa);
            txtNpm = (TextView) itemView.findViewById(R.id.txt_npm_mahasiswa);
            txtNoHp = (TextView) itemView.findViewById(R.id.txt_nohp_mahasiswa);
        }
    }
}
