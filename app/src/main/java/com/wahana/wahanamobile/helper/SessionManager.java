package com.wahana.wahanamobile.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.wahana.wahanamobile.LoginActivity;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.webserviceClient.SoapClientMember;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	SharedPreferences tokenPref;
	
	// Editor for Shared preferences
	Editor editor;
	Editor tokenEditor;
	
	// Context
	Activity _activity;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "WahanaPref";

	private static final String KeyID = "id";
	private static final String KeyUsername = "Username";
	private static final String KeyName = "Name";
	private static final String KeyRoleID = "roleId";
	private static final String KeyRole = "Group";
	private static final String KeySessionID = "SessionID";
	private static final String KeyJenis = "Jenis";
	private static final String KeyExpired = "Expired";
	private static final String Keyjmlstatic = "jmlstatic";
	private static final String KeyKodeKantor = "kodekantor";

	// Sharedpref file name
	private static final String PREF_FIREBASE = "TokenPref";

	private static final String KeyToken = "id";
    private static final String TAG = "Logout";
	private static final String KeyTokenJWT = "TokenJWT";
	private static final String KeyPhoto = "photo";

	private static final String KeyVaksin = "vaksin";

    ProgressDialog progressDialog;


	// Constructor
	public SessionManager(Activity activity){
		this._activity = activity;
		pref = _activity.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		tokenPref = _activity.getSharedPreferences(PREF_FIREBASE, PRIVATE_MODE);
		editor = pref.edit();
        tokenEditor = tokenPref.edit();
	}

	public void createLoginSession(String ID, String username,String name, String roleID, String group, String sessionID, String jenis,String kodekantor)
	{
		editor.putString(KeyID, ID);
		editor.putString(KeyUsername, username);
		editor.putString(KeyName, name);
		editor.putString(KeyRoleID, roleID);
		editor.putString(KeyRole, group);
		editor.putString(KeySessionID, sessionID);
		editor.putString(KeyJenis, jenis);
		editor.putString(KeyKodeKantor, kodekantor);
		editor.putLong(KeyExpired, System.currentTimeMillis()+(5*60*60*1000));
		editor.commit();
	}

	public void tokenSession(String token)
	{
		tokenEditor.putString(KeyToken, token);
        tokenEditor.commit();
	}

	public void tokenJWT(String tokenjwt, String photo, String vaksin)
	{
		editor.putString(KeyTokenJWT, tokenjwt);
		editor.putString(KeyPhoto, photo);
		editor.putString(KeyVaksin, vaksin);
		editor.commit();
	}


	public void jmlmenustatic(int jml)
	{
		editor.putString(Keyjmlstatic, String.valueOf(jml));
		editor.commit();
	}
	
	/**
	 * Mengecek apakah user pernah login atau belum
	 * @return mengembalikan status login user dalam bentuk boolean
	 */
	public boolean isLogin()
	{
		if(!pref.getString(KeyID, "").intern().contentEquals(""))
		return true;
		else
		return false;
	}

	public void checkLogin(){
		// Check login status
		if(!this.isLogin()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_activity, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_activity.startActivity(i);
			_activity.finish();
		}
	}

	public void checkUserLogin(){
		// Check login status
		if(this.isLogin()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_activity, MainActivity.class);
			// Closing all the Activities
		//	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
		//	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_activity.startActivity(i);
		}
	}

	/**
	 * menghapus session user, password, dan waktu expired
	 */
	public void clearSession()
	{
		editor.putString(KeyID, "");
		editor.putString(KeyUsername, "");
		editor.putString(KeyName, "");
		editor.putString(KeyRoleID, "");
		editor.putString(KeySessionID, "");
		editor.putString(KeyJenis, "");
        tokenEditor.putString(KeyToken, "");
		editor.commit();
	}
	
	/**
	 * Mendapatkan id dari user yang sedang login
	 * @return mengembalikan id dalam bentuk String
	 */
	public String getID()
	{
		return pref.getString(KeyID, "");
	}
	
	/**
	 * Mendapatkan Nama dari user yang sedang login
	 * @return mengembalikan nama user dalam bentuk String
	 */
	public String getUsername()
	{
		return pref.getString(KeyUsername, "");
	}
	public String getName()
	{
		return pref.getString(KeyName, "");
	}
	public String getRoleID()
	{
		return pref.getString(KeyRoleID, "");
	}
	public String getRole()
	{
		return pref.getString(KeyRole, "");
	}
	public String getSessionID()
	{
		return pref.getString(KeySessionID, "");
	}
	public String getJenis()
	{
		return pref.getString(KeyJenis, "");
	}
	public String getToken()
	{
		return tokenPref.getString(KeyToken, "");
	}

	public String getjmlmenustatic()
	{
		return pref.getString(Keyjmlstatic, "");
	}

	public String getKeyTokenJWT()
	{
		return pref.getString(KeyTokenJWT, "");
	}

	public String getKodeKantor()
	{
		return pref.getString(KeyKodeKantor, "");
	}

	public String getPhoto()
	{
		return pref.getString(KeyPhoto, "");
	}

	public String getVaksin()
	{
		return pref.getString(KeyVaksin, "");
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(){
        if (getJenis().equals("member")){
            ArrayList<String> parameter = new ArrayList<String>();
            parameter.add("gcmUserLogout");
            parameter.add(getID());
            new SoapClientMember(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.i(TAG, "onPreExecute");
                    progressDialog = new ProgressDialog(_activity, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Logout...");
                    progressDialog.show();
                }

                @Override
                protected void onPostExecute(SoapObject result) {
                    super.onPostExecute(result);
                    Log.d("hasil soap", ""+result);
                    progressDialog.dismiss();
                    if(result==null){
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(_activity, _activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        try{
                            final String code = result.getProperty("resCode").toString();
                            final String text = result.getProperty("resText").toString();
                            if (code.equals("1")) {
                                DatabaseHandler db = new DatabaseHandler(_activity);
                                db.deleteUser();
                                // Clearing all data from Shared Preferences
                                editor.clear();
                                editor.commit();

                                // After logout redirect user to Loing Activity
                                Intent i = new Intent(_activity, LoginActivity.class);
                                // Closing all the Activities
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                // Add new Flag to start new Activity
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // Staring Login Activity
                                _activity.startActivity(i);
                                _activity.finish();
                            }else{
                                progressDialog.dismiss();
                                _activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(_activity, text,Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            _activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(_activity, _activity.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }.execute(parameter);
        }else {
            DatabaseHandler db = new DatabaseHandler(_activity);
            db.deleteUser();
			db.deleteRole();
            // Clearing all data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Loing Activity
            Intent i = new Intent(_activity, LoginActivity.class);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _activity.startActivity(i);
            _activity.finish();
        }
	}

	public void clearSessionData(){
        DatabaseHandler db = new DatabaseHandler(_activity);
        db.deleteUser();
        db.deleteRole();
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

	private boolean applicationInForeground() {
		ActivityManager activityManager = (ActivityManager) _activity.getSystemService(_activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
		boolean isActivityFound = false;

		if (services.get(0).processName
				.equalsIgnoreCase(_activity.getPackageName())) {
			isActivityFound = true;
		}

		return isActivityFound;
	}

    public boolean isExpired(long Time)
    {
        if(pref.getLong(KeyExpired, 0) > Time)
        { return false; }
        else
        { return true; }
    }

    public void updateExpired(long Time)
    {
        editor.putLong(KeyExpired, Time);
        editor.commit();
    }

}
