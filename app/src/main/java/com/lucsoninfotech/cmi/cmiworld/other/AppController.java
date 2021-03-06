package com.lucsoninfotech.cmi.cmiworld.other;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lucsoninfotech.cmi.cmiworld.helper.SessionManager;


/**
 * Created by lucsonmacpc5 on 18/10/16.
 */
public class AppController extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private SessionManager pref;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public SessionManager getPrefManager() {
        if (pref == null) {
            pref = new SessionManager(this);
        }

        return pref;
    }

}
