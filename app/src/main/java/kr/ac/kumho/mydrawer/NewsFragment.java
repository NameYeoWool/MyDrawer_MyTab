package kr.ac.kumho.mydrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;


public class NewsFragment extends Fragment {

    public static final String LOG_TAG = "LOGNewsFragment";

    // protected TextView mTextView;
    public static final String QUEUE_TAG = "VolleyRequest";
    protected RequestQueue mQueue = null;
    JSONObject mResult = null;
    ArrayList<NewsInfo> mList = new ArrayList<NewsInfo>();
    protected NewsAdapter mAdapter = new NewsAdapter(mList);
    SessionManager mSession = SessionManager.getsInstance(getContext());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        RecyclerView r = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        r.setAdapter(mAdapter);
        r.setLayoutManager(new LinearLayoutManager(getContext()));
        r.setItemAnimator(new DefaultItemAnimator());

        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();

        requestNews();
        return rootView;
    }

    public class NewsInfo {
        String email;
        int newsid;
        String memo;
        String time;

        public NewsInfo(String email,int newsid, String memo, String time) {
            this.email = email;
            this.newsid = newsid;
            this.memo = memo;
            this.time = time;
        }

        public String getEmail() {
            return email;
        }


        public int getNewsid() {
            return newsid;
        }



        public String getMemo() {
            return memo;
        }


        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        ArrayList<NewsInfo> mArray = null;

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{


            //each dat item is just a string in this case
            public TextView txtEmail;
            public TextView txtMemo;
            public TextView txtTime;

            public ViewHolder(View root) {
                super(root);
                root.setOnClickListener(this);
                txtEmail = (TextView) root.findViewById(R.id.txtEmail);
                txtMemo = (TextView) root.findViewById(R.id.txtMemo);
                txtTime = (TextView) root.findViewById(R.id.txtTime);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),mArray.get(getAdapterPosition()).getMemo(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        public NewsAdapter(ArrayList<NewsInfo> list) {
            mArray = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtEmail.setText(mArray.get(position).getEmail());
            holder.txtMemo.setText(mArray.get(position).getMemo());
            holder.txtTime.setText(mArray.get(position).getTime());
        }



        @Override
        public int getItemCount() {
            return mArray.size();
        }
    }


    public void drawList() {
        mList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = ((JSONObject) items.get(i));
                String email = info.optString("email","none");
                int newsid = info.getInt("newsid");
                String memo = info.optString("memo","noMemo");
                String time = info.optString("time","noTime");

                mList.add(new NewsInfo(email,newsid,memo,time));
            }

        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_LONG).show();
            mResult = null;

        }

        mAdapter.notifyDataSetChanged();
    }


    protected void requestNews() {
        String url = "http://192.168.56.1/listnews.php?since=0&max=100";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        //mTextView.setText(response.toString());
                        drawList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }


}
