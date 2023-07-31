package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ttkUser;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 10/03/2017.
 */
public class InputFotoAgenAdapter  extends BaseAdapter {
    private static final String TAG = "Delete TTK Member";
    private Activity activity;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Context context;
    private static LayoutInflater inflater=null;
    String[] listfoto;
    List<ttkUser> listTTk = new ArrayList<ttkUser>();
    ProgressDialog progressDialog;
    SessionManager session;
    Uri imageUri;
    private Button btnClickImage, btnCancel, btnSend, btnInput;
    Spinner spinnerAbsensi;
    private ListView listView;
    private ImageLoadingUtils utils;
    String tipe = "foto_agen";
    private ImageView imgView;
    private ImageUriDatabases database;
    private ImageListAdapter adapterImage;
    private Cursor cursor;
    private LruCache<String, Bitmap> memoryCache;
    public InputFotoAgenAdapter(Activity a, String[] ListFoto) {
        activity = a;
        this.listfoto = ListFoto;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return listfoto.length    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView txtListFoto;
        ImageView imgFoto;
        Button btnUpload;
//        Button btnDelete;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.input_foto_listview, null);
        database = new ImageUriDatabases(activity);
        database.destroy();
        adapterImage = new ImageListAdapter(activity, cursor, true);
        holder.txtListFoto= (TextView)view.findViewById(R.id.label_foto);
        holder.imgFoto= (ImageView)view.findViewById(R.id.image_foto);
        holder.btnUpload = (Button)view.findViewById(R.id.btn_upload_foto);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.txtListFoto.setTypeface(typeSemibold);
        holder.txtListFoto.setText(listfoto[position]);

        holder.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define the file-name to save photo taken by Camera activity
                String fileName = System.currentTimeMillis()+".jpg";
                //create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
                //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                activity.startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });
        return view;
    }

    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_layout);
        imgView = (ImageView) dialog.findViewById(R.id.dlgImageView);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnSend = (Button) dialog.findViewById(R.id.btnSend);
        return dialog;
    }

    @Deprecated
    protected void onPrepareDialog(int id, final Dialog dialog, Bundle bundle) {
        switch (id){
            case 1:
                if(bundle != null){
                    final String filePath = bundle.getString("FILE_PATH");
                    imgView.setImageBitmap(utils.decodeBitmapFromPath(filePath));

                    btnCancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    btnSend.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            database.insertUri(filePath,tipe);
                            cursor = database.getallUri();
                            adapterImage.changeCursor(cursor);
                            dialog.dismiss();
                        }
                    });
                }
        }
    }

    class ImageListAdapter extends CursorAdapter {
        private ImageLoadingUtils utils;

        public ImageListAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
            utils = new ImageLoadingUtils(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            loadBitmap(cursor.getString(cursor.getColumnIndex(ImageUriDatabases.PATH_NAME)), holder.imageView, context);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imgView);
            view.setTag(holder);
            return view;
        }

        public void loadBitmap(String filePath, ImageView imageView, Context context) {
            if (cancelPotentialWork(filePath, imageView)) {
                final Bitmap bitmap = getBitmapFromMemCache(filePath);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
                else{
                    final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), utils.icon, task);
                    imageView.setImageDrawable(asyncDrawable);
                    task.execute(filePath);
                }
            }
        }

        class ViewHolder{
            ImageView imageView;
        }

        class AsyncDrawable extends BitmapDrawable {

            private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

            public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
                super(res, bitmap);
                bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
            }

            public BitmapWorkerTask getBitmapWorkerTask() {
                return bitmapWorkerTaskReference.get();
            }
        }

        public boolean cancelPotentialWork(String filePath, ImageView imageView) {

            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (bitmapWorkerTask != null) {
                final String bitmapFilePath = bitmapWorkerTask.filePath;
                if (bitmapFilePath != null && !bitmapFilePath.equalsIgnoreCase(filePath)) {
                    bitmapWorkerTask.cancel(true);
                } else {
                    return false;
                }
            }
            return true;
        }

        private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
            if (imageView != null) {
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AsyncDrawable) {
                    final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                    return asyncDrawable.getBitmapWorkerTask();
                }
            }
            return null;
        }

        class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;
            public String filePath;

            public BitmapWorkerTask(ImageView imageView){
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                filePath = params[0];
                Bitmap bitmap = utils.decodeBitmapFromPath(filePath);
                addBitmapToMemoryCache(filePath, bitmap);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (isCancelled()) {
                    bitmap = null;
                }
                if(imageViewReference != null && bitmap != null){
                    final ImageView imageView = imageViewReference.get();
                    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                    if (this == bitmapWorkerTask && imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }



}
