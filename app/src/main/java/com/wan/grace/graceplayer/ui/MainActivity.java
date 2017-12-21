package com.wan.grace.graceplayer.ui;

import android.os.Bundle;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.handler.HandlerUtil;
import com.wan.grace.graceplayer.widget.SplashScreen;

public class MainActivity extends BaseActivity {

    private SplashScreen splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = new SplashScreen(this);
        splashScreen.show(R.mipmap.art_login_bg,
                SplashScreen.SLIDE_LEFT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
