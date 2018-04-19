package ru.c0ner.tprofit.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import ru.c0ner.tprofit.Adapters.DownLoadIMage;
import ru.c0ner.tprofit.Adapters.Person_Adapter;
import ru.c0ner.tprofit.R;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataRecogniseResponse;

public class t_SelectPerson extends Fragment implements View.OnClickListener{
    Context _context;
    dataRecogniseResponse recogniseResponse ;

    ImageView mImageView;
    RecyclerView rw;
    Button send,vibor;
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
       mImageView = v.findViewById(R.id.select_im);
       rw = v.findViewById(R.id.select_recicle);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rw.setLayoutManager(mLayoutManager);
        rw.setVisibility(View.INVISIBLE);
        mAdapter = new Person_Adapter(getContext());
        mAdapter.set_memoryCache(_memoryCache);
        mAdapter.setItemList(mItemList);


       // mBtnSend = v.findViewById(R.id.recognise_send);
       // mBtnSend.setOnClickListener(this);
        DownLoadIMage dnImage = new DownLoadIMage(getContext());
        dnImage.set_memoryCache(_memoryCache);
        String img_url = _context.getResources().getString(R.string.str_t_site)+recogniseResponse.getUrl().toString();
        dnImage.loadBitmap(_context,img_url,mImageView);
        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
