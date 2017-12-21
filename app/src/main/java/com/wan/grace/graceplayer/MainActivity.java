package com.wan.grace.graceplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wan.grace.graceplayer.handler.HandlerUtil;
import com.wan.grace.graceplayer.widget.SplashScreen;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends AppCompatActivity {

    private SplashScreen splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = new SplashScreen(this);
        splashScreen.show(R.mipmap.art_login_bg,
                SplashScreen.CENTER_OUT);
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
