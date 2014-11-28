package cn.njcit.util.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import cn.njcit.R;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by YK on 2014/11/28.
 */
public class DialogUtils {

   static Dialog transparentDialog = null;


   public static void showTrasparentDialog(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        transparentDialog = new Dialog(context, R.style.TANCStyle);
        transparentDialog.setContentView(R.layout.dialog_transparent);
        transparentDialog.setCancelable(true);
        transparentDialog.show();
    }

    public static void hideTrasparentDialog(Context context){
        transparentDialog.hide();
    }

}
