package ru.c0ner.tprofit.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataObject;

public class p_Person extends Fragment implements AdapterView.OnItemClickListener{

    public static final String TAG = "p_PersonTAG";
    public dataObject parent_Object;
    public String API_Server_URL;
    public interface  PPersonCallBack {
        public void p_Person_onItemSelect (String fagmengTAG, int position, Person personObject );

    }
    public PPersonCallBack mCallBack;
    public String API_URL;

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public ArrayList<Person> mItemList = new ArrayList<Person>();

    public void setParent_Object(dataObject parent_Object) {
        this.parent_Object = parent_Object;
        mItemList = new ArrayList<Person>();
    }

    public ListView mListView;
    public RecyclerView mRecyclerView;
    public Person_Adapter mAdapter;

    public void setTitle(String title) {
        Title = title;
    }

    public String Title = "";
    public class Person_Adapter_ extends BaseAdapter {

        public class ViewHolderObject{
            TextView mName;
            TextView mPhone;
            ImageView mIcon;

        }

        public Context context;
        //   ViewHolderObject vHolder;

        public Person_Adapter_(Context context) {
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
            Person m = (Person) getItem(position);
            String str =  (m.getName()!=null)?m.getName().toString():"" ;


            ViewHolderObject holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.person_list_item, parent, false);
                holder = new ViewHolderObject();
                holder.mName = (TextView)convertView.findViewById(R.id.person_list_item_Name);
                holder.mPhone = (TextView)convertView.findViewById(R.id.person_list_item_Phone);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.person_list_item_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderObject) convertView.getTag();
            }
            holder.mName.setText(str);
            holder.mPhone.setText( (m.getPhone()!=null)?m.getPhone().toString():"");
            if (m.getSmallPhoto() !=null) {
                new DownloadImageTask(holder.mIcon)
                        .execute(m.getSmallPhoto().toString());
                        //.execute("http://api.aoprofit.com/img/photo/1.jpg");
            }
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
                mItemList.addAll(Arrays.asList( gson.fromJson(res, Person[].class)));
                mAdapter.notifyDataSetChanged();

            }
            else {
                //textView.setText(R.string.error_gettings_followers);
            }
        }
        //return null;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mPhone;
        ImageView mIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.person_list_item_Name);
            mPhone = (TextView)itemView.findViewById(R.id.person_list_item_Phone);
            mIcon = (ImageView) itemView.findViewById(R.id.person_list_item_pic);

        }


    }
    public class Person_Adapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE);
            View convertView = inflater.inflate(R.layout.person_list_item, parent, false);
            // View convertView = inflater.inflate(R.layout., parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Person m = (Person) mItemList.get(position);
            String str =  (m.getName()!=null)?m.getName().toString():"" ;
            holder.mName.setText(str);
            holder.mPhone.setText( (m.getPhone()!=null)?m.getPhone().toString():"");
            if (m.getSmallPhoto() !=null) {
                new DownloadImageTask(holder.mIcon)
                        .execute(m.getSmallPhoto().toString());
                //.execute("http://api.aoprofit.com/img/photo/1.jpg");
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
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
            mCallBack = (PPersonCallBack) getActivity();
        }
        catch (ClassCastException e) {
            e.getMessage() ;
        }
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.p_person_layout ,container,false);


        // mListView = v.findViewById(R.id.person_fragment_listview);
        mRecyclerView = v.findViewById(R.id.person_fragment_listview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Person_Adapter();//new Person_Adapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        //mListView.setAdapter(mAdapter);
        //mListView.setOnItemClickListener(this);
        TextView tw = v.findViewById(R.id.person_fragment_title);
        tw.setText(Title);
        API_Server_URL = getString(R.string.str_server_api_name);
       // getPerson();
        return v;
    }

    public void getPerson() {
        ServerAPI s = new ServerAPI();
      //  String url = getString(R.string.str_server_api_name)+"/api/object/id/"+parent_Object.getId()+"/person";
        s.execute(API_URL);
    }

    public void setItemList(ArrayList<Person> itemList) {
        mItemList = itemList;
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        // Do something in response to the click
       // dataObject m = (dataObject) parent.getAdapter().getItem(position);
        // Toast.makeText(this.getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();
        // str = m.getTitle().toString();
       // mCallBack.p_Object_onItemSelect(TAG, position, m);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        //private final WeakReference<ImageView> bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                try {
                    mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}
