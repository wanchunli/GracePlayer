package com.wan.grace.graceplayer.api;

import com.wan.grace.graceplayer.manager.AppContext;
import com.wan.grace.graceplayer.utils.StateUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Werb on 2016/8/18.
 * Werb is Wanbo.
 * Contact Me : werbhelius@gmail.com
 * retrofit instance
 */
public class ApiRetrofit {

    public MainApi mainApiService;
    public NetPlayApi netPlayApiService;
    public static final String MAIN_BASE_URL = "http://api.map.baidu.com/telematics/v3/";
    public static final String NET_PLAY_BASE_URL = "http://news-at.zhihu.com/api/4/";
    public static final String GANK_BASE_URL = "http://gank.io/api/";
    public static final String DAILY_BASE_URL = "http://app3.qdaily.com/app3/";

    public MainApi getMainApiService() {
        return mainApiService;
    }

    public NetPlayApi getNetPlayApiService() {
        return netPlayApiService;
    }

    ApiRetrofit() {
        //cache url
        File httpCacheDirectory = new File(AppContext.getInstance().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (!StateUtils.isNetworkAvailable(AppContext.getInstance())) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();

            }
            Response originalResponse = chain.proceed(request);
            if (StateUtils.isNetworkAvailable(AppContext.getInstance())) {
                int maxAge = 0; // read from cache
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache).build();

        Retrofit retrofit_main = new Retrofit.Builder()
                .baseUrl(MAIN_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofit_netplay = new Retrofit.Builder()
                .baseUrl(NET_PLAY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofit_zhihu = new Retrofit.Builder()
                .baseUrl(NET_PLAY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofit_gank = new Retrofit.Builder()
                .baseUrl(GANK_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Retrofit retrofit_daily= new Retrofit.Builder()
                .baseUrl(DAILY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mainApiService = retrofit_main.create(MainApi.class);
    }

}
