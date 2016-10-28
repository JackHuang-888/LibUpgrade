package com.cy.lib.upgrade.callback;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>更新检查回调接口</p>
 */
public interface UpdateCheckCallback {
    void onCheckSuccess();

    void onCheckFail(String error);

}
