package com.cy.lib.upgrade.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.cy.lib.upgrade.LibUpgradeInitializer;
import com.cy.lib.upgrade.bsdiff.BsDiff;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Caiyuan Huang
 * <p>2016/10/28</p>
 * <p>安装工具包</p>
 */
public class InstallUtils {
    /**
     * 提示安装apk文件
     *
     * @param context     上下文
     * @param apkFilePath apk文件的绝对路径
     */
    public static void notifyInstallApk(Context context, String apkFilePath) {
        if (context != null && !TextUtils.isEmpty(apkFilePath) && (new File(apkFilePath).exists())) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(new File(apkFilePath)),
                    "application/vnd.android.package-archive");
            context.startActivity(i);
        }
    }

    /**
     * 是否支持静默安装
     *
     * @return true表示支持静默安装，否则不支持
     */
    public static boolean isSupportSilentInstall() {
        boolean isSupport = false;
        try {
            isSupport = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSupport;
    }


    /**
     * 静默安装
     *
     * @param apkFilePath apk文件路径
     * @return true表示安装成功，否则返回false
     */
    public static boolean silentInstall(String apkFilePath) {
        boolean isInstallOk = false;
        if (isSupportSilentInstall()) {
            DataOutputStream dataOutputStream = null;
            BufferedReader bufferedReader = null;
            try {
                Process process = Runtime.getRuntime().exec("su");
                dataOutputStream = new DataOutputStream(process.getOutputStream());
                String command = "pm install -r " + apkFilePath + "\n";
                dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                dataOutputStream.flush();
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
                process.waitFor();
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder msg = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    msg.append(line);
                }
                if (msg.toString().contains("Success")) {
                    isInstallOk = true;
                }
            } catch (Exception e) {
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return isInstallOk;
    }

    /**
     * 合并补丁
     *
     * @param oldApkPath 旧版apk文件路径
     * @param newApkPath 新版apk文件路径
     * @param patchPath  补丁文件路径
     * @param newApkMD5  新版apk文件的MD5值
     * @return 若合并成功返回true，否则返回false
     */
    public static boolean mergePatch(String oldApkPath, String newApkPath, String patchPath, String newApkMD5) {
        boolean isMergeOk = false;
        try {
            File newApkFile = new File(newApkPath);
            File oldApkFile = new File(oldApkPath);
            File patchFile = new File(patchPath);
            if (newApkFile.exists()) {
                newApkFile.delete();
            }
            if (oldApkFile.exists()) {
                oldApkFile.delete();
            }
            copyFile(AppInfoUtils.getApkPath(LibUpgradeInitializer.getContext()), oldApkPath, true);
            if (oldApkFile.exists() && patchFile.exists()) {
                BsDiff.patch(oldApkPath, newApkPath, patchPath);
                if (newApkFile.exists()) {
                    String newApkMd5 = MD5Utils.get32BitsMD5(newApkFile);
                    if (newApkMd5.equals(newApkMD5)) {
                        isMergeOk = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isMergeOk = false;
        }
        return isMergeOk;
    }

    /**
     * 拷贝文件
     *
     * @param srcFilePath 源文件路径
     * @param dstFilePath 目标路径
     * @param isForce     是否强制拷贝，如果是true则文件如果存在则会被删除然后再拷贝，false若存在则不拷贝
     * @return true拷贝成功，false拷贝失败
     */
    public static boolean copyFile(String srcFilePath, String dstFilePath, boolean isForce) {
        File srcFile = new File(srcFilePath);
        File dstFile = new File(dstFilePath);
        if (!TextUtils.isEmpty(srcFilePath) && !TextUtils.isEmpty(dstFilePath) && srcFile.exists()) {
            if (!isForce && dstFile.exists()) {
                return true;
            }
            if (isForce && dstFile.exists()) {
                dstFile.delete();
            }
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            boolean isCopy = false;
            try {
                inputStream = new FileInputStream(srcFilePath);
                outputStream = new FileOutputStream(dstFilePath);
                isCopy = true;
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return isCopy;
        }
        return false;
    }
}
