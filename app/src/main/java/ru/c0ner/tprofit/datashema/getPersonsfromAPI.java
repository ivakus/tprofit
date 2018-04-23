package ru.c0ner.tprofit.datashema;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.OkHttpClient;
import ru.c0ner.tprofit.Adapters.Person_Adapter;

public class getPersonsfromAPI {

    public ArrayList<Person> mItemList = new ArrayList<Person>();
    Person_Adapter mPerson_adapter;
    Context context;

    public getPersonsfromAPI(Context _context, String url , Person_Adapter person_adapter, ArrayList item) {
        mPerson_adapter = person_adapter;
        context = _context;
        mItemList = item;
        ServerAPI s = new ServerAPI (context);
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
                mPerson_adapter.notifyDataSetChanged();

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

}
