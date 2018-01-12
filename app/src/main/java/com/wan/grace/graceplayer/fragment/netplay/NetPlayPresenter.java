package com.wan.grace.graceplayer.fragment.netplay;

import android.content.Context;

import com.wan.grace.graceplayer.base.BaseClazzPresenter;
import com.wan.grace.graceplayer.base.BasePresenter;
import com.wan.grace.graceplayer.ui.main.MainView;

/**
 * Created by 开发部 on 2018/1/12.
 */

public class NetPlayPresenter extends BaseClazzPresenter<NetPlayView> {

    private Context context;
    private NetPlayView netPlayView;

    public NetPlayPresenter(Context context) {
        this.context = context;
    }
}
