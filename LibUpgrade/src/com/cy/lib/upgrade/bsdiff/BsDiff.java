package com.cy.lib.upgrade.bsdiff;

/**
 * Caiyuan Huang
 * <p>2016/10/26</p>
 * <p>用于增量更新的差分及合并工具类</p>
 */
public class BsDiff {

    static {
        try {
            System.loadLibrary("bsdiff_utils");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static native int diff(String oldPath, String newPath, String patchPath);

    public static native int patch(String oldPath, String newPath, String patchPath);
}
