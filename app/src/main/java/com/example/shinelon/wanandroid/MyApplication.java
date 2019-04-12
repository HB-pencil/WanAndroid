package com.example.shinelon.wanandroid;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.example.shinelon.wanandroid.helper.WordsSQLiteHelper;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {
    private static Context context ;
    private static WordsSQLiteHelper sqLiteHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        sqLiteHelper =  new WordsSQLiteHelper(context,1);
        LeakCanary.install(this);
    }

    public static Context getAppContext(){
        return context;
    }

    public static WordsSQLiteHelper getSqLiteHelper() {
        return sqLiteHelper;
    }
}
