package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ttkUser;
import com.wahana.wahanamobile.DetailMemberAirways;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 05/12/2016.
 */
public class MemberAirwaysAdapter extends BaseAdapter {
    private static final String TAG = "Delete TTK Member";
    private Activity activity;
    Context context;
    private static LayoutInflater inflater=null;
    DatabaseHandler db;
    String[] nottk, status, keterangan;
    LinearLayout layoutForm;
    List<ttkUser> listTTk = new ArrayList<ttkUser>();
    ProgressDialog progressDialog;
    SessionManager session;
    private MemberAirwaysAdapter adapter;

    public MemberAirwaysAdapter(Activity a, String[] NoTTK, String[] Keterangan) {
        activity = a;
        this.nottk = NoTTK;

        this.keterangan = Keterangan;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public MemberAirwaysAdapter(Activity a, List<ttkUser> ttk) {
        activity = a;
        this.listTTk = ttk;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(activity);
        adapter = this;
    }

    public int getCount() {
        return listTTk.size()    ;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView txtTTK, txtStatus, txtKeterangan;
        Button btnDetail;
        Button btnDelete;
//        ListView listViewDeliveryNote;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.member_airways_listview, null);


//        ListTTK ttk = listTTk.get(position);

        holder.txtTTK= (TextView)view.findViewById(R.id.nomor_ttk_header);
//        holder.txtStatus= (TextView)view.findViewById(R.id.status_header);
        holder.txtKeterangan= (TextView)view.findViewById(R.id.keterangan_header);
        holder.btnDetail = (Button)view.findViewById(R.id.btn_detail_airway);
        holder.btnDelete = (Button)view.findViewById(R.id.btn_delete_detail_airway);
        Typeface typeRegular = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansRegular.ttf");
        Typeface typeSemibold = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansSemibold.ttf");
        holder.txtTTK.setTypeface(typeSemibold);
//        holder.txtStatus.setTypeface(typeSemibold);
        holder.txtKeterangan.setTypeface(typeSemibold);

        final ttkUser ttk = listTTk.get(position);
        holder.txtTTK.setText(ttk.getNoTtk());
//        holder.txtStatus.setText(status[position]);
        holder.txtKeterangan.setText(ttk.getKet());

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, DetailMemberAirways.class);
                i.putExtra("noTtk", ttk.getNoTtk());
                activity.startActivity(i);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("My TTK");
                alertDialog.setMessage("Apakah Anda Ingin Menghapus TTK ini ?");
                alertDialog.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTtkMember(ttk.getNoTtk());
                            }
                        });

                alertDialog.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
            }
        });

        return view;
    }


    public void deleteTtkMember(String ttk) {
        Log.d(TAG, "Delete TTK Member");

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("deleteTtkUser");
        parameter.add(session.getID());
        parameter.add(ttk);

        new SoapClientMember(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    progressDialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resText").toString();
                        if (code.equals("1")) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, text,Toast.LENGTH_LONG).show();
                                }
                            });
                            Intent intent = activity.getIntent();
                            activity.finish();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activity.startActivity(intent);
                        }else{
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }
}
