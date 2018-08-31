package kr.ac.kumho.mydrawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by aaa on 2018-08-31.
 */

public class SessionManager {

    private static SessionManager sInstance;

    public static final String SERVER_ADDR = "http://115.145.117.55/";

    public static final String PREF_NAME = "SessionManagerPref";
    public static final String QUEUE_TAG = "VolleyRequest";
    public static final String LOG_TAG = "LOGSessionManager";
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";

    static Context mContext = null;

    protected RequestQueue mQueue = null;
    protected boolean mIsLogin = false;

    protected String mID = null;
    protected String mEmail = null;
    protected  String mEmailToLogin = null;

    //------------------------------------------------
    public static SessionManager getsInstance(Context context){
        if(sInstance == null ){
            sInstance = new SessionManager(context);
        }
        return sInstance;
    }
    //------------------------------------------------

    public SessionManager(Context context){
        SessionManager.mContext = context;
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mID = pref.getString(KEY_ID,"");
        mEmail = pref.getString(KEY_EMAIL, "");
        if(mID.length() > 0 && mEmail.length() > 0 ){
            mIsLogin = true;
        }

        CookieHandler.setDefault(new CookieManager());

        mQueue = Volley.newRequestQueue(context);

    }

}
