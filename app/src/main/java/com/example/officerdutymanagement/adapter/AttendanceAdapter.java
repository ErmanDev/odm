package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.Attendance;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<Attendance> attendanceList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public AttendanceAdapter(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.bind(attendance);
    }

    @Override
    public int getItemCount() {
        return attendanceList != null ? attendanceList.size() : 0;
    }

    public void updateAttendanceList(List<Attendance> newList) {
        this.attendanceList = newList;
        notifyDataSetChanged();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewOfficerName;
        private TextView textViewClockIn;
        private TextView textViewClockOut;
        private TextView textViewStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewOfficerName = itemView.findViewById(R.id.textViewOfficerName);
            textViewClockIn = itemView.findViewById(R.id.textViewClockIn);
            textViewClockOut = itemView.findViewById(R.id.textViewClockOut);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(Attendance attendance) {
            if (attendance.getDate() != null) {
                textViewDate.setText(dateFormat.format(attendance.getDate()));
            }
            
            if (attendance.getOfficer() != null) {
                textViewOfficerName.setText(attendance.getOfficer().getName());
            } else {
                textViewOfficerName.setText("Unknown");
            }
            
            if (attendance.getClockIn() != null) {
                textViewClockIn.setText("In: " + timeFormat.format(attendance.getClockIn()));
            } else {
                textViewClockIn.setText("In: --");
            }
            
            if (attendance.getClockOut() != null) {
                textViewClockOut.setText("Out: " + timeFormat.format(attendance.getClockOut()));
            } else {
                textViewClockOut.setText("Out: --");
            }
            
            if (attendance.getStatus() != null) {
                textViewStatus.setText(attendance.getStatus().toUpperCase());
            } else {
                textViewStatus.setText("--");
            }
        }
    }
}

