package cn.njcit.view.fragements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.njcit.R;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.adapter.HistorycheckedLeaveAdapter;
import cn.njcit.util.adapter.UncheckedLeaveAdapter;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.viewpagerindicator.TabPageIndicator;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@EFragment(R.layout.fragment_student_check_uncheck)
public class FragmentStudentCheckHistoryCheckdLeaveFragment  extends ListFragment {
    private View rootView;
    private int pageNum = 1;
    private int pageSize = 20;
    private boolean isInstanced = false;//当前这个实力对象是否已经存在，已经存在则不再去调用初始化数据

    @ViewById(R.id.pull_to_refresh_listview)
    PullToRefreshListView pullToRefreshView;

    public static Fragment newInstance() {
        return new FragmentStudentCheckHistoryCheckdLeaveFragment_();
    }


    /**
     * 为避免每次切换tab的时候重新加载fragment，强制重写了此方法。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_student_check_historycheck, container, false);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @AfterViews
    public void initAdapter() {
        if (!isInstanced) {
            getUncheckedLeaveAdapter();
            isInstanced = true;
        }
        Log.e("实例化", isInstanced + "");
    }

    public void getUncheckedLeaveAdapter() {
        RequestParams rp = new RequestParams();
        String currentTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        rp.add("requestTime", currentTime);
        rp.add("token", MD5Utils.md5Hex(SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(), "userId") + AppConstants.SOCKET_KEY));
        rp.add("studentQueryLeaveListType", "3");
        rp.add("userId", SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(), "userId"));
        rp.add("pageNum", pageNum + "");
        rp.add("pageSize", pageSize + "");
        HttpClientUtils.post("/leave/studentGetLeaveList.do", rp, new LeaveJsonHttpResponseHandler(this.getActivity()) {
            @Override
            public void getJsonObject(JSONObject jsonObject) throws Exception {
                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                String code = jsonObject.getString("code");
                if ("200".equals(code)) {
                    pageNum++;//页码加 1
                    String dataStr = jsonObject.getString("data");
                    JSONArray ja = new JSONArray(dataStr);
                    int length = ja.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Map<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("leaveId", jo.getString("leaveId"));
                        hashMap.put("approved", jo.getString("approved"));//-1 未审批0 未通过 1通过 2辅导员已审批等待学管处审批
                        hashMap.put("classId", jo.getString("classId"));
                        hashMap.put("colleageId", jo.getString("colleageId"));
                        hashMap.put("courseIndex", jo.getString("courseIndex"));
                        hashMap.put("courseName", jo.getString("courseName"));
                        hashMap.put("createTime", jo.getString("createTime"));
                        hashMap.put("instructorNote", jo.getString("instructorNote"));
                        hashMap.put("leaveDate", jo.getString("leaveDate"));
                        hashMap.put("leaveDays", jo.getString("leaveDays"));
                        hashMap.put("leaveStartDate", jo.getString("leaveStartDate"));
                        hashMap.put("leaveEndDate", jo.getString("leaveEndDate"));
                        hashMap.put("leaveSickDate", jo.getString("leaveSickDate"));
                        hashMap.put("leaveSicked", jo.getString("leaveSicked"));
                        hashMap.put("leaveType", jo.getString("leaveType"));//0 节次请假，1天数请假
                        hashMap.put("needSecondApprove", jo.getString("needSecondApprove"));
                        hashMap.put("studentId", jo.getString("studentId"));
                        hashMap.put("studentMobile", jo.getString("studentMobile"));
                        hashMap.put("studentName", jo.getString("studentName"));
                        hashMap.put("studentNote", jo.getString("studentNote"));
                        hashMap.put("studentPipeNote", jo.getString("studentPipeNote"));
                        hashMap.put("teacherName", jo.getString("teacherName"));
                        data.add(hashMap);
                    }
                } else {
                    Toast.makeText(FragmentStudentCheckHistoryCheckdLeaveFragment.this.getActivity(), "获取未审批列表数据失败", Toast.LENGTH_SHORT).show();
                }
                HistorycheckedLeaveAdapter ul = new HistorycheckedLeaveAdapter(data, FragmentStudentCheckHistoryCheckdLeaveFragment.this.getActivity());
                pullToRefreshView.setAdapter(ul);
            }
        });
//        }else{//直接从缓存中去取
//
//        }


    }

}