package kr.ac.kumho.mydrawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aaa on 2018-08-31.
 */

public class SessionManager {

    private static SessionManager sInstance;

    public static final String SERVER_ADDR = "http://192.168.56.1/";

    public static final String PREF_NAME = "SessionManagerPref";
    public static final String QUEUE_TAG = "VolleyRequest";
    public static final String LOG_TAG = "LOGSessionManager";
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";

    //mainActivity 접근용
    static Context mContext = null;

    //volly
    protected RequestQueue mQueue = null;
    public RequestQueue getQueue(){
        return mQueue;}
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

    public boolean isLogin(){
        return mIsLogin;
    }

    public String getEmail(){
        return mEmail;
    }

    protected void sessionLogin(){
        //mID는 parseNetworkResponse()에서 세팅
        mEmail = mEmailToLogin;
        Log.i(LOG_TAG, "mEmail = mEmailToLogin;");
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMAIL, mEmailToLogin);
        editor.commit();
        mEmailToLogin = "";

        mIsLogin = true;
        Log.i(LOG_TAG, "mIsLogin = true;");

        ((MainActivity) mContext).setNavEmail(mEmail);
    }

    protected  void sessionLogout(){
        mID = "";
        mEmail = "";
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mIsLogin = false;
        Log.i(LOG_TAG, "mIsLogin = false;");
        ((MainActivity) mContext).setNavEmail(mEmail);
    }

        public void Login(String email, String pass){
            String url = SERVER_ADDR + "login.php";

            Map<String, String> params = new HashMap<String,String>();
            params.put("email", email);
            params.put("pass", pass);
            JSONObject jsonObj = new JSONObject(params);

            mEmailToLogin = email;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObj,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(LOG_TAG, "Response: " + response.toString());
                            try{
                                if(response.has("status")){
                                    if(response.getString("status").equals("Success")){
                                        sessionLogin();
                                    }
                                }
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(LOG_TAG, "Error: " + error.getMessage());
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.i(LOG_TAG, "getHeaders()");
                    HashMap<String, String> headers = new HashMap<String,String>();

                    if(mID.length() > 0 && mEmail.length() > 0 ){
                        String cookie = String.format("id=" + mID + ";email=" + mEmail);
                        headers.put("Cookie", cookie);
                    }
                    return headers;
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Log.i(LOG_TAG,"parseNetworkResponse()");

                    Log.i("response",response.headers.toString());
                    Map<String, String> responseHeaders = response.headers;
                    String cookie = responseHeaders.get("Set-Cookie");
                    if(cookie != null){
                        Log.i("Set-Cookie", cookie);
                        int p = cookie.indexOf("id=");
                        Log.i("index=", "" + p);
                        if(p>=0){
                            mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                            int end = mID.indexOf(";");
                            if(end>0)
                                mID = mID.substring(0,end);
                            Log.i("mID", mID);
                            SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                    Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(KEY_ID, mID);
                            editor.commit();
                        }
                    }
                    return super.parseNetworkResponse(response);

                }
            };

            request.setTag(QUEUE_TAG);
            mQueue.add(request);

        }

    //-----------------------------------------------------------------------

    public void Signup(String email, String pass, String pass2, String nick){
        String url = SERVER_ADDR + "signup.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("pass", pass);
        params.put("pass2", pass2);
        params.put("nick", nick);
        JSONObject jsonObj = new JSONObject(params);

        mEmailToLogin = email;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject >(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        try{
                            if (response.has("status")) {
                                if (response.getString("status").equals("Success")) {
                                    sessionLogout();
                                }
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "Error: " + error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG, "getHeaders()");
                HashMap<String, String> headers = new HashMap<String,String>();

                if(mID.length() > 0 && mEmail.length()> 0){
                    String cookie = String.format("id=" + mID + ";email=" + mEmail);
                    headers.put("Cookie", cookie);
                }

                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i(LOG_TAG, "parserNetworkResponse()");

                Log.i("response", response.headers.toString());
                Map<String,String> responseHeaders = response.headers;
                String cookie = responseHeaders.get("Set-Cookie");
                if( cookie != null){
                    Log.i("Set-Cookie", cookie);
                    int p = cookie.indexOf("id=");
                    Log.i("index=", "" + p);
                    if(p >= 0){
                        mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                        int end = mID.indexOf(";");
                        if(end >0)
                            mID = mID.substring(0, end);
                        Log.i("mID", mID);
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor =pref.edit();
                        editor.putString(KEY_ID, mID);
                        editor.commit();
                    }
                }

                return super.parseNetworkResponse(response);

            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //-----------------------------------------------------------------------
    public void Logout() {
        String url = SERVER_ADDR + "logout.php";
        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "Response: " + response.toString());
                        sessionLogout();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "Error: " + error.getMessage());

                        mID = "";
                        mEmail = "";
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        mIsLogin = false;
                        Log.i(LOG_TAG, "mIsLogin = false");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG, "getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mEmail.length() > 0) {
                    String cookie = String.format("id=" + mID + ";email=" + mEmail);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------
    public void cancelQueue() {
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }

}
