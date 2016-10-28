package com.cy.upgrade.interfaceimpl;

import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.callback.DownloadCallback;
import com.cy.lib.upgrade.model.UpdateInfo;
import com.cy.lib.upgrade.utils.FileUtils;
import com.cy.lib.upgrade.utils.UIHandleUtils;
import com.cy.upgrade.interfacedef.Downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>默认的文件下载器实现</p>
 */
public class DefaultDownloader implements Downloader {
    private UpdaterConfiguration mConfig;
    private Future<?> mTask;
    private DownloadRunnable mDownloadRunnable;

    public DefaultDownloader(UpdaterConfiguration config) {
        this.mConfig = config;
    }

    @Override
    public void download() {
        mDownloadRunnable = new DownloadRunnable(mConfig.getDownloadCallback(), mConfig.getUpdateInfo());
        mTask = mConfig.getExecutorService().submit(mDownloadRunnable);

    }

    class DownloadRunnable implements Runnable {
        private DownloadCallback mCallback;
        private UpdateInfo mUpdateInfo;
        private boolean isCancel = false;
        private HttpURLConnection mURLConnection;

        public DownloadRunnable(DownloadCallback callback, UpdateInfo updateInfo) {
            this.mCallback = callback;
            this.mUpdateInfo = updateInfo;
        }

        public void setIsCancel(boolean isCancel) {
            this.isCancel = isCancel;
        }

        private void sendProgress(int progress, int total) {
            if (mCallback != null) {
                mCallback.onProgress(progress, total);
            }
        }

        private void sendComplete(String path) {
            if (mConfig.getDownloadCallback() != null && !isCancel) {
                mCallback.onComplete(path);
            }
        }

        private void sendStart() {
            if (mCallback != null) {
                mCallback.onStart();
            }
        }

        private void sendError(String error) {
            if (mCallback != null) {
                mCallback.onError(error);
            }
        }

        private void download(String url, String path) throws Exception {
            sendStart();
            InputStream inputStream;
            FileOutputStream outputStream;
            URL httpUrl = new URL(url);
            mURLConnection = (HttpURLConnection) httpUrl.openConnection();
            mURLConnection.connect();
            int responseCode = mURLConnection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException(mURLConnection.getResponseMessage());
            } else {
                outputStream = new FileOutputStream(new File(path));
                inputStream = mURLConnection.getInputStream();
                byte[] buffer = new byte[8 * 1024];
                int len;
                int total = mURLConnection.getContentLength();
                int progress = 0;
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1 && !isCancel) {
                    progress += len;
                    outputStream.write(buffer, 0, len);
                    if (total != 0) {
                        sendProgress(progress, total);
                    }
                }
                inputStream.close();
                outputStream.close();
            }

        }

        @Override
        public void run() {
            boolean isTotalUpdate = mUpdateInfo.getUpdateType() == UpdateInfo.UpdateType.TOTAL_UPDATE;
            String url = isTotalUpdate ? mUpdateInfo.getTotalUpdateInfo().getApkUrl() : mUpdateInfo.getIncrementalUpdateInfo().getPatchUrl();
            String fileSuffix = isTotalUpdate ? ".apk" : ".patch";
            String path = FileUtils.getFileSavePath(url, fileSuffix);
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            String error = "";
            boolean isSuccess = true;
            try {
                download(url, path);
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
                error = UIHandleUtils.getExceptionMsg(e);
            }

            if (isSuccess) {
                sendComplete(path);
            } else {
                sendError(error);
            }
        }

    }


    @Override
    public void cancel() {
        if (mTask != null && !mTask.isDone()) {
            boolean isCancel = mTask.cancel(true);
            if (isCancel) {
                if (mConfig.getDownloadCallback() != null) {
                    mConfig.getDownloadCallback().onCancel();
                }
            }
        }
        if (mDownloadRunnable != null) {
            mDownloadRunnable.setIsCancel(true);
        }

    }
}
