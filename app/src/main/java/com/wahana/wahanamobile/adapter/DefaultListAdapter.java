package com.wahana.wahanamobile.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DemoItem;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Locale;

/**
 * Created by Reza on 09/06/2016.
 */
public class DefaultListAdapter  extends ArrayAdapter<DemoItem> implements DemoAdapter {
    private static final String TAG = "MainActivity";

    private final LayoutInflater layoutInflater;
    public ImageView imageView;
    public ImageView imageView_odd;
    public TextView textview;
    public ImageView layout_kotak;
    public static TextView textView1;

    public DefaultListAdapter(Context context, List<DemoItem> items) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
    }

    public DefaultListAdapter(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

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
        String planet = getContext().getResources().getStringArray(R.array.menuutama)[position];
        String judul = getContext().getResources().getStringArray(R.array.judulutama)[position];
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

           // textView.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
            //layout_kotak.setImageResource(imageId);
//            layout_kotak.setImageBitmap(
//                    decodeSampledBitmapFromResource(getContext().getResources(), imageId, 250, 250));
            //layout_kotak.setBackgroundDrawable(getContext().getResources().getDrawable(imageId));

            textView.setText(judul);
        }


        return v;
    }

//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }
//
//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }

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
