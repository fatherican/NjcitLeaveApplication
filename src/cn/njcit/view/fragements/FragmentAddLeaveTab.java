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
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.AESEncryptor;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import cn.njcit.util.view.DialogUtils;
import com.loopj.android.http.RequestParams;
import org.androidannotations.annotations.*;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    @ViewById(R.id.studentNoteEt)
    EditText studentNoteEt;
    @ViewById(R.id.studentMobileEt)
    EditText studentMobileEt;
    @ViewById(R.id.courseIndexEt)
    EditText courseIndexEt;
    @ViewById(R.id.courseNameEt)
    EditText courseNameEt;
    @ViewById(R.id.teacherNameEt)
    EditText teacherNameEt;
    @ViewById(R.id.leaveStartDateEt)
    EditText leaveStartDateEt;
    @ViewById(R.id.leaveEndDateEt)
    EditText leaveEndDateEt;



    private static Map<String,String> leaveTypeMap = new HashMap<String,String>();
    static{
        leaveTypeMap.put("courseType","0");
        leaveTypeMap.put("dayType","1");
    }


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

    /**
     * 请假确定按钮
     */
    @Click(R.id.add_leave_bt)
    void addLeaveItem(){
        if(courseLeaveRB.isChecked()){
            submitAddLeave(0);
        }else{
            submitAddLeave(1);
        }
    }


    /**
     *
     * @param radioButtIndex  0 代表课程请假  1 代表天数请假
     */
    void submitAddLeave(int radioButtIndex){
        if(isValidLeaveForm()){
            RequestParams rp = new RequestParams();
            rp.add("userId",SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(),"userId"));
            String token = MD5Utils.md5Hex(SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(),"userId")+SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity()
                    , AppConstants.SOCKET_KEY));
            rp.add("token",token);
            rp.add("studentNote",studentNoteEt.getText().toString());
            rp.add("studentMobile",studentMobileEt.getText().toString());
            switch (radioButtIndex) {
                case 0:
                    rp.add("leaveType","0");
                    rp.add("courseIndex",courseIndexEt.getText().toString());
                    rp.add("courseName",courseNameEt.getText().toString());
                    rp.add("teacherName",teacherNameEt.getText().toString());
                    rp.add("leaveDate", DateFormatUtils.format(new Date(),"yyyy-MM-dd"));
                    break;
                case 1:
                    rp.add("leaveType","1");
                    rp.add("leaveStartDate",leaveStartDateEt.getText().toString());
                    rp.add("leaveEndDate",leaveEndDateEt.getText().toString());
                    try {
                        rp.add("leaveDays",String.valueOf(getSubDays(DateUtils.parseDate(leaveEndDateEt.getText().toString(),new String[]{"yyyy-MM-dd"}),DateUtils.parseDate(leaveStartDateEt.getText().toString(),new String[]{"yyyy-MM-dd"}))+1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    rp.add("teacherName",teacherNameEt.getText().toString());
                    rp.add("leaveDate", DateFormatUtils.format(new Date(),"yyyy-MM-dd"));
                    break;
            }
            HttpClientUtils.post("/leave/addLeave.do", rp, new LeaveJsonHttpResponseHandler(FragmentAddLeaveTab.this.getActivity()) {
                @Override
                public void getJsonObject(JSONObject resultObject) throws JSONException {
                    String code = resultObject.getString("code");
                    DialogUtils.hideTrasparentDialog(FragmentAddLeaveTab.this.getActivity());
                    if ("200".equals(code)) {
                        Toast.makeText(FragmentAddLeaveTab.this.getActivity(),"请假成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FragmentAddLeaveTab.this.getActivity(), resultObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 检验提交的form表单是否都符合规范
     * @return
     */
    private boolean isValidLeaveForm() {
        DialogUtils.showTrasparentDialog(this.getActivity());
        return true;
    }


    /**
     * 计算两个日期之间的天数
     * @param firstDate
     * @param secondDate
     * @return
     */
    private long getSubDays(Date firstDate,Date secondDate){
        return (firstDate.getTime()-secondDate.getTime())/(86400000);
    }



}