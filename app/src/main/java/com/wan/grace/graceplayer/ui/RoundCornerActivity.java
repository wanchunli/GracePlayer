package com.wan.grace.graceplayer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.BaseClazzPresenter;
import com.wan.grace.graceplayer.base.MVPBaseActivity;
import com.wan.grace.graceplayer.service.KeepCornerLiveService;
import com.wan.grace.graceplayer.utils.SettingsDataKeeper;
import com.wan.grace.graceplayer.utils.Utilities;
import butterknife.OnClick;

public class RoundCornerActivity extends MVPBaseActivity implements SeekBar.OnSeekBarChangeListener{

    private static final String TAG = "MainActivity";
    private static final String LEFT_TOP = "left_top";
    private static final String LEFT_BOTTOM = "left_bottom";
    private static final String RIGHT_TOP = "right_top";
    private static final String RIGHT_BOTTOM = "right_bottom";
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 2222;
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    TextView title;
    ImageView ivAction;
    RelativeLayout rlCornerEnable;
    Switch swCornerEnable;
    RelativeLayout rlNotifyEnable;
    Switch swNotifyEnable;
    SeekBar sbChangeCornerSize;
    SeekBar sbChangeOpacity;

    TextView tvCornerSize;
    TextView tvOpacity;
    ImageView ivCurrentColor;

    ImageView ivCornerLeftTop;

    ImageView ivCornerLeftBottom;

    ImageView ivCornerRightTop;

    ImageView ivCornerRightBottom;
    private boolean cornerEnable;
    private boolean notifyEnable;
    private int currentOpacity;
    private int currentCornerSize;
    private int currentColor;
    private boolean leftTopEnable;
    private boolean leftBottomEnable;
    private boolean rightTopEnable;
    private boolean rightBottomEnable;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_round_corner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppCornerTheme);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION );
        init();
    }

    protected void init() {
        // settings data
        cornerEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE);
        notifyEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.NOTIFICATION_ENABLE);
        currentCornerSize = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_SIZE);
        currentOpacity = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_OPACITY);
        currentColor =  SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_COLOR);

        leftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        leftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        rightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        rightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
        initViews();
    }

    private void initViews() {
        title = findViewById(R.id.title);
        ivAction = findViewById(R.id.iv_action);
        rlCornerEnable = findViewById(R.id.rl_corner_enable_layout);
        rlCornerEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cornerEnable) {
                    swCornerEnable.setChecked(false);
                } else {
                    swCornerEnable.setChecked(true);
                }
            }
        });
        swCornerEnable = findViewById(R.id.sw_corner_enable);
        swCornerEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                confirmCornerEnable(swCornerEnable.isChecked());
            }
        });
        rlNotifyEnable = findViewById(R.id.rl_notify_enable_layout);
        rlNotifyEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyEnable) {
                    swNotifyEnable.setChecked(false);
                } else {
                    swNotifyEnable.setChecked(true);
                }
            }
        });
        swNotifyEnable = findViewById(R.id.sw_notify_enable);
        swNotifyEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                confirmNotifyEnable(swNotifyEnable.isChecked());
            }
        });

        sbChangeCornerSize = findViewById(R.id.sb_change_corner_size);
        sbChangeOpacity = findViewById(R.id.sb_change_opacity);
        tvCornerSize = findViewById(R.id.tv_corner_size);
        tvOpacity = findViewById(R.id.tv_opacity);
        ivCurrentColor = findViewById(R.id.iv_current_color);
        ivCurrentColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder colorPickDialog = new AlertDialog.Builder(RoundCornerActivity.this);
                View colorPickLayout = View.inflate(RoundCornerActivity.this,R.layout.choose_color_layout,null);
                colorPickDialog.setView(colorPickLayout);
                final ColorPicker picker = (ColorPicker) colorPickLayout.findViewById(R.id.cp_colors_panel);
                ValueBar valueBar = (ValueBar) colorPickLayout.findViewById(R.id.cp_color_value);
                OpacityBar opacityBar = (OpacityBar) colorPickLayout.findViewById(R.id.cp_color_opacity);
                picker.addValueBar(valueBar);
                picker.addOpacityBar(opacityBar);
                picker.setColor(currentColor);
                colorPickDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentColor = picker.getColor();
                        changeCornerColor(currentColor);
                    }
                });
                colorPickDialog.create();
                colorPickDialog.show();
            }
        });
        ivCornerLeftTop = findViewById(R.id.iv_corner_left_top);
        ivCornerLeftTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"showOrHideLeftTopCorner :: leftTopEnable =:" + leftTopEnable);
                if(leftTopEnable){
                    leftTopEnable = false;
                } else {
                    leftTopEnable = true;
                }
                updateLocationFlag(ivCornerLeftTop,leftTopEnable);
                SettingsDataKeeper.writteSettingsBoolean(RoundCornerActivity.this,
                        SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE,leftTopEnable);
                updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE,leftTopEnable);
            }
        });
        ivCornerLeftBottom = findViewById(R.id.iv_corner_left_bottom);
        ivCornerLeftBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leftBottomEnable){
                    leftBottomEnable = false;
                } else {
                    leftBottomEnable = true;
                }
                updateLocationFlag(ivCornerLeftBottom,leftBottomEnable);
                SettingsDataKeeper.writteSettingsBoolean(RoundCornerActivity.this,
                        SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE,leftBottomEnable);
                updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE,leftBottomEnable);
            }
        });
        ivCornerRightTop = findViewById(R.id.iv_corner_right_top);
        ivCornerRightTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightTopEnable){
                    rightTopEnable = false;
                } else {
                    rightTopEnable = true;
                }
                updateLocationFlag(ivCornerRightTop,rightTopEnable);
                SettingsDataKeeper.writteSettingsBoolean(RoundCornerActivity.this,
                        SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE,rightTopEnable);
                updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE,rightTopEnable);
            }
        });
        ivCornerRightBottom = findViewById(R.id.iv_corner_right_bottom);
        ivCornerRightBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightBottomEnable){
                    rightBottomEnable = false;
                } else {
                    rightBottomEnable = true;
                }
                updateLocationFlag(ivCornerRightBottom,rightBottomEnable);
                SettingsDataKeeper.writteSettingsBoolean(RoundCornerActivity.this,
                        SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE,rightBottomEnable);
                updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE,rightBottomEnable);
            }
        });

        ivAction.setImageResource(R.drawable.ic_share);
        ivAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });
        swCornerEnable.setChecked(cornerEnable);
        swNotifyEnable.setChecked(notifyEnable);
        sbChangeCornerSize.setProgress(currentCornerSize);
        sbChangeCornerSize.setMax(100);
        sbChangeCornerSize.setOnSeekBarChangeListener(this);

        sbChangeOpacity.setMax(255);
        sbChangeOpacity.setProgress(currentOpacity);
        sbChangeOpacity.setOnSeekBarChangeListener(this);
        tvCornerSize.setText(currentCornerSize + "");
        tvOpacity.setText(getOpacity(currentOpacity));
        updateColorFlower(currentColor);
        updateLocationFlag(ivCornerLeftTop,leftTopEnable);
        updateLocationFlag(ivCornerLeftBottom,leftBottomEnable);
        updateLocationFlag(ivCornerRightTop,rightTopEnable);
        updateLocationFlag(ivCornerRightBottom,rightBottomEnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cornerEnable = checkPermission();
        swCornerEnable.setChecked(cornerEnable);
    }
    private void updateLocationFlag(ImageView imageView,boolean enable){
        Drawable drawable = imageView.getDrawable();
        int color = enable? getCompatColor(R.color.position_selected_color) : getColor(R.color.black);
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }

    private boolean checkPermission() {
        return Utilities.checkFloatWindowPermission(this);
    }

    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE,true);
            }
        }
    }
    private void confirmNotifyEnable(boolean checked) {
        if (checked) {
            showTips("显示通知");
        } else {
            showTips("取消通知");
        }
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
        updateCornersWithBool(SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
    }

    private void confirmCornerEnable(boolean checked) {
        Log.e(TAG,"confirmCornerEnable :: checked =:" + checked);
        if (checked) {
            if (checkPermission()) {
                SettingsDataKeeper.writteSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, true);
            } else {
                requestPermission();
                showTips("请允许显示悬浮窗");
                return;
            }
        } else {
            SettingsDataKeeper.writteSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, false);
        }
        updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE,checked);
    }
    private void updateCornersWithBool(String key,boolean value){
        Intent intent = new Intent(this, KeepCornerLiveService.class);
        intent.putExtra(key,value);
        intent.setAction(key);
        startService(intent);
    }
    private void updateCornersWithInteger(String key,int value){
        Intent intent = new Intent(this, KeepCornerLiveService.class);
        intent.setAction(key);
        intent.putExtra(key,value);
        startService(intent);
    }
    private void changeOpacity(int opacity) {
        Log.e(TAG, "changeOpacity :: opacity =:" + opacity);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_OPACITY,opacity);
        SettingsDataKeeper.writteSettingsInt(
                RoundCornerActivity.this,SettingsDataKeeper.CORNER_OPACITY,currentOpacity);
    }

    private void changeCornerSize(int cornerSize) {
        Log.e(TAG, "changeCornerSize :: cornerSize =:" + cornerSize);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_SIZE,cornerSize);
        SettingsDataKeeper.writteSettingsInt(
                RoundCornerActivity.this,SettingsDataKeeper.CORNER_SIZE,currentCornerSize);
    }
    private void changeCornerColor(int color) {
        updateColorFlower(color);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_COLOR,color);
        SettingsDataKeeper.writteSettingsInt(RoundCornerActivity.this,SettingsDataKeeper.CORNER_COLOR,currentColor);
    }
    private void updateColorFlower(int color){
        Drawable drawable = getDrawable(R.drawable.ic_color_selected);
        drawable.setTint(color);
        ivCurrentColor.setImageDrawable(drawable);
    }

    private String getOpacity(float opacity) {
        return (int) ((opacity / 255) * 100) + "%";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                currentOpacity = i;
                tvOpacity.setText(getOpacity(currentOpacity));
                break;
            case R.id.sb_change_corner_size:
                currentCornerSize = i;
                tvCornerSize.setText(currentCornerSize + "");
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                changeOpacity(currentOpacity);
                break;
            case R.id.sb_change_corner_size:
                changeCornerSize(currentCornerSize);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean canBack() {
        return super.canBack();
    }

    @Override
    public Boolean isSetRefresh() {
        return super.isSetRefresh();
    }

    @Override
    protected BaseClazzPresenter createPresenter() {
        return null;
    }

    @Override
    protected void showQuickControl(boolean show) {
        super.showQuickControl(false);
    }
}
