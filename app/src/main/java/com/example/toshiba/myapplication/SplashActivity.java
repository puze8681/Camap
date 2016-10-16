package com.example.toshiba.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by TOSHIBA on 2016-08-31.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 1500); // 3초
    }
}
