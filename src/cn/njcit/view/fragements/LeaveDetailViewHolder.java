package cn.njcit.view.fragements;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import cn.njcit.R;

/**
 * Created by YK on 2014/12/13.
 */
public class LeaveDetailViewHolder {
    public TextView leaveTypeTV ;
    public TableRow courseNameRow ;
    public TextView courseNameTV ;
    public TableRow courseIndezRow;
    public TextView courseIndexTV ;
    public TextView leaveDteTV ;
    public TextView leaveReasonTV;
    public TextView approveStateTV;
    public TableRow instructorNoteRow;
    public TextView instructorNoteTV;
    public TableRow studentPipeNoteRow;
    public TextView studentPipeNoteTV;

    public static void initView(View v,Map<String,String> detailMap){
        TextView leaveTypeTV = (TextView) v.findViewById(R.id.leave_type);
        TableRow courseNameRow = (TableRow) v.findViewById(R.id.course_name_row);
        TextView courseNameTV = (TextView) v.findViewById(R.id.course_name);
        TableRow courseIndexRow = (TableRow) v.findViewById(R.id.course_index_row);
        TextView courseIndexTV = (TextView) v.findViewById(R.id.course_index);

        TextView leaveDateTV = (TextView) v.findViewById(R.id.leave_date);
        TextView leaveReasonTV = (TextView) v.findViewById(R.id.leave_reason);
        TextView approveStateTV = (TextView) v.findViewById(R.id.approve_state);
        TableRow instructorNoteRow = (TableRow) v.findViewById(R.id.instructor_note_row);
        TextView instructorNoteTV = (TextView) v.findViewById(R.id.instructor_note);
        TableRow studentPipeNoteRow = (TableRow) v.findViewById(R.id.student_pipe_note_row);
        TextView studentPipeNoteTV = (TextView) v.findViewById(R.id.student_pipe_note);


        leaveReasonTV.setText(detailMap.get("studentNote"));
        String approved = detailMap.get("approved");//-1 未审批0 未通过 1通过 2辅导员已审批等待学管处审批
        String leaveType = detailMap.get("leaveType");// 0 节次请假，1天数请假
        if ("-1".equals(approved)) {
            approveStateTV.setText("未审批");
        } else if ("0".equals(approved)) {
            approveStateTV.setText("不同意");
        } else if ("1".equals(approved)) {
            instructorNoteRow.setVisibility(View.VISIBLE);
            studentPipeNoteRow.setVisibility(View.VISIBLE);
            approveStateTV.setText("同意");
            instructorNoteTV.setText(detailMap.get("instructorNote"));
            studentPipeNoteTV.setText(detailMap.get("studentPipeNote"));
        } else if ("2".equals(approved)) {
            approveStateTV.setText("审核中");
        }

        if ("0".equals(leaveType)) {
            String courseName =detailMap.get("courseName");
            courseNameTV.setText(courseName);
            leaveTypeTV.setText("课程");
            courseIndexTV.setText(detailMap.get("courseIndex"));
            leaveDateTV.setText(detailMap.get("leaveDate"));
            courseNameRow.setVisibility(View.VISIBLE);
            courseIndexRow.setVisibility(View.VISIBLE);
        } else if ("1".equals(leaveType)) {
            String leaveStartDate =detailMap.get("leaveStartDate");
            String leaveEndDate = detailMap.get("leaveEndDate");
            leaveDateTV.setText(leaveStartDate + "至" + leaveEndDate);
            leaveTypeTV.setText("天次");
            courseNameRow.setVisibility(View.GONE);
            courseIndexRow.setVisibility(View.GONE);

        }

    }

}
