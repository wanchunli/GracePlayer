package com.wan.grace.graceplayer.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.adapter.CustomViewPagerAdapter;
import com.wan.grace.graceplayer.base.MVPBaseActivity;
import com.wan.grace.graceplayer.bean.WeatherInfo;
import com.wan.grace.graceplayer.fragment.MainFragment;
import com.wan.grace.graceplayer.fragment.NetPlayerFragment;
import com.wan.grace.graceplayer.handler.HandlerUtil;
import com.wan.grace.graceplayer.manager.AppContext;
import com.wan.grace.graceplayer.ui.RoundCornerActivity;
import com.wan.grace.graceplayer.widget.CustomViewPager;
import com.wan.grace.graceplayer.widget.SplashScreen;
import com.wan.grace.graceplayer.bean.WeatherInfo.ResultsBean.WeatherDataBean;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 主界面
 */
public class MainActivity extends MVPBaseActivity<MainView, MainPresenter> implements MainView {

    private long time = 0;
    private ActionBar ab;
    private SplashScreen splashScreen;
    private ArrayList<ImageView> tabs = new ArrayList<>();
    private ImageView barnet, barmusic, barfriends, search;
    private DrawerLayout drawerLayout;
    private CustomViewPager customViewPager;
    private CustomViewPagerAdapter customViewPagerAdapter;
    private AppContext ac;
    private ImageView mweathericon;
    private TextView mTemperature;
    private TextView mAddress;

    private ImageView graceImage;
    private ImageView graceImageBg;
    private SuperTextView cornerSettingLayout;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = new SplashScreen(this);
        splashScreen.show(R.mipmap.loading_bg,
                SplashScreen.SLIDE_LEFT);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        initView();
        setToolBar();
        setViewPager();
        ac = (AppContext) getApplicationContext();
        HandlerUtil.getInstance(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen.removeSplashScreen();
            }
        }, 3000);
//        mPresenter.loadWeather(ac);
        String longitude = (String) ac.getBaiduLocation().getMap().get("longitude");
        String latitude = (String) ac.getBaiduLocation().getMap().get("latitude");
        String location = longitude + "," + latitude;
        Log.i("location", location);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("GracePlayer");
    }

    private void initView() {
        barnet = (ImageView) findViewById(R.id.bar_net);
        barmusic = (ImageView) findViewById(R.id.bar_music);
        barfriends = (ImageView) findViewById(R.id.bar_friends);
        search = (ImageView) findViewById(R.id.bar_search);
        barmusic = (ImageView) findViewById(R.id.bar_music);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mweathericon = (ImageView) findViewById(R.id.weathericon);
        mTemperature = (TextView) findViewById(R.id.wendu);
        mAddress = (TextView) findViewById(R.id.address);
        graceImageBg = findViewById(R.id.grace_image_bg);
        loadGraceBg(graceImageBg);
        graceImage = findViewById(R.id.grace_image);
        loadGraceBg(graceImage);
        cornerSettingLayout = findViewById(R.id.corner_setting);

    }

    private void loadGraceBg(ImageView graceImage) {
        Glide.with(MainActivity.this)
                .load("file:///android_asset/me.jpg")
                .error(R.drawable.me)
                .placeholder(R.drawable.me)
                .crossFade(1000)
                .bitmapTransform(new BlurTransformation(this, 15, 3))
                .into(graceImage);
    }

    private void setViewPager() {
        tabs.add(barnet);
        tabs.add(barmusic);
        customViewPager = (CustomViewPager) findViewById(R.id.custom_viewpager);
        MainFragment mainFragment = new MainFragment();
        NetPlayerFragment tabNetPlayerFragment = new NetPlayerFragment();
        customViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        customViewPagerAdapter.addFragment(tabNetPlayerFragment);
        customViewPagerAdapter.addFragment(mainFragment);
        customViewPager.setAdapter(customViewPagerAdapter);
        customViewPager.setCurrentItem(1);
        barmusic.setSelected(true);
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        barnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewPager.setCurrentItem(0);
            }
        });
        barmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewPager.setCurrentItem(1);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent intent = new Intent(MainActivity.this, NetSearchWordsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                MainActivity.this.startActivity(intent);
            }
        });
        cornerSettingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoundCornerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home: //Menu icon
                mPresenter.loadWeather(ac);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchTabs(int position) {
        for (int i = 0; i < tabs.size(); i++) {
            if (position == i) {
                tabs.get(i).setSelected(true);
            } else {
                tabs.get(i).setSelected(false);
            }
        }
    }

    @Override
    public void setViewRefresh(String date, String temperature, WeatherDataBean weatherDataBean, boolean refresh) {
        mTemperature.setText(temperature);
        setWeatherImage(weatherDataBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashScreen.removeSplashScreen();
    }

    /**
     * 功能：获取天气图片
     */
    private void setWeatherImage(WeatherInfo.ResultsBean.WeatherDataBean weatherDataBean) {
        String weather = weatherDataBean.getWeather();
        if (weather.equals("晴")) {
            mweathericon.setBackgroundResource(R.drawable.w_qing_00);
        } else if (weather.equals("多云")) {
            mweathericon.setBackgroundResource(R.drawable.w_duoyun_01);
        } else if (weather.equals("阴")) {
            mweathericon.setBackgroundResource(R.drawable.w_yin_02);
        } else if (weather.equals("阵雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhenyu_03);
        } else if (weather.equals("雷阵雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_leizhenyu_04);
        } else if (weather.equals("雷阵雨伴有冰雹")) {
            mweathericon.setBackgroundResource(R.drawable.w_leizhenyubanyoubingbao_05);
        } else if (weather.equals("雨夹雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_yujiaxue_06);
        } else if (weather.equals("小雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_xiaoyu_07);
        } else if (weather.equals("中雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhongyu_08);
        } else if (weather.equals("大雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dayu_09);
        } else if (weather.equals("暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_baoyu_10);
        } else if (weather.equals("大暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dabaoyu_11);
        } else if (weather.equals("特大暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_tedabaoyu_12);
        } else if (weather.equals("阵雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhenxue_13);
        } else if (weather.equals("小雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_xiaoxue_14);
        } else if (weather.equals("中雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhongxue_15);
        } else if (weather.equals("大雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_daxue_16);
        } else if (weather.equals("暴雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_baoxue_17);
        } else if (weather.equals("雾")) {
            mweathericon.setBackgroundResource(R.drawable.w_wu_18);
        } else if (weather.equals("冻雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dongyu_19);
        } else if (weather.equals("沙尘暴")) {
            mweathericon.setBackgroundResource(R.drawable.w_sachenbao_20);
        } else if (weather.equals("小雨转中雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_xiaodaozhongyu_21);
        } else if (weather.equals("中雨转大雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhongdadayu_22);
        } else if (weather.equals("大雨转暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dadaobaoyu_23);
        } else if (weather.equals("暴雨转大暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dayudaodabaoyu_24);
        } else if (weather.equals("大暴雨转特大暴雨")) {
            mweathericon.setBackgroundResource(R.drawable.w_dabaoyudaotedabaoyu_25);
        } else if (weather.equals("小雪转中雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_xiaodaozhongxue_26);
        } else if (weather.equals("中雪转大雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_zhongdaodaxue_27);
        } else if (weather.equals("大雪转暴雪")) {
            mweathericon.setBackgroundResource(R.drawable.w_dadaobaoxue_28);
        } else if (weather.equals("浮尘")) {
            mweathericon.setBackgroundResource(R.drawable.w_fuchen_29);
        } else if (weather.equals("扬沙")) {
            mweathericon.setBackgroundResource(R.drawable.w_yangsha_30);
        } else if (weather.equals("强沙尘暴")) {
            mweathericon.setBackgroundResource(R.drawable.w_qiangshachenbao_31);
        } else if (weather.equals("霾")) {
            mweathericon.setBackgroundResource(R.drawable.w_mai_53);
        }
    }

    /**
     * 双击返回桌面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - time > 1000)) {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
