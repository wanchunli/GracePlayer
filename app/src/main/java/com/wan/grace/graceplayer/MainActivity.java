package com.wan.grace.graceplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppName("GracePlayer")
                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setAnimationFinishTime(100)
                .setAnimationInterval(5000)
                .create();
        openingStartAnimation.show(this);
    }
}
