package vn.hhtv.imagefortext;

import android.app.Application;

/**
 * Created by iservice on 2/3/16.
 */
public class AppApplication extends Application{
    private static AppApplication instance;

    public static AppApplication get(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
