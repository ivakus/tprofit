package ru.c0ner.tprofit.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.c0ner.tprofit.Adapters.Person_Adapter;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataObject;

public class p_Person extends Fragment implements Person_Adapter.t_OnItemClickListener {

    public static final String TAG = "p_PersonTAG";
    protected Context _context;

    public void set_context(Context _context) {
        this._context = _context;
    }

    public dataObject parent_Object;
    public String API_Server_URL;


    public interface  PPersonCallBack {
        public void p_Person_onItemSelect (String fagmengTAG, int position, Person personObject );

    }
    public PPersonCallBack mCallBack;
    public String API_URL;

    public android.support.v4.util.LruCache<String, Bitmap> _memoryCache;

    public void set_memoryCache(android.support.v4.util.LruCache<String, Bitmap> _memoryCache) {
        this._memoryCache = _memoryCache;
    }

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
    public class ServerAPI extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        Context _context;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

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


    private String getUsingOkHttp(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "t.Mobile ver 0.01")
                    .build();

            Response response = createClient().newCall(request).execute();
            if (response.isSuccessful()) {
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



        mRecyclerView = v.findViewById(R.id.person_fragment_listview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Person_Adapter(getContext());
        mAdapter.set_memoryCache(_memoryCache);
        mAdapter.setItemList(mItemList);
        mAdapter.SetOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
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

    public void onItemClick(View v, int position) {

        Person m = (Person) mItemList.get(position);
        Toast.makeText(getContext(), "Из фрагмента "+m.getName().toString(), Toast.LENGTH_SHORT).show();

        // Do something in response to the click
       // dataObject m = (dataObject) parent.getAdapter().getItem(position);
        // Toast.makeText(this.getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();
        // str = m.getTitle().toString();
       // mCallBack.p_Object_onItemSelect(TAG, position, m);
    }


    public void onItemLongClick(int position,int menuitem) {

        Person m = (Person) mItemList.get(position);
        // Звонок по телефону


        if ( m != null ) {
            String str = m.getPhone();
            if (m.getPhone() !=null ) {
                switch (menuitem) {
                    case R.id.menu_item_Call: {
                        String tel = "tel:" + m.getPhone().toString();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                        startActivity(intent);
                        break;
                    }
                    case R.id.menu_item_SendText: {

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(sharingIntent, "Отправить Сообщение "));
                        break;
                    }
                }
            }

            else{

                Toast.makeText(this.getContext(), "Ошибка ! не заполнено поле 'Телефон' ", Toast.LENGTH_SHORT).show();
            }


            /*

             */
            // getMenuInflater().inflate(R.menu.t_profit, menu);
            //Toast.makeText(getContext(), m.getName().toString() + "  " + position, Toast.LENGTH_SHORT).show();

            // Do something in response to the click
            // dataObject m = (dataObject) parent.getAdapter().getItem(position);
            // Toast.makeText(this.getContext(),, Toast.LENGTH_SHORT).show();
            // str = m.getTitle().toString();
            // mCallBack.p_Object_onItemSelect(TAG, position, m);
        }
    }


}
