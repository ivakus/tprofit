package ru.c0ner.tprofit.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadIMage {
    Context _context;

    public android.support.v4.util.LruCache<String, Bitmap> _memoryCache;

    public DownLoadIMage(Context _cntxt ) {
        _context = _cntxt;

    }

    public void set_memoryCache(LruCache<String, Bitmap> _memoryCache) {
        this._memoryCache = _memoryCache;
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
