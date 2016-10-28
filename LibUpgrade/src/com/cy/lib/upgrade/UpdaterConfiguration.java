package com.cy.lib.upgrade;

import com.cy.lib.upgrade.callback.AppInstallCallback;
import com.cy.lib.upgrade.callback.DefaultAppInstallCallback;
import com.cy.lib.upgrade.callback.DefaultDownloadCallback;
import com.cy.lib.upgrade.callback.DefaultUpdateCheckCallback;
import com.cy.lib.upgrade.callback.DownloadCallback;
import com.cy.lib.upgrade.callback.UpdateCheckCallback;
import com.cy.lib.upgrade.model.UpdateInfo;
import com.cy.upgrade.interfacedef.AppInstaller;
import com.cy.upgrade.interfacedef.DownloadUIHandler;
import com.cy.upgrade.interfacedef.Downloader;
import com.cy.upgrade.interfacedef.UpdateChecker;
import com.cy.upgrade.interfacedef.UpdateCheckUIHandler;
import com.cy.upgrade.interfaceimpl.AppNotifyInstaller;
import com.cy.upgrade.interfaceimpl.AppSilentInstaller;
import com.cy.upgrade.interfaceimpl.DefaultDownloadUIHandler;
import com.cy.upgrade.interfaceimpl.DefaultDownloader;
import com.cy.upgrade.interfaceimpl.DefaultUpdateCheckUIHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>更新配置</p>
 */
public final class UpdaterConfiguration {

    private UpdateChecker mUpdateChecker;
    private UpdateCheckCallback mUpdateCheckCallback;
    private UpdateCheckUIHandler mUpdateUIHandler;
    private Downloader mDownloader;
    private DownloadCallback mDownloadCallback;
    private DownloadUIHandler mDownloadUIHandler;
    private ExecutorService mExecutorService;
    private AppInstaller mSilentInstaller;
    private AppInstaller mNotifyInstaller;
    private AppInstallCallback mInstallCallback;
    private UpdateInfo mUpdateInfo;


    public UpdaterConfiguration updateInfo(UpdateInfo info) {
        this.mUpdateInfo = info;
        return this;
    }

    public UpdaterConfiguration updateChecker(UpdateChecker updateChecker) {
        this.mUpdateChecker = updateChecker;
        return this;
    }

    public UpdaterConfiguration updateCheckCallback(UpdateCheckCallback callback) {
        this.mUpdateCheckCallback = callback;
        return this;
    }

    public UpdaterConfiguration updateUIHandler(UpdateCheckUIHandler updateUIHandler) {
        this.mUpdateUIHandler = updateUIHandler;
        return this;
    }

    public UpdaterConfiguration downloader(Downloader downloader) {
        this.mDownloader = downloader;
        return this;
    }

    public UpdaterConfiguration downloadCallback(DownloadCallback callback) {
        this.mDownloadCallback = callback;
        return this;
    }

    public UpdaterConfiguration downloadUIHandler(DownloadUIHandler downloadUIHandler) {
        this.mDownloadUIHandler = downloadUIHandler;
        return this;
    }

    public UpdaterConfiguration executorService(ExecutorService executorService) {
        this.mExecutorService = executorService;
        return this;
    }

    public UpdaterConfiguration silentInstaller(AppSilentInstaller installer) {
        this.mSilentInstaller = installer;
        return this;
    }

    public UpdaterConfiguration notifyInstaller(AppNotifyInstaller installer) {
        this.mNotifyInstaller = installer;
        return this;
    }

    public UpdaterConfiguration intallCallback(AppInstallCallback callback) {
        this.mInstallCallback = callback;
        return this;
    }

    public UpdateChecker getUpdateChecker() {
        if (mUpdateChecker == null) {
            throw new RuntimeException("updateChecker must be not null");
        }
        return mUpdateChecker;
    }

    public UpdateCheckUIHandler getUpdateUIHandler() {
        if (mUpdateUIHandler == null) {
            mUpdateUIHandler = new DefaultUpdateCheckUIHandler(this);
        }
        return mUpdateUIHandler;
    }

    public Downloader getDownloader() {
        if (mDownloader == null) {
            mDownloader = new DefaultDownloader(this);
        }
        return mDownloader;
    }

    public DownloadUIHandler getDownloadUIHandler() {
        if (mDownloadUIHandler == null) {
            mDownloadUIHandler = new DefaultDownloadUIHandler(this);
        }
        return mDownloadUIHandler;
    }

    public UpdateCheckCallback getUpdateCheckCallback() {
        if (mUpdateCheckCallback == null) {
            mUpdateCheckCallback = new DefaultUpdateCheckCallback(this);
        }
        return mUpdateCheckCallback;
    }

    public DownloadCallback getDownloadCallback() {
        if (mDownloadCallback == null) {
            mDownloadCallback = new DefaultDownloadCallback(this);
        }
        return mDownloadCallback;
    }

    public ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
        return mExecutorService;
    }

    public AppInstaller getNotifyInstaller() {
        if (mNotifyInstaller == null) {
            mNotifyInstaller = new AppNotifyInstaller(this);
        }
        return mNotifyInstaller;
    }

    public AppInstaller getSilentInstaller() {
        if (mSilentInstaller == null) {
            mSilentInstaller = new AppSilentInstaller(this);
        }
        return mSilentInstaller;
    }

    public AppInstallCallback getInstallCallback() {
        if (mInstallCallback == null) {
            mInstallCallback = new DefaultAppInstallCallback();
        }
        return mInstallCallback;
    }

    public UpdateInfo getUpdateInfo() {
        if (mUpdateInfo == null) {
            throw new RuntimeException("update info is null,please invoke UpdaterConfiguration.updateInfo method");
        }
        return mUpdateInfo;
    }
}
