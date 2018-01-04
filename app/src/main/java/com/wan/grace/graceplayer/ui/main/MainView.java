package com.wan.grace.graceplayer.ui.main;

import com.wan.grace.graceplayer.bean.WeatherInfo;

/**
 * Created by 开发部 on 2018/1/4.
 */

public interface MainView {
    void setViewRefresh(String date, String temperature, WeatherInfo.ResultsBean.WeatherDataBean weatherDataBean, boolean refresh);
}
