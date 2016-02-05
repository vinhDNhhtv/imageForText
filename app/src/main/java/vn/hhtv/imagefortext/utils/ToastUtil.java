package vn.hhtv.imagefortext.utils;

import android.widget.Toast;

import vn.hhtv.imagefortext.AppApplication;
import vn.hhtv.imagefortext.R;

/**
 * Created by vinh on 10/8/15.
 */
public class ToastUtil {
    private static Toast toast;

    public static void show(String text) {
        toast = Toast.makeText(AppApplication.get(), text, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void show(int textId) {
        toast = Toast.makeText(AppApplication.get(), AppApplication.get().getString(textId), Toast.LENGTH_LONG);
        toast.show();
    }
    public static void showError() {
        toast = Toast.makeText(AppApplication.get(), AppApplication.get().getString(R.string.error_tex), Toast.LENGTH_LONG);
        toast.show();
    }

    public static void dismiss() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
