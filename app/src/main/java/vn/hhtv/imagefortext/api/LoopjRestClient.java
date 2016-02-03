package vn.hhtv.imagefortext.api;

import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;
import vn.hhtv.imagefortext.AppApplication;
import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.config.ApiConfig;
import vn.hhtv.imagefortext.utils.NetworkUtil;
import vn.hhtv.imagefortext.utils.ToastUtil;

public class LoopjRestClient {

    public static final String TAG = LoopjRestClient.class.getSimpleName();
    private final static int TIME_OUT = 60000;
    private final static AsyncHttpClient ASYNC_HTTP_CLIENT = new AsyncHttpClient();
    private final static AsyncHttpClient SYNC_HTTP_CLIENT = new SyncHttpClient();
    protected static AsyncHttpClient mClient = ASYNC_HTTP_CLIENT;
    private static IStrategyHandleError mHandleError;
    private static boolean useSynchronizeMode = false;

    static {
        ASYNC_HTTP_CLIENT.setTimeout(TIME_OUT);
    }

    static {
        SYNC_HTTP_CLIENT.setTimeout(TIME_OUT);
    }

    public static void setHandleError(IStrategyHandleError handleError) {
        mHandleError = handleError;
    }

    /**
     * Use {@link SyncHttpClient} to request in synchronize mode
     * thread will be blocked
     */
    public static void synchronize() {
        useSynchronizeMode = true;
    }

    /**
     * Check synchronize mode before request
     */
    private static void checkSynchronize() {
        if (useSynchronizeMode) {
            mClient = SYNC_HTTP_CLIENT;
            useSynchronizeMode = false; //reset to asynchronous mode (default) for next request
        } else {
            mClient = ASYNC_HTTP_CLIENT;
        }
    }

    public static void cancelRequest(Context context, boolean interrupt){
        mClient.cancelRequests(context, interrupt);
    }

    /**
     * get request with parameters
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void get(String url, RequestParams params,
                           TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            checkSynchronize();

            showLogRequest(url, params, null);
            if(params == null){
                params = new RequestParams();
            }
            params.put("key", "xlhF32NeOlibJcoOW9tvZg7TkHcAZI3a");
            mClient.get(url, params, makeWrapperHandler(url, responseHandler));
        }
    }
    /**
     * get request with parameters
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static RequestHandle getWithHandle(String url, RequestParams params,
                           TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            checkSynchronize();

            showLogRequest(url, params, null);
            if(params == null){
                params = new RequestParams();
            }
            params.put("key", ApiConfig.geteyk());
            return mClient.get(url, params, makeWrapperHandler(url, responseHandler));
        }
        return null;
    }

    /**
     * get request with parameters and headers
     *
     * @param url
     * @param params
     * @param headers
     * @param responseHandler
     */
    public static void get(String url, RequestParams params,
                           HashMap<String, String> headers,
                           TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {

            checkSynchronize();
            if(headers == null)
                headers = new HashMap<>();

            Set<String> keys = headers.keySet();
            for (String key : keys) {
                mClient.addHeader(key, headers.get(key));
            }

            showLogRequest(url, params, headers);
            if(params == null){
                params = new RequestParams();
            }
            params.put("key", ApiConfig.geteyk());
            mClient.get(url, params, makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * post request
     *
     * @param url
     * @param responseHandler
     */
    public static void post(String url, TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            checkSynchronize();

            showLogRequest(url, new JSONObject(), null);
            mClient.post(url, makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * Post with parameters
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void post(String url, RequestParams params,
                            TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            checkSynchronize();

            showLogRequest(url, params, null);
            if(params == null){
                params = new RequestParams();
            }
            params.put("key", ApiConfig.geteyk());
            mClient.post(url, params, makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * post with parameters and headers
     *
     * @param url
     * @param params
     * @param headers
     * @param responseHandler
     */
    public static void post(String url, RequestParams params,
                            HashMap<String, String> headers,
                            TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {

            checkSynchronize();

            Set<String> keys = headers.keySet();
            for (String key : keys) {
                mClient.addHeader(key, headers.get(key));
            }

            showLogRequest(url, params, headers);
            mClient.post(url, params, makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * Post with body data
     *
     * @param url
     * @param data
     * @param responseHandler
     */
    public static void post(String url, String data,
                            TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            HttpEntity entity;
            entity = new StringEntity(data.toString(), HTTP.UTF_8);
            checkSynchronize();
            mClient.post(AppApplication.get(), url, entity, "application/json",
                    makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * put request with parameters
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void put(String url, RequestParams params,
                           TextHttpResponseHandler responseHandler) {
        if (checkNetworkConnected(responseHandler)) {
            checkSynchronize();

            showLogRequest(url, params, null);
            if (params == null) {
                params = new RequestParams();
            }
            params.put("key", ApiConfig.geteyk());
            params.put("_method", "PUT");
//        mClient.put(url, params, makeWrapperHandler(url, responseHandler));
            /**
             * Edit method PUT -> POST with params _method
             */
            mClient.post(url, params, makeWrapperHandler(url, responseHandler));
        }
    }

    /**
     * put request with JSON body data
     *
     * @param context
     * @param url
     * @param data
     * @param responseHandler
     */
    public static void put(Context context, String url, String data,
                           TextHttpResponseHandler responseHandler) {
        HttpEntity entity;
        entity = new StringEntity(data.toString(), HTTP.UTF_8);
        checkSynchronize();
        mClient.put(context, url, entity, "application/json",
                makeWrapperHandler(url, responseHandler));
    }

    /**
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void delete(String url, RequestParams params,
                              TextHttpResponseHandler responseHandler) {
        checkSynchronize();
        showLogRequest(url, params, null);
        if(params == null){
            params = new RequestParams();
        }
        params.put("key", ApiConfig.geteyk());
        params.put("_method", "DELETE");
        /**
         * Edit method PUT -> POST with params _method
         */
//        mClient.delete(url,params, makeWrapperHandler(url, responseHandler));
        mClient.post(url,params, makeWrapperHandler(url, responseHandler));
    }

    /**
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void delete(String url, JSONObject params,
                              TextHttpResponseHandler responseHandler) {
        checkSynchronize();
        showLogRequest(url, params, null);
        mClient.delete(url, makeWrapperHandler(url, responseHandler));
    }

    //region check network

    public static void download(String url, FileAsyncHttpResponseHandler responseHandler) {
        checkSynchronize();
        mClient.get(url, responseHandler);
    }

    private static boolean checkNetworkConnected(TextHttpResponseHandler responseHandler) {
        if (!isNetworkConnected()) {
            handleNetworkDisconnected(responseHandler);
            return false;
        }
        return true;
    }

    private static boolean isNetworkConnected() {
        return NetworkUtil.getInstance(AppApplication.get()).isConnect();
    }

    //endregion

    private static void handleNetworkDisconnected(TextHttpResponseHandler responseHandler) {
        if (responseHandler != null) {
            ToastUtil.show(AppApplication.get().getString(R.string.message_network_is_unavailable));
            responseHandler.onFailure(-1, null,
                    "No Internet Connection", null);
        }
    }

    /**
     * Make wrapper handler for log request/response
     *
     * @param originHandler
     * @return
     */
    private static TextHttpResponseHandler makeWrapperHandler(final String url,
                                                              final TextHttpResponseHandler originHandler) {
        TextHttpResponseHandler wrapperHandler = new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {
                android.util.Log.d(TAG, "==============================");
                android.util.Log.d(TAG, "statusCode: " + statusCode);
                android.util.Log.d(TAG, "ResponseSuccess: " + responseString);
                android.util.Log.d(TAG, "==============================");
                if (originHandler != null) {
                    originHandler
                            .onSuccess(statusCode, headers, responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                android.util.Log.d(TAG, "==============================");
                android.util.Log.d(TAG, "statusCode: " + statusCode);
                android.util.Log.d(TAG, "ResponseFailure: " + responseString);
                android.util.Log.d(TAG, "==============================");
                boolean isHandled = false;
                if (mHandleError != null) {
                    isHandled = mHandleError.handleError(statusCode, headers,
                            responseString, throwable, originHandler);
                }
                if (!isHandled && originHandler != null) {
                    originHandler.onFailure(statusCode, headers,
                            responseString, throwable);
                }
            }

            @Override
            public void onStart() {
                if (originHandler != null) {
                    originHandler.onStart();
                }
            }

            @Override
            public void onFinish() {
                if (originHandler != null) {
                    originHandler.onFinish();
                }
            }
        };
        return wrapperHandler;
    }

    public static void showLogRequest(String url, RequestParams params, HashMap<String, String> headers) {
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "url: " + url);
        try {
            if (params != null)
                android.util.Log.d(TAG, "params: " + params);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        android.util.Log.d(TAG, "headers: " + headers);
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "====================================");
    }
    public static void showLogRequest(String url, JSONObject params, HashMap<String, String> headers) {
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "url: " + url);
        try {
            if (params != null)
                android.util.Log.d(TAG, "params: " + params.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        android.util.Log.d(TAG, "headers: " + headers);
        android.util.Log.d(TAG, "====================================");
        android.util.Log.d(TAG, "====================================");
    }
}