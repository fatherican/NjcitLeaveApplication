package cn.njcit.view.fragements;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.njcit.R;
import cn.njcit.util.view.DialogUtils;
import org.androidannotations.annotations.*;

@EFragment(R.layout.fragment_add_leave_tab)
public class FragmentAddLeaveTab extends Fragment {

    @ViewById(R.id.courseLeaveRB)
    RadioButton courseLeaveRB;
    @ViewById(R.id.dayLeaveRB)
    RadioButton dayLeaveRB;
    @ViewById(R.id.courseLeaveTL)
    TableLayout courseLeaveTL;
    @ViewById(R.id.dayLeaveTL)
    TableLayout dayLeaveTL;
    @ViewById(R.id.add_leave_bt)
    Button addLeaveBt;




    private View rootView;

    /**
     * 为避免每次切换tab的时候重新加载fragment，强制重写了此方法。
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_add_leave_tab,null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }


    @CheckedChange(R.id.courseLeaveRB)
    void courseLeaveRBChanged(CompoundButton checkedRadio,boolean isChecked){
        if(isChecked){
            courseLeaveTL.setVisibility(View.VISIBLE);
            dayLeaveTL.setVisibility(View.GONE);
        }

    }

    @CheckedChange(R.id.dayLeaveRB)
    void dayLeaveRBChanged(CompoundButton checkedRadio,boolean isChecked){
        if(isChecked){
            courseLeaveTL.setVisibility(View.GONE);
            dayLeaveTL.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.add_leave_bt)
    void addLeaveItem(){
       DialogUtils.showTrasparentDialog(this.getActivity());
    }


}