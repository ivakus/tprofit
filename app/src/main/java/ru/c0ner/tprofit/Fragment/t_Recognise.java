package ru.c0ner.tprofit.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.dataLogin;
import ru.c0ner.tprofit.datashema.dataLoginResponse;
import ru.c0ner.tprofit.datashema.dataObject_Status;
import ru.c0ner.tprofit.datashema.dataRecogniseResponse;

public class t_Recognise extends Fragment implements  View.OnClickListener{

    ImageView mImageView;
    Button mBtnSend;
    String curent_photo_name;
    String curent_photo_path;
    String Bitmapbase64;
    Bitmap curent_photo;
    String Token;
    boolean img_set = false;

    public void setToken(String token) {
        Token = token;
    }

    public static final String TAG = "t_RecogniseTAG";

    public interface t_FragmentCallBack {
        public void onItemClick (String _TAG);
    }
    t_FragmentCallBack mCallBack;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (t_FragmentCallBack) getActivity();
        }
        catch (ClassCastException e) {
            e.getMessage() ;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.p_recognise_fragment ,container,false);
        //create_object();
        mImageView = v.findViewById(R.id.recognise_im);
        mBtnSend = v.findViewById(R.id.recognise_send);
        mBtnSend.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recognise_send) {
            sendRecognise();


        }


    }


    public void sendRecognise (){
        if (img_set) {
            Bitmapbase64 = decodeBitmap();
            ServerAPI serverAPI = new ServerAPI(getContext());
            serverAPI.execute(getString(R.string.str_recognise_api_url));
        }

    }


    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        curent_photo_path = image.getAbsolutePath();
        curent_photo_name = curent_photo_path;
        return image;
    }

    public void setPic() {
        // Get the dimensions of the View
        Log.d(TAG, "setPic: "+curent_photo_name);
        if (mImageView !=null ){
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(curent_photo_name, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            curent_photo = BitmapFactory.decodeFile(curent_photo_name, bmOptions);
         //   mImageView.setImageBitmap(curent_photo);
        }
    }

    public void showPhoto(){

        File file = new File(curent_photo_name);

        try {
            ExifInterface exif = new ExifInterface(file.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            curent_photo = Bitmap.createBitmap(curent_photo,0,0, curent_photo.getWidth(), curent_photo.getHeight(), matrix, true);

        }catch(IOException ex){
            Log.e("LOG EXIF", "Failed to get Exif data", ex);
        }

        mImageView.setImageBitmap(curent_photo);
        img_set = true;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private String decodeBitmap (){

        String encodeBase64 = new String();
       // File f = new File()
        if (curent_photo !=null ){
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            curent_photo.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            byte[] byteArray = oStream.toByteArray();
            encodeBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return  encodeBase64;
        }

        return null;
    }


    public class ServerAPI extends AsyncTask<String,Void,String> {
        Context _context;
        public ProgressDialog dialog;

        public ServerAPI(Context _context) {
            this._context = _context;
        }

        @Override
        protected String doInBackground(String... strings) {

            return getUsingOkHttp(strings[0]);
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
                if (recogniseResponse.getSuccess() == 1) {

                } else {
                    //textView.setText(R.string.error_gettings_followers);
                    Toast.makeText(getContext(), "Ошибка Формата изображения, Введенные данные не верны" + recogniseResponse.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
            //return null;

        }
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private String getUsingOkHttp(String url) {
        try {

         //   Gson gson = new GsonBuilder().create();
         //   String Login_request = gson.toJson(L);

            RequestBody body = new FormBody.Builder()//FormEncodingBuilder()
                    .add("token", Token)
                    .add("img", "data:image/(png|jpeg);base64,"+ Bitmapbase64)
                    .build();


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
