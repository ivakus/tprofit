package ru.c0ner.tprofit.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.dataObject;
import ru.c0ner.tprofit.datashema.dataObject_Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;


public class p_MainFragment extends Fragment {

    public static final String TAG = "p_mainfragmentTAG";

    public ArrayList<dataObject_Status> mItemList;
    public ListView mListView;
    public Main_Adapter mAdapter;

    public void setTitle(String title) {
        Title = title;
    }

    public String Title = "";
    public class Main_Adapter extends BaseAdapter {

        public class ViewHolderObject{
            TextView mName;
            TextView mToday;
            TextView mAll;
            TextView mProrabname;
            ProgressBar mPbar;
        }

        public Context context;
        //   ViewHolderObject vHolder;

        public Main_Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            dataObject_Status m = (dataObject_Status) getItem(position);
            String str = m.getName().toString();

            ViewHolderObject holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.main_fragment_list_item, parent, false);
                holder = new ViewHolderObject();
                holder.mName = (TextView)convertView.findViewById(R.id.main_fragment_object_title);
                holder.mProrabname = (TextView)convertView.findViewById(R.id.main_fragment_prorab_name);
                holder.mToday = (TextView)convertView.findViewById(R.id.main_fragment_today);
                holder.mAll = (TextView) convertView.findViewById(R.id.main_fragment_allperson);
                holder.mPbar = (ProgressBar) convertView.findViewById(R.id.main_fragment_progress);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderObject) convertView.getTag();
            }
            holder.mName.setText(str);
            holder.mProrabname.setText(m.getManagerName().toString());
            holder.mToday.setText(""+m.getTodayPerson());
            holder.mAll.setText(""+m.getAllPerson());
            holder.mPbar.setMax(m.getAllPerson());
            holder.mPbar.setProgress(m.getTodayPerson());
            return convertView;
        }
    }
    public class ServerAPI extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {

            return getUsingOkHttp(strings[0]);
        }

        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            if (res != null) {
                //mItemList = new ArrayList<>(res);
                //switchButtonState();
                Gson gson = new GsonBuilder().create();
                //  dataObject[] ret  = gson.fromJson(res, dataObject[].class);

                //   ArrayList m = new ArrayList<dataObject>(Arrays.asList( gson.fromJson(res, dataObject[].class)));
                mItemList.addAll(Arrays.asList( gson.fromJson(res, dataObject_Status[].class)));
                mAdapter.notifyDataSetChanged();

            }
            else {
                //textView.setText(R.string.error_gettings_followers);
            }
        }
        //return null;

    }

    private String getUsingOkHttp(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "t.Mobile ver 0.01")
                    .build();

            Response response = createClient().newCall(request).execute();
            if (response.isSuccessful()) {

                // Gson gson = new GsonBuilder().create();
                //  dataObject[] ret  = gson.fromJson(response.body().string(), dataObject[].class);

                // ArrayList m = new ArrayList<dataObject>(Arrays.asList( gson.fromJson(response.body().string(), dataObject[].class)));
                //m.addAll(ret);
                // mItemList = m;
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
           // mCallBack = (PPersonCallBack) getActivity();
        }
        catch (ClassCastException e) {
            e.getMessage() ;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.p_main_fragment_layout ,container,false);
        //create_object();
        mItemList = new ArrayList<dataObject_Status>();
        mListView = v.findViewById(R.id.p_main_fragment_listview);
        mAdapter = new Main_Adapter(getContext());
        mListView.setAdapter(mAdapter);
        //mListView.setOnItemClickListener(this);
        getPerson();
        return v;
    }

    private void getPerson() {
        ServerAPI s = new ServerAPI();
        String url = getString(R.string.str_server_api_name)+"/api/object_status";
        s.execute(url);
    }


    public void onItemClick(AdapterView parent, View v, int position, long id) {
        // Do something in response to the click
        // dataObject m = (dataObject) parent.getAdapter().getItem(position);
        // Toast.makeText(this.getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();
        // str = m.getTitle().toString();
        // mCallBack.p_Object_onItemSelect(TAG, position, m);
    }





}
