package ru.c0ner.tprofit.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.c0ner.tprofit.Fragment.p_Person;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;

public  class Person_Adapter extends RecyclerView.Adapter<Person_Adapter.ViewHolder> {
    Context _context;
    ArrayList <Person> mItemList;
    public android.support.v4.util.LruCache<String, Bitmap> _memoryCache;

    public void set_memoryCache(android.support.v4.util.LruCache<String, Bitmap> _memoryCache) {
        this._memoryCache = _memoryCache;
    }

    public void setItemList(ArrayList<Person> itemList) {
        mItemList = itemList;
    }

    public Person_Adapter(Context _context) {
        this._context = _context;
    }


    public interface t_OnItemClickListener {
        public void onItemClick(View v, int position);
        public void onItemLongClick (int position,int menuitem);
    }

    public void SetOnItemClickListener( t_OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    t_OnItemClickListener mItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener,PopupMenu.OnMenuItemClickListener {


        TextView mName;
        TextView mPhone;
        ImageView mIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.person_list_item_Name);
            mPhone = (TextView)itemView.findViewById(R.id.person_list_item_Phone);
            mIcon = (ImageView) itemView.findViewById(R.id.person_list_item_pic);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (null != mItemClickListener) {
                mItemClickListener.onItemClick(v,position);
            }

        }

        public boolean onLongClick(View v)
        {
            int position = getAdapterPosition();
            showPopupMenu(v);
            /*
            if (null != mItemClickListener) {
                mItemClickListener.onItemLongClick(v,position);
            }
            */
            return false;
        }


        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(_context, v);
            popupMenu.inflate(R.menu.persona_fragment_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_item_Call:
                        mItemClickListener.onItemLongClick(getAdapterPosition(),R.id.menu_item_Call);
                        return true;
                    case R.id.menu_item_SendText:
                        mItemClickListener.onItemLongClick(getAdapterPosition(),R.id.menu_item_SendText);
                        //getAdapterPosition();

                        return true;
                    default:
                        return false;
                }
            }

    }


    @NonNull
    @Override
    public Person_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext()); //(LayoutInflater)getActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.person_list_item, parent, false);
        Person_Adapter.ViewHolder holder = new Person_Adapter.ViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Person_Adapter.ViewHolder holder, int position) {
        Person m = (Person) mItemList.get(position);
        String str =  (m.getName()!=null)?m.getName().toString():"" ;
        holder.mName.setText(str);
        holder.mPhone.setText( (m.getPhone()!=null)?m.getPhone().toString():"");
        if (m.getSmallPhoto() !=null) {
            // new DownloadImageTask(holder.mIcon)
            //       .execute(m.getSmallPhoto().toString());
            //.execute("http://api.aoprofit.com/img/photo/1.jpg");
            loadBitmap(_context,m.getSmallPhoto().toString(),holder.mIcon);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
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
            Bitmap bm = getBitmapFromMemCache(_name);
            if (bm == null && result != null) {
                addBitmapToMemoryCache(_name, result);
                bm = result;
            }

            bmImage.setImageBitmap(bm);
        }
    }

    public void loadBitmap(Context context, String name, ImageView iv) {
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