package com.wan.grace.graceplayer.manager;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wan.grace.graceplayer.utils.GetBaiduLocation;

/**
 * Created by 开发部 on 2017/12/21.
 */

public class AppContext extends Application{

    private static AppContext sInstance;
    public GetBaiduLocation baiduLocation;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //初始化Fresco
        Fresco.initialize(getApplicationContext());
        baiduLocation = new GetBaiduLocation(this);
    }

    public static AppContext getInstance() {
        return sInstance;
    }

    public GetBaiduLocation getBaiduLocation(){
        if(baiduLocation == null){
            baiduLocation = new GetBaiduLocation(this);
        }
        return baiduLocation;
    }
}
