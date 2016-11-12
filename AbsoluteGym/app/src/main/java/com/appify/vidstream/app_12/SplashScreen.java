package com.appify.vidstream.app_12;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.appify.vidstream.utility.CheckInternetConnection;

public class SplashScreen extends AppCompatActivity {

    private CheckInternetConnection cic;
    private Boolean isInternetPresent = false;
    private int TIME = 1 * 1000;// 1 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                cic = new CheckInternetConnection(getApplicationContext());
                isInternetPresent = cic.isConnectingToInternet();
                if (!isInternetPresent) {
                    Intent intent = new Intent(SplashScreen.this,
                            NoInternetScreen.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }else{
                    Intent intent = new Intent(SplashScreen.this,
                            CategorizationScreen.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            }
        },TIME);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        }
        catch (Exception ignored){
            return true;
        }
    }
}
