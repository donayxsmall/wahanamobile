package com.wahana.wahanamobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reza on 17/06/2016.
 */
public class ImageUriDatabases {
    private static final String CREATE_DATABASE = "create table URI_TABLE (_id integer primary key autoincrement," + "_uri text not null," + "_tipe text not null);";
    private static final String CREATE_DATABASE_TTK= "create table URI_TABLE_TTK (_id integer primary key autoincrement," + "_uri text not null," + "_tipe text not null," + "_ttk text not null);";
    private static final String DATABASE_NAME = "IMAGE_URI_DATABSE";
    private static final String DATABASE_TABLE_URI = "URI_TABLE";
    private static final String DATABASE_TABLE_URI_TTK = "URI_TABLE_TTK";
    private static final int DATABASE_VERSION = 1;

    public static final String ENTITY_ID = "_id";
    public static final String PATH_NAME = "_uri";
    public static final String PATH_TIPE = "_tipe";
    public static final String PATH_TTK = "_ttk";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    Context mContext;

    public ImageUriDatabases(Context context) {
        DBHelper = new DatabaseHelper(context);
        mContext = context;
    }

    private void open() {
        if (db != null) {

            if (!db.isOpen()) {
                db = DBHelper.getWritableDatabase();
            }
        } else {
            db = DBHelper.getWritableDatabase();
        }
    }

    public void create() {

        DBHelper.onCreate(db);

    }

    public void destroy() {
        if (db != null) {
            DBHelper.onDestroy(db);
        }
    }

    public void deleteUri(String id)
    {
        db.delete(DATABASE_TABLE_URI, ENTITY_ID + " = ?", new String[] { id });
    }

    public void deleteUriTTK(String id)
    {
        db.delete(DATABASE_TABLE_URI_TTK, ENTITY_ID + " = ?", new String[] { id });
    }

    public void insertUri(String uri, String Tipe){
        String tipe = Tipe;
        open();
        //create();
        ContentValues values = new ContentValues();
        values.put(PATH_NAME, uri);
        values.put(PATH_TIPE, tipe);
        db.insert(DATABASE_TABLE_URI, null, values);
    }

    public void insertTTKUri(String uri, String Tipe, String ttk){
        String tipe = Tipe;
        open();
        //create();
        ContentValues values = new ContentValues();
        values.put(PATH_NAME, uri);
        values.put(PATH_TIPE, tipe);
        values.put(PATH_TTK, ttk);
        db.insert(DATABASE_TABLE_URI_TTK, null, values);
    }

    public List<String> getAllTTK(String nottk) {
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

    public List<String> getAllTTKTerkirim() {
        List<String> uriList = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM "+DATABASE_TABLE_URI;
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                uriList.add(cursor.getString(cursor.getColumnIndex(PATH_NAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return uriList;
    }

    public List<String> getAllTTKBT(String nottk) {
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

    public Cursor getallUri(){
        Cursor cursor = null;
        open();
        //create();
        //cursor = db.query(DATABASE_TABLE_URI, new String[]{}, null, null, null, null, ENTITY_ID + " DESC");
        cursor = db.query(DATABASE_TABLE_URI, new String[]{}, null, null, null, null, ENTITY_ID + " DESC");
        if(cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getallUriTTK(String ttk){
        String nomorTTK = ttk;
        Cursor cursor = null;
        open();
        //create();
        cursor = db.query(DATABASE_TABLE_URI_TTK, new String[]{}, "_ttk = ?", new String[]{nomorTTK}, null, null, ENTITY_ID + " DESC");

        if(cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getSpesificUri(){
        Cursor cursor = null;
        open();
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

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private Context mContext;

        public DatabaseHelper(Context context) {

            super(context, getDatabasePath(), null, DATABASE_VERSION);
            mContext = context;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

                db.execSQL(CREATE_DATABASE);
                db.execSQL(CREATE_DATABASE_TTK);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS 'URI_TABLE'");
                onCreate(db);
        }

        public void onDestroy(SQLiteDatabase db)
        {
            db.execSQL("delete from "+ DATABASE_TABLE_URI);
            db.execSQL("delete from "+ DATABASE_TABLE_URI_TTK);
            mContext.deleteDatabase(DATABASE_NAME);
        }
    }
}
