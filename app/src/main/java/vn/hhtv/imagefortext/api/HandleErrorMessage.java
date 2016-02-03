package vn.hhtv.imagefortext.api;

import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vinhdn on 1/19/2016.
 * <p/>
 * Parse error message from server to show alert
 */
public class HandleErrorMessage implements IStrategyHandleError {
    @Override
    public boolean handleError(int statusCode, Header[] headers, String responseString, Throwable throwable, TextHttpResponseHandler originHandler) {
        Log.d("IGO10 handle E","ERROR ================================");
        Log.d("IGO10 handle E","status: " + statusCode);
        Log.d("IGO10 handle E","response: " + responseString);
        Log.d("IGO10 handle E","ERROR ================================");

        if (originHandler != null) {
            originHandler.onFailure(statusCode, headers, responseString, throwable);
        }

        return true;
    }
}
