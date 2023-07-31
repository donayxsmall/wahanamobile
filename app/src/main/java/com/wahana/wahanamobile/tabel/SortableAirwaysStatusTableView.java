package com.wahana.wahanamobile.tabel;

import android.content.Context;
import android.util.AttributeSet;

import com.wahana.wahanamobile.Data.Kode;
import com.wahana.wahanamobile.R;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

/**
 * Created by Reza on 12/06/2016.
 */
public class SortableAirwaysStatusTableView  extends SortableTableView<Kode> {
    public SortableAirwaysStatusTableView(Context context) {
        this(context, null);
    }

    public SortableAirwaysStatusTableView(Context context, AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableAirwaysStatusTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);


        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, "TTK", "Ada", "Tidak Ada");
        simpleTableHeaderAdapter.setTextColor(context.getResources().getColor(R.color.table_header_text));
        setHeaderAdapter(simpleTableHeaderAdapter);

        int rowColorEven = context.getResources().getColor(R.color.table_data_row_even);
        int rowColorOdd = context.getResources().getColor(R.color.table_data_row_odd);
        setDataRowColorizer(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        setColumnWeight(0, 2);
        setColumnWeight(1, 2);
        setColumnWeight(2, 2);


    }
}
