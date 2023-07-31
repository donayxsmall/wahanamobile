package com.wahana.wahanamobile;

import com.wahana.wahanamobile.model.DemoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 09/06/2016.
 */
final class DemoUtils {
    int currentOffset;

    DemoUtils() {
    }

    public List<DemoItem> moarItems(int qty) {
        List<DemoItem> items = new ArrayList<>();

        for (int i = 0; i < qty; i++) {
            int colSpan = 2;
                   // Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            // int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = colSpan;
            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
            items.add(item);
        }

        currentOffset += qty;

        return items;
    }
}
