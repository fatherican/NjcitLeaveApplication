package cn.njcit.constants;

/**
 * Created by YK on 2014/11/25.
 */
public class AppConstants {
    public static final String SOCKET_KEY="123456";
    public static final int RELOGIN_TIMES = 3 ;//session过期后，尝试重新链接的次数
    public static  int CURRENT_LOGIN_TIME = 0 ;//session过期后，当前 重试的次数
}
