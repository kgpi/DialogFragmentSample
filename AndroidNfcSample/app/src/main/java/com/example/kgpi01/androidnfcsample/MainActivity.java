package com.example.kgpi01.androidnfcsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickPendingIntentActivityButton(View v) {
        startActivity(new Intent(this, PendingIntentActivity.class));
    }

    public void onClickCallbackActivityButton(View v) {
        //startActivity(new Intent(this, CallbackActivity.class));
    }
}
