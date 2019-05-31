package map.test.testmap.utils;


import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 应用角标提示数工具类
 */

public class BadgeUtil {

    /**
     * 设置角标提示数
     *
     * @param context The context of the application package.
     * @param count   Badge count to be set
     */
    public static void setBadgeCount(Context context, int count) {
        Log.d("BadgeUtil", "setBadgeCount: Build.MANUFACTURER--> " + Build.MANUFACTURER);
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            sendToXiaomi(context, count);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            sendToSony(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung") ||
                Build.MANUFACTURER.toLowerCase().contains("lg")) {
            sendToSamsumgOrLG(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("huawei")||Build.BRAND.equals("Huawei")||Build.BRAND.equals("HONOR")) {
            setHuaweiBadgeCount(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("google")) {
            try {
                sendToGoogle(context, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            try {
                sendToVivo(context, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            try {
                sendToHTC(context, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {
            setNovaBadgeCount(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("OPPO")) {
            setOPPOBadgeCount(context, count);
        } else {
//            Toast.makeText(context,"不支持角标提示数",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 小米（未验证）
     * 官方文档：https://dev.mi.com/console/doc/detail?pId=939
     */
    private static void sendToXiaomi(Context context, int count) {
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, String.valueOf(count == 0 ? "" : count));  // 设置信息数-->这种发送必须是miui 6才行
        } catch (Exception e) {
            e.printStackTrace();
            // miui 6之前的版本
            Intent localIntent = new Intent(
                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra(
                    "android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/" + getLauncherClassName(context));
            localIntent.putExtra(
                    "android.intent.extra.update_application_message_text", String.valueOf(count == 0 ? "" : count));
            context.sendBroadcast(localIntent);
        }
    }


    /**
     * 索尼（未验证）
     * 需添加权限：<uses-permission android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" />
     */
    private static void sendToSony(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }


    /**
     * 三星、LG（未验证）
     */
    private static void sendToSamsumgOrLG(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);

        context.sendBroadcast(intent);
    }


    /**
     * 华为（已验证通过）
     * 需添加权限： <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE"/>
     * 官方文档：https://developer.huawei.com/consumer/cn/devservice/doc/30802
     */
    private static void setHuaweiBadgeCount(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);//桌面图标对应的应用入口Activity类
        Bundle localBundle = new Bundle();//需要存储的数据
        localBundle.putString("package", context.getPackageName());//包名
        localBundle.putString("class", launcherClassName);
        localBundle.putInt("badgenumber", count);//角标数字
        context.getApplicationContext().getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
    }


    /**
     * 谷歌（未验证）
     * Android8.0开始支持,只能在图标上显示一个点,长按这个点,能弹出一个小pop提示有多少消息
     */
    private static void sendToGoogle(Context context, int count) throws Exception {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            throw new Exception("ERROR_LAUNCHER_NOT_SUPPORT:  Google");
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));

        context.sendBroadcast(intent);
    }


    /**
     * Vivo: vivoXplay5 vivo x7无效果（未验证）
     */
    private static void sendToVivo(Context context, int count) throws Exception {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", getLauncherClassName(context));
        intent.putExtra("notificationNum", count);

        context.sendBroadcast(intent);
    }


    /**
     * HTC（未验证）
     * 需添加权限：<uses-permission android:name="com.htc.launcher.permission.READ_SETTINGS"/>
     * <uses-permission android:name="com.htc.launcher.permission.UPDATE_SHORTCUT"/>
     */
    private static void sendToHTC(Context context, int count) throws Exception {
        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        ComponentName localComponentName = new ComponentName(context.getPackageName(), getLauncherClassName(context));
        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
        intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);
        context.sendBroadcast(intentNotification);

        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra("packagename", context.getPackageName());
        intentShortcut.putExtra("count", count);
        context.sendBroadcast(intentShortcut);
    }


    /**
     * Nova（未验证）
     */
    private static void setNovaBadgeCount(Context context, int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag", context.getPackageName() + "/" + getLauncherClassName(context));
        contentValues.put("count", count);
        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"),
                contentValues);
    }


    /**
     * OPPO :OPPO角标提示数目前只针对内部软件还有微信、QQ开放，其他的暂时无法提供（未验证）
     */
    private static void setOPPOBadgeCount(Context context,int count){
        try {
            Bundle extras = new Bundle();
            extras.putInt("app_badge_count", count);
            context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", String.valueOf(count), extras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 重置、清除Badge未读显示数<br/>
     *
     * @param context
     */
    public static void resetBadgeCount(Context context) {
        setBadgeCount(context, 0);
    }


    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }
}

