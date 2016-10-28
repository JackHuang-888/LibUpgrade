package com.cy.upgrade.interfaceimpl;

import android.os.Looper;

import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.utils.InstallUtils;
import com.cy.upgrade.interfacedef.AppInstaller;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>APP静默安装实现</p>
 */
public class AppSilentInstaller implements AppInstaller {
    private UpdaterConfiguration mConfig;

    public AppSilentInstaller(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void install(final String filePath) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            installInner(filePath);
        } else {
            mConfig.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    installInner(filePath);
                }
            });
        }
    }

    private void installInner(String apkFilePath) {
        final boolean isInstallOk = InstallUtils.silentInstall(apkFilePath);
        if (isInstallOk) {
            mConfig.getInstallCallback().onInstallSuccess();
        } else {
            mConfig.getInstallCallback().onInstallFail();
        }
    }
}
