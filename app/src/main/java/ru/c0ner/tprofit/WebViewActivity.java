package ru.c0ner.tprofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static ru.c0ner.tprofit.R.string.callback_url;


/**
 * Created by kirillf on 10/25/16.
 */

public class WebViewActivity extends AppCompatActivity implements AuthorizationListener {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String AUTH_ERROR = "error";

    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_PASSWORD = "PASSWORD";

    private String username;
    private String password;
    public static int STATUS_OK = 200;

    private String authUrlTemplate;
    private String redirectUrl;

    private WebView webView;

    public static Intent createAuthActivityIntent(Context context, String username, String password) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        webView = (WebView) findViewById(R.id.web_view);
        authUrlTemplate = getString(R.string.auth_url);
        redirectUrl = getString(callback_url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onAuthStarted();
        String url = String.format(authUrlTemplate);
        URI uri = URI.create(url);
     //   String cookie = "sessionid=wc68dqzz05xnwau07syu7tdj9m2k3zvm; csrftoken=8MK2v11Mg5h3uuEmLTRAi0jXCEsdk7e9YAY5gzkbb7rPRefaPAR75n9lXcUYwwai";
     //   CookieManager cookieManager = CookieManager.getInstance();
     //   cookieManager.setCookie(uri.toString(),cookie);
        webView.setWebViewClient(new OAuthWebClient(this));
        webView.loadUrl(uri.toString());
    }

    @Override
    public void onAuthStarted() {

    }

    @Override
    public void onComplete(String token) {
        Intent intent = new Intent(this,t_profit.class);
        intent.putExtra(ACCESS_TOKEN, token);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String error) {
       // Intent intent = new Intent();
       // intent.putExtra(AUTH_ERROR, error);
       // setResult(Activity.RESULT_CANCELED, intent);
        Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
        finish();
    }

    private class OAuthWebClient extends WebViewClient {
        private AuthorizationListener listener;

        public OAuthWebClient(AuthorizationListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.startsWith(getString(R.string.login_soc_url)) ) {
                Log.d("URL4:","Сераница загрузиласть вот такой URL -" + url);
                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("URL4", "All the cookies in a string:" + cookies);
                onComplete(cookies);
            }

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            listener.onError(error.toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
           // Log.d("URL3:","Сераница загрузиласть вот такой URL -" + url);
           // String cookies = CookieManager.getInstance().getCookie(url);
           // Log.d("URL3", "All the cookies in a string:" + cookies);
        }
    }


}
