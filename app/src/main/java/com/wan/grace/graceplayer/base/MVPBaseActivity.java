package com.wan.grace.graceplayer.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.fragment.GraceControlFragment;
import com.wan.grace.graceplayer.manager.Constants;

import butterknife.ButterKnife;

/**
 * Created by Werb on 2016/7/25.
 * Werb is Wanbo.
 * Contact Me : werbhelius@gmail.com
 * Base of Activity
 */
public abstract class MVPBaseActivity<V, T extends BaseClazzPresenter<V>> extends AppCompatActivity {

    protected T mPresenter;
    protected Toolbar mToolbar;
    private GraceControlFragment fragment; //底部播放控制栏
    private String TAG = "BaseActivity";
    protected Handler mBackHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        //允许为空，不是所有都要实现MVP模式
        showQuickControl(true);
        if (createPresenter() != null) {
            mPresenter = createPresenter();
            mPresenter.attachView((V) this);
        }
        setContentView(provideContentViewId());//布局

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar); //把Toolbar当做ActionBar给设置
            if (canBack()) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null)
                    actionBar.setDisplayHomeAsUpEnabled(true);//设置ActionBar一个返回箭头，主界面没有，次级界面有
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 此时android.R.id.home即为返回箭头
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 为子类提供监听事件的方法
     */
    public void setListener(){

    }

    /**
     * 判断当前 Activity 是否允许返回
     * 主界面不允许返回，次级界面允许返回
     *
     * @return false
     */
    public boolean canBack() {
        return false;
    }

    /**
     * 判断子Activity是否需要刷新功能
     *
     * @return false
     */
    public Boolean isSetRefresh() {
        return false;
    }

    public void showTips(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public int getCompatColor(int color) {
        return ContextCompat.getColor(this, color);
    }

    protected abstract T createPresenter();

    abstract protected int provideContentViewId();//用于引入布局文件

    /**
     * @param show 显示或关闭底部播放控制栏
     */
    protected void showQuickControl(boolean show) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show) {
            if (fragment == null) {
                fragment = GraceControlFragment.newInstance();
                ft.add(R.id.bottom_container, fragment).commitAllowingStateLoss();
            } else {
                ft.show(fragment).commitAllowingStateLoss();
            }
        } else {
            if (fragment != null)
                ft.hide(fragment).commitAllowingStateLoss();
        }
    }

    /**
     * 检查权限方法
     * @param permissions
     * @return
     */
    public boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this,permission)!=
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
        ActivityCompat.requestPermissions(this,permissions,code);
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
