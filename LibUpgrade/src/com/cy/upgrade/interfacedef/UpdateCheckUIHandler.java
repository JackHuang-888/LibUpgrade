package com.cy.upgrade.interfacedef;

import android.content.Context;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>更新提示UI处理接口</p>
 */
public interface UpdateCheckUIHandler {
    void setContext(Context context);

    void hasUpdate();

    void noUpdate();

    void checkError(String error);
}
