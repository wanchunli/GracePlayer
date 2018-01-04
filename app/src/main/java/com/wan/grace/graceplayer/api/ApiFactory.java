package com.wan.grace.graceplayer.api;

/**
 * Created by Werb on 2016/8/18.
 * Werb is Wanbo.
 * Contact Me : werbhelius@gmail.com
 * Singleton Factory with retrofit
 */
public class ApiFactory {

    protected static final Object monitor = new Object();
    static MainApi mainApiSingleton = null;

    //return Singleton
    public static MainApi getMainApiSingleton() {
        synchronized (monitor) {
            if (mainApiSingleton == null) {
                mainApiSingleton = new ApiRetrofit().getMainApiService();
            }
            return mainApiSingleton;
        }
    }
}
