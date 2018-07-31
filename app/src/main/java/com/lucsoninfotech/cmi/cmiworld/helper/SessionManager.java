package com.lucsoninfotech.cmi.cmiworld.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lucsoninfotech.cmi.cmiworld.Activity.LoginActivity;


public class SessionManager {


    // Shared preferences file name
    private static final String PREF_NAME = "CMI_pref";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;
    // Shared pref mode
    private final int PRIVATE_MODE = 0;
    SharedPreferences examplePrefs;
    SharedPreferences.Editor editor_wholedata;
    private SQLiteHandler db;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();


    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void logoutUser() {
        db = new SQLiteHandler(_context);
        editor.clear();
        editor.commit();
        db.deleteUsers();
        //    LoginManager.getInstance().logOut();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring LoginActivity Activity
        _context.startActivity(i);
    }
}