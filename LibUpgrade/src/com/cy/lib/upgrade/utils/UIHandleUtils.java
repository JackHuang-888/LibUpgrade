package com.cy.lib.upgrade.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.cy.lib.upgrade.LibUpgradeInitializer;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>UI处理工具类</p>
 */
public class UIHandleUtils {
    private static Handler mainHandler;

    private static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    public static void runOnUIThread(Runnable runnable) {
        if (runnable != null) {
            getMainHandler().post(runnable);
        }
    }

    public static void showToast(final String msg) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LibUpgradeInitializer.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showToast(int id) {
        showToast(getString(id));
    }

    public static String getString(int id) {
        return LibUpgradeInitializer.getContext().getResources().getString(id);
    }

    public static int dpToPx(float dp) {
        final float scale = LibUpgradeInitializer.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String getExceptionMsg(Exception e) {
        return e == null ? "" : (e.getMessage() == null ? "" : e.getMessage().toString());
    }


}
