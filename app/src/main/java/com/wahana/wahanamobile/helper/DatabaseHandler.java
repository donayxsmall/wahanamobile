package com.wahana.wahanamobile.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.Data.DataSJP;
import com.wahana.wahanamobile.Data.Group;
import com.wahana.wahanamobile.Data.KodeSortir;
import com.wahana.wahanamobile.Data.KotaAsal;
import com.wahana.wahanamobile.Data.ListMsSTSM;
import com.wahana.wahanamobile.Data.ListTTK;
import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Data.ListTtkSTMS;
import com.wahana.wahanamobile.Data.ListTtkSTPUBERAT;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.Data.Manifest;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.Data.Role;
import com.wahana.wahanamobile.Data.SuratJalan;
import com.wahana.wahanamobile.Data.TTk;
import com.wahana.wahanamobile.Data.TtkKeranjangRetur;
import com.wahana.wahanamobile.Data.TtkTidakTerkirim;
import com.wahana.wahanamobile.Data.User;
import com.wahana.wahanamobile.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 5;

    // Database Name
    public static final String DATABASE_NAME = "db_wahana_mobile";

    // table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_KOTA = "kotaAsal";
    private static final String TABLE_AGENT = "agent";
    private static final String TABLE_GROUP = "group_table";
    private static final String TABLE_KODE = "kodeSortir";
    private static final String TABLE_TTK = "ttk";
    private static final String TABLE_MANIFEST = "manifest";
    private static final String TABLE_TTKTIDAKTERKIRIM = "tttkTidakTerkirim";
    private static final String TABLE_SURATJALAN = "suratJalan";
    private static final String TABLE_LISTTTK = "listTTK";
    private static final String TABLE_OFFLINETTK = "offlineTTK";
    private static final String TABLE_ORIGIN = "origin";
    private static final String TABLE_ROLE = "role";
    private static final String TABLE_SJ = "listSuratJalan";
    private static final String TABLE_STMS = "stms";
    private static final String TABLE_STSM = "stsm";
    private static final String TABLE_MS = "manifestSortir";
    private static final String TABLE_SM = "suratMuatan";
    private static final String TABLE_SJP = "suratJalanPenerus";
    private static final String TABLE_STBT = "serahTerimaBelumTerkirim";
    private static final String TABLE_KENDARAAN = "kendaraan";
    private static final String TABLE_MA = "modaAngkutan";
    private static final String TABLE_STK = "stk";
    private static final String TABLE_DATA_SJP = "dataSJP";
    private static final String TABLE_DATA_Pickup = "listTtkPickup";
    private static final String TABLE_DATA_SumberPickup = "SumberTtkPickup";
    private static final String TABLE_DATA_STPU = "listTtkStpu";
    private static final String TABLE_DATA_SumberSTPU = "SumberTtkStpu";
    private static final String TABLE_TTK_KERANJANG_RETUR = "ttkkeranjangretur";
    private static final String TABLE_DATA_STSM = "listMsSTSM";
    private static final String TABLE_DATA_SumberSTSM = "SumberMsSTSM";
    private static final String TABLE_DATA_STMS = "listTtkSTMS";
    private static final String TABLE_DATA_SumberSTMS = "SumberTtkSTMS";
    private static final String TABLE_DATA_STPUMA = "listTtkSTPUMA";
    private static final String TABLE_DATA_SumberSTPUMA = "SumberTtkSTPUMA";
    private static final String TABLE_DATA_BONGKARMA = "listTtkBONGKARMA";
    private static final String TABLE_DATA_SumberBONGKARMA = "SumberTtkBONGKARMA";
    private static final String TABLE_DATA_StpuBerat = "listTtkStpuBerat";
    private static final String TABLE_DATA_SumberStpuBerat = "SumberTtkStpuBerat";


    // User Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_OFFICELAT = "officelat";
    private static final String KEY_OFFICELONG = "officelong";
    private static final String KEY_MAINGROUP = "maingroup";
    private static final String KEY_SESSIONID = "sessionid";
    private static final String KEY_CODE = "employeecode";
    private static final String KEY_LASTLOGIN = "last_login";
    private static final String KEY_KODEAGEN = "kodeagen";
    private static final String KEY_KODEVERIFIKASI = "kodeverifikasi";
    private static final String KEY_NIKSUPIR = "niksupir";
    private static final String KEY_KODEOPSTTK = "kodeopsttk";
    private static final String KEY_KODESORTIR = "kodesortir";
    private static final String KEY_KODESORTIRSTPU = "kodesortirstpu";
    private static final String KEY_NIKSTMA = "nikSTMA";
    private static final String KEY_NIKPICKUP = "NikPickup";
    private static final String KEY_PANJANG = "panjang";
    private static final String KEY_LEBAR = "lebar";
    private static final String KEY_TINGGI = "tinggi";

    //STSM
    private static final String KEY_NOMA = "noMa";
    private static final String KEY_NOMS = "noMs";


    // KotaAsal Table Columns names
    private static final String KEY_KOTA = "kotaasal";

    // Agent Table Columns names
    private static final String KEY_AGENT = "agent";

    // Group Table Columns names
    private static final String KEY_GROUP = "group_name";

    // KodeSortir Table Columns names
    private static final String KEY_KODE = "kodesortir";
    private static final String KEY_JUMLAH = "jumlahttk";
    private static final String KEY_STATUS = "status";
    private static final String KEY_VERIFY = "verify";
    private static final String KEY_UNVERIFY = "unverify";
    private static final String KEY_NOVERIFY = "noverify";
    private static final String KEY_DATEMIN = "datemin";
    private static final String KEY_DATEMAX = "datemax";
    private static final String KEY_INPUT = "input";

    // TTK Table Columns names
    private static final String KEY_NOTTK = "nottk";
    private static final String KEY_STA = "status";
    private static final String KEY_SORTIR = "kodesortir";
    private static final String KEY_NOREF = "noref";

    // Manifest Table Columns names
    private static final String KEY_NOMANIFEST = "nomanifest";
    private static final String KEY_URL = "url";

    // TTK Tidak Terkirim Columns names
    private static final String KEY_ALASAN = "alasan";
    private static final String KEY_KET = "keterangan";

    // Surat Jalan Columns names
    private static final String KEY_SJ = "suratjalan";
    private static final String KEY_TGL = "tgl";
    private static final String KEY_TOTALTTK = "total";
    private static final String KEY_TTKTERKIRIM = "terkirim";
    private static final String KEY_TTKBELUMTERKIRIM = "belum";
    private static final String KEY_TTKAKTIF = "aktif";
    private static final String KEY_TTKST = "ST";

    // TTK List Columns names
    private static final String KEY_KOLI = "koli";
    private static final String KEY_BERAT = "berat";

    // offline ttk Columns names
    private static final String KEY_REQUESTID = "requestId";
    private static final String KEY_STATUSDATE = "statusDate";
    private static final String KEY_PACKAGESTATUS = "packageStatus";
    private static final String KEY_REASON = "reason";
    private static final String KEY_RECEIVERNAME = "receiverName";
    private static final String KEY_RECEIVERTYPE = "receiverType";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_LATITUDE = "lattitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STATUSTTK = "statusTTk";
    private static final String KEY_MESSAGESOAP = "messageSoap";

    // Role Columns names
    private static final String KEY_ROLE_CODE = "roleCode";
    private static final String KEY_ROLE_NAME = "roleName";

    // offline ttk Columns names
    private static final String KEY_PROVINCE = "provinsi";
    private static final String KEY_CITY = "kota";

    private static final String KEY_KENDARAAN = "noKendaraan";
    private static final String KEY_SEAL = "seal";
    private static final String KEY_TUJUAN = "tujuan";

    private static final String KEY_KODEOPS = "kodeops";
    private static final String KEY_BIAYA = "biaya";
    private static final String KEY_TTKVENDOR = "ttkvendor";
    private static final String KEY_BATAL = "batal";

    private static final String CREATE_DATABASE = "create table URI_TABLE (_id integer primary key autoincrement," + "_uri text not null," + "_tipe text not null);";
    private static final String CREATE_DATABASE_TTK= "create table URI_TABLE_TTK (_id integer primary key autoincrement," + "_uri text not null," + "_tipe text not null," + "_ttk text not null);";
    private static final String DATABASE_TABLE_URI = "URI_TABLE";
    private static final String DATABASE_TABLE_URI_TTK = "URI_TABLE_TTK";

    public static final String ENTITY_ID = "_id";
    public static final String PATH_NAME = "_uri";
    public static final String PATH_TIPE = "_tipe";
    public static final String PATH_TTK = "_ttk";

    Activity context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_KOTA_TABLE = "CREATE TABLE " + TABLE_KOTA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_KOTA + " TEXT" + ")";
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_OFFICELAT + " DOUBLE,"
                + KEY_OFFICELONG + " DOUBLE," + KEY_MAINGROUP + " TEXT,"
                + KEY_SESSIONID + " TEXT," + KEY_CODE + " INTEGER,"
                + KEY_LASTLOGIN + " TEXT" + ")";
        String CREATE_AGENT_TABLE = "CREATE TABLE " + TABLE_AGENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AGENT + " TEXT" + ")";
        String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GROUP + " TEXT" + ")";

        String CREATE_KODESORTIR_TABLE = "CREATE TABLE " + TABLE_KODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_KODE + " TEXT,"
                + KEY_VERIFY + " INTEGER ," + KEY_NOVERIFY + " INTEGER,"
                + KEY_UNVERIFY + " INTEGER ," + KEY_DATEMIN + " TEXT,"
                + KEY_DATEMAX + " TEXT ," + KEY_JUMLAH + " INTEGER,"
                + KEY_STATUS + " TEXT," + KEY_INPUT + " TEXT" + ")";

        String CREATE_TTK_TABLE = "CREATE TABLE " + TABLE_TTK + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_STA + " INTEGER," + KEY_SORTIR + " TEXT" + ")";
        String CREATE_MANIFEST_TABLE = "CREATE TABLE " + TABLE_MANIFEST + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOMANIFEST + " TEXT,"
                + KEY_URL + " TEXT" + ")";
        String CREATE_TTK_TIDAK_TERKIRIM = "CREATE TABLE " + TABLE_TTKTIDAKTERKIRIM + "("
                + KEY_NOTTK + " TEXT PRIMARY KEY," + KEY_ALASAN + " TEXT,"
                + KEY_KET + " TEXT" + ")";
        String CREATE_SURATJALAN_TABLE = "CREATE TABLE " + TABLE_SURATJALAN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SJ + " TEXT,"
                + KEY_TGL + " DATE," + KEY_TOTALTTK + " INTEGER,"
                + KEY_TTKTERKIRIM + " INTEGER ," + KEY_TTKBELUMTERKIRIM + " INTEGER,"
                + KEY_TTKAKTIF + " INTEGER ,"+ KEY_TTKST + " INTEGER " + ")";
        String CREATE_TTKLIST_TABLE = "CREATE TABLE " + TABLE_LISTTTK + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_KOLI + " INTEGER," + KEY_BERAT + " INTEGER,"
                + KEY_STATUS + " TEXT," + KEY_TGL + " DATE" + ")";
        String CREATE_OFFLINETTK_TABLE = "CREATE TABLE " + TABLE_OFFLINETTK + "("
                + KEY_NOTTK + " TEXT PRIMARY KEY," + KEY_SESSIONID + " TEXT,"
                + KEY_REQUESTID + " TEXT ," + KEY_CODE + " TEXT,"
                + KEY_STATUSDATE + " TEXT ," + KEY_PACKAGESTATUS + " TEXT,"
                + KEY_REASON + " TEXT ," + KEY_RECEIVERNAME + " TEXT,"
                + KEY_RECEIVERTYPE + " TEXT ," + KEY_COMMENT + " TEXT,"
                + KEY_LATITUDE + " TEXT ," + KEY_LONGITUDE + " TEXT,"
                + KEY_STATUSTTK + " TEXT," + KEY_MESSAGESOAP + " TEXT" + ")";
        String CREATE_ORIGIN_TABLE = "CREATE TABLE " + TABLE_ORIGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PROVINCE + " TEXT,"
                + KEY_CITY + " TEXT" + ")";
        String CREATE_ROLE_TABLE = "CREATE TABLE " + TABLE_ROLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ROLE_CODE + " TEXT,"
                + KEY_ROLE_NAME + " TEXT" + ")";
        String CREATE_SJ_TABLE = "CREATE TABLE " + TABLE_SJ + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT" + ")";
        String CREATE_STMS_TABLE = "CREATE TABLE " + TABLE_STMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_STSM_TABLE = "CREATE TABLE " + TABLE_STSM + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_MS_TABLE = "CREATE TABLE " + TABLE_MS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_SM_TABLE = "CREATE TABLE " + TABLE_SM + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_SJP_TABLE = "CREATE TABLE " + TABLE_SJP + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_STBT_TABLE = "CREATE TABLE " + TABLE_STBT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_KENDARAAN_TABLE = "CREATE TABLE " + TABLE_KENDARAAN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_KENDARAAN + " TEXT,"
                + KEY_SEAL + " TEXT," + KEY_TUJUAN + " TEXT" + ")";
//        String CREATE_MA_TABLE = "CREATE TABLE " + TABLE_MA + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
//                + KEY_KENDARAAN + " TEXT," + KEY_TUJUAN + " TEXT" + ")";
        String CREATE_MA_TABLE = "CREATE TABLE " + TABLE_MA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT not null unique,"
                + KEY_TUJUAN + " TEXT" + ")";
        String CREATE_STK_TABLE = "CREATE TABLE " + TABLE_STK + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_DATASJP_TABLE = "CREATE TABLE " + TABLE_DATA_SJP + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_KODEOPS + " TEXT ," + KEY_BERAT + " TEXT,"
                + KEY_KOLI + " TEXT ," + KEY_BIAYA + " TEXT,"
                + KEY_TTKVENDOR + " TEXT ," + KEY_BATAL + " TEXT,"
                + KEY_ALASAN + " TEXT)";
        String CREATE_TTK_Pickup = "CREATE TABLE " + TABLE_DATA_Pickup + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_NOREF + " TEXT," + KEY_KODEAGEN + " TEXT,"+ KEY_KODEVERIFIKASI + " TEXT,"+ KEY_TGL + " DATETIME DEFAULT CURRENT_TIMESTAMP,"+ KEY_KODESORTIR + " TEXT)";
        String CREATE_TTK_SumberPickup = "CREATE TABLE " + TABLE_DATA_SumberPickup + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_NOREF + " TEXT," + KEY_KODESORTIR + " TEXT," + KEY_STATUS + " TEXT)";
        String CREATE_TTK_STPU = "CREATE TABLE " + TABLE_DATA_STPU + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_NOREF + " TEXT," + KEY_NIKSUPIR + " TEXT,"+ KEY_KODEVERIFIKASI + " TEXT," +KEY_KODEOPSTTK+ " TEXT,"+KEY_KODESORTIR+ " TEXT," + KEY_TGL + " DATETIME DEFAULT CURRENT_TIMESTAMP," + KEY_KODESORTIRSTPU + " TEXT)";
        String CREATE_TTK_SumberSTPU = "CREATE TABLE " + TABLE_DATA_SumberSTPU + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_NOREF + " TEXT," +KEY_KODEOPSTTK+ " TEXT,"+KEY_KODESORTIR+ " TEXT," + KEY_KODESORTIRSTPU + " TEXT )";
        String CREATE_KERANJANGTTKRETUR_TABLE = "CREATE TABLE " + TABLE_TTK_KERANJANG_RETUR + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT" + ")";
        String CREATE_DATA_STSM_TABLE = "CREATE TABLE " + TABLE_DATA_STSM + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOMA + " TEXT,"
                + KEY_NOMS + " TEXT)";
        String CREATE_DATA_SUMBER_STSM_TABLE = "CREATE TABLE " + TABLE_DATA_SumberSTSM + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOMS + " TEXT)";
        String CREATE_DATA_STMS_TABLE = "CREATE TABLE " + TABLE_DATA_STMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOMS + " TEXT,"
                + KEY_NOTTK + " TEXT)";
        String CREATE_DATA_SUMBER_STMS_TABLE = "CREATE TABLE " + TABLE_DATA_SumberSTMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";
        String CREATE_DATA_STPUMA_TABLE = "CREATE TABLE " + TABLE_DATA_STPUMA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_KODESORTIR + " TEXT," + KEY_NIKSUPIR + " TEXT,"+ KEY_TGL + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String CREATE_TTK_SumberSTPUMA_TABLE = "CREATE TABLE " + TABLE_DATA_SumberSTPUMA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT,"
                + KEY_KODESORTIR + " TEXT)";
        String CREATE_DATA_BONGKARMA_TABLE = "CREATE TABLE " + TABLE_DATA_BONGKARMA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT," + KEY_NIKSTMA + " TEXT," + KEY_NOMA + " TEXT,"
                + KEY_TGL + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String CREATE_TTK_SumberBONGKARMA_TABLE = "CREATE TABLE " + TABLE_DATA_SumberBONGKARMA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";

        String CREATE_DATA_STPU_BERAT_TABLE = "CREATE TABLE " + TABLE_DATA_StpuBerat + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT," + KEY_NIKPICKUP + " TEXT," + KEY_KODESORTIR + " TEXT," + KEY_BERAT + " TEXT,"
                + KEY_PANJANG + " TEXT," + KEY_LEBAR + " TEXT," + KEY_TINGGI + " TEXT,"
                + KEY_TGL + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String CREATE_TTK_SumberSTPU_BERAT_TABLE = "CREATE TABLE " + TABLE_DATA_SumberStpuBerat + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTTK + " TEXT)";


        db.execSQL(CREATE_KOTA_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_AGENT_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_KODESORTIR_TABLE);
        db.execSQL(CREATE_TTK_TABLE);
        db.execSQL(CREATE_MANIFEST_TABLE);
        db.execSQL(CREATE_TTK_TIDAK_TERKIRIM);
        db.execSQL(CREATE_SURATJALAN_TABLE);
        db.execSQL(CREATE_TTKLIST_TABLE);
        db.execSQL(CREATE_OFFLINETTK_TABLE);
        db.execSQL(CREATE_ORIGIN_TABLE);
        db.execSQL(CREATE_ROLE_TABLE);
        db.execSQL(CREATE_SJ_TABLE);
        db.execSQL(CREATE_STMS_TABLE);
        db.execSQL(CREATE_STSM_TABLE);
        db.execSQL(CREATE_MS_TABLE);
        db.execSQL(CREATE_SM_TABLE);
        db.execSQL(CREATE_SJP_TABLE);
        db.execSQL(CREATE_STBT_TABLE);
        db.execSQL(CREATE_KENDARAAN_TABLE);
        db.execSQL(CREATE_MA_TABLE);
        db.execSQL(CREATE_STK_TABLE);
        db.execSQL(CREATE_DATASJP_TABLE);
        db.execSQL(CREATE_DATABASE);
        db.execSQL(CREATE_DATABASE_TTK);
        db.execSQL(CREATE_TTK_Pickup);
        db.execSQL(CREATE_TTK_SumberPickup);
        db.execSQL(CREATE_TTK_STPU);
        db.execSQL(CREATE_TTK_SumberSTPU);
        db.execSQL(CREATE_KERANJANGTTKRETUR_TABLE);
        db.execSQL(CREATE_DATA_STSM_TABLE);
        db.execSQL(CREATE_DATA_SUMBER_STSM_TABLE);
        db.execSQL(CREATE_DATA_STMS_TABLE);
        db.execSQL(CREATE_DATA_SUMBER_STMS_TABLE);
        db.execSQL(CREATE_DATA_STPUMA_TABLE);
        db.execSQL(CREATE_TTK_SumberSTPUMA_TABLE);
        db.execSQL(CREATE_DATA_BONGKARMA_TABLE);
        db.execSQL(CREATE_TTK_SumberBONGKARMA_TABLE);
        db.execSQL(CREATE_DATA_STPU_BERAT_TABLE);
        db.execSQL(CREATE_TTK_SumberSTPU_BERAT_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("Database Version: OLD: ",""+ oldVersion + " = NEW: "+newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KOTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TTK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANIFEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TTKTIDAKTERKIRIM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURATJALAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTTTK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINETTK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORIGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SJ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STSM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SJP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STBT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KENDARAAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SJP);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_URI);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_URI_TTK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_Pickup);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberPickup);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_STPU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberSTPU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TTK_KERANJANG_RETUR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_STSM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberSTSM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_STMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberSTMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_STPUMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberSTPUMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_BONGKARMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberBONGKARMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_StpuBerat);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_SumberStpuBerat);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addAgent(AgentCode agentCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AGENT, agentCode.getAgentCode());

        // Inserting Row
        db.insert(TABLE_AGENT, null, values);
        db.close(); // Closing database connection
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_NAME, user.getName());
//        values.put(KEY_MAINGROUP, user.getMaingroup());
        values.put(KEY_SESSIONID, user.getSessionid());
        values.put(KEY_CODE, user.getEmployeecode());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addKota(KotaAsal kota) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KOTA, kota.getKotaAsal());

        // Inserting Row
        db.insert(TABLE_KOTA, null, values);
        db.close(); // Closing database connection
    }

    public void addGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP, group.getGroup());
        // Inserting Row
        db.insert(TABLE_GROUP, null, values);
        db.close(); // Closing database connection
    }

    public void addKodeSortir(KodeSortir kode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KODE, kode.getKodesortir());
        values.put(KEY_VERIFY, kode.getVerify());
        values.put(KEY_NOVERIFY, kode.getNoverify());
        values.put(KEY_UNVERIFY, kode.getUnverify());
        values.put(KEY_DATEMIN, kode.getDatemin());
        values.put(KEY_DATEMAX, kode.getDatemax());
        values.put(KEY_JUMLAH, kode.getJumlahttk());
        values.put(KEY_STATUS, kode.getStatus());
        values.put(KEY_INPUT, kode.getInput());

        // Inserting Row
        db.insert(TABLE_KODE, null, values);
        db.close(); // Closing database connection
    }

    public void addTTK(TTk ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, ttk.getNottk());
        values.put(KEY_STA, ttk.getStatus());
        values.put(KEY_SORTIR, ttk.getKodesortir());

        // Inserting Row
        db.insert(TABLE_TTK, null, values);
        db.close(); // Closing database connection
    }

    public void addManifest(Manifest manifest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOMANIFEST, manifest.getNomanifest());
        values.put(KEY_URL, manifest.getURL());

        // Inserting Row
        db.insert(TABLE_MANIFEST, null, values);
        db.close(); // Closing database connection
    }

    public void addTtkTidakTerkirim(TtkTidakTerkirim ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, ttk.getNoTtk());
        values.put(KEY_ALASAN, ttk.getAlasan());
        values.put(KEY_KET, ttk.getKeterangan());

        // Inserting Row
        db.insert(TABLE_TTKTIDAKTERKIRIM, null, values);
        db.close(); // Closing database connection
    }

    public void addSJ(SuratJalan sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SJ, sj.getSJ());
        values.put(KEY_TGL, sj.getTgl());
        values.put(KEY_TOTALTTK, sj.getTotal());
        values.put(KEY_TTKTERKIRIM, sj.getTerkirim());
        values.put(KEY_TTKBELUMTERKIRIM, sj.getBelum());
        values.put(KEY_TTKAKTIF, sj.getAktif());
        values.put(KEY_TTKST, sj.getSerah());

        // Inserting Row
        db.insert(TABLE_SURATJALAN, null, values);
        db.close(); // Closing database connection
    }

    public void addTTKList(ListTTK ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, ttk.getNoTTK());
        values.put(KEY_KOLI, ttk.getKoli());
        values.put(KEY_BERAT, ttk.getBerat());
        values.put(KEY_STATUS, ttk.getStatus());
        values.put(KEY_TGL, ttk.getTgl());
        // Inserting Row
        db.insert(TABLE_LISTTTK, null, values);
        db.close(); // Closing database connection
    }

    public void addOrigin(Origin origin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, origin.getId());
        values.put(KEY_PROVINCE, origin.getProvince());
        values.put(KEY_CITY, origin.getCity());
        // Inserting Row
        db.insert(TABLE_ORIGIN, null, values);
        db.close(); // Closing database connection
    }

    public void addRole(Role role) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROLE_CODE, role.getCode());
        values.put(KEY_ROLE_NAME, role.getName());
        // Inserting Row
        db.insert(TABLE_ROLE, null, values);
        db.close(); // Closing database connection
    }

    public void addBuatSJ(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_SJ, null, values);
        db.close(); // Closing database connection
    }

//    public void addSTMS(BuatSJ sj) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NOTTK, sj.getTtk());
//        // Inserting Row
//        db.insert(TABLE_STMS, null, values);
//        db.close(); // Closing database connection
//    }

    public void addSTK(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_STK, null, values);
        db.close(); // Closing database connection
    }


//    public void addSTSM(BuatSJ sj) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NOTTK, sj.getTtk());
//        // Inserting Row
//        db.insert(TABLE_STSM, null, values);
//        db.close(); // Closing database connection
//    }

    public void addMS(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_MS, null, values);
        db.close(); // Closing database connection
    }

    public void addSM(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_SM, null, values);
        db.close(); // Closing database connection
    }

    public void addSJP(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_SJP, null, values);
        db.close(); // Closing database connection
    }

    public void addTTKreturKeranjang(TtkKeranjangRetur sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_TTK_KERANJANG_RETUR, null, values);
        db.close(); // Closing database connection
    }

    public void addSTBT(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_STBT, null, values);
        db.close(); // Closing database connection
    }

    public void addMA(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
//        values.put(KEY_KENDARAAN, sj.getId());
        values.put(KEY_TUJUAN, sj.getTujuan());
        // Inserting Row
        db.insertOrThrow(TABLE_MA, null, values);
        db.close(); // Closing database connection
    }

    public void addDataSJP(DataSJP sjp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, sjp.getId());
        values.put(KEY_NOTTK, sjp.getTtk());
        values.put(KEY_KODEOPS, sjp.getKodeops());
        values.put(KEY_BERAT, sjp.getBerat());
        values.put(KEY_KOLI, sjp.getKoli());
        values.put(KEY_BIAYA, sjp.getBiaya());
        values.put(KEY_TTKVENDOR, sjp.getTtkVendor());
        values.put(KEY_BATAL, sjp.getBatal());
        values.put(KEY_ALASAN, sjp.getAlasan());
        // Inserting Row
        db.insert(TABLE_DATA_SJP, null, values);
        db.close(); // Closing database connection
    }

    public void addKendaraan(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KENDARAAN, sj.getTtk());
        values.put(KEY_SEAL, sj.getSeal());
        values.put(KEY_TUJUAN, sj.getTujuan());
        // Inserting Row
        db.insert(TABLE_KENDARAAN, null, values);
        db.close(); // Closing database connection
    }

    public void addOfflineTTK(OfflineTTK ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, ttk.getTTK());
        values.put(KEY_SESSIONID, ttk.getSessionid());
        values.put(KEY_REQUESTID, ttk.getRequestid());
        values.put(KEY_CODE, ttk.getEmployeecode());
        values.put(KEY_STATUSDATE, ttk.getStatusdate());
        values.put(KEY_PACKAGESTATUS, ttk.getPackagestatus());
        values.put(KEY_REASON, ttk.getReason());
        values.put(KEY_RECEIVERNAME, ttk.getReceivername());
        values.put(KEY_RECEIVERTYPE, ttk.getReceivertype());
        values.put(KEY_COMMENT, ttk.getComment());
        values.put(KEY_LATITUDE, ttk.getLatitude());
        values.put(KEY_LONGITUDE, ttk.getLongitude());
        values.put(KEY_STATUSTTK, ttk.getStatusTTK());
        values.put(KEY_MESSAGESOAP, ttk.getMessageSoap());
        // Inserting Row
        db.insert(TABLE_OFFLINETTK, null, values);
        db.close(); // Closing database connection
    }



    //PICKUP


    public List<ListTtkPickup> getAllSumberPickup() {
        List<ListTtkPickup> kodeList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_SumberPickup;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup kode = new ListTtkPickup();
                kode.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                kode.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                kodeList.add(kode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return kodeList;
    }



    public void addTTKPickup(ListTtkPickup ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            //values.put(KEY_NOREF, ttk.getNoref());
            values.put(KEY_KODEAGEN, ttk.getKodeagen());
            values.put(KEY_KODEVERIFIKASI, ttk.getKodeverifikasi());
            values.put(KEY_TGL, ttk.getTgl());
            values.put(KEY_KODESORTIR, ttk.getKodesortir());

            // Inserting Row
            db.insert(TABLE_DATA_Pickup, null, values);
            db.close(); // Closing database connection

        }catch (Exception e){
            System.out.println("Exceptions " +e);
        }

//        ContentValues values = new ContentValues();
//        values.put(KEY_NOTTK, ttk.getTtk());
//        //values.put(KEY_NOREF, ttk.getNoref());
//        values.put(KEY_KODEAGEN, ttk.getKodeagen());
//        values.put(KEY_KODEVERIFIKASI, ttk.getKodeverifikasi());
//        values.put(KEY_TGL, ttk.getTgl());
//        values.put(KEY_KODESORTIR, ttk.getKodesortir());
//
//        // Inserting Row
//        db.insert(TABLE_DATA_Pickup, null, values);
//        db.close(); // Closing database connection
    }


    public List<ListTtkPickup> getAllDataPickup(String kodeagen,String kodeverifikasi,String kodesortir) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        try {

            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        ListTtkPickup sj = new ListTtkPickup();
                        sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                        sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                        sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
                        sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                        sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                        sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                        ttkkList.add(sj);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        }catch (SQLiteException e){
            Log.d("error1", e.getMessage());
        }

//        if (cursor.moveToFirst()) {
//            do {
//                ListTtkPickup sj = new ListTtkPickup();
//                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
//                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
//                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
//                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//                ttkkList.add(sj);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
        return ttkkList;
    }


    public List<ListTtkPickup> getAllDataPickupAll(String kodeagen,String kodeverifikasi) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"'  ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        try {

            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        ListTtkPickup sj = new ListTtkPickup();
                        sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                        sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                        sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
                        sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                        sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                        sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                        ttkkList.add(sj);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        }catch (SQLiteException e){
            Log.d("error1", e.getMessage());
        }

//        if (cursor.moveToFirst()) {
//            do {
//                ListTtkPickup sj = new ListTtkPickup();
//                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
//                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
//                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
//                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//                ttkkList.add(sj);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
        return ttkkList;
    }



    public List<ListTtkPickup> getAllDataPickupPreview(String kodeagen,String kodeverifikasi,String kodesortir) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);




        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<ListTtkPickup> getAllDataPickupPreviewAll(String kodeagen,String kodeverifikasi) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"'  ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);




        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }



    public List<ListTtkPickup> getAllDataPickupPreviewTTKtidakditemukan() {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberPickup+" t1 LEFT JOIN "+TABLE_DATA_Pickup+" t2 on t2.nottk=t1.nottk where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setKodeagen(cursor.getString(cursor.getColumnIndex(KEY_KODEAGEN)));
//                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }


    public int countListPUnotFound(){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberPickup+" t1 LEFT JOIN "+TABLE_DATA_Pickup+" t2 on t2.nottk=t1.nottk where t2.id is null";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }





    public int  checkListPickup(String ttk,String kodeagen,String kodeverifikasi,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }





//        try{
//            cursor.moveToFirst();
//            int icount = cursor.getInt(0);
//            if(icount>0){
//                return 1;
//            }else{
//                return 0;
//            }
//
//        }catch (Exception e){
//
//        }finally{
//            cursor.close();
//        }

//        cursor.moveToFirst();
//        int icount = cursor.getInt(0);
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
//
//        cursor.close();

        return jumlah;
    }


    public int  checkListPickupAll(String ttk,String kodeagen,String kodeverifikasi){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }





//        try{
//            cursor.moveToFirst();
//            int icount = cursor.getInt(0);
//            if(icount>0){
//                return 1;
//            }else{
//                return 0;
//            }
//
//        }catch (Exception e){
//
//        }finally{
//            cursor.close();
//        }

//        cursor.moveToFirst();
//        int icount = cursor.getInt(0);
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
//
//        cursor.close();

        return jumlah;
    }


    public void deleteListPickup(String kodeagen,String kodeverifikasi,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_Pickup +" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ");
        db.close();
    }

    public void deleteListPickupAll(String kodeagen,String kodeverifikasi){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_Pickup +" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' ");
        db.close();
    }


    public int deleteTTKListPickup(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_Pickup, KEY_NOTTK + " = '"+ttk+"'",null);
    }


    public int checkListPickupHistory(String kodeagen,String kodeverifikasi,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }


    public int checkListPickupHistoryAll(String kodeagen,String kodeverifikasi){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }


    public ListTtkPickup getTTKhistoryMax(String kodeagen,String kodeverifikasi,String kodesortir) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }

    public ListTtkPickup getTTKhistoryMaxAll(String kodeagen,String kodeverifikasi) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }


    public String getKodeSortir(String ttk) {

        String selectQuery = "SELECT  "+KEY_KODESORTIR+" FROM "+TABLE_DATA_SumberPickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"' order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

//        ListTtkPickup kodesortir = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));

        String kodesortir = cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR));

        cursor.close();
        return kodesortir;
    }


    public int countPUlistScan(String kodeagen,String kodeverifikasi,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);

        return icount;

    }

    public int countPUlistScanAll(String kodeagen,String kodeverifikasi){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_Pickup+" WHERE "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);

        return icount;

    }


    public ArrayList<String> getAllTTKPUfix(String kodeagen,String kodeverifikasi ,String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);


        try{
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }catch (Exception e){
            System.out.println("Exceptions " +e);
        }

        return myTTK;

//        if (cursor.moveToFirst()) {
//            do {
//                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return myTTK;
    }


    public ArrayList<String> getAllTTKPUfixAll(String kodeagen,String kodeverifikasi) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_Pickup+" where "+KEY_KODEAGEN+" = '"+kodeagen+"' and "+KEY_KODEVERIFIKASI+" = '"+kodeverifikasi+"'  ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);


        try{
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }catch (Exception e){
            System.out.println("Exceptions " +e);
        }

        return myTTK;

//        if (cursor.moveToFirst()) {
//            do {
//                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return myTTK;
    }

    public ArrayList<String> getAllTtkPUnotFoundFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberPickup+" t1 LEFT JOIN "+TABLE_DATA_Pickup+" t2 on t2.nottk=t1.nottk where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);


        try {
            if (cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }catch (Exception e){
            System.out.println("Exceptions " +e);
        }

//        if (cursor.moveToFirst()) {
//            do {
//                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
        return myTTK;
    }



    //STSM
    public int checkListSTSMHistory(String noMa){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMA+" = '"+noMa+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public ListMsSTSM getMshistoryMax(String noMa) {

        String selectQuery = "SELECT  "+KEY_NOMS+" FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMA+" = '"+noMa+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListMsSTSM noMs = new ListMsSTSM(cursor.getString(cursor.getColumnIndex(KEY_NOMS)));

        cursor.close();
        return noMs;
    }

    public int countSTSMlistScan(String noMa){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMA+" = '"+noMa+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);

        return icount;

    }

    public void deleteListSTSM(String noMa){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_STSM +" where "+KEY_NOMA+" = '"+noMa+"' ");
        db.close();
    }

    public int deleteMsSTSM(String ms){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_STSM, KEY_NOMS + " = '"+ms+"'",null);
    }


    public int countSTSMSumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_SumberSTSM+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));


        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();
            cursor.close();
            return icount;
        }else{
            return 0;
        }

    }

    public int checkSTSMV2(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMS+" = '"+noMs+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public void addSTSMV2(ListMsSTSM ms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOMS, ms.getNoMs());
        values.put(KEY_NOMA, ms.getNoMa());
        // Inserting Row
        db.insert(TABLE_DATA_STSM, null, values);
        db.close(); // Closing database connection
    }

    public void addSTSM(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_STSM, null, values);
        db.close(); // Closing database connection
    }

    public List<BuatSJ> getAllTtkSTSMV2(String noMa) {
        List<BuatSJ> MsList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMA+" = '"+noMa.toUpperCase()+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ ms = new BuatSJ();
                ms.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOMS)));
                MsList.add(ms);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return MsList;
    }

        public void deleteSTSM(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_STSM);
        db.close();
    }

    public int checkListSTSMSumber(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTSM+" WHERE "+KEY_NOMS+" = '"+noMs+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor)+" "+noMs);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public ArrayList<String> getAllMsSTSMFix(String noMa) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STSM+" WHERE "+KEY_NOMA+" = '"+noMa+"' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOMS)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllMsSTSMnotFoundFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTSM+" t1 LEFT JOIN "+TABLE_DATA_STSM+" t2 on t2.noMs=t1.noMs where t2.id is null";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOMS)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }





    public int countListSTSMnotFound(){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTSM+" t1 LEFT JOIN "+TABLE_DATA_STSM+" t2 on t2.noMs=t1.noMs where t2.id is null";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }

    public List<ListTtkPickup> getAllDataPickupPreviewMStidakditemukan() {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTSM+" t1 LEFT JOIN "+TABLE_DATA_STSM+" t2 on t2.noMs=t1.noMs where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOMS)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }





    ///////////------------END---------------///////



    public int checkSTMS(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_STMS+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public void addSTMS(BuatSJ sj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTTK, sj.getTtk());
        // Inserting Row
        db.insert(TABLE_STMS, null, values);
        db.close(); // Closing database connection
    }


    ////////////////--------------------STMSV2--------------------////////

    public void deleteListSumberSTMS(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberSTMS);
        db.close();
    }

    public int checkListSTMSHistory(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public ListTtkSTMS getTtkSTMShistoryMax(String noMs) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkSTMS noTtk = new ListTtkSTMS(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return noTtk;
    }

    public int countSTMSlistScan(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);

        return icount;

    }

    public void deleteListSTMS(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_STMS +" where "+KEY_NOMS+" = '"+noMs+"' ");
        db.close();
    }

    public List<BuatSJ> getAllTtkSTMSScan(String noMs) {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public int countSTMSSumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_SumberSTMS+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));


        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();
            cursor.close();
            return icount;
        }else{
            return 0;
        }

    }

    public int checkSTMSScan(String ttk,String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOTTK+" = '"+ttk+"' AND  "+KEY_NOMS+" = '"+noMs+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkListSTMSSumber(String noTtk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTMS+" WHERE "+KEY_NOTTK+" = '"+noTtk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor)+" "+noTtk);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public List<BuatSJ> getAllTtkSTMSV2(String noMs) {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"'  ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public void addSTMSScan(ListTtkSTMS ttk) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOMS, ttk.getNoMs());
        values.put(KEY_NOTTK, ttk.getNoTtk());
        // Inserting Row
        db.insert(TABLE_DATA_STMS, null, values);
        db.close(); // Closing database connection
    }

    public int deleteTTKSTMS(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_STMS, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int countListSTMSnotFound(String noMs){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTMS+" t1 LEFT JOIN "+TABLE_DATA_STMS+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NOMS+" = '"+noMs+"' where t2.id is null";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }

    public List<ListTtkPickup> getAllDataPreviewTTKSTMStidakditemukan(String noMs) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTMS+" t1 LEFT JOIN "+TABLE_DATA_STMS+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NOMS+" = '"+noMs+"' where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public ArrayList<String> getAllTtkSTMSFixScan(String noMs) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STMS+" WHERE "+KEY_NOMS+" = '"+noMs+"' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTMSnotFoundFix(String noMs) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTMS+" t1 LEFT JOIN "+TABLE_DATA_STMS+" t2 on t2.nottk=t1.nottk AND t2.noMs = '"+noMs+"' where t2.id is null";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }


    ////////////////---------------------END--------------------////////


    ////BONGKAR MA

    public List<ListTtkBongkarMA> getAllDataBONGKARMA(String nikSTMA,String noMa) {
        List<ListTtkBongkarMA> ttkkList = new ArrayList<ListTtkBongkarMA>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_BONGKARMA+" WHERE "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkBongkarMA sj = new ListTtkBongkarMA();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public int checkListBONGKARMA(String ttk,String nikSTMA,String noMa){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_BONGKARMA+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;

    }

    public int checkListBONGKARMASumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberBONGKARMA+" WHERE "+KEY_NOTTK+" = '"+ttk+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;
    }

    public int countBONGKARMASumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_DATA_SumberBONGKARMA+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

//        if(cursor!=null)

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();

            cursor.close();
            return icount;
        }else{
            return 0;
        }


    }


    public List<ListTtkBongkarMA> getAllDataBONGKARMAPreview(String nikSTMA,String noMa) {
        List<ListTtkBongkarMA> ttkkList = new ArrayList<ListTtkBongkarMA>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_BONGKARMA+" WHERE "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkBongkarMA sj = new ListTtkBongkarMA();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public ArrayList<String> getAllTTKBONGKARMAfix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_BONGKARMA+"  ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }





    public void addTTKBONGKARMA(ListTtkBongkarMA ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            values.put(KEY_NIKSTMA, ttk.getNikSTMA());
            values.put(KEY_NOMA, ttk.getNoMa());
            values.put(KEY_TGL, ttk.getTgl());

            // Inserting Row
            db.insert(TABLE_DATA_BONGKARMA, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public int checkListBONGKARMAHistory(String nikSTMA,String noMa){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_BONGKARMA+" WHERE "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int countBONGKARMAlistScan(String nikSTMA,String noMa){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_BONGKARMA+" WHERE "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }

    public ListTtkBongkarMA getTTKhistoryMaxBONGKARMA(String nikSTMA,String noMa) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_BONGKARMA+" where "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkBongkarMA nottk = new ListTtkBongkarMA(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }

    public void deleteListBONGKARMA(String nikSTMA,String noMa){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_BONGKARMA+" where "+KEY_NIKSTMA+" = '"+nikSTMA+"' and "+KEY_NOMA+" = '"+noMa+"' ");
        db.close();
    }


    public int deleteTTKListBONGKARMA(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_BONGKARMA, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public void deleteListSumberBONGKARMA(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberBONGKARMA);
        db.close();
    }





    ////////////////---------------------END--------------------////////



    /////STPU DIRECT MA

    public List<ListTtkSTPUMA> getAllDataSTPUMA(String niksupir, String kodesortir) {
        List<ListTtkSTPUMA> ttkkList = new ArrayList<ListTtkSTPUMA>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPUMA+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkSTPUMA sj = new ListTtkSTPUMA();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public int checkListSTPUMA(String ttk,String niksupir,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPUMA+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;

    }

    public ListTtkSTPUMA getDataSumberSTPUMA(String ttk) {

        String selectQuery = "SELECT  * FROM " + TABLE_DATA_STPUMA + " WHERE " + KEY_NOTTK + " = '" + ttk + "' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        ListTtkSTPUMA nottk = new ListTtkSTPUMA();

        try {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    nottk = new ListTtkSTPUMA(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                } while (cursor.moveToNext());
            }



        }catch (RuntimeException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        return nottk;

    }


    public void addTTKSTPUMA(ListTtkSTPUMA ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            values.put(KEY_NIKSUPIR, ttk.getNiksupir());
            values.put(KEY_KODESORTIR, ttk.getKodesortir());
            values.put(KEY_TGL, ttk.getTgl());

            // Inserting Row
            db.insert(TABLE_DATA_STPUMA, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public void addTTKSTPUMAsumber(ListTtkSTPUMA ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            values.put(KEY_KODESORTIR, ttk.getKodesortir());

            // Inserting Row
            db.insert(TABLE_DATA_SumberSTPUMA, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public int countSTPUMASumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_DATA_SumberSTPUMA+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

//        if(cursor!=null)

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();

            cursor.close();
            return icount;
        }else{
            return 0;
        }


    }

    public List<ListTtkSTPUMA> getAllDataSTPUMAPreview(String niksupir,String kodesortir) {
        List<ListTtkSTPUMA> ttkkList = new ArrayList<ListTtkSTPUMA>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPUMA+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkSTPUMA sj = new ListTtkSTPUMA();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public int checkListSTPUMASumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTPUMA+" WHERE "+KEY_NOTTK+" = '"+ttk+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;
    }


    public int countListSTPUMAnotFound(String niksupir,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTPUMA+" t1 LEFT JOIN "+TABLE_DATA_STPUMA+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NIKSUPIR+" = '"+niksupir+"' AND t2."+KEY_KODESORTIR+" = '"+kodesortir+"' where t2.id is null ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }

    public ArrayList<ListTtkPickup> getAllDataSTPUMAnotFound(String niksupir,String kodesortir) {
        ArrayList<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTPUMA+" t1 LEFT JOIN "+TABLE_DATA_STPUMA+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NIKSUPIR+" = '"+niksupir+"' AND t2."+KEY_KODESORTIR+" = '"+kodesortir+"'  where t2.id is null  ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public ArrayList<String> getAllTTKSTPUMAfix(String niksupir, String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPUMA+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' AND "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTPUMAnotFoundFix(String niksupir, String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTPUMA+" t1 LEFT JOIN "+TABLE_DATA_STPUMA+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NIKSUPIR+" = '"+niksupir+"' AND t2."+KEY_KODESORTIR+" = '"+kodesortir+"'  where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public int checkListSTPUMAHistory(String niksupir,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPUMA+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int countSTPUMAlistScan(String niksupir,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPUMA+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }

    public ListTtkPickup getTTKhistoryMaxSTPUMA(String niksupir,String kodesortir) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_STPUMA+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }


    public void deleteListSTPUMA(String niksupir,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_STPUMA+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ");
        db.close();
    }

    public int deleteTTKListSTPUMA(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_STPUMA, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public void deleteListSumberSTPUMA(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberSTPUMA);
        db.close();
    }



////----------END--------



    //STPU

    public void addTTKSTPU(ListTtkPickup ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            //values.put(KEY_NOREF, ttk.getNoref());
            values.put(KEY_NIKSUPIR, ttk.getNiksupir());
//        values.put(KEY_KODEVERIFIKASI, ttk.getKodeverifikasi());
            values.put(KEY_KODEOPSTTK, ttk.getKodeopsttk());
            values.put(KEY_KODESORTIR, ttk.getKodesortir());
            values.put(KEY_TGL, ttk.getTgl());
            values.put(KEY_KODESORTIRSTPU, ttk.getKodesortirstpu());

            // Inserting Row
            db.insert(TABLE_DATA_STPU, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public List<ListTtkPickup> getAllDataSTPU(String niksupir,String kodesortirstpu) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPU+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"' ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodeopsttk(cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortirstpu(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIRSTPU)));
//                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<ListTtkPickup> getAllDataSTPUPreview(String niksupir,String kodesortirstpu) {
        List<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPU+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodeopsttk(cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortirstpu(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIRSTPU)));
//                sj.setKodeverifikasi(cursor.getString(cursor.getColumnIndex(KEY_KODEVERIFIKASI)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }


    public ArrayList<ListTtkPickup> getAllDataSTPUnotFound() {
        ArrayList<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTPU+" t1 LEFT JOIN "+TABLE_DATA_STPU+" t2 on t2.nottk=t1.nottk where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
                sj.setKodeopsttk(cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setKodesortirstpu(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIRSTPU)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }


    public int countListSTPUnotFound(){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTPU+" t1 LEFT JOIN "+TABLE_DATA_STPU+" t2 on t2.nottk=t1.nottk where t2.id is null";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }



    public int checkListSTPU(String ttk,String niksupir,String kodesortirstpu){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPU+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;

    }


    public void deleteListSTPU(String niksupir,String kodesortirstpu){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_STPU+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"' ");
        db.close();
    }


    public int deleteTTKListSTPU(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_STPU, KEY_NOTTK + " = '"+ttk+"'",null);
    }


    public int checkListSTPUHistory(String niksupir,String kodesortirstpu){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPU+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"'  ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }


    public ListTtkPickup getTTKhistoryMaxSTPU(String niksupir,String kodesortirstpu) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_STPU+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }


    public int countSTPUlistScan(String niksupir,String kodesortirstpu){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_STPU+" WHERE "+KEY_NIKSUPIR+" = '"+niksupir+"' and "+KEY_KODESORTIRSTPU+" = '"+kodesortirstpu+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }


    public ArrayList<String> getAllTTKSTPUfix(String niksupir, String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_STPU+" where "+KEY_NIKSUPIR+" = '"+niksupir+"' AND "+KEY_KODESORTIRSTPU+" = '"+kodesortir+"' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }


    public ArrayList<String> getAllTtkSTPUnotFoundFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberSTPU+" t1 LEFT JOIN "+TABLE_DATA_STPU+" t2 on t2.nottk=t1.nottk where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    ////------------END--------------///



    ///-------------STPU BERAT---------------///


    public void deleteListSumberSTPUBerat(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberStpuBerat);
        db.close();
    }


    public int checkListSTPUBERATHistory(String nikpickup,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int countSTPUBERATlistScan(String nikpickup,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

    }

    public ListTtkSTPUBERAT getTTKhistoryMaxSTPUBERAT(String nikpickup,String kodesortir) {

        String selectQuery = "SELECT  "+KEY_NOTTK+" FROM "+TABLE_DATA_StpuBerat+" where "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"'  order by "+KEY_ID+" desc limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        ListTtkSTPUBERAT nottk = new ListTtkSTPUBERAT(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

        cursor.close();
        return nottk;
    }

    public void deleteListSTPUBERAT(String nikpickup,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_StpuBerat+" where "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ");
        db.close();
    }

    public int checkListSTPUBERAT(String ttk,String nikpickup,String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NOTTK+" = '"+ttk+"' and "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;

    }

    public int checkListSTPUBERATSumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberStpuBerat+" WHERE "+KEY_NOTTK+" = '"+ttk+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;
    }

    public void addTTKSTPUBERAT(ListTtkSTPUBERAT ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());
            values.put(KEY_NIKPICKUP, ttk.getNikpickup());
            values.put(KEY_KODESORTIR, ttk.getKodesortir());
            values.put(KEY_BERAT, ttk.getBerat());
            values.put(KEY_PANJANG, ttk.getPanjang());
            values.put(KEY_LEBAR, ttk.getLebar());
            values.put(KEY_TINGGI, ttk.getTinggi());
            values.put(KEY_TGL, ttk.getTgl());

            // Inserting Row
            db.insert(TABLE_DATA_StpuBerat, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public void addTTKSumberSTPUBERAT(ListTtkSTPUBERAT ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTTK, ttk.getTtk());

            // Inserting Row
            db.insert(TABLE_DATA_SumberStpuBerat, null, values);
            db.close(); // Closing database connection

        }catch (Exception e) {
            System.out.println("Exceptions " + e);
        }
    }

    public int countSTPUBERATSumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_DATA_SumberStpuBerat+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);


        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();

            cursor.close();
            return icount;
        }else{
            return 0;
        }


    }


    public int countListSTPUBERATnotFound(String nikpickup, String kodesortir){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberStpuBerat+" t1 LEFT JOIN "+TABLE_DATA_StpuBerat+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NIKPICKUP+" = '"+nikpickup+"' AND t2."+KEY_KODESORTIR+" = '"+kodesortir+"' where t2.id is null ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);

        return icount;

//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }

    public ArrayList<ListTtkPickup> getAllDataSTPUBERATnotFound(String nikpickup,String kodesortir) {
        ArrayList<ListTtkPickup> ttkkList = new ArrayList<ListTtkPickup>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberStpuBerat+" t1 LEFT JOIN "+TABLE_DATA_StpuBerat+" t2 on t2.nottk=t1.nottk AND t2."+KEY_NIKPICKUP+" = '"+nikpickup+"' AND t2."+KEY_KODESORTIR+" = '"+kodesortir+"'  where t2.id is null  ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkPickup sj = new ListTtkPickup();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
//                sj.setNiksupir(cursor.getString(cursor.getColumnIndex(KEY_NIKSUPIR)));
//                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }


    public List<ListTtkSTPUBERAT> getAllDataSTPUBERAT(String nikpickup, String kodesortir) {
        List<ListTtkSTPUBERAT> ttkkList = new ArrayList<ListTtkSTPUBERAT>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNikpickup(cursor.getString(cursor.getColumnIndex(KEY_NIKPICKUP)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setBerat(cursor.getString(cursor.getColumnIndex(KEY_BERAT)));
                sj.setPanjang(cursor.getString(cursor.getColumnIndex(KEY_PANJANG)));
                sj.setLebar(cursor.getString(cursor.getColumnIndex(KEY_LEBAR)));
                sj.setTinggi(cursor.getString(cursor.getColumnIndex(KEY_TINGGI)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<ListTtkSTPUBERAT> getAllDataSTPUBERATPreview(String nikpickup, String kodesortir) {
        List<ListTtkSTPUBERAT> ttkkList = new ArrayList<ListTtkSTPUBERAT>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNikpickup(cursor.getString(cursor.getColumnIndex(KEY_NIKPICKUP)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setBerat(cursor.getString(cursor.getColumnIndex(KEY_BERAT)));
                sj.setPanjang(cursor.getString(cursor.getColumnIndex(KEY_PANJANG)));
                sj.setLebar(cursor.getString(cursor.getColumnIndex(KEY_LEBAR)));
                sj.setTinggi(cursor.getString(cursor.getColumnIndex(KEY_TINGGI)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public ArrayList<String> getAllTTKSTPUBERATfix(String nikpickup, String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_StpuBerat+" where "+KEY_NIKPICKUP+" = '"+nikpickup+"' AND "+KEY_KODESORTIR+" = '"+kodesortir+"' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }


    public List<ListTtkSTPUBERAT> getAllDataSTPUBERATfixwithDimensi(String nikpickup, String kodesortir) {
        List<ListTtkSTPUBERAT> ttkkList = new ArrayList<ListTtkSTPUBERAT>();

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_StpuBerat+" WHERE "+KEY_NIKPICKUP+" = '"+nikpickup+"' and "+KEY_KODESORTIR+" = '"+kodesortir+"' ORDER BY "+KEY_ID+"";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                sj.setNikpickup(cursor.getString(cursor.getColumnIndex(KEY_NIKPICKUP)));
                sj.setKodesortir(cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                sj.setBerat(cursor.getString(cursor.getColumnIndex(KEY_BERAT)));
                sj.setPanjang(cursor.getString(cursor.getColumnIndex(KEY_PANJANG)));
                sj.setLebar(cursor.getString(cursor.getColumnIndex(KEY_LEBAR)));
                sj.setTinggi(cursor.getString(cursor.getColumnIndex(KEY_TINGGI)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }


    public ArrayList<String> getAllTtkSTPUBERATnotFoundFix(String nikpickup,String kodesortir) {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  t1.* FROM "+TABLE_DATA_SumberStpuBerat+" t1 LEFT JOIN "+TABLE_DATA_StpuBerat+" t2 on t2.nottk=t1.nottk and t2."+KEY_NIKPICKUP+" = '"+nikpickup+"' and t2."+KEY_KODESORTIR+" = '"+kodesortir+"'  where t2.id is null ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public int deleteTTKListSTPUBERAT(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATA_StpuBerat, KEY_NOTTK + " = '"+ttk+"'",null);
    }


    public String importCsvSTPUBERAT(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), Utils.downloadDirectorySTPUBERAT+"/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberStpuBerat+" ("+KEY_NOTTK+") VALUES (?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String noTtk = insertValues[0].toString().toUpperCase();


                //statement.clearBindings();
                statement.bindString(1, noTtk);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }



    //////////////----------END--------------///




    //SUMBER STPU

    public String importscvSTPU(String namefile){

        SQLiteDatabase db = this.getWritableDatabase();
        String output = null;

        Log.d("namafile",""+namefile);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/STPURetail/"+formattedCal);

        File IMPORT_ITEM_FILE = new File(filename, namefile);

//        if (!IMPORT_ITEM_FILE.exists()) {
//            IMPORT_ITEM_FILE.mkdirs();
//        }


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberSTPU+" ("+KEY_NOTTK+", "+KEY_NOREF+" , "+KEY_KODESORTIR+", "+KEY_KODEOPSTTK+"  ) VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

//                if (flag_is_header) {

//                    db.open();

                String ttk = insertValues[0].toString();
                String noref = insertValues[1].toString();
                String kodeopsttk = insertValues[2].toString();
                String kodesortir = insertValues[3].toString();

                Log.d("ttk",""+""+ttk+":"+noref+" "+kodeopsttk+" "+kodesortir);

//                    contentValues.put(KEY_NOTTK, ttk);
//                    contentValues.put(KEY_NOREF, noref);

                //statement.clearBindings();
                statement.bindString(1, ttk);
                statement.bindString(2, noref);
                statement.bindString(3, kodeopsttk);
                statement.bindString(4, kodesortir);

                statement.executeInsert();

//                    db.insert(TABLE_DATA_SumberPickup, null, contentValues);

//                } else {
//                    flag_is_header = true;
//                }
            }
//            db.setTransactionSuccessful();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output="berhasil";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }


    //SUMBER STPU Account

    public String importscvSTPUAccount(String namefile){

        SQLiteDatabase db = this.getWritableDatabase();
        String output = null;

        Log.d("namafile",""+namefile);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/STPUAccount/"+formattedCal);

        File IMPORT_ITEM_FILE = new File(filename, namefile);

//        if (!IMPORT_ITEM_FILE.exists()) {
//            IMPORT_ITEM_FILE.mkdirs();
//        }


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberSTPU+" ("+KEY_NOTTK+", "+KEY_NOREF+" , "+KEY_KODESORTIR+", "+KEY_KODEOPSTTK+"  ) VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

//                if (flag_is_header) {

//                    db.open();

                String ttk = insertValues[0].toString();
                String noref = insertValues[1].toString();
                String kodeopsttk = insertValues[2].toString();
                String kodesortir = insertValues[3].toString();

                Log.d("ttk",""+""+ttk+":"+noref+" "+kodeopsttk+" "+kodesortir);

//                    contentValues.put(KEY_NOTTK, ttk);
//                    contentValues.put(KEY_NOREF, noref);

                //statement.clearBindings();
                statement.bindString(1, ttk);
                statement.bindString(2, noref);
                statement.bindString(3, kodeopsttk);
                statement.bindString(4, kodesortir);

                statement.executeInsert();

//                    db.insert(TABLE_DATA_SumberPickup, null, contentValues);

//                } else {
//                    flag_is_header = true;
//                }
            }
//            db.setTransactionSuccessful();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output="berhasil";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public int checkListSTPUSumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberSTPU+" WHERE "+KEY_NOTTK+" = '"+ttk+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

        return jumlah;
    }


    public void deleteListSumberSTPU(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberSTPU);
        db.close();
    }

    public int countSTPUSumber(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_DATA_SumberSTPU+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

//        if(cursor!=null)

        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();

            cursor.close();
            return icount;
        }else{
            return 0;
        }


//
//        cursor.moveToFirst();
//        int icount = cursor.getCount();
//
//        cursor.close();
//
//        return icount;
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }



    public ListTtkPickup getDataSumber(String ttk) {

        String selectQuery = "SELECT  * FROM " + TABLE_DATA_SumberSTPU + " WHERE " + KEY_NOTTK + " = '" + ttk + "' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        Log.v("Cursor Object1", DatabaseUtils.dumpCursorToString(cursor));

        ListTtkPickup nottk = new ListTtkPickup();

        try {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
                } while (cursor.moveToNext());
            }



        }catch (RuntimeException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        return nottk;

//        if(cursor!=null && cursor.moveToFirst()) {
//
//            ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//            cursor.close();
//
//
//            return nottk;
//        }else{
//            return null;
//
////            Log.d("erorr data",""+cursor);
//        }









//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        ListTtkPickup nottk = new ListTtkPickup(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODEOPSTTK)),cursor.getString(cursor.getColumnIndex(KEY_KODESORTIR)));
//
//            cursor.close();
//        return nottk;
    }






    //SUMBER PU
    public boolean addTTKPickupSumber(ListTtkPickup ttk) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();

        db.beginTransaction();

        values.put(KEY_NOTTK, ttk.getTtk());
        values.put(KEY_NOREF, ttk.getNoref());

        // Inserting Row
        //db.insert(TABLE_DATA_SumberPickup, null, values);

        long rowInserted = db.insert(TABLE_DATA_SumberPickup, null, values);

        Log.d("ttk",""+ttk.getTtk()+":"+ttk.getNoref());

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close(); // Closing database connection

        if(rowInserted == -1){
            return false;
        }else{
            return true;
        }


    }

//    public void runDownload(){
//        DownloadTask cls = new DownloadTask(PickupVerifikasi.this,btnInput,Utils.downloadcsvUrl);
//    }


    public String cekcsv(String namefile){

        String output = null;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/Pickup/");


        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());

        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(bufRdr.readLine()==null){
                output="gagal";
            }else{
                importscv(namefile);
                output="berhasil";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }


    public String importscv(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/PickupRetail/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberPickup+" ("+KEY_NOTTK+", "+KEY_NOREF+", "+KEY_KODESORTIR+", "+KEY_STATUS+") VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String ttk = insertValues[0].toString();
                String noref = insertValues[1].toString();
                String kodesortir = insertValues[2].toString();
                String status = insertValues[3].toString();

                Log.d("ttk", "" + "" + ttk + ":" + noref + ":"+kodesortir+":"+status);

                //statement.clearBindings();
                statement.bindString(1, ttk);
                statement.bindString(2, noref);
                statement.bindString(3, kodesortir);
                statement.bindString(4, status);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }


    public String importscvPuAccount(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/PickupAccount/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberPickup+" ("+KEY_NOTTK+", "+KEY_NOREF+", "+KEY_KODESORTIR+") VALUES (?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String ttk = insertValues[0].toString();
                String noref = insertValues[1].toString();
                String kodesortir = insertValues[2].toString();

                Log.d("ttk", "" + "" + ttk + ":" + noref + ":"+kodesortir);

                //statement.clearBindings();
                statement.bindString(1, ttk);
                statement.bindString(2, noref);
                statement.bindString(3, kodesortir);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }



    public String importCsvSTSM(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/STSM/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberSTSM+" ("+KEY_NOMS+") VALUES (?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String noMs = insertValues[0].toString().toUpperCase();


                //statement.clearBindings();
                statement.bindString(1, noMs);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }


    public String importCsvSTMS(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/STMS/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberSTMS+" ("+KEY_NOTTK+") VALUES (?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String noTtk = insertValues[0].toString().toUpperCase();


                //statement.clearBindings();
                statement.bindString(1, noTtk);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }





    public String importCsvSTPUMA(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/STPUMA/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberSTPUMA+" ("+KEY_NOTTK+") VALUES (?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String noTtk = insertValues[0].toString().toUpperCase();


                //statement.clearBindings();
                statement.bindString(1, noTtk);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }


    public String importCsvBONGKARMA(String namefile){


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd");

        String formattedCal = dateTime.format(c.getTime());


        String output = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("namafile",""+namefile);

        boolean flag_is_header = false;

        File filename = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/BONGKARMA/"+formattedCal);

        Log.d("direc",""+filename);

        File IMPORT_ITEM_FILE = new File(filename, namefile);


        File file = new File(IMPORT_ITEM_FILE.getPath());


        if (file.exists()) {
        }
        BufferedReader bufRdr = null;
        try {
            bufRdr = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String line = null;
        ContentValues contentValues = new ContentValues();

        db.beginTransaction();
        String sql = "INSERT INTO "+TABLE_DATA_SumberBONGKARMA+" ("+KEY_NOTTK+") VALUES (?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            while ((line = bufRdr.readLine()) != null) {

                String[] insertValues = line.split(",");

                String noTtk = insertValues[0].toString().toUpperCase();


                //statement.clearBindings();
                statement.bindString(1, noTtk);

                statement.executeInsert();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            bufRdr.close();
            output = "berhasil";


        } catch (IOException e) {
            e.printStackTrace();
//                    return "gagal";

        }

        return output;
    }







    public int checkListPickupSumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

//        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberPickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"' or "+KEY_NOREF+" = '"+ttk+"' ";
        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_DATA_SumberPickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"' ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

//        cursor.moveToFirst();
//        int icount = cursor.getInt(0);
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }

        return jumlah;
    }



    public int checkListPickupSumberAccount(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT COUNT(*) FROM "+TABLE_DATA_SumberPickup+" WHERE "+KEY_NOTTK+" = ? ";
        Cursor cursor      = db.rawQuery(selectQuery, new String[]{ttk});

        int jumlah=0;

        try{
            if(cursor != null){
                try {
                    if(cursor.moveToFirst()){
                        int icount = cursor.getInt(0);
                        if(icount>0){
                            jumlah= 1;
                        }else{
                            jumlah= 0;
                        }
                    }else{
                        Log.d("error1",""+"error");
                    }
                }finally {
                    cursor.close();
                }
            }
        }catch (SQLiteException e){
            Log.d("error2",""+e.getMessage());
        }

//        cursor.moveToFirst();
//        int icount = cursor.getInt(0);
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }

        return jumlah;
    }


    public void deleteListSumberPickup(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberPickup);
        db.close();
    }

    public void deleteListSumberSTSM(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DATA_SumberSTSM);
        db.close();
    }




    public Cursor getListPickupSumber(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_DATA_SumberPickup+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        return cursor;


//        ListTtkPickup students = new ListTtkPickup();
//        students.ttk = cursor.getString(cursor.getColumnIndex(KEY_NOTTK));
//        students.name = c.getString(c.getColumnIndex(KEY_NAME));

//        if (cursor != null) {
//            cursor.moveToFirst();
//            System.out.println(selectQuery);
//        }
//        return cursor;

//        sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));

//        int icount = cursor.getInt(0);
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }


    }


    public int countPickupSumber(){
        SQLiteDatabase db = this.getReadableDatabase();

//        int icount = 0;

        String selectQuery = "SELECT  * FROM "+TABLE_DATA_SumberPickup+" ";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));


//        int icount=0;
//        try {
//
//            if(cursor!=null && cursor.moveToFirst()){
//                int icount = cursor.getCount();
//                cursor.close();
//                return icount;
//            }else{
////                return 0;
//                icount=0;
//            }
//
//        }catch (Exception e){
//            System.out.println("Exceptions " +e);
//        }
//
//        return icount;


        if(cursor!=null && cursor.moveToFirst()){
            int icount = cursor.getCount();
            cursor.close();
            return icount;
        }else{
            return 0;
        }



//        cursor.moveToFirst();
//        int icount = cursor.getCount();
//
//        Log.d("jums",""+cursor.getInt(0));

//        return icount;


//        if (cursor.moveToFirst()) {
//            do {
//                icount=cursor.getInt(0);
//
//            } while(cursor.moveToNext());
//
//        }
//
//
////        cursor.moveToFirst();
////        int icount = cursor.getI nt(0);
//
//        cursor.close();
//        return icount;
//        if(icount>0){
//            return 1;
//        }else{
//            return 0;
//        }
    }





    public int updateUserLocation(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OFFICELAT, user.getOfficelat());
        values.put(KEY_OFFICELONG, user.getOfficelong());

        // updating row
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getIDuser()) });
    }
//    User getUserSessionid(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_SESSIONID }, KEY_USERNAME + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        User user = new User(Integer.parseInt(cursor.getString(0)));
//
//        return user;
//    }

    public KodeSortir getDate(String kode) {

        String selectQuery = "SELECT  datemin, datemax FROM "+TABLE_KODE+" WHERE "+KEY_KODE+" == '"+kode+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        KodeSortir date = new KodeSortir(cursor.getString(cursor.getColumnIndex("datemin")), cursor.getString(cursor.getColumnIndex("datemax")));

        cursor.close();
        return date;
    }

    public KodeSortir getTotal(String kode){
        String selectQuery = "SELECT  kodesortir,jumlahttk,verify FROM "+TABLE_KODE+" WHERE "+KEY_KODE+" == '"+kode+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        KodeSortir total = new KodeSortir(cursor.getString(cursor.getColumnIndex("kodesortir")),cursor.getInt(cursor.getColumnIndex("jumlahttk")),cursor.getInt(cursor.getColumnIndex("verify")));

        cursor.close();
        return total;
    }

    public User getLastLogin(String employeeCode){
        String selectQuery = "SELECT  last_login FROM "+TABLE_USER+" WHERE "+KEY_CODE+" == '"+employeeCode+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        User total = new User(employeeCode,cursor.getString(cursor.getColumnIndex("last_login")));

        cursor.close();
        return total;
    }

    public TtkTidakTerkirim getNoTTKTidak(String noTTK){
        String selectQuery = "SELECT  nottk FROM "+TABLE_TTKTIDAKTERKIRIM+" WHERE "+KEY_NOTTK+" = '"+noTTK+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        Log.d("cursor", ""+cursor);

        TtkTidakTerkirim total;

        if (cursor != null)
            cursor.moveToFirst();
            if (cursor.getColumnCount() == 0){
                total = new TtkTidakTerkirim(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } else {
                total = new TtkTidakTerkirim(null);
            }


        cursor.close();
        return total;
    }

    public List<KodeSortir> getAllDataSortir() {
        List<KodeSortir> kodeList = new ArrayList<KodeSortir>();

        String selectQuery = "SELECT  * FROM "+TABLE_KODE;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                KodeSortir kode = new KodeSortir();
                kode.setKodesortir(cursor.getString(cursor.getColumnIndex("kodesortir")));
                kode.setJumlahttk(cursor.getInt(cursor.getColumnIndex("jumlahttk")));
                kode.setVerify(cursor.getInt(cursor.getColumnIndex("verify")));
                kode.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                kodeList.add(kode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return kodeList;
    }

    public List<OfflineTTK> getAllOfflineTTK(String employeeCode) {
        List<OfflineTTK> offlineList = new ArrayList<OfflineTTK>();

        String selectQuery = "SELECT  * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_CODE+" = '"+employeeCode+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OfflineTTK offline = new OfflineTTK();
                offline.setTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                offline.setSessionid(cursor.getString(cursor.getColumnIndex(KEY_SESSIONID)));
                offline.setRequestid(cursor.getString(cursor.getColumnIndex(KEY_REQUESTID)));
                offline.setEmployeecode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                offline.setStatusdate(cursor.getString(cursor.getColumnIndex(KEY_STATUSDATE)));
                offline.setPackagestatus(cursor.getString(cursor.getColumnIndex(KEY_PACKAGESTATUS)));
                offline.setReason(cursor.getString(cursor.getColumnIndex(KEY_REASON)));
                offline.setReceivername(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERNAME)));
                offline.setReceivertype(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERTYPE)));
                offline.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                offline.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                offline.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                offline.setStatusTTK(cursor.getString(cursor.getColumnIndex(KEY_STATUSTTK)));
                offline.setMessageSoap(cursor.getString(cursor.getColumnIndex(KEY_MESSAGESOAP)));
                offlineList.add(offline);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return offlineList;
    }

    public OfflineTTK getOfflineEditTTK(String ttk) {
        OfflineTTK offline = new OfflineTTK();

        String selectQuery = "SELECT  * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                offline.setTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                offline.setSessionid(cursor.getString(cursor.getColumnIndex(KEY_SESSIONID)));
                offline.setRequestid(cursor.getString(cursor.getColumnIndex(KEY_REQUESTID)));
                offline.setEmployeecode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                offline.setStatusdate(cursor.getString(cursor.getColumnIndex(KEY_STATUSDATE)));
                offline.setPackagestatus(cursor.getString(cursor.getColumnIndex(KEY_PACKAGESTATUS)));
                offline.setReason(cursor.getString(cursor.getColumnIndex(KEY_REASON)));
                offline.setReceivername(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERNAME)));
                offline.setReceivertype(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERTYPE)));
                offline.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                offline.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                offline.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                offline.setStatusTTK(cursor.getString(cursor.getColumnIndex(KEY_STATUSTTK)));
                offline.setMessageSoap(cursor.getString(cursor.getColumnIndex(KEY_MESSAGESOAP)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return offline;
    }

    public List<OfflineTTK> getAllOfflineTTKSend(String employeeCode) {
        List<OfflineTTK> offlineList = new ArrayList<OfflineTTK>();

        String selectQuery = "SELECT  * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_STATUSTTK+" != 'Terkirim' AND "+KEY_CODE+" = "+employeeCode;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OfflineTTK offline = new OfflineTTK();
                offline.setTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                offline.setSessionid(cursor.getString(cursor.getColumnIndex(KEY_SESSIONID)));
                offline.setRequestid(cursor.getString(cursor.getColumnIndex(KEY_REQUESTID)));
                offline.setEmployeecode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                offline.setStatusdate(cursor.getString(cursor.getColumnIndex(KEY_STATUSDATE)));
                offline.setPackagestatus(cursor.getString(cursor.getColumnIndex(KEY_PACKAGESTATUS)));
                offline.setReason(cursor.getString(cursor.getColumnIndex(KEY_REASON)));
                offline.setReceivername(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERNAME)));
                offline.setReceivertype(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERTYPE)));
                offline.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                offline.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                offline.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                offline.setStatusTTK(cursor.getString(cursor.getColumnIndex(KEY_STATUSTTK)));
                offline.setMessageSoap(cursor.getString(cursor.getColumnIndex(KEY_MESSAGESOAP)));
                offlineList.add(offline);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return offlineList;
    }

    public List<OfflineTTK> getAllOffline(String employeeCode) {
        List<OfflineTTK> offlineList = new ArrayList<OfflineTTK>();

        String selectQuery = "SELECT  * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_STATUSTTK+" = 'belum diproses' AND "+KEY_CODE+" = "+employeeCode;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OfflineTTK offline = new OfflineTTK();
                offline.setTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                offline.setSessionid(cursor.getString(cursor.getColumnIndex(KEY_SESSIONID)));
                offline.setRequestid(cursor.getString(cursor.getColumnIndex(KEY_REQUESTID)));
                offline.setEmployeecode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                offline.setStatusdate(cursor.getString(cursor.getColumnIndex(KEY_STATUSDATE)));
                offline.setPackagestatus(cursor.getString(cursor.getColumnIndex(KEY_PACKAGESTATUS)));
                offline.setReason(cursor.getString(cursor.getColumnIndex(KEY_REASON)));
                offline.setReceivername(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERNAME)));
                offline.setReceivertype(cursor.getString(cursor.getColumnIndex(KEY_RECEIVERTYPE)));
                offline.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                offline.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                offline.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                offline.setStatusTTK(cursor.getString(cursor.getColumnIndex(KEY_STATUSTTK)));
                offline.setMessageSoap(cursor.getString(cursor.getColumnIndex(KEY_MESSAGESOAP)));
                offlineList.add(offline);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return offlineList;
    }

    public OfflineTTK getStatusTTK(String ttk){
        String selectQuery = "SELECT * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        OfflineTTK offline = new OfflineTTK(cursor.getString(cursor.getColumnIndex(KEY_STATUSTTK)), cursor.getString(cursor.getColumnIndex(KEY_MESSAGESOAP)));;
        return offline;
    }

    public int updateOfflineMessage(String ttk, String message, String resCode){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGESOAP, message);
        values.put(KEY_STATUSTTK, resCode);
        // updating row
        return db.update(TABLE_OFFLINETTK, values, KEY_NOTTK + " = ?",
                new String[] { String.valueOf(ttk) });
    }

    public OfflineTTK checkTTK(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM "+TABLE_OFFLINETTK+" WHERE "+KEY_NOTTK+" == '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        OfflineTTK offline;
        try {
            offline = new OfflineTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
        }catch (Exception e){
            offline = new OfflineTTK(null);
            Log.d("hasi", e+"");
        }

        cursor.close();
        return offline;
    }

    public int deleteTTK(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_OFFLINETTK, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKSJ(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SJ, KEY_NOTTK + " = '"+ttk+"'",null);
    }


    public int deleteTTKSTK(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STK, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKSTSM(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STSM, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKMS(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MS, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKSM(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SM, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKSJP(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SJP, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKKeranjangRetur(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TTK_KERANJANG_RETUR, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteTTKSTBT(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STBT, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int deleteKendaraan(String ttk, String tujuan){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MA, KEY_NOTTK + " = '"+ttk+"' AND "+KEY_KENDARAAN + " = '"+ttk+"' AND "+KEY_TUJUAN+" = '"+tujuan+"'",null);
        return db.delete(TABLE_KENDARAAN, KEY_KENDARAAN + " = '"+ttk+"' AND "+KEY_TUJUAN+" = '"+tujuan+"'",null);
    }

    public int deleteMA(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MA, KEY_NOTTK + " = '"+ttk+"' ",null);
    }

    public int updateStatusSortir(String kode, int verify){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, "submit");
        values.put(KEY_VERIFY, verify);
        // updating row
        return db.update(TABLE_KODE, values, KEY_KODE + " = ?",
                new String[] { String.valueOf(kode) });
    }

    public int updateLastAbsen(String id, String tgl){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LASTLOGIN, tgl);
        // updating row
        return db.update(TABLE_USER, values, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(id) });
    }

    public int updateTotalMenu(String id, String total){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MAINGROUP, total);
        // updating row
        return db.update(TABLE_USER, values, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(id) });
    }

    public String getTotalMenu(String nik){
        String total = null;
        String selectQuery = "SELECT "+KEY_MAINGROUP+" FROM "+TABLE_USER+" WHERE "+KEY_CODE+" = "+nik;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        total = cursor.getString(cursor.getColumnIndex(KEY_MAINGROUP));

        return total;
    }

    public int updateSortir(String kode, int verify, int noverify){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, "submit");
        values.put(KEY_VERIFY, verify);
        values.put(KEY_NOVERIFY, noverify);
        // updating row
        return db.update(TABLE_KODE, values, KEY_KODE + " = ?",
                new String[] { String.valueOf(kode) });
    }

    public int getTotalUnsubmit(){
        int status = 0;
        String selectQuery = "SELECT count(*) as status FROM "+TABLE_KODE+" WHERE "+KEY_STATUS+" == 'unsubmit'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        status = cursor.getInt(cursor.getColumnIndex("status"));

        return status;
    }

    public String getTotalInput(String kode){
        String input;
        String selectQuery = "SELECT input FROM "+TABLE_KODE+" WHERE "+KEY_KODE+" = '"+kode+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        input = cursor.getString(cursor.getColumnIndex(KEY_INPUT));

        return input;
    }

    public int updateTotalInput(String kode, String total){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INPUT, total);
        // updating row
        return db.update(TABLE_KODE, values, KEY_KODE + " = ?",
                new String[] { String.valueOf(kode) });
    }

    public AgentCode getKodeagent(){
        String selectQuery = "SELECT * FROM "+TABLE_AGENT;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();
        AgentCode kode = new AgentCode(cursor.getString(cursor.getColumnIndex("agent")));;
        return kode;
    }

    public List<SuratJalan> getAllDataSJ(String date) {
        List<SuratJalan> sjList = new ArrayList<SuratJalan>();

        String selectQuery = "SELECT  * FROM "+TABLE_SURATJALAN+" WHERE "+KEY_TGL+" > date('"+date+"')";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SuratJalan sj = new SuratJalan();
                sj.setSJ(cursor.getString(cursor.getColumnIndex(KEY_SJ)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setTotal(cursor.getInt(cursor.getColumnIndex(KEY_TOTALTTK)));
                sj.setTerkirim(cursor.getInt(cursor.getColumnIndex(KEY_TTKTERKIRIM)));
                sj.setBelum(cursor.getInt(cursor.getColumnIndex(KEY_TTKBELUMTERKIRIM)));
                sj.setAktif(cursor.getInt(cursor.getColumnIndex(KEY_TTKAKTIF)));
                sjList.add(sj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return sjList;
    }

    public List<SuratJalan> getSJPerDay(String date) {
        List<SuratJalan> sjList = new ArrayList<SuratJalan>();

        String selectQuery = "SELECT  * FROM "+TABLE_SURATJALAN+" WHERE "+KEY_TGL+" = date('"+date+"')";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SuratJalan sj = new SuratJalan();
                sj.setSJ(cursor.getString(cursor.getColumnIndex(KEY_SJ)));
                sj.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                sj.setTotal(cursor.getInt(cursor.getColumnIndex(KEY_TOTALTTK)));
                sj.setTerkirim(cursor.getInt(cursor.getColumnIndex(KEY_TTKTERKIRIM)));
                sj.setBelum(cursor.getInt(cursor.getColumnIndex(KEY_TTKBELUMTERKIRIM)));
                sj.setAktif(cursor.getInt(cursor.getColumnIndex(KEY_TTKAKTIF)));
                sjList.add(sj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return sjList;
    }

    public List<ListTTK> getAllListTTK() {
        List<ListTTK> listTTK = new ArrayList<ListTTK>();

        String selectQuery = "SELECT  * FROM "+TABLE_LISTTTK;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTTK ttk = new ListTTK();
                ttk.setNoTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttk.setKoli(cursor.getInt(cursor.getColumnIndex(KEY_KOLI)));
                ttk.setBerat(cursor.getInt(cursor.getColumnIndex(KEY_BERAT)));
                ttk.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                ttk.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                listTTK.add(ttk);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listTTK;
    }

    public List<ListTTK> getAllListTTKDate() {
        List<ListTTK> listTTK = new ArrayList<ListTTK>();

        String selectQuery = "SELECT  DISTINCT "+KEY_TGL+" FROM "+TABLE_LISTTTK+" ORDER BY "+KEY_TGL+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTTK ttk = new ListTTK();
                ttk.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                listTTK.add(ttk);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listTTK;
    }

    public List<ListTTK> getOneTTKByDate(String date) {
        List<ListTTK> listTTK = new ArrayList<ListTTK>();

        String selectQuery = "SELECT  DISTINCT "+KEY_TGL+" FROM "+TABLE_LISTTTK+" WHERE "+KEY_TGL+" = date('"+date+"')";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTTK ttk = new ListTTK();
                ttk.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                listTTK.add(ttk);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listTTK;
    }

    public List<ListTTK> getAllListTTKByDate(String date) {
        List<ListTTK> listTTK = new ArrayList<ListTTK>();

        String selectQuery = "SELECT  * FROM "+TABLE_LISTTTK+" WHERE "+KEY_TGL+" = date('"+date+"')";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListTTK ttk = new ListTTK();
                ttk.setNoTTK(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttk.setKoli(cursor.getInt(cursor.getColumnIndex(KEY_KOLI)));
                ttk.setBerat(cursor.getInt(cursor.getColumnIndex(KEY_BERAT)));
                ttk.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                ttk.setTgl(cursor.getString(cursor.getColumnIndex(KEY_TGL)));
                listTTK.add(ttk);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listTTK;
    }

    public List<TtkTidakTerkirim> getAllDataTtkTidakTerkirim() {
        List<TtkTidakTerkirim> ttkList = new ArrayList<TtkTidakTerkirim>();

        String selectQuery = "SELECT  * FROM "+TABLE_TTKTIDAKTERKIRIM;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TtkTidakTerkirim ttk = new TtkTidakTerkirim();
                ttk.setNoTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttk.setAlasan(cursor.getString(cursor.getColumnIndex(KEY_ALASAN)));
                ttk.setKeterangan(cursor.getString(cursor.getColumnIndex(KEY_KET)));
                ttkList.add(ttk);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ttkList;
    }

    public List<Group> getAllGroup(){
        List<Group> groupList = new ArrayList<Group>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_GROUP, null);
        if (cursor.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroup(cursor.getString(1));
                groupList.add(group);
            } while (cursor.moveToNext());
        }

        return groupList;
    }

    public List<Origin> getAllDataOrigin(String nama) {
        List<Origin> originList = new ArrayList<Origin>();

        String selectQuery = "SELECT  * FROM "+TABLE_ORIGIN+" WHERE "+KEY_PROVINCE+" LIKE '%"+nama+"%' OR "
                +KEY_CITY+" LIKE '%"+nama+"%'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Origin ori = new Origin();
                ori.setProvince(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE)));
                ori.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                ori.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                originList.add(ori);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return originList;
    }

    public List<Origin> getAllDataOriginSJP(String nama) {
        List<Origin> originList = new ArrayList<Origin>();

        String selectQuery = "SELECT  * FROM "+TABLE_ORIGIN+" WHERE "+KEY_PROVINCE+" LIKE '%"+nama+"%' OR "
                +KEY_CITY+" LIKE '%"+nama+"%' ";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Origin ori = new Origin();
                ori.setProvince(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE)));
                ori.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                ori.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                originList.add(ori);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return originList;
    }

    public List<Origin> getAllDataVia(String nama) {
        List<Origin> originList = new ArrayList<Origin>();

        String selectQuery = "SELECT  * FROM "+TABLE_ORIGIN+" WHERE "+KEY_PROVINCE+" LIKE '%"+nama+"%' OR "
                +KEY_CITY+" LIKE '%"+nama+"%' GROUP BY "+KEY_PROVINCE;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Origin ori = new Origin();
                ori.setProvince(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE)));
                ori.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                originList.add(ori);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return originList;
    }

    public int checkSJ(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_SJ+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkSJP(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_SJP+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkTtkReturKeranjang(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_TTK_KERANJANG_RETUR+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }


    public int checkSTK(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_STK+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int deleteTTKSW(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STMS, KEY_NOTTK + " = '"+ttk+"'",null);
    }

    public int checkSTSM(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_STSM+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }


    public int checkMS(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_MS+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkSM(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_SM+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkSTBT(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_STBT+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public int checkKendaraan(String no, String tujuan){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_KENDARAAN+" WHERE "+KEY_KENDARAAN+" = '"+no+"' AND "+KEY_TUJUAN+" = '"+tujuan+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public String checkTujuan(String no){
        SQLiteDatabase db = this.getWritableDatabase();
        String tujuan = null;

        String selectQuery = "SELECT "+KEY_TUJUAN+" FROM "+TABLE_KENDARAAN+" WHERE "+KEY_KENDARAAN+" = '"+no+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
            tujuan = cursor.getString(cursor.getColumnIndex(KEY_TUJUAN));
        }

        return tujuan;
    }

    public String checkSeal(String no){
        SQLiteDatabase db = this.getWritableDatabase();
        String seal = null;

        String selectQuery = "SELECT "+KEY_SEAL+" FROM "+TABLE_KENDARAAN+" WHERE "+KEY_KENDARAAN+" = '"+no+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
            seal = cursor.getString(cursor.getColumnIndex(KEY_SEAL));
        }

        return seal;
    }

    public int checkMA(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT(*) FROM "+TABLE_MA+" WHERE "+KEY_NOTTK+" = '"+ttk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount>0){
            return 1;
        }else{
            return 0;
        }
    }

    public List<BuatSJ> getAllTtkSJ() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_SJ+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkSTMS() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_STMS+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkSTSM() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_STSM+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkSTK() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_STK+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkMS() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_MS+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkSM() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_SM;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllTtkSJP() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_SJP+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<TtkKeranjangRetur> getAllTTKKeranjangRetur() {
        List<TtkKeranjangRetur> ttkkList = new ArrayList<TtkKeranjangRetur>();

        String selectQuery = "SELECT  * FROM "+TABLE_TTK_KERANJANG_RETUR+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TtkKeranjangRetur sj = new TtkKeranjangRetur();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }



    public List<BuatSJ> getAllTtkSTBT() {
        List<BuatSJ> ttkkList = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_STBT+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                ttkkList.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ttkkList;
    }

    public List<BuatSJ> getAllKendaraan() {
        List<BuatSJ> listKendaraan = new ArrayList<BuatSJ>();

        String selectQuery = "SELECT  * FROM "+TABLE_KENDARAAN;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_KENDARAAN)));
                sj.setTujuan(cursor.getString(cursor.getColumnIndex(KEY_TUJUAN)));
                listKendaraan.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listKendaraan;
    }

    public List<BuatSJ> getAllMA(String tujuan) {
        List<BuatSJ> listKendaraan = new ArrayList<BuatSJ>();

//        String selectQuery = "SELECT  * FROM "+TABLE_MA+" WHERE "+KEY_KENDARAAN+" = '"+no+"' AND "+KEY_TUJUAN+" = '"+tujuan+"'";
        String selectQuery = "SELECT  * FROM "+TABLE_MA+" WHERE "+KEY_TUJUAN+" = '"+tujuan+"' ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuatSJ sj = new BuatSJ();
                sj.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sj.setTtk(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
                listKendaraan.add(sj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listKendaraan;
    }

    public ArrayList<String> getAllTtkSJFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_SJ;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTMSFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_STMS;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTKFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_STK;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTSMFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_STSM;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkMSFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_MS;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSMFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_SM;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSJPFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_SJP;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }


    public ArrayList<String> getAllTtkKeranjangReturFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_TTK_KERANJANG_RETUR;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllTtkSTBTFix() {
        ArrayList<String> myTTK = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+TABLE_STBT;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public ArrayList<String> getAllMAFix(String tujuan) {
        ArrayList<String> myTTK = new ArrayList<String>();

//        String selectQuery = "SELECT  * FROM "+TABLE_MA+" WHERE "+KEY_KENDARAAN+" = '"+no+"' AND "+KEY_TUJUAN+" = '"+tujuan+"'";
        String selectQuery = "SELECT  * FROM "+TABLE_MA+" WHERE "+KEY_TUJUAN+" = '"+tujuan+"'";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myTTK.add(cursor.getString(cursor.getColumnIndex(KEY_NOTTK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myTTK;
    }

    public List<Role> getAllRole(){
        List<Role> roleList = new ArrayList<Role>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_ROLE, null);
        if (cursor.moveToFirst()) {
            do {
                Role role = new Role();
                role.setCode(cursor.getString(cursor.getColumnIndex(KEY_ROLE_CODE)));
                role.setName(cursor.getString(cursor.getColumnIndex(KEY_ROLE_NAME)));
                roleList.add(role);
            } while (cursor.moveToNext());
        }

        return roleList;
    }

    public int getTotalRole(){
        int total = 0;
        String selectQuery = "SELECT count(id) as total FROM "+TABLE_ROLE;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            total = cursor.getInt(cursor.getColumnIndex("total"));
        }else {
            total = 0;
        }

        return total;
    }

    public String getRole(){
        String role = null;
        String selectQuery = "SELECT roleCode FROM "+TABLE_ROLE;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            role = cursor.getString(cursor.getColumnIndex("roleCode"));
        }

        return role;
    }

    public void deleteBuatSJ(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_SJ);
        db.close();
    }

    public void deleteBuatSJP(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_SJP);
        db.close();
    }

    public void deleteKeranjangReturTtk(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_TTK_KERANJANG_RETUR);
        db.close();
    }

    public void deleteSTMS(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_STMS);
        db.close();
    }

    public void deleteSTK(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_STK);
        db.close();
    }

//    public void deleteSTSM(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("delete from "+ TABLE_STSM);
//        db.close();
//    }

    public void deleteMAAllFix(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_MA);
        db.close();
    }

    public void deleteMAAll(String no){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_MA+" WHERE "+KEY_TUJUAN+" ='"+no+"'");
        db.execSQL("delete from "+TABLE_KENDARAAN+" WHERE "+KEY_TUJUAN+" ='"+no+"'");
        db.close();
    }

    public void deleteSM(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_SM);
        db.close();
    }

    public void deleteMS(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_MS);
        db.close();
    }

    public void deleteBuatSTBT(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_STBT);
        db.close();
    }

    public void deleteKode(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_KODE);
        db.close();
    }

    public void deletePickup(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_KODE);
        db.execSQL("delete from "+ TABLE_TTK);
        db.execSQL("delete from "+ TABLE_AGENT);
        db.close();
    }

    public void deleteTTK(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_TTKTIDAKTERKIRIM);
        db.close();
    }

    public void deleteUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_USER);
        db.close();
    }

    public void deleteSJ(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_SURATJALAN);
        db.close();
    }

    public void deleteListTTK(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_LISTTTK);
        db.close();
    }

    public void deleteOfflineTTK(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_OFFLINETTK);
        db.execSQL("delete from "+ DATABASE_TABLE_URI_TTK);
        db.close();
    }

    public void deleteOrigin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_ORIGIN);
        db.close();
    }

    public void deleteRole(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_ROLE);
        db.close();
    }

    public void insertUri(String uri, String Tipe){
        SQLiteDatabase db = this.getWritableDatabase();
        String tipe = Tipe;
        //create();
        ContentValues values = new ContentValues();
        values.put(PATH_NAME, uri);
        values.put(PATH_TIPE, tipe);
        db.insert(DATABASE_TABLE_URI, null, values);
    }

    public void insertTTKUri(String uri, String Tipe, String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        String tipe = Tipe;
        //create();
        ContentValues values = new ContentValues();
        values.put(PATH_NAME, uri);
        values.put(PATH_TIPE, tipe);
        values.put(PATH_TTK, ttk);
        db.insert(DATABASE_TABLE_URI_TTK, null, values);
    }

    public List<String> getAllTTKURI(String nottk) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> uriList = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+DATABASE_TABLE_URI_TTK +" WHERE "+PATH_TTK+" = '"+nottk+"'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                uriList.add(cursor.getString(cursor.getColumnIndex(PATH_NAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return uriList;
    }

    public Cursor getallUri(String nottk){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        //create();
        //cursor = db.query(DATABASE_TABLE_URI, new String[]{}, null, null, null, null, ENTITY_ID + " DESC");
//        cursor = db.query(DATABASE_TABLE_URI, new String[]{}, null, null, null, null, ENTITY_ID + " DESC");
        String selectQuery = "SELECT  * FROM "+DATABASE_TABLE_URI_TTK +" WHERE "+PATH_TTK+" = '"+nottk+"' ORDER BY "+ENTITY_ID +" DESC";
        cursor = db.rawQuery(selectQuery, null);
        if(cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getallUriTTK(String ttk){
        SQLiteDatabase db = this.getWritableDatabase();
        String nomorTTK = ttk;
        Cursor cursor = null;
        //create();
        cursor = db.query(DATABASE_TABLE_URI_TTK, new String[]{}, "_ttk = ?", new String[]{nomorTTK}, null, null, ENTITY_ID + " DESC");

        if(cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getSpesificUri(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        //create();
        cursor = db.query(DATABASE_TABLE_URI, new String[]{}, null, null, null, null, ENTITY_ID + " DESC");
        if(cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }


    public static boolean checkExternalStorage(){
        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = true;
        }
        else {
            mExternalStorageAvailable = false;
        }
        return mExternalStorageAvailable;
    }

    public static String getDatabasePath() {
        String dbPath = DATABASE_NAME;
        if(checkExternalStorage()){
            File direct = new File(Environment.getExternalStorageDirectory()+ "/WahanaImageUri");
            if (!direct.exists()) {
                if (direct.mkdir());
            }
            dbPath =direct.getAbsolutePath()+"/"+DATABASE_NAME;
        }
        return dbPath;
    }

}
