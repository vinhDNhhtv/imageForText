package vn.hhtv.imagefortext;

import android.app.Application;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by iservice on 2/3/16.
 */
public class AppApplication extends Application{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "cmwCZ3HRrdFVphHz39C91BWKs";
    private static final String TWITTER_SECRET = "AQSLaXZub7fWwr1cteyf2H6T9JMg442fViT4m85bXm3RumQjXo";

    private static AppApplication instance;

    public static AppApplication get(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        instance = this;
    }
}
