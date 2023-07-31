package com.wahana.wahanamobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wahana.wahanamobile.adapter.AdapterRecycleView;
import com.wahana.wahanamobile.model.dataRecycleview;

import java.util.ArrayList;

public class RecycleView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterRecycleView adapter;
    private ArrayList<dataRecycleview> mahasiswaArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        addData();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapter = new AdapterRecycleView(mahasiswaArrayList);

        int numberOfColumns = 3;

//        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecycleView.this);
//
//        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        recyclerView.setAdapter(adapter);




    }

    void addData(){
        mahasiswaArrayList = new ArrayList<>();
        mahasiswaArrayList.add(new dataRecycleview("Dimas Maulana", "1414370309", "123456789"));
        mahasiswaArrayList.add(new dataRecycleview("Fadly Yonk", "1214234560", "987654321"));
        mahasiswaArrayList.add(new dataRecycleview("Ariyandi Nugraha", "1214230345", "987648765"));
        mahasiswaArrayList.add(new dataRecycleview("Aham Siswana", "1214378098", "098758124"));
    }
}
