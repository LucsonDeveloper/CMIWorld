package com.lucsoninfotech.cmi.cmiworld.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lucsoninfotech.cmi.cmiworld.Activity.LoginActivity;


/**
 * Created by lucsonmacpc5 on 18/10/16.
 */
public class SessionManager {

    // Shared preferences file name
    private static final String PREF_NAME = "CMI_pref";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_LOGGEDINCHAT = "isLoggedInChat";
    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    private final Context _context;
    // Shared pref mode
    private final int PRIVATE_MODE = 0;
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // Constructor
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

    public void setLoginChat(boolean isLoggedInChat) {

        editor.putBoolean(KEY_IS_LOGGEDINCHAT, isLoggedInChat);

        // commit changes
        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isLoggedInChat() {
        return pref.getBoolean(KEY_IS_LOGGEDINCHAT, false);
    }

    public void logoutUser() {

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Staring LoginActivity Activity
        _context.startActivity(i);
    }


















   /* public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }
*/
    /*public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(id, name, email);
            return user;
        }
        return null;
    }*/

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }


}
