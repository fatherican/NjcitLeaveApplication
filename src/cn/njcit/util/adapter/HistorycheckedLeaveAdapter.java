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
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by YK on 2014/12/2.
 */
public class HistorycheckedLeaveAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Map<String,String>> data ;
    private Context context;
    public HistorycheckedLeaveAdapter(List<Map<String, String>> data, Context context) {
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
            convertView = inflater.inflate(R.layout.fragment_student_check_historycheck_item,parent,false);
            viewHolder.leaveDateTV = (TextView) convertView.findViewById(R.id.leaveDateTV);
            viewHolder.approvedTV = (TextView) convertView.findViewById(R.id.approvedTV);
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
            viewHolder.approvedTV.setText(courseName);
        }else  if("1".equals(leaveType)){
            String leaveStartDate =  data.get(position).get("leaveStartDate");
            String leaveEndDate =  data.get(position).get("leaveEndDate");
            viewHolder.approvedTV.setText(leaveStartDate+"至"+leaveEndDate);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请假处理");
        builder.setMessage("是否撤销本次请假？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                            data.remove(clickPosition);
                            HistorycheckedLeaveAdapter.this.notifyDataSetChanged();
                            Toast.makeText(context,"假条撤销成功",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context,"系统异常",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }








    class ViewHolder{
        public TextView leaveDateTV;
        public TextView approvedTV;
        public ImageView deleteImageView;

    }
}
