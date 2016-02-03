package vn.hhtv.imagefortext.api;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import vn.hhtv.imagefortext.MainActivity;
import vn.hhtv.imagefortext.config.ApiConfig;

public class RestClient {

    static {
        LoopjRestClient.setHandleError(new HandleErrorMessage());
    }

    public static RestClient synchronize() {
        LoopjRestClient.synchronize();
        return new RestClient();
    }

    public static void cancelRequest(Context context, boolean interrupt) {
        LoopjRestClient.cancelRequest(context, interrupt);
    }

    public static void downloadFile(String url, FileAsyncHttpResponseHandler responseHandler) {
        LoopjRestClient.download(url, responseHandler);
    }

    public static void search(String text, TextHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.add(ApiConfig.KEY_TEXT, text);
        params.add(ApiConfig.KEY_SIZE, MainActivity.screenResolution);
        LoopjRestClient.get(ApiConfig.SEARCH_URL, params, responseHandler);
    }
}
