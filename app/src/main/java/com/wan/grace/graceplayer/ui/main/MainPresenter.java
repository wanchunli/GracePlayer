package com.wan.grace.graceplayer.ui.main;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.BaseClazzPresenter;
import com.wan.grace.graceplayer.bean.WeatherInfo;
import com.wan.grace.graceplayer.bean.WeatherInfo.ResultsBean.WeatherDataBean;
import com.wan.grace.graceplayer.manager.AppContext;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 开发部 on 2018/1/4.
 */

public class MainPresenter extends BaseClazzPresenter<MainView> {

    private Context context;
    private MainView mainView;

    public MainPresenter(Context context) {
        this.context = context;
    }

    public String loadDistrict(AppContext ac) {
        //加载地区
        String district = (String) ac.getBaiduLocation().getMap().get("district");
        if (district != null && !district.equals("")) {
            return district;
        } else {
            String province = (String) ac.getBaiduLocation().getMap().get("province");
            if (province != null && !province.equals("")) {
                return province;
            } else {
                return "深圳";
            }
        }
    }

    public void loadWeather(AppContext ac) {
        String longitude = (String) ac.getBaiduLocation().getMap().get("longitude");
        String latitude = (String) ac.getBaiduLocation().getMap().get("latitude");
        String location = longitude + "," + latitude;
        Log.i("location",location);
        mainView = getView();
        if (mainView != null) {
            mainApi.getDetailWeather(location)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weatherInfo -> {
                        displayWeather(context, weatherInfo, mainView);
                    }, this::loadError);
        }
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT).show();
    }

    private void displayWeather(Context context, WeatherInfo weatherInfo, MainView mainView) {
        String date = "";
        String temperature = "";
        WeatherDataBean mWeatherDataBean = new WeatherDataBean();
        //加载温度
        List<WeatherDataBean> weather_data = weatherInfo.getResults().get(0).getWeather_data();
        for (int i = 0; i < weather_data.size(); i++) {
            WeatherDataBean weatherDataBean = weather_data.get(i);
            date = weatherDataBean.getDate();// "date": "周四 10月13日 (实时：14℃)",
            if (date.contains("(")) {
                String[] split = date.split("：");
                temperature = split[1].substring(0, split[1].length() - 1);
                //加载天气图片
                mWeatherDataBean = weatherDataBean;
                break;
            }
        }
        mainView.setViewRefresh(date, temperature, mWeatherDataBean, true);
    }

}
