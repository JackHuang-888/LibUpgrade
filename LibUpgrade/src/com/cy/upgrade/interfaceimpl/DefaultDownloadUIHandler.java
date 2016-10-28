package com.cy.upgrade.interfaceimpl;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cy.lib.upgrade.R;
import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.model.UpdateInfo;
import com.cy.lib.upgrade.utils.FileUtils;
import com.cy.lib.upgrade.utils.InstallUtils;
import com.cy.lib.upgrade.utils.UIHandleUtils;
import com.cy.upgrade.interfacedef.DownloadUIHandler;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>默认的文件下载UI处理实现</p>
 */
public final class DefaultDownloadUIHandler implements DownloadUIHandler {
    private UpdaterConfiguration mConfig;
    private AlertDialog mDialog;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private Context mContext;

    public DefaultDownloadUIHandler(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void setContext(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("context must be instance of activity");
        }
        this.mContext = context;
    }

    @Override
    public void downloadStart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(UIHandleUtils.getString(R.string.update_tips));
        if (!mConfig.getUpdateInfo().isForceInstall()) {
            builder.setNegativeButton(UIHandleUtils.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mConfig.getDownloader().cancel();
                }
            });
        }
        LinearLayout view = new LinearLayout(mContext);
        view.setOrientation(LinearLayout.VERTICAL);
        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        mTvProgress = new TextView(mContext);
        view.addView(mTvProgress);
        view.addView(mProgressBar);
        int dp20 = UIHandleUtils.dpToPx(20);
        builder.setView(view, dp20, dp20, dp20, dp20);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(!mConfig.getUpdateInfo().isForceInstall());
        mDialog.setCancelable(!mConfig.getUpdateInfo().isForceInstall());
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.show();
    }

    @Override
    public void downloadProgress(int progress, int total) {
        mProgressBar.setMax(total);
        mTvProgress.setText(String.format(UIHandleUtils.getString(R.string.downloading), (int) (progress * 1.0 / total * 100), 100));
        mProgressBar.setProgress(progress);
    }

    @Override
    public void downloadComplete(final String path) {
        mDialog.dismiss();
        if (mConfig.getUpdateInfo().getUpdateType() == UpdateInfo.UpdateType.TOTAL_UPDATE) {
            if (mConfig.getUpdateInfo().getInstallType() == UpdateInfo.InstallType.NOTIFY_INSTALL) {
                mConfig.getNotifyInstaller().install(path);
            } else if (mConfig.getUpdateInfo().getInstallType() == UpdateInfo.InstallType.SILENT_INSTALL) {
                mConfig.getSilentInstaller().install(path);
            }
        } else if (mConfig.getUpdateInfo().getUpdateType() == UpdateInfo.UpdateType.INCREMENTAL_UPDATE) {
            mConfig.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    String newApkPath = FileUtils.getFileSavePath("new", ".apk");
                    String oldApkPath = FileUtils.getFileSavePath("old", ".apk");
                    String newApkMD5 = mConfig.getUpdateInfo().getIncrementalUpdateInfo().getFullApkMD5();
                    boolean isPatchOk = InstallUtils.mergePatch(oldApkPath, newApkPath, path, newApkMD5);
                    if (isPatchOk) {
                        if (mConfig.getUpdateInfo().getInstallType() == UpdateInfo.InstallType.NOTIFY_INSTALL) {
                            mConfig.getNotifyInstaller().install(newApkPath);
                        } else if (mConfig.getUpdateInfo().getInstallType() == UpdateInfo.InstallType.SILENT_INSTALL) {
                            mConfig.getSilentInstaller().install(newApkPath);
                        }
                    } else {
                        mConfig.getInstallCallback().onInstallFail();
                    }

                }
            });

        }

    }


    @Override
    public void downloadError(String error) {
        UIHandleUtils.showToast(R.string.download_error);
        mDialog.dismiss();
    }

    @Override
    public void downloadCancel() {
        mDialog.dismiss();
    }
}
