package com.wan.grace.graceplayer.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.handler.HandlerUtil;
import com.wan.grace.graceplayer.widget.SplashScreen;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

    private ActionBar ab;
    private SplashScreen splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = new SplashScreen(this);
        splashScreen.show(R.mipmap.loading_bg,
                SplashScreen.SLIDE_LEFT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();
//        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
//                .setDrawStategy(new NormalDrawStrategy())
//                .setAppName("GracePlayer")
//                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
//                .setAnimationFinishTime(100)
//                .setAnimationInterval(5000)
//                .create();
//        openingStartAnimation.show(this);
        HandlerUtil.getInstance(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen.removeSplashScreen();
            }
        }, 3000);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("GracePlayer");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashScreen.removeSplashScreen();
    }
}
