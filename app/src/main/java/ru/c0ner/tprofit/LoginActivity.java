package ru.c0ner.tprofit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.c0ner.tprofit.datashema.dataLogin;
import ru.c0ner.tprofit.datashema.dataLoginResponse;
import ru.c0ner.tprofit.datashema.t_SharedStoradge;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public final String ACCESS_TOKEN = "ACCESS_TOKEN";
    final String USERNAME = "USERNAME";
    final String PASSWORD = "PASSWORD";
    final String SAVE_SETTING = "SAVE_SETTING";
    dataLoginResponse Login_response;
    EditText mLogin;
    EditText mPassword;
    CheckBox is_Save;
    String _Login;
    String _Pass;
    Button mButton;
    boolean isSave;
    t_SharedStoradge mStor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mLogin = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_pass);
        is_Save = (CheckBox) findViewById(R.id.login_checkBox);
        mButton = (Button) findViewById(R.id.login_button);
        mButton.setOnClickListener(this);
        mStor = new t_SharedStoradge(getApplicationContext());
        readSavedData();

    }

    void readSavedData(){
        if (mStor !=null) {
            isSave = mStor.getDataBoolean(SAVE_SETTING);
            if (isSave) {
                _Login = mStor.getDataString(USERNAME);
                _Pass = mStor.getDataString(PASSWORD);

            }
            else {
                isSave = false;
            }
        }

    }
    void saveData () {
        if (mStor !=null) {
            mStor.addData(SAVE_SETTING,isSave);
            mStor.addData(USERNAME,_Login);
            mStor.addData(PASSWORD,_Pass);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSave) {
                mLogin.setText(_Login);
                mPassword.setText(_Pass);
                Login();
        }
        is_Save.setChecked(isSave);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button )
        {

                _Login = mLogin.getText().toString();
                _Pass = mPassword.getText().toString();
                if (is_Save.isChecked()) { isSave = true;}
                if (isSave) {
                    saveData();
                }

               // SaveAndEnd();
                Login();
        }

    }
    public void Login ()
    {
        ServerAPI serverAPI = new ServerAPI(this);
        serverAPI.execute(getString(R.string.str_login_api_url));
    }

    public void SaveAndEnd (){
        Intent intent = new Intent(this,t_profit.class);
        intent.putExtra(ACCESS_TOKEN, Login_response.getToken().toString());
        setResult(AppCompatActivity.RESULT_OK, intent);
        startActivity(intent);
        finish();
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
                //mItemList = new ArrayList<>(res);
                //switchButtonState();
                Gson gson = new GsonBuilder().create();
                Login_response = gson.fromJson(res,dataLoginResponse.class);
                //  dataObject[] ret  = gson.fromJson(res, dataObject[].class);

                //   ArrayList m = new ArrayList<dataObject>(Arrays.asList( gson.fromJson(res, dataObject[].class)));
                // mItemList.addAll(Arrays.asList( gson.fromJson(res, dataObject[].class)));
                if (Login_response.getSuccess() == 1) {
                    SaveAndEnd();
                }
                else {
                    Log.d("LOGIN", "onPostExecute: "+Login_response.getMessage());
                    Toast.makeText(getApplicationContext(), ""+Login_response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                //textView.setText(R.string.error_gettings_followers);
                Toast.makeText(getApplicationContext(), "Ошибка авторизации, Введенные данные не верны", Toast.LENGTH_LONG).show();
            }
        }
        //return null;

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
            dataLogin L = new dataLogin(_Login,_Pass);
            Gson gson = new GsonBuilder().create();
            String Login_request = gson.toJson(L);

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
