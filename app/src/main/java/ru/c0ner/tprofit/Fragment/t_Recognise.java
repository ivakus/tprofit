package ru.c0ner.tprofit.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.dataObject_Status;

public class t_Recognise extends Fragment {

    ImageView mImageView;
    Button mBtnSend;
    String curent_photo_name;
    String curent_photo_path;
    Bitmap curent_photo;

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

        return v;
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
            mImageView.setImageBitmap(curent_photo);
        }
    }
}
