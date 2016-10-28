package com.cy.lib.upgrade;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p></p>
 */
public class Updater {

    private UpdaterConfiguration mConfig;
    private static Updater instance;

    public static Updater getInstance() {
        if (instance == null) {
            synchronized (Updater.class) {
                if (instance == null) {
                    instance = new Updater();
                }
            }
        }
        return instance;
    }

    public void init(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    public void check(Context context) {
        if (mConfig == null) {
            throw new RuntimeException("config is null,please invoke init method");
        }
        mConfig.getUpdateUIHandler().setContext(context);
        mConfig.getDownloadUIHandler().setContext(context);
        mConfig.getUpdateChecker().check(mConfig.getUpdateCheckCallback());
    }


}
