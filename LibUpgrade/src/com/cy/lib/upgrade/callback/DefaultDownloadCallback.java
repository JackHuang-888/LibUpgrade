package com.cy.lib.upgrade.callback;

import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.utils.UIHandleUtils;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>默认的文件下载回调实现</p>
 */
public final class DefaultDownloadCallback implements DownloadCallback {
    private UpdaterConfiguration mConfig;

    public DefaultDownloadCallback(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void onStart() {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mConfig.getDownloadUIHandler() != null) {
                    mConfig.getDownloadUIHandler().downloadStart();
                }
            }
        });
    }

    @Override
    public void onProgress(final int progress, final int total) {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mConfig.getDownloadUIHandler() != null) {
                    mConfig.getDownloadUIHandler().downloadProgress(progress, total);
                }
            }
        });

    }

    @Override
    public void onComplete(final String path) {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mConfig.getDownloadUIHandler() != null) {
                    mConfig.getDownloadUIHandler().downloadComplete(path);
                }
            }
        });
    }

    @Override
    public void onError(final String error) {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mConfig.getDownloadUIHandler() != null) {
                    mConfig.getDownloadUIHandler().downloadError(error);
                }
            }
        });
    }

    @Override
    public void onCancel() {
        UIHandleUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mConfig.getDownloadUIHandler() != null) {
                    mConfig.getDownloadUIHandler().downloadCancel();
                }
            }
        });
    }
}
