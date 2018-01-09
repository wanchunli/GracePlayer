package com.wan.grace.graceplayer.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.ui.main.MainActivity;

import java.lang.reflect.Method;

/**
 * Created by Xijun.Wang on 2017/11/3.
 */

public class Utilities {

    private static final int NOTIFY_ID = 1111;

    public static boolean isCanUseToastType() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int)method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e("", Log.getStackTraceString(e));
            }
        } else {
            Log.e("Utilities", "Below API 19 cannot invoke!");
        }
        return false;
    }


    public static Notification buildAlarmNotification(Context context){
        Notification notification;
        // bigView.setOnClickPendingIntent() etc..
        Notification.Builder notifyBuilder = new Notification.Builder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,0,new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);
        notification = notifyBuilder.setSmallIcon(R.drawable.ic_change_number)
                .setAutoCancel(false)
                .setContentTitle("屏幕圆角")
                .setContentText("点击设置圆角，使你的设备更圆滑")
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(0)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLocalOnly(true)
                .setColor(context.getColor(R.color.transparent))
                .build();
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(context.getColor(R.color.transparent));
        return notification;
    }
}
