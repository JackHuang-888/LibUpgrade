package com.cy.lib.upgrade.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Caiyuan Huang
 * <p>2016/10/26</p>
 * <p>APP信息工具类</p>
 */
public class AppInfoUtils {

    /**
     * 获取已安装apk的路径
     *
     * @param context apk的上下文
     * @return apk文件路径
     */
    public static String getApkPath(Context context) {
        if (context != null) {
            ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
            return applicationInfo.sourceDir;
        }
        return "";
    }

    /**
     * 获取apk的版本号
     *
     * @param context apk的上下文
     * @return 若获取成功则返回版本号，否则返回-1
     */
    public static int getApkVersionCode(Context context) {
        int versionCode = -1;
        if (context != null) {
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                versionCode = info.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                versionCode = -1;
            }
        }
        return versionCode;
    }


}
