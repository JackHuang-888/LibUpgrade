package com.cy.upgrade.interfaceimpl;

import com.cy.lib.upgrade.LibUpgradeInitializer;
import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.utils.InstallUtils;
import com.cy.lib.upgrade.utils.UIHandleUtils;
import com.cy.upgrade.interfacedef.AppInstaller;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>App提示安装实现</p>
 */
public class AppNotifyInstaller implements AppInstaller {
    private UpdaterConfiguration mConfig;

    public AppNotifyInstaller(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void install(final String filePath) {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                InstallUtils.notifyInstallApk(LibUpgradeInitializer.getContext(), filePath);
            }
        });
    }
}
