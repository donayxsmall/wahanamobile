package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.EditOffline;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.helper.DatabaseHandler;

import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 31/08/2016.
 */
public class OfflineDeliveryAdapter extends BaseAdapter {
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    List<OfflineTTK> offlineTTK = new ArrayList<OfflineTTK>();
    private static final String TAG = "DeliveredAirways";

    ProgressDialog progressDialog;
    DatabaseHandler db;
    String encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;

    public OfflineDeliveryAdapter(Activity a, List<OfflineTTK> offlineTTK) {
        activity = a;
        this.offlineTTK = offlineTTK;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return offlineTTK.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView ttkLabel;
        TextView ttkIsi;
        TextView tandaLabel;
        TextView tandaIsi;
        TextView statusLabel;
        TextView statusIsi;
        TextView keteranganLabel;
        TextView keteranganIsi;
        Button editButton, sendButton;
        LinearLayout offlineButton;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        db = new DatabaseHandler(activity);
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.offline_delivery_listview, null);
        holder.ttkLabel= (TextView)view.findViewById(R.id.ttk_label);
        holder.ttkIsi= (TextView)view.findViewById(R.id.ttk_isi);
        holder.tandaLabel= (TextView)view.findViewById(R.id.tanda_label);
        holder.tandaIsi= (TextView)view.findViewById(R.id.tanda_isi);
        holder.statusLabel= (TextView)view.findViewById(R.id.status_label);
        holder.statusIsi= (TextView)view.findViewById(R.id.status_isi);
        holder.keteranganLabel= (TextView)view.findViewById(R.id.keterangan_label);
        holder.keteranganIsi= (TextView)view.findViewById(R.id.keterangan_isi);
        holder.offlineButton = (LinearLayout) view.findViewById(R.id.button_offline_lala);
        holder.editButton = (Button) view.findViewById(R.id.edit_button);
        holder.sendButton = (Button) view.findViewById(R.id.send_button);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.ttkLabel.setTypeface(typeSemibold);
        holder.ttkIsi.setTypeface(typeSemibold);
        holder.tandaLabel.setTypeface(typeSemibold);
        holder.tandaIsi.setTypeface(typeSemibold);
        holder.statusLabel.setTypeface(typeSemibold);
        holder.statusIsi.setTypeface(typeRegular);
        holder.keteranganLabel.setTypeface(typeRegular);
        holder.keteranganIsi.setTypeface(typeRegular);

        final OfflineTTK offlineTTKs = offlineTTK.get(position);
        holder.ttkIsi.setText(offlineTTKs.getTTK());
        if (offlineTTKs.getPackagestatus().equals("1")){
            holder.tandaIsi.setText("T");
        }else if (offlineTTKs.getPackagestatus().equals("2")){
            holder.tandaIsi.setText("BT");
        }
        holder.statusIsi.setText(offlineTTKs.getStatusTTK());
        if (offlineTTKs.getMessageSoap().equals("")){
            holder.keteranganIsi.setText("-");
        }else {
            holder.keteranganIsi.setText(offlineTTKs.getMessageSoap());
        }
        if (!holder.statusIsi.getText().equals("Terkirim"))
        {
            holder.offlineButton.setVisibility(View.VISIBLE);
            holder.editButton .setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, EditOffline.class);
                    intent.putExtra("ttk", offlineTTKs.getTTK());
                    activity.startActivity(intent);
                }
            });
        }

//        holder.editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> uris = db.getAllTTKURI(offlineTTKs.getTTK());
                for (int s = 0; s < uris.size(); s++){
                    String uri = uris.get(s);
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Bitmap bmp= BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(Uri.parse("file://"+uri)));
                        bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
                        byte[] raw = out.toByteArray();
                        if (s == 0){
                            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
                            Log.d("gambar 1", ""+encodedImage);
                        }else if (s==1){
                            encodedImage2 = Base64.encodeToString(raw, Base64.DEFAULT);
                            Log.d("gambar 2", ""+encodedImage2);
                        }else if (s==2){
                            encodedImage3 = Base64.encodeToString(raw, Base64.DEFAULT);
                            Log.d("gambar 3", ""+encodedImage3);
                        }else if (s==3){
                            encodedImage4 = Base64.encodeToString(raw, Base64.DEFAULT);
                            Log.d("gambar 4", ""+encodedImage4);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Keterangan", e+"");
                    }
                }
                ArrayList<String> parameter = new ArrayList<String>();
                parameter.add("setPackageDeliveryStatus");
                parameter.add(offlineTTKs.getSessionid());
                parameter.add(offlineTTKs.getRequestid());
                parameter.add(offlineTTKs.getEmployeecode());
                parameter.add(offlineTTKs.getTTK());
                parameter.add(offlineTTKs.getStatusdate());
                parameter.add(offlineTTKs.getPackagestatus());
                parameter.add(offlineTTKs.getReason());
                parameter.add(offlineTTKs.getReceivername());
                parameter.add(offlineTTKs.getReceivertype());
                parameter.add(offlineTTKs.getComment());
                parameter.add(encodedImage);
                parameter.add(encodedImage2);
                parameter.add(encodedImage3);
                parameter.add(encodedImage4);
                parameter.add(offlineTTKs.getLatitude());
                parameter.add(offlineTTKs.getLongitude());
                Log.d("hasil gambar kirim", ""+encodedImage);

                new SoapClient(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Log.d("adding data", "add");
                        Log.i(TAG, "onPreExecute");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Adding Data...");
                        progressDialog.show();
                    }
                    @Override
                    protected void onPostExecute(SoapObject result) {
                        super.onPostExecute(result);
                        Log.d("Hasil", result+"");
                        if (result == null) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            try{
                                final String text = result.getProperty(1).toString();
                                if (text.equals("OK")) {
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, activity.getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                                    db.updateOfflineMessage(offlineTTKs.getTTK(), text, "Terkirim");
                                    holder.statusIsi.setText("Terkirim");
                                    holder.keteranganIsi.setText(text);
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                    db.updateOfflineMessage(offlineTTKs.getTTK(), text, "Gagal");
                                    holder.statusIsi.setText("Gagal");
                                    holder.keteranganIsi.setText(text);
                                }
                            }catch (Exception e){
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(activity, activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                db.updateOfflineMessage(offlineTTKs.getTTK(), "System Error", "Gagal");
                                holder.statusIsi.setText("Gagal");
                                holder.keteranganIsi.setText("System Error");
                            }
                        }
                    }
                }.execute(parameter);
            }
        });

        return view;
    }

}
