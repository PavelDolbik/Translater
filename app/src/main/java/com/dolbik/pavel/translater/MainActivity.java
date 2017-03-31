package com.dolbik.pavel.translater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    static {
        // Для использования vector на pre lollipop. (To use a vector to pre lollipop.)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            TApplication.getRefWatcher(this).watch(this);
        }
    }

}
