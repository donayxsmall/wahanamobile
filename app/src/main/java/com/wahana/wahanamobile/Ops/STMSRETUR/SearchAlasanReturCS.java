package com.wahana.wahanamobile.Ops.STMSRETUR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.ModelApiOPS.aoGetAlasanRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoGetAlasanRetur_Result;
import com.wahana.wahanamobile.ModelApiOPS.aoGetListKeranjang;
import com.wahana.wahanamobile.ModelApiOPS.aoGetListKeranjang_Result;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterSearchAlasanReturCS;
import com.wahana.wahanamobile.adapter.AdapterSearchKeranjang;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.DataAlasanReturCS;
import com.wahana.wahanamobile.model.DataKeranjang;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAlasanReturCS extends AppCompatActivity {

    private static final String TAG = "SearchKeranjang";

    ProgressDialog progressDialog;
    private ListView listView;
    private AdapterSearchAlasanReturCS adapter;
    SessionManager session;
    ArrayList<DataAlasanReturCS> DataAlasanReturList = new ArrayList<DataAlasanReturCS>();

    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_alasan_retur_cs);

        progressDialog = new ProgressDialog(SearchAlasanReturCS.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(SearchAlasanReturCS.this);
        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        setData();

        setDatav2();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                hideSoftKeyboard(SearchAlasanReturCS.this);

                TextView text = (TextView)view.findViewById(R.id.text);
                TextView idalasanretur = (TextView)view.findViewById(R.id.id);


                Intent pindah = new Intent(SearchAlasanReturCS.this, InputTTKRetur.class);
                pindah.putExtra("textalasanreturcs", text.getText().toString());
                pindah.putExtra("idalasanretur", idalasanretur.getText().toString());
                setResult(RESULT_OK, pindah);

                finish();

            }
        });


    }

    private void setDatav2(){

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        Call<aoGetAlasanRetur> result = mApiInterface.aoGetAlasanRetur("aoGetAlasanRetur",getString(R.string.partnerid),session.getKeyTokenJWT());

        result.enqueue(new Callback<aoGetAlasanRetur>() {

            @Override
            public void onResponse(Call<aoGetAlasanRetur> call, Response<aoGetAlasanRetur> response) {

                progressDialog.dismiss();


                if(response.isSuccessful()) {

                    try {


                        Log.d("retro",""+response.body().getCode());

                        aoGetAlasanRetur data = response.body();
                        List<aoGetAlasanRetur_Result> dataList = new ArrayList<>();
                        dataList = data.getData();

                        for (int i = 0; i < dataList.size(); i++) {
                            Log.d("fit1", "" + dataList.get(i).getATRLVNM());

                            DataAlasanReturCS prov = new DataAlasanReturCS();
                            prov.setId(dataList.get(i).getATRLVCD());
                            prov.setText(dataList.get(i).getATRLVNM());
                            DataAlasanReturList.add(prov);
                        }

                        adapter = new AdapterSearchAlasanReturCS(SearchAlasanReturCS.this, R.layout.listviewsearchdefault, DataAlasanReturList);
                        listView.setAdapter(adapter);




                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();

                    Log.d("error1",""+response.code());

                    Toast.makeText(SearchAlasanReturCS.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();


                }



            }

            @Override
            public void onFailure(Call<aoGetAlasanRetur> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                Log.d("error2",""+t.toString());

                Toast.makeText(SearchAlasanReturCS.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                // finish();
                Intent intent = new Intent(this, InputTTKRetur.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, intent);

                finish();



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


}




