package ru.c0ner.tprofit.Fragment;

import android.app.ProgressDialog;
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
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataObject;

public class p_Person extends Fragment  {

    public static final String TAG = "p_PersonTAG";
    protected Context _context;

    public void set_context(Context _context) {
        this._context = _context;
    }

    public dataObject parent_Object;
    public String API_Server_URL;
    public android.support.v4.util.LruCache<String, Bitmap> _memoryCache;

    public void set_memoryCache(android.support.v4.util.LruCache<String, Bitmap> _memoryCache) {
        this._memoryCache = _memoryCache;
    }

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

    public  class Person_Adapter extends RecyclerView.Adapter<Person_Adapter.ViewHolder> {
/*
        public interface t_OnItemClickListener {
            public void onItemClick(View v, int position);
        }

        public void SetOnItemClickListener( t_OnItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }
        t_OnItemClickListener mItemClickListener;
*/
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mName;
            TextView mPhone;
            ImageView mIcon;
            public ViewHolder(View itemView) {
                super(itemView);
                mName = (TextView)itemView.findViewById(R.id.person_list_item_Name);
                mPhone = (TextView)itemView.findViewById(R.id.person_list_item_Phone);
                mIcon = (ImageView) itemView.findViewById(R.id.person_list_item_pic);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Person m = (Person) mItemList.get(position);
                Toast.makeText(getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();


            }

        }


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
               // new DownloadImageTask(holder.mIcon)
                 //       .execute(m.getSmallPhoto().toString());
                //.execute("http://api.aoprofit.com/img/photo/1.jpg");
                p_Person.this.loadBitmap(getContext(),m.getSmallPhoto().toString(),holder.mIcon);
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

        mAdapter = new Person_Adapter();
       // mAdapter.SetOnItemClickListener(this);
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
        Toast.makeText(getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();

        // Do something in response to the click
       // dataObject m = (dataObject) parent.getAdapter().getItem(position);
        // Toast.makeText(this.getContext(), m.getName().toString(), Toast.LENGTH_SHORT).show();
        // str = m.getTitle().toString();
       // mCallBack.p_Object_onItemSelect(TAG, position, m);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Context _context;
        String _name;
        //private final WeakReference<ImageView> bmImage;
        public DownloadImageTask(Context _context, ImageView bmImage) {
            this.bmImage = bmImage;
            this._context = _context;
        }

        protected Bitmap decodeFile(File file) {
            try {
                InputStream is = new FileInputStream(file);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, opt);
                opt.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,opt);
                //Log.d("LOAD_IMAGE", " name = " + _name + " w = " + bitmap.getWidth() + " h = " + bitmap.getHeight());
                return bitmap;
            } catch (IOException e) {
                //Log.e("LoadImageTask", "LoadImageTask.LoadBitmap IOException " + e.getMessage(), e);
            }
            return null;
        }

        public void CopyStream(InputStream is, OutputStream os)
        {
            final int buffer_size=1024;
            try
            {
                byte[] bytes=new byte[buffer_size];
                for(;;)
                {
                    int count=is.read(bytes, 0, buffer_size);
                    if(count==-1)
                        break;
                    os.write(bytes, 0, count);
                }
            }
            catch(Exception ex){}
        }


        protected Bitmap doInBackground(String... urls) {

             _name = urls[0].toString();
                Context context = _context;
                Bitmap bitmap;
                File file;
                if (context != null) {
                    //InputStream is = context.getAssets().open(_name);
                    file = new File(context.getCacheDir(), _name.replace("/", ""));
                    bitmap = decodeFile(file);
                    if (null == bitmap ) {
                        Log.d ("LOAD_FROM Net",_name);
                        final OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(urls[0]).build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (response.isSuccessful()) {
                            try {
                               // bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                OutputStream os = new FileOutputStream(file);
                                CopyStream(response.body().byteStream(),os);
                                os.close();
                                bitmap = decodeFile(file);
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        }
                     else {
                        {Log.d ("LOAD_FROM File System",_name);}
                    }
                    return bitmap;
                }
        return null;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap bm = p_Person.this.getBitmapFromMemCache(_name);
            if (bm == null && result != null) {
                p_Person.this.addBitmapToMemoryCache(_name, result);
                bm = result;
            }

            bmImage.setImageBitmap(bm);
        }
    }

    private void loadBitmap(Context context, String name, ImageView iv) {
        final Bitmap bm = getBitmapFromMemCache(name);
        if (null != bm) {
           // cancelDownload(name, iv);
            Log.d ("LOAD_FROM CAche",name);
            iv.setImageBitmap(bm);
        } else {

            new DownloadImageTask(context,iv)
                    .execute(name);

        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return _memoryCache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        _memoryCache.put(key, bitmap);
    }


}
