package io.virtualapp.utils;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;

/**
 * @author weishu
 * @date 2018/7/5.
 */
public class DialogUtil {
    public static void showDialog(AlertDialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog(ProgressDialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
