package com.cy.upgrade.interfaceimpl;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;

import com.cy.lib.upgrade.R;
import com.cy.lib.upgrade.UpdaterConfiguration;
import com.cy.lib.upgrade.utils.UIHandleUtils;
import com.cy.upgrade.interfacedef.UpdateCheckUIHandler;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>默认的提示更新UI处理</p>
 */
public final class DefaultUpdateCheckUIHandler implements UpdateCheckUIHandler {
    private UpdaterConfiguration mConfig;
    private Context mContext;

    public DefaultUpdateCheckUIHandler(UpdaterConfiguration config) {
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
    public void hasUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(UIHandleUtils.getString(R.string.update_tips));
        builder.setMessage(mConfig.getUpdateInfo().getUpdateInfo());
        if (!mConfig.getUpdateInfo().isForceInstall()) {
            builder.setNegativeButton(UIHandleUtils.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.setPositiveButton(UIHandleUtils.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mConfig.getDownloader().download();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(!mConfig.getUpdateInfo().isForceInstall());
        dialog.setCancelable(!mConfig.getUpdateInfo().isForceInstall());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    @Override
    public void noUpdate() {
        UIHandleUtils.showToast(R.string.no_update);
    }

    @Override
    public void checkError(String error) {
        UIHandleUtils.showToast(R.string.check_error);
    }


}
