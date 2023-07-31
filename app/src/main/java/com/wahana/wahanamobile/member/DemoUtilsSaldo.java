package com.wahana.wahanamobile.member;

import com.wahana.wahanamobile.model.DemoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 24/03/2017.
 */
public class DemoUtilsSaldo {
    int currentOffset;

    DemoUtilsSaldo() {
    }

    public List<DemoItem> moarItems(int qty) {
        List<DemoItem> items = new ArrayList<>();

        for (int i = 0; i < qty; i++) {
            if (i==0)
            {
                int colSpan = 4;
                // Math.random() < 0.2f ? 2 : 1;
                // Swap the next 2 lines to have items with variable
                // column/row span.
                // int rowSpan = Math.random() < 0.2f ? 2 : 1;
                int rowSpan = 2;
                DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
                items.add(item);
                continue;
            }
            int colSpan = 2;
            // Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            // int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = 2;
            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
            items.add(item);
        }

        currentOffset += qty;

        return items;
    }
}
