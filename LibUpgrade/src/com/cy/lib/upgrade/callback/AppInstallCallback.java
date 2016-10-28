package com.cy.lib.upgrade.callback;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>应用安装回调</p>
 */
public interface AppInstallCallback {

    void onInstallSuccess();

    void onInstallFail();
}
