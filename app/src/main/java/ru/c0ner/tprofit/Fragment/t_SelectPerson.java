package ru.c0ner.tprofit.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.c0ner.tprofit.Adapters.DownLoadIMage;
import ru.c0ner.tprofit.Adapters.Person_Adapter;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataAcc;
import ru.c0ner.tprofit.datashema.dataRecogniseResponse;
import ru.c0ner.tprofit.datashema.getPersonsfromAPI;

public class t_SelectPerson extends Fragment implements View.OnClickListener, Person_Adapter.t_OnItemClickListener{
    Context _context;
    dataRecogniseResponse recogniseResponse ;

    dataAcc mDataAcc;
    ImageView mImageView;
    RecyclerView rw;
    Button send,vibor;
    String TOKEN;

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public ArrayList<Person> mItemList = new ArrayList<Person>();

    public final String TAG = "t_SelectTEG";
    private Person_Adapter mAdapter;

    public void setRecogniseResponse(dataRecogniseResponse recogniseResponse) {
        this.recogniseResponse = recogniseResponse;
    }

    public interface t_SelectCallBack {
        public void  OnReturn (String TAG);
    }
    t_SelectCallBack mCallBack;

        public android.support.v4.util.LruCache<String, Bitmap> _memoryCache;

        public void set_memoryCache(android.support.v4.util.LruCache<String, Bitmap> _memoryCache) {
            this._memoryCache = _memoryCache;
        }


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (t_SelectCallBack) getActivity();
        }
        catch (ClassCastException e) {
            e.getMessage() ;
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.p_select_fragment ,container,false);
        _context = getContext();
        //create_object();
       mImageView = (ImageView) v.findViewById(R.id.select_im);
       rw = (RecyclerView) v.findViewById(R.id.select_recicle);
       vibor = (Button) v.findViewById(R.id.select_btn_select_person);
       vibor.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rw.setLayoutManager(mLayoutManager);
        rw.setVisibility(View.GONE);
        mAdapter = new Person_Adapter(getContext());
        mAdapter.set_memoryCache(_memoryCache);
        mAdapter.setItemList(mItemList);
        mAdapter.SetOnItemClickListener(this);
        rw.setAdapter(mAdapter);
       // mBtnSend = v.findViewById(R.id.recognise_send);
       // mBtnSend.setOnClickListener(this);
        DownLoadIMage dnImage = new DownLoadIMage(getContext());
        dnImage.set_memoryCache(_memoryCache);
       if (recogniseResponse.getUrl() != null ) {
           String img_url = _context.getResources().getString(R.string.str_t_site) + "employees/"+recogniseResponse.getUrl().toString();
           dnImage.loadBitmap(_context, img_url, mImageView);
       }
        getPerson("http://api.aoprofit.com/api/object/id/2/person/");

        mDataAcc = new dataAcc();
        mDataAcc.setUrl(recogniseResponse.getUrl());
        mDataAcc.setId_predict(recogniseResponse.getIdPredict());
        mDataAcc.setToken(TOKEN);
        return v;
    }

    public void getPerson(String url1) {
        if ((mItemList != null)&&(mItemList.size()>0)) {
            mItemList.clear();
        }
        ServerAPI s = new ServerAPI(_context);
        String url;
        if (recogniseResponse.getIdCurrentObject() != null) {
                 url = getString(R.string.str_server_api_name)+"/api/object/id/"+recogniseResponse.getIdCurrentObject()+"/person";
        }
        else {
              url = getString(R.string.str_server_api_name)+"/api/person";
        }

        s.execute(url);
    }




    public class ServerAPI extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        Context _context;

        public ServerAPI(Context _context) {
            this._context = _context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                 dialog = new ProgressDialog(_context);
                 dialog.setMessage("Загрузка");
                 dialog.setIndeterminate(true);
                 dialog.setCancelable(true);
                 dialog.show();


        }

        protected String doInBackground(String... strings) {

            return getUsingOkHttp(strings[0]);
        }

        protected void onPostExecute(String res) {
            super.onPostExecute(res);
               dialog.dismiss();
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
    public void onClick(View v) {

            if (v.getId() == R.id.select_btn_select_person){
                if (rw.getVisibility() == View.GONE) {

                    rw.setVisibility(View.VISIBLE);
                   // rw.setAdapter(mAdapter);
                }
                else {rw.setVisibility(View.GONE);}
            }

            if (v.getId() == R.id.select_send)
            register();
    }




    public void register (){

            ServerAPIPost s1 = new ServerAPIPost(_context);
            String url = getString(R.string.str_verifi_api_url);
            s1.execute(url);


    }

    public void onItemClick(View v, int position){

            mDataAcc.setId_predict(mItemList.get(position).getId());
            register();
    }


    public void onItemLongClick(int position,int menuitem) {}


    public class ServerAPIPost extends AsyncTask<String,Void,String> {
        Context _context;
        public ProgressDialog dialog;

        public ServerAPIPost(Context _context) {
            this._context = _context;
        }

        @Override
        protected String doInBackground(String... strings) {

            return getUsingOkHttpPost(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(_context);
            dialog.setMessage("Загрузка");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();

        }

        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            dialog.dismiss();
            if (res != null) {

                Gson gson = new GsonBuilder().create();
                Log.d("RECOGNISE", "onPostExecute: " + res);

                dataRecogniseResponse recogniseResponse = gson.fromJson(res, dataRecogniseResponse.class);

                if ((recogniseResponse.getSuccess() == 1) ) {
                    Toast.makeText(getContext(), "Успешно " + recogniseResponse.getMessage().toString(), Toast.LENGTH_LONG).show();
                    mCallBack.OnReturn(TAG);
                } else {
                    //textView.setText(R.string.error_gettings_followers);
                    Toast.makeText(getContext(), " : " + recogniseResponse.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                if ((recogniseResponse.getSuccess() == 1)) {

                   // mCallBack.RecogniseResponse(TAG,recogniseResponse);

                }
            }
            //return null;

        }
    }

    private String getUsingOkHttpPost(String url) {

        try {
            Gson gson = new GsonBuilder().create();
            String Login_request = gson.toJson(mDataAcc);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, Login_request);

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "t.Mobile ver 0.01")
                    .post(body)
                    .build();
            Response response = createClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
            else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
     }


}
