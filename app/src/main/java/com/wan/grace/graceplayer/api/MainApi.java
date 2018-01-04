package com.wan.grace.graceplayer.api;

import com.wan.grace.graceplayer.bean.WeatherInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Werb on 2016/8/18.
 * Werb is Wanbo.
 * Contact Me : werbhelius@gmail.com
 * get Zhihu with retrofit
 */
public interface MainApi {
    public static final String WEATHER_TAG = "&output=json&ak=KfbSjSdHgX838fY8aQyKSblQDclZKMOf";

    @GET("weather" + WEATHER_TAG)
    Observable<WeatherInfo> getDetailWeather(@Query("location") String location);
}
