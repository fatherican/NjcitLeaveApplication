package cn.njcit.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.njcit.R;

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
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_student_check_uncheck,parent,false);
            TextView leaveDateTV = (TextView) convertView.findViewById(R.id.leaveDateTV);
            TextView approvedTV = (TextView) convertView.findViewById(R.id.approvedTV);
            leaveDateTV.setText(data.get(position).get("leaveDate"));
            String approved = data.get(position).get("approved");//-1 未审批0 未通过 1通过 2辅导员已审批等待学管处审批
            String leaveType =data.get(position).get("leaveType");// 0 节次请假，1天数请假
            if("0".equals(leaveType)){
                String courseName =  data.get(position).get("courseName");
                approvedTV.setText(courseName);
            }else  if("1".equals(leaveType)){
                String leaveStartDate =  data.get(position).get("leaveStartDate");
                String leaveEndDate =  data.get(position).get("leaveEndDate");
                approvedTV.setText(leaveStartDate+"至"+leaveEndDate);
            }
        }
        return convertView;
    }
}
