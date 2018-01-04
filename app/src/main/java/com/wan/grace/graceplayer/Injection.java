package com.wan.grace.graceplayer;

import android.content.Context;

import com.wan.grace.graceplayer.manager.AppContext;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:11 PM
 * Desc: Injection
 */
public class Injection {

    public static Context provideContext() {
        return AppContext.getInstance();
    }
}
