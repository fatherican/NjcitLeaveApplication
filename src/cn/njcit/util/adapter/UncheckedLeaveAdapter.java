package cn.njcit.util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.njcit.R;
import cn.njcit.constants.AppConstants;
import cn.njcit.util.LeaveJsonHttpResponseHandler;
import cn.njcit.util.data.SharedPrefeenceUtils;
import cn.njcit.util.enctype.MD5Utils;
import cn.njcit.util.http.HttpClientUtils;
import cn.njcit.util.view.DialogUtils;
import lib.Effectstype;
import lib.NiftyDialogBuilder;

import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by YK on 2014/12/2.
 */
public class UncheckedLeaveAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Map<String,String>> data ;
    private Context context;
    public UncheckedLeaveAdapter(List<Map<String, String>> data,Context context) {
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
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_student_check_uncheck_item,parent,false);
            viewHolder.leaveDateTV = (TextView) convertView.findViewById(R.id.leaveDateTV);
            viewHolder.leaveTypeTV = (TextView) convertView.findViewById(R.id.leaveTypeTV);
            viewHolder.typeContentTV = (TextView) convertView.findViewById(R.id.leaveContentTv);
            viewHolder.deleteImageView = (ImageView) convertView.findViewById(R.id.deleteBT);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();

        }
        viewHolder.leaveDateTV.setText(data.get(position).get("leaveDate"));
        String approved = data.get(position).get("approved");//-1 未审批0 未通过 1通过 2辅导员已审批等待学管处审批
        String leaveType =data.get(position).get("leaveType");// 0 节次请假，1天数请假
        if("0".equals(leaveType)){
            String courseName =  data.get(position).get("courseName");
            viewHolder.leaveTypeTV.setText("课");
            viewHolder.typeContentTV.setText(courseName);
        }else  if("1".equals(leaveType)){
            String leaveStartDate =  data.get(position).get("leaveStartDate");
            String leaveEndDate =  data.get(position).get("leaveEndDate");
            viewHolder.leaveTypeTV.setText("天");
            viewHolder.typeContentTV.setText(leaveStartDate + "至" + leaveEndDate);
        }

        viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //弹出选择框
                showLeaveDeleteDialg(context,clickPosition);
            }
        });

        return convertView;
    }


    public void showLeaveDeleteDialg(final Context context, final int clickPosition){

        final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitleColor("#FFFFFF")
                .withTitle("撤销假条")
                .withMessage("是否撤销本次请假？")
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
                        HttpClientUtils.post("leave/delLeaveItem.do",rp,new LeaveJsonHttpResponseHandler(context) {
                            @Override
                            public void getJsonObject(JSONObject jsonObject) throws Exception {
                                String code = jsonObject.getString("code");
                                if("200".equals(code)){
                                    DialogUtils.hideTrasparentDialog(context);
                                    dialogBuilder.dismiss();
                                    dialogBuilder.cancel();
                                    data.remove(clickPosition);
                                    UncheckedLeaveAdapter.this.notifyDataSetChanged();
                                    Toast.makeText(context,"假条撤销成功",Toast.LENGTH_LONG).show();
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
                })
                .show();

    }


    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    class ViewHolder{
        public TextView leaveDateTV;
        public TextView leaveTypeTV;
        public TextView typeContentTV;
        public ImageView deleteImageView;

    }
}
