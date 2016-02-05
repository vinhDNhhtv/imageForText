package vn.hhtv.imagefortext.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by iservice on 2/5/16.
 */
public class KeyboardUtil {

    public static void hideSoftKeyboard(final Activity activity) {
        if(activity == null) return;
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = activity.getCurrentFocus();
                if (view != null) {
                    IBinder binder = view.getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            }
        });

    }
}
