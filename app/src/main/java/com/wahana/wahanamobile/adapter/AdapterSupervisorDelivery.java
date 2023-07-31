package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DemoItem;

import java.util.List;
import java.util.Locale;

/**
 * Created by Lazuardy on 31/05/2017.
 */

public class AdapterSupervisorDelivery extends ArrayAdapter<DemoItem> implements DemoAdapter  {
    private static final String TAG = "SalesMainActivity";

    private final LayoutInflater layoutInflater;
    public ImageView imageView;
    public ImageView imageView_odd;
    public TextView textview;
    public ImageView layout_kotak;
    public static TextView textView1;
    public AdapterSupervisorDelivery(Context context, List<DemoItem> items) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
    }

    public AdapterSupervisorDelivery(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;

        DemoItem item = getItem(position);


        if (convertView == null) {
//            if (position ==1 )
//            {
//                v = layoutInflater.inflate(
//                        R.layout.adapter_item , parent, false);
//            }else
//            {
            v = layoutInflater.inflate(
                    R.layout.adapter_item_odd , parent, false);
//            }

        } else {
            v = convertView;
        }
//        layout_kotak = (ImageView) v.findViewById(R.id.layout_kotak);
        String planet = getContext().getResources().getStringArray(R.array.menusupervisordelivery)[position];
        String judul = getContext().getResources().getStringArray(R.array.judulsupervisordelivery)[position];
        int imageId =  getContext().getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                "drawable",  getContext().getPackageName());

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getContext().getResources(), imageId, options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
//        String imageType = options.outMimeType;

        for (int i=0;i<=position;i++)
        {
//            if (position == 1)
//            {
//
//                TextView textView = (TextView) v.findViewById(R.id.textview_odd);
//                textView1 = (TextView) v.findViewById(R.id.textview_content);
//                Typeface type = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansLight.ttf");
//                Typeface typeContent = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansRegular.ttf");
//                textView.setTypeface(type);
//                textView1.setTypeface(typeContent);
//                //layout_kotak.setImageResource(imageId);
////                layout_kotak.setImageBitmap(
////                        decodeSampledBitmapFromResource(getContext().getResources(), imageId, 250, 250));
//
//                textView.setText(judul);
////                String total = MainActivity.total;
//                textView1.setText("0");
//                continue;
//            }

            TextView textView = (TextView) v.findViewById(R.id.textview_odd);
            layout_kotak = (ImageView) v.findViewById(R.id.imageview_content);
            layout_kotak.setBackgroundDrawable(getContext().getResources().getDrawable(imageId));
            Typeface type = Typeface.createFromAsset(getContext().getAssets(),"font/OpenSansLight.ttf");
            textView.setTypeface(type);

            textView.setText(judul);
        }


        return v;
    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    public void appendItems(List<DemoItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<DemoItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
}
