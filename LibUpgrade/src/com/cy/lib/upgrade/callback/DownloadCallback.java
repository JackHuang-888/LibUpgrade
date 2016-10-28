package com.cy.lib.upgrade.callback;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>文件下载回调</p>
 */
public interface DownloadCallback {
    void onStart();

    void onProgress(int progress, int total);

    void onComplete(String path);

    void onError(String error);

    void onCancel();
}
