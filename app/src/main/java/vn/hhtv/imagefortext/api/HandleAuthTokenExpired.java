package vn.hhtv.imagefortext.api;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class HandleAuthTokenExpired implements IStrategyHandleError {

    @Override
    public boolean handleError(int statusCode, Header[] headers,
                               String responseString, Throwable throwable, TextHttpResponseHandler originHandler) {
        if (statusCode == 403) {
            // token is expired, back to login activity
            return true;
        }
        return false;
    }

}
