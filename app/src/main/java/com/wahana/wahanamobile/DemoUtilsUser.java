package com.wahana.wahanamobile;

import com.wahana.wahanamobile.model.DemoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 21/07/2016.
 */
final class DemoUtilsUser {
    int currentOffset;

    DemoUtilsUser() {
    }

    public List<DemoItem> moarItems(int qty) {
        List<DemoItem> items = new ArrayList<>();
        int colSpan=0 , rowSpan = 0;
        for (int i = 0; i < qty; i++) {
            if(i==0 || i==3)
            {
                colSpan = 2;
                // Math.random() < 0.2f ? 2 : 1;
                // Swap the next 2 lines to have items with variable
                // column/row span.
                // int rowSpan = Math.random() < 0.2f ? 2 : 1;
                rowSpan = 2;

            }else
            {
                colSpan = 2;
                // Math.random() < 0.2f ? 2 : 1;
                // Swap the next 2 lines to have items with variable
                // column/row span.
                // int rowSpan = Math.random() < 0.2f ? 2 : 1;
                rowSpan = 2;
            }

            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
            items.add(item);
        }

        currentOffset += qty;

        return items;
    }
}
