package com.wan.grace.graceplayer.manager;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wan.grace.graceplayer.ui.MainActivity;

/**
 * Created by 开发部 on 2017/12/21.
 */

public class AppContext extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Fresco
        Fresco.initialize(getApplicationContext());
    }
}
