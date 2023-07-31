package com.wahana.wahanamobile.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.PickupAirwaysStatus;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Reza on 29/06/2016.
 */
public class ConclusionAdapter  extends BaseAdapter {
    private static final String TAG = "setStatus";
    private Activity activity;
    private final int REQUEST_FOR_ACTIVITY_CODE = 1;
    String [] KodeSortir;
    String [] JumlahKoli;
    String _agentcode;
    public String agentCode, dateMin, dateMax, sortingCode;
    SessionManager session;
    Context context;
    List<TTk> ttkList = new ArrayList<TTk>();
    private String username, user_id;
    private static LayoutInflater inflater=null;
    List<KodeSortir> kodeList;
    ProgressDialog progressDialog;
    String kode = null;
    DatabaseHandler db;

    public ConclusionAdapter(Activity a, String agenttcode, String[] kodeSortir) {
        activity = a;
        KodeSortir = kodeSortir;
        _agentcode = agenttcode;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ConclusionAdapter(Activity a, String agenttcode, List<KodeSortir> kodes) {
        activity = a;
        kodeList = kodes;
        _agentcode = agenttcode;
        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ConclusionAdapter(Activity a, List<KodeSortir> kodes) {
        activity = a;
        kodeList = kodes;
        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kodeList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView label_sortir;
        TextView label_koli;
        TextView sortir;
        TextView jumlah;
        EditText inputKoli;
        Button buttonInput;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        db = new  DatabaseHandler(activity);
        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.conclusion_listview, null);
        holder.label_sortir = (TextView)view.findViewById(R.id.kode_sortir_label);
        holder.label_koli = (TextView)view.findViewById(R.id.kode_koli_label);
        holder.sortir = (TextView)view.findViewById(R.id.kode_sortir_isi);
        holder.jumlah = (TextView)view.findViewById(R.id.jumlah);
        holder.inputKoli = (EditText) view.findViewById(R.id.input_jumlah_koli);
        holder.buttonInput = (Button) view.findViewById(R.id.input_button);
        holder.buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseHandler db = new DatabaseHandler(activity);
                session = new SessionManager(activity);
                username = session.getUsername();
                user_id = session.getID();
                if(holder.inputKoli.getText().toString().isEmpty())
                {
                    holder.inputKoli.setError("Masukkan Jumlah Koli");
                } else {
                    KodeSortir hasil = db.getTotal(holder.sortir.getText().toString());
                    int total = hasil.getJumlahttk();
                    int verify = hasil.getVerify();
                    final int input = Integer.parseInt(holder.inputKoli.getText().toString());
                    kode = hasil.getKodesortir();
                    holder.inputKoli.setError(null);
                    Log.d("jumlah verify3","jumlah "+total+" ver "+verify);
                    KodeSortir text = db.getDate(kode);
                    if (input == total){
                        Random r = new Random();
                        int requestCode = r.nextInt(9999);
                        String rc = String.valueOf(requestCode);

                        ArrayList<String> parameter = new ArrayList<String>();
                        parameter.add("pickupGetPackageListPerSortingCode");
                        parameter.add(session.getSessionID());
                        parameter.add(session.getID());
                        parameter.add(_agentcode);
                        parameter.add(kode);
                        parameter.add(text.getDatemin());
                        parameter.add(text.getDatemax());

                        new SoapClient(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Log.i("Conclusion Adapter", "onPreExecute");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Authenticating...");
                                progressDialog.show();
                            }

                            @Override
                            protected void onPostExecute(SoapObject result) {
                                super.onPostExecute(result);
                                Log.d("hasil soap", ""+result);
                                if(result==null){
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(activity, activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }else {
                                    try {
                                        final String text = result.getProperty(1).toString();
                                        if (text.equals("OK")) {
                                            progressDialog.dismiss();
                                            for (int i = 2; i<result.getPropertyCount(); i++){
                                                SoapObject so = (SoapObject) result.getProperty(i);
                                                String nottk = so.getProperty("packageNumber").toString();
                                                TTk ttk = new TTk();
                                                ttk.setNottk(nottk);
                                                ttk.setStatus(1);
                                                ttkList.add(ttk);
                                                DatabaseHandler db = new DatabaseHandler(activity);
                                                db.addTTK(new TTk(nottk, 1, kode));
                                            }
                                            holder.inputKoli.setVisibility(View.GONE);
                                            holder.buttonInput.setVisibility(View.GONE);
                                            holder.jumlah.setText(""+input);
                                            int a = db.updateStatusSortir(kode, input);
//                                            new status().execute();
                                            Log.d("jumlah", input+"<<"+a+kode);
                                        }else{
                                            progressDialog.dismiss();
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
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        }.execute(parameter);
                    }else if(total== verify) {
                        holder.inputKoli.setVisibility(View.GONE);
                        holder.buttonInput.setVisibility(View.GONE);
                        holder.jumlah.setText(total + "");

                    }else{
                        String totalInput = db.getTotalInput(holder.sortir.getText().toString());
                        if (Integer.parseInt(totalInput) < 2){
                            int sisa = 3 - (Integer.parseInt(totalInput)+1);
                            Toast.makeText(activity, "Jumlah Koli Anda Masukkan Salah, Sisa Percobaan "+sisa, Toast.LENGTH_SHORT).show();
                            db.updateTotalInput(holder.sortir.getText().toString(), String.valueOf(Integer.parseInt(totalInput)+1));
                        }else{
                            Intent intent = new Intent(activity, PickupAirwaysStatus.class);
                            intent.putExtra("agentcode", _agentcode);
                            intent.putExtra("sortingcode", holder.sortir.getText().toString());
                            ((Activity) activity).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                        }
                    }
                }
            }
        });
        KodeSortir kode = kodeList.get(position);
        Log.d("kode ",kode+"");
        String status = kode.getStatus();
        String kodesortir = kode.getKodesortir();
        int verify=kode.getVerify();
        int total = kode.getJumlahttk();
        Log.d("jumlah verify","jumlah "+total+" ver "+verify);
        if (status.equals("submit")){
            //verify = kode.getVerify();
            holder.inputKoli.setVisibility(View.GONE);
            holder.buttonInput.setVisibility(View.GONE);
            holder.jumlah.setText(""+verify);
        }
        Typeface type = Typeface.createFromAsset(activity.getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        holder.label_sortir.setTypeface(type);
        holder.sortir.setTypeface(type);
        holder.inputKoli.setTypeface(type);
        holder.sortir.setText(kodesortir);
        return view;
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == activity.RESULT_OK){
            Log.d("MyAdapter", "onActivityResult");
        }
    }

}
