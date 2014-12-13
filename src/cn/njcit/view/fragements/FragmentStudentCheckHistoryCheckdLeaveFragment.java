package cn.njcit.view.fragements;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import cn.njcit.R;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.adapter.HistorycheckedLeaveAdapter;
import cn.njcit.util.adapter.NewcheckedLeaveAdapter;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import lib.Effectstype;
import lib.NiftyDialogBuilder;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@EFragment(R.layout.fragment_student_check_historycheck)
public class FragmentStudentCheckHistoryCheckdLeaveFragment  extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView>,PullToRefreshBase.OnLastItemVisibleListener{
    private View rootView;
    private int pageNum = 1;
    private int pageSize = 20;
    private boolean isInstanced = false;//当前这个实力对象是否已经存在，已经存在则不再去调用初始化数据
    @ViewById(R.id.history_query_linearLayout)
    LinearLayout historyQueryLinearLayout;
    @ViewById(R.id.query_arrow_linearLayout)
    LinearLayout queryArrowLinearLayout;
    @ViewById(R.id.query_arrow_image)
    ImageView queryArrowImage;
    @ViewById(R.id.query_content_linearLayout)
    LinearLayout queryContentLinearLayout;
    @ViewById(R.id.leaveStartDateEt)
    EditText leaveStartDateET;
    @ViewById(R.id.leaveEndDateEt)
    EditText leaveEndDateET;
    @ViewById(R.id.approvedStateSP)
    Spinner approvedStatesSP;
    @ViewById(R.id.queryBT)
    Button queryBT;
    @ViewById(R.id.holeLinearLayout)
    LinearLayout holeLinearLayout;

    HistorycheckedLeaveAdapter hl = null;
    List<Map<String,String>> data = new ArrayList<Map<String, String>>();


    @ViewById(R.id.pull_to_refresh_listview)
    PullToRefreshListView pullToRefreshView;
    private boolean isShown=true;
    int height = 0;
    ViewPropertyAnimator vpa =null;



    public static Fragment newInstance() {
        return new FragmentStudentCheckHistoryCheckdLeaveFragment_();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView){
        getUncheckedLeaveAdapter(Direction.DOWN);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView){
        getUncheckedLeaveAdapter(Direction.UP);
    }

    @Override
    public void onLastItemVisible() {
        if(data.size()%pageSize!=0){
            Toast.makeText(this.getActivity(),"没有更多数据",Toast.LENGTH_SHORT).show();
        }
    }


    @Click(R.id.queryBT)
    public void  query(){
        getUncheckedLeaveAdapter(null);
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
        if(!isInstanced){
            getUncheckedLeaveAdapter(null);
            isInstanced = true;
            hl = new HistorycheckedLeaveAdapter(data,FragmentStudentCheckHistoryCheckdLeaveFragment.this.getActivity());
            pullToRefreshView.setAdapter(hl);
            pullToRefreshView.setOnRefreshListener(this);
            pullToRefreshView.setOnLastItemVisibleListener(this);
        }
        pullToRefreshView.setOnItemClickListener(this);
    }

    @Click(R.id.query_arrow_linearLayout)
    void queryContentShowOrHide(){
        if(height==0){
            height = queryContentLinearLayout.getHeight()-queryArrowLinearLayout.getHeight();
        }
        if(vpa==null){
            vpa =  queryContentLinearLayout.animate();
            vpa.setDuration(200);
        }
        if(isShown){
            historyQueryLinearLayout.setVisibility(View.GONE);
            isShown =false;
            queryArrowImage.setImageResource(android.R.drawable.arrow_down_float);

            /** vpa.translationYBy(-height);
             vpa.start();

             queryArrowImage.setImageResource(android.R.drawable.arrow_down_float);*/
        }else{
          /**  vpa.translationYBy(height);
            vpa.start();

            queryArrowImage.setImageResource(android.R.drawable.arrow_up_float);*/
            historyQueryLinearLayout.setVisibility(View.VISIBLE);
            isShown =true;
            queryArrowImage.setImageResource(android.R.drawable.arrow_up_float);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Map<String,String> item = data.get(position-1);
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        View v = layoutInflater.inflate(R.layout.leave_detail, null);
        LeaveDetailViewHolder.initView(v,item);
        final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(this.getActivity());
        dialogBuilder.withTitleColor("#FFFFFF")
                .withTitle("请假详情")
                .withMessage("")
                .withEffect(Effectstype.Fadein)
                .withDuration(400)
                .withDialogColor("#FFcccccc")
                .withButton1Text("关闭")
                .setCustomView(v, this.getActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        dialogBuilder.cancel();
                    }
                })
                .show();
    }







    public void getUncheckedLeaveAdapter(final Direction direction) {
        final Direction finalDirection = direction;
        if(finalDirection==Direction.DOWN||finalDirection==null){//重新加载list数据，所以，开启上下拉都允许模式
            pageNum=1;
        }
        RequestParams rp = new RequestParams();
        String currentTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        rp.add("requestTime", currentTime);
        rp.add("token", MD5Utils.md5Hex(SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(), "userId") + AppConstants.SOCKET_KEY));
        rp.add("studentQueryLeaveListType", "3");
        rp.add("userId", SharedPrefeenceUtils.getSharedPreferenceString(this.getActivity(), "userId"));
        rp.add("pageNum", pageNum + "");
        rp.add("pageSize", pageSize + "");
        rp.add("startTime",leaveStartDateET.getText().toString());
        rp.add("endTime",leaveEndDateET.getText().toString());
        if("未审批".equals(approvedStatesSP.getSelectedItem().toString())){
            rp.add("approved","-1");
        }else if("同意".equals(approvedStatesSP.getSelectedItem().toString())){
            rp.add("approved","1");
        }else if("不同意".equals(approvedStatesSP.getSelectedItem().toString())){
            rp.add("approved","0");
        }else if("审核中".equals(approvedStatesSP.getSelectedItem().toString())){
            rp.add("approved","2");
        }


        HttpClientUtils.post("/leave/studentGetLeaveList.do", rp, new LeaveJsonHttpResponseHandler(this.getActivity()) {
            @Override
            public void getJsonObject(JSONObject jsonObject) throws Exception {

                String code = jsonObject.getString("code");
                if ("200".equals(code)) {
                    pageNum++;//页码加 1
                    if(finalDirection==Direction.DOWN||finalDirection==null){//重新加载list数据，所以，开启上下拉都允许模式
                        data.clear();
                        pullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
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
                hl.notifyDataSetChanged();
                pullToRefreshView.onRefreshComplete();
                if(data.size()%pageSize!=0){//当前页面的数据少于一页的数据，所以上拉获取更多数据模式关闭
                    pullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else{
                    pullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }
        });
    }

    enum Direction{
        UP,DOWN;
    }
}