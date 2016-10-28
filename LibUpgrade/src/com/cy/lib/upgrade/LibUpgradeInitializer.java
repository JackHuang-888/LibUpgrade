package com.cy.lib.upgrade;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>
 * 2015-5-27
 * </p>
 * <p>
 * lib初始化入口类
 * </p>
 */
public class LibUpgradeInitializer {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    synchronized public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("please invoke LibUpgradeInitializer.init method in Application onCreate()");
        }
        return mContext;
    }

}
