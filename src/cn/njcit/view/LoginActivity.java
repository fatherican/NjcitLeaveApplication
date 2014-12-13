package cn.njcit.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.njcit.R;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.AESEncryptor;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import lib.NiftyDialogBuilder;

import com.loopj.android.http.RequestParams;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@EActivity(R.layout.login)
@WindowFeature({Window.FEATURE_NO_TITLE})
public class LoginActivity extends Activity {

    /**用户名*/
    @ViewById(R.id.userNo)
    EditText mUserNo;
    /**密码*/
    @ViewById(R.id.password)
    EditText mPassword;
    /**角色*/
    @ViewById(R.id.role)
    EditText mRole;
    /**确定按钮*/
    @ViewById(R.id.loginBT)
    Button mLogin;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();//初始化  动作
    }


    /**
     * 项目刚启动后，需要做的动作，
     * 例如，检查用户上次登陆的时候如果勾选上了自动登陆，则此次登陆就自动读取配置配置信息登陆
     */
    private void init() {
        Log.e("粗偶","这个 init 方法有没有执行 啊 ");
    }


    /**
     * 登陆按钮点击事件
     */
    @Click(R.id.loginBT)
    void loginBtClick(){
        login(true);
    }


    /**登陆
     * @param handLogin  手动登陆标志(手动点击登陆按钮登陆)
     * */
     private void login(boolean handLogin){
        RequestParams rp = new RequestParams();
        String userNo = null;
        String password = null;
        String role = null;
        if(handLogin){
            userNo = mUserNo.getText().toString().trim().toUpperCase();
            rp.add("userNo",userNo);
            password = mPassword.getText().toString();
            rp.add("password",password);
            role = mRole.getText().toString().trim();
            rp.add("role",role);
        }else{
            userNo = SharedPrefeenceUtils.getSharedPreferenceString(this, "userNo");
            String enPassword = SharedPrefeenceUtils.getSharedPreferenceString(this, "password");
            password = AESEncryptor.decrypt(enPassword);
            role = SharedPrefeenceUtils.getSharedPreferenceString(this, "role");
        }
        String currentTime =  DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        rp.add("requestTime",currentTime);
        rp.add("token", MD5Utils.md5Hex(currentTime + AppConstants.SOCKET_KEY));

        final String finalUserNo = userNo;
        final String finalPassword = password;
        final String finalRole = role;
        HttpClientUtils.post("/user/loginIn.do", rp, new LeaveJsonHttpResponseHandler(this) {
            @Override
            public void getJsonObject(JSONObject resultObject) throws JSONException {
                String code = resultObject.getString("code");
                if ("200".equals(code)) {
                    //保存用户数据信息到sharedPreference
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "userNo", finalUserNo);
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "password", AESEncryptor.encrypt(finalPassword));
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "role", finalRole);
                    //保存服务器端数据
                    JSONObject data =resultObject.getJSONObject("data");
                    String classId = data.getString("classId");
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "classId", classId);
                    String className = data.getString("className");
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "className", className);
                    String colleageId = data.getString("colleageId");
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "colleageId", colleageId);
                    String name = data.getString("name");
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "name", name);
                    String userId = data.getString("userId");
                    SharedPrefeenceUtils.setSharedPreferenceString(LoginActivity.this, "userId", userId);
                    //跳转到 主界面
                    jumpToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, resultObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void jumpToMainActivity(){
        Intent intent = new Intent(this,MainActivity_.class);
        startActivity(intent);
    }


}
