package com.wan.grace.graceplayer.api;

import com.wan.grace.graceplayer.bean.WeatherInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 开发部 on 2018/1/12.
 */

public interface NetPlayApi {

    @GET("weather?")
    Observable<WeatherInfo> getDetailWeather(@Query("location") String location);
}
