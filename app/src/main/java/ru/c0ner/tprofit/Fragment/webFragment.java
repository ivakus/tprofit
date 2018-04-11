package ru.c0ner.tprofit.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import ru.c0ner.tprofit.R;

// фрагмент для отображения WEB Контента
public class webFragment extends Fragment{

    public static String TAG = "webFargmentTAG";

    WebView mWebView ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_webview,container,false);
       // mWebView = v.findViewById(R.id.fargment_webview_webvie);
        return v;
    }
}
