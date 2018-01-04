package com.wan.grace.graceplayer.base;

import com.wan.grace.graceplayer.api.ApiFactory;
import com.wan.grace.graceplayer.api.MainApi;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Werb on 2016/8/18.
 * Werb is Wanbo.
 * Contact Me : werbhelius@gmail.com
 * Base of Presenter
 */
public abstract class BaseClazzPresenter<V> {

    protected Reference<V> mViewRef;

    public static final MainApi mainApi = ApiFactory.getMainApiSingleton();

    public void attachView(V view){
        mViewRef = new WeakReference<V>(view);
    }

    protected V getView(){
        return mViewRef.get();
    }

    public boolean isViewAttached(){
        return mViewRef != null&&mViewRef.get()!=null;
    }

    public void detachView(){
        if(mViewRef!=null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

}