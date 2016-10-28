package com.cy.lib.upgrade.utils;

import android.os.Environment;

import com.cy.lib.upgrade.LibUpgradeInitializer;

import java.io.File;

/**
 * Caiyuan Huang
 * <p>2016/10/28</p>
 * <p>文件工具类</p>
 */
public class FileUtils {

    /**
     * 获取文件保存地址
     *
     * @param fileUrl    文件下载地址
     * @param fileSuffix 文件后缀
     * @return 文件保存路径
     */
    public static String getFileSavePath(String fileUrl, String fileSuffix) {
        String cacheDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // /sdcard/Android/data/<application package>/cache
            cacheDir = LibUpgradeInitializer.getContext().getExternalCacheDir()
                    .getAbsolutePath();
        } else {
            // /data/data/<application package>/file
            cacheDir = LibUpgradeInitializer.getContext().getFilesDir().getAbsolutePath();
        }
        String MD5 = MD5Utils.get32BitsMD5(fileUrl);
        return cacheDir + File.separator + MD5 + fileSuffix;

    }
}
