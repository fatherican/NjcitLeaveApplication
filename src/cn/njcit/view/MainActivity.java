package cn.njcit.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import cn.njcit.R;
import cn.njcit.view.fragements.*;
import org.androidannotations.annotations.*;

/**
 * Created by YK on 2014/11/27.
 */
@EActivity(R.layout.main)
@WindowFeature({Window.FEATURE_NO_TITLE})
public class MainActivity extends FragmentActivity {

    @ViewById(android.R.id.tabhost)
    FragmentTabHost tabhost;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = new Class[]{FragmentAddLeaveTab_.class, FragmentStucentCheckTab_.class, FragmentStudentSickLeavTab_.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_item_1_btn,R.drawable.ic_launcher,R.drawable.tab_item_1_btn, R.drawable.ic_launcher, R.drawable.ic_launcher};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"请假", "审批", "销假"};
    LayoutInflater layoutInflater = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void initVIew() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        //得到fragment的个数,冰一个个添加 Fragment
        int count = fragmentArray.length;
        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            tabhost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }
}
