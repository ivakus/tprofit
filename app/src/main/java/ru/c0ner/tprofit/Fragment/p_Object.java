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
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.dataObject;

public class p_Object extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "p_ObjectTAG";

    public interface  PObjectCallBack {
        public void p_Object_onItemSelect (String fagmengTAG, int position, dataObject mdataObject );

    }

    public PObjectCallBack mCallBack;
    public ArrayList <dataObject> mItemList;
    public ListView mListView;
    public Object_Adapter mAdapter;
    public class Object_Adapter extends BaseAdapter{

        public class ViewHolderObject{
            TextView mName;
            TextView mProrab_name;
        }

        public Context context;
     //   ViewHolderObject vHolder;

        public Object_Adapter(Context context) {
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
            dataObject m = (dataObject) getItem(position);
            String str =  (m.getName()!=null)?m.getName().toString():"" ;

            ViewHolderObject holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.p_object_listitem_layout, parent, false);
                holder = new ViewHolderObject();
                holder.mName = (TextView)convertView.findViewById(R.id.p_object_listview_item_obj_name);
                holder.mProrab_name = (TextView)convertView.findViewById(R.id.p_object_listview_item_obj_prorab_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderObject) convertView.getTag();
            }
            holder.mName.setText(str);
            String m_name = m.getManagerName();
            if ( m_name != null ){
                holder.mProrab_name.setText(m.getManagerName().toString());
            }
            else {
                holder.mProrab_name.setText("");
            }
            return convertView;
        }
    }
    public class ServerAPI extends AsyncTask<String,Void,String>{
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
                    mItemList.addAll(Arrays.asList( gson.fromJson(res, dataObject[].class)));
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


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (PObjectCallBack) getActivity();
        }
        catch (ClassCastException e) {
            e.getMessage() ;
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.p_object_layout ,container,false);
        create_object();
        mListView = v.findViewById(R.id.p_object_listview);
        mAdapter = new Object_Adapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        return v;

    }

    public void create_object (){
        mItemList = new ArrayList<dataObject>();
/*
        for (int i = 0 ; i< 100;i++){
            dataObject m = new dataObject();
            m.setId(i);
            m.setName("Object :"+i);
            m.setManagerName("Ivanov : "+i);
            mItemList.add(m);
        }*/
      ServerAPI serverAPI = new ServerAPI();
      serverAPI.execute(getString(R.string.str_object_api_url));
     //   Gson gson = new GsonBuilder().create();
     //   mItemList = new ArrayList<dataObject>(Arrays.asList( gson.fromJson(serverAPI.get(), dataObject[].class)));
    //   mItemList = (ArrayList) getUsingOkHttp("http://192.168.108.2/api/object/");
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        // Do something in response to the click
        dataObject m = (dataObject) parent.getAdapter().getItem(position);
       // Toast.makeText(this.getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();
        // str = m.getTitle().toString();
        mCallBack.p_Object_onItemSelect(TAG, position, m);
    }
}
