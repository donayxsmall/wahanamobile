package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.Kode;
import com.wahana.wahanamobile.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by Reza on 11/06/2016.
 */
public class ManifestTableDataAdapter extends TableDataAdapter<Kode> {
    private static final int TEXT_SIZE = 14;


    public ManifestTableDataAdapter(final Context context, final List<Kode> data) {
        super(context, data);
        setColumnCount(2);
    }

    @Override
    public View getCellView(final int rowIndex, final int columnIndex, final ViewGroup parentView) {
        final Kode kode = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderKodeSortir(kode, parentView);
                break;
            case 1:
                renderedView = renderManifestSubmit(kode, parentView);
                break;
        }

        return renderedView;
    }


    private View renderKodeSortir(final Kode kode, final ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.table_cell_sortir, parentView, false);
        final TextView textView = (TextView) view.findViewById(R.id.sortir_text);
        textView.setText(kode.getKodeSortir());
        return view;
    }

    private View renderManifestSubmit(final Kode kode, final ViewGroup parentView) {
        boolean isSubmitted;
        final View view = getLayoutInflater().inflate(R.layout.table_cell_submit, parentView, false);
        final TextView textView = (TextView) view.findViewById(R.id.submit_text);
        isSubmitted = kode.getisSubmitted();
        if (isSubmitted == true)
        {
            textView.setText("Submitted");
        }
        else
        {
            textView.setText("[]Submit");
        }
        return view;
    }


    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

}
