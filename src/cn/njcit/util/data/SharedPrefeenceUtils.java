package cn.njcit.util.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by YK on 2014/11/25.
 */
public class SharedPrefeenceUtils {

    public static String getSharedPreferenceString(Context context , String key){
        SharedPreferences sp = context.getSharedPreferences("setting",0);
        return sp.getString(key,null);
    }

    public static void setSharedPreferenceString(Context context , String key,String value){
        SharedPreferences sp = context.getSharedPreferences("setting",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
