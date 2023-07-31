package com.wahana.wahanamobile.adapter;

/**
 * Created by Reza on 09/06/2016.
 */

import android.widget.ListAdapter;


import com.wahana.wahanamobile.model.DemoItem;

import java.util.List;

public interface DemoAdapter extends ListAdapter {

    void appendItems(List<DemoItem> newItems);

    void setItems(List<DemoItem> moreItems);
}
