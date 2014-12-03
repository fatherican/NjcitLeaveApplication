package cn.njcit.util.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by YK on 2014/11/25.
 */
public class HttpClientUtils {
    private static final String BASE_URL = "http://121.40.75.181:8080/studentLeave";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        if(!relativeUrl.startsWith("/")){
            relativeUrl = "/"+relativeUrl;
        }
        return BASE_URL + relativeUrl;
    }
}
