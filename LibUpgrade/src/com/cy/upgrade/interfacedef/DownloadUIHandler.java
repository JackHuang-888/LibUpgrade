package com.cy.upgrade.interfacedef;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>文件下载UI处理接口</p>
 */
public interface DownloadUIHandler {

    void setContext(Context context);

    void downloadStart();

    void downloadProgress(int progress, int total);

    void downloadComplete(String path);

    void downloadError(String error);

    void downloadCancel();
}
