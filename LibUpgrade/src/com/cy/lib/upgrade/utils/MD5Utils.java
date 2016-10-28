package com.cy.lib.upgrade.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Caiyuan Huang
 * <p>2016/10/28</p>
 * <p></p>
 */
public class MD5Utils {
    /**
     * 获取字符串的MD5值
     *
     * @param string 字符串
     * @return 32位MD5值
     */
    public static String get32BitsMD5(String string) {
        if (!TextUtils.isEmpty(string)) {
            String md5;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(string.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                md5 = buf.toString();
            } catch (Exception e) {
                e.printStackTrace();
                md5 = "";
            }
            return md5;
        }
        return "";
    }

    /**
     * 获取文件的32位md5值
     *
     * @param file 文件对象
     * @return 32位md5值，若获取失败则返回""
     */
    public static String get32BitsMD5(File file) {
        if (file != null && file.exists()) {
            String md5;
            try {
                byte[] buffer = new byte[1024];
                FileInputStream inputStream = new FileInputStream(file);
                int len;
                MessageDigest md = MessageDigest.getInstance("MD5");
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    md.update(buffer, 0, len);
                }
                byte b[] = md.digest();
                int i;
                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                md5 = buf.toString();
            } catch (Exception e) {
                e.printStackTrace();
                md5 = "";
            }
            return md5;
        }
        return "";
    }
}
