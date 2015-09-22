package com.phongbm.common;

import android.app.Application;

import com.facebook.FacebookSdk;

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }

}