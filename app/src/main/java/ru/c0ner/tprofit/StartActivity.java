package ru.c0ner.tprofit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public  class StartActivity extends AppCompatActivity {
    public final int ACTIVITY_LOGIN = 200;
    public final String ACCESS_TOKEN = "ACCESS_TOKEN";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, ACTIVITY_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) { return; }
        else {
            if (requestCode == AppCompatActivity.RESULT_OK) {
                String Token = data.getStringExtra(ACCESS_TOKEN);
                Intent intent = new Intent(this, t_profit.class);
                intent.putExtra(ACCESS_TOKEN, Token);
                startActivity(intent);
                finish();
            }
            if (requestCode == AppCompatActivity.RESULT_CANCELED)
            {
                finish();
                return;
            }
        }
    }
}