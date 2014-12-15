package cn.njcit.util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.njcit.R;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import cn.njcit.util.view.DialogUtils;
import lib.Effectstype;
import lib.NiftyDialogBuilder;

/**
 * Created by YK on 2014/12/2.
 */
public class SickLeaveAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Map<String, String>> data;
    private Context context;

    public SickLeaveAdapter(List<Map<String, String>> data, Context context) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int clickPosition = position;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_student_sick_item, parent, false);
            viewHolder.leaveDateTV = (TextView) convertView.findViewById(R.id.leaveDateTV);
            viewHolder.approvedTV = (TextView) convertView.findViewById(R.id.approvedTV);
            viewHolder.leaveTypeTV = (TextView) convertView.findViewById(R.id.leaveTypeTV);
            viewHolder.rightContentTV = (TextView) convertView.findViewById(R.id.rightContentTV);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.leaveDateTV.setText(data.get(position).get("leaveDate"));

        viewHolder.approvedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaveDeleteDialg(context,clickPosition);
            }
        });


        String leaveType = data.get(position).get("leaveType");// 0 节次请假，1天数请假
        if ("0".equals(leaveType)) {
            String courseName = data.get(position).get("courseName");
            viewHolder.rightContentTV.setText(courseName);
            viewHolder.leaveTypeTV.setText("课");
        } else if ("1".equals(leaveType)) {
            String leaveStartDate = data.get(position).get("leaveStartDate");
            String leaveEndDate = data.get(position).get("leaveEndDate");
            viewHolder.rightContentTV.setText(leaveStartDate + "至" + leaveEndDate);
            viewHolder.leaveTypeTV.setText("天");
        }

        return convertView;
    }


    public void showLeaveDeleteDialg(final Context context, final int clickPosition) {
        final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitleColor("#FFFFFF")
                .withTitle("销假")
                .withMessage("确定销假？")
                .withEffect(Effectstype.Slideright)
                .withDuration(400)
                .setCustomView(android.R.layout.activity_list_item,context)
                .withDialogColor("#FFcccccc")
                .withButton1Text("确定")
                .withButton2Text("取消")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams rp = new RequestParams();
                        rp.add("userId", SharedPrefeenceUtils.getSharedPreferenceString(context, "userId"));
                        rp.add("token", MD5Utils.md5Hex(SharedPrefeenceUtils.getSharedPreferenceString(context, "userId") + AppConstants.SOCKET_KEY));
                        rp.add("leaveId", data.get(clickPosition).get("leaveId"));
                        DialogUtils.showTrasparentDialog(context);
                        HttpClientUtils.post("leave/studentSickLeave.do",rp,new LeaveJsonHttpResponseHandler(context) {
                            @Override
                            public void getJsonObject(JSONObject jsonObject) throws Exception {
                                String code = jsonObject.getString("code");
                                if("200".equals(code)){
                                    DialogUtils.hideTrasparentDialog(context);
                                    dialogBuilder.dismiss();
                                    dialogBuilder.cancel();
                                    data.remove(clickPosition);
                                    SickLeaveAdapter.this.notifyDataSetChanged();
                                    Toast.makeText(context,"销假成功",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context,"系统异常",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }) .setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        }).show();

    }


    class ViewHolder {
        public TextView leaveDateTV;
        public TextView approvedTV;
        public TextView leaveTypeTV;
        public TextView rightContentTV;

    }
}