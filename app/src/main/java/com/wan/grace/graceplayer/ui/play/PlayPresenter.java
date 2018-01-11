package com.wan.grace.graceplayer.ui.play;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.BaseClazzPresenter;
import com.wan.grace.graceplayer.bean.WeatherInfo;
import com.wan.grace.graceplayer.bean.WeatherInfo.ResultsBean.WeatherDataBean;
import com.wan.grace.graceplayer.manager.AppContext;
import com.wan.grace.graceplayer.ui.main.MainView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 开发部 on 2018/1/4.
 */

public class PlayPresenter extends BaseClazzPresenter<PlayView> {

    private Context context;
    private PlayView playView;

    public PlayPresenter(Context context) {
        this.context = context;
    }

}
