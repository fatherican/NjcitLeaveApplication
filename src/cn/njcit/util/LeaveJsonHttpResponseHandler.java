package cn.njcit.util;

import android.content.Context;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.AESEncryptor;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by YK on 2014/11/25.
 */
public abstract class LeaveJsonHttpResponseHandler extends JsonHttpResponseHandler {
    private Context context;
    public LeaveJsonHttpResponseHandler(Context context) {
        super();
        this.context = context;
    }

    public LeaveJsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    public abstract void getJsonObject(JSONObject jsonObject) throws Exception;

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        try {
            if(response!=null){
                String code = response.getString("code");
                if(StringUtils.isNotEmpty(code)){
                    if("300".equals(code)){// 300（session过时）,重新登陆一次
                        login(context);
                    }else{//其他的情况，由所属的业务自己去处理
                        getJsonObject(response);
                    }
                }else{
                    throw new Exception("respon 为空");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(final Context context) {
        final Context innerContext = context;
        //获取保存的用户信息
        String userNo = SharedPrefeenceUtils.getSharedPreferenceString(innerContext, "userNo");
        String enPassword = SharedPrefeenceUtils.getSharedPreferenceString(innerContext, "password");
        String password = AESEncryptor.decrypt(enPassword);
        String role = SharedPrefeenceUtils.getSharedPreferenceString(innerContext, "role");
        //重新登陆
        RequestParams rp = new RequestParams();
        rp.add("userNo",userNo);
        rp.add("password",password);
        rp.add("role",role);
        String currentTime =  DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        rp.add("requestTime",currentTime);
        rp.add("token", MD5Utils.md5Hex(currentTime + AppConstants.SOCKET_KEY));


        HttpClientUtils.post("/user/loginIn.do", rp, new LeaveJsonHttpResponseHandler(context) {
            @Override
            public void getJsonObject(JSONObject jsonObject) throws JSONException {
                if ("200".equals(jsonObject.getString("code"))) {
                    AppConstants.CURRENT_LOGIN_TIME = 0;
                    return;
                } else {
                    if (AppConstants.CURRENT_LOGIN_TIME < AppConstants.RELOGIN_TIMES) {//失败后重试
                        AppConstants.CURRENT_LOGIN_TIME++;
                        login(context);
                    } else {
                        AppConstants.CURRENT_LOGIN_TIME = 0;
                    }
                }
            }
        });
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        super.onSuccess(statusCode, headers, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        super.onSuccess(statusCode, headers, responseString);
    }

    @Override
    protected Object parseResponse(byte[] responseBody) throws JSONException {
        return super.parseResponse(responseBody);
    }
}
