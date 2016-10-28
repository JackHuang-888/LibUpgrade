package com.cy.lib.upgrade.callback;

import com.cy.lib.upgrade.LibUpgradeInitializer;
import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.utils.AppInfoUtils;
import com.cy.lib.upgrade.utils.UIHandleUtils;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>默认的更新检测回调实现</p>
 */
public final class DefaultUpdateCheckCallback implements UpdateCheckCallback {
    private UpdaterConfiguration mConfig;

    public DefaultUpdateCheckCallback(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void onCheckSuccess() {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                boolean hasUpdate;
                hasUpdate = mConfig.getUpdateInfo().isForceInstall();
                if (!hasUpdate) {
                    int curVersionCode = AppInfoUtils.getApkVersionCode(LibUpgradeInitializer.getContext());
                    hasUpdate = curVersionCode < mConfig.getUpdateInfo().getVersionCode();
                }
                if (mConfig.getUpdateUIHandler() != null) {
                    if (hasUpdate) {
                        mConfig.getUpdateUIHandler().hasUpdate();
                    } else {
                        mConfig.getUpdateUIHandler().noUpdate();
                    }
                }
            }
        });

    }

    @Override
    public void onCheckFail(String error) {
        if (mConfig.getUpdateUIHandler() != null) {
            mConfig.getUpdateUIHandler().checkError(error);
        }
    }
}
