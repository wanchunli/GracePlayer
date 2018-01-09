package com.wan.grace.graceplayer.fragment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.wan.grace.graceplayer.manager.Constants;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wm on 2016/3/17.
 */
public class BaseFragment extends Fragment {

    public Activity mContext;
    private CompositeSubscription mSubscriptions;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
//        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
//        reloadAdapter();
    }

    @Override
    public void onStop() {
        super.onStop();
//        ((BaseActivity) getActivity()).removeMusicStateListenerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    protected Subscription subscribeEvents() {
        return null;
    }

    protected void addSubscription(Subscription subscription) {
        if (subscription == null) return;
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }
        mSubscriptions.add(subscription);
    }

    /**
     * 检查权限方法
     * @param permissions
     * @return
     */
    public boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if(ContextCompat.checkSelfPermission(getContext(),permission)!=
                    PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 权限请求方法
     * @param code
     * @param permissions
     */
    public void requestPersions(int code,String... permissions){
        ActivityCompat.requestPermissions(getActivity(),permissions,code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case Constants.WRITE_EXTERNAL_CODE:
                doWritePermission();
                break;
            case Constants.READ_EXTERNAL_CODE:
                doReadPermission();
                break;
        }

    }

    /**
     * 默认写内存的权限
     */
    public void doWritePermission(){

    }

    /**
     * 默认读内存的权限
     */
    public void doReadPermission(){

    }

}
