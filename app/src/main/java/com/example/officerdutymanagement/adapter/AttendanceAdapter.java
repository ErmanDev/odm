package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.model.ClockSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<Attendance> attendanceList;
    private ClockSettings clockSettings;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public AttendanceAdapter(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public void setClockSettings(ClockSettings clockSettings) {
        this.clockSettings = clockSettings;
        notifyDataSetChanged();
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
        private LinearLayout layoutBadges;
        private TextView badgeClockIn;
        private TextView badgeClockOut;
        private TextView badgeLate;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewOfficerName = itemView.findViewById(R.id.textViewOfficerName);
            textViewClockIn = itemView.findViewById(R.id.textViewClockIn);
            textViewClockOut = itemView.findViewById(R.id.textViewClockOut);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            layoutBadges = itemView.findViewById(R.id.layoutBadges);
            badgeClockIn = itemView.findViewById(R.id.badgeClockIn);
            badgeClockOut = itemView.findViewById(R.id.badgeClockOut);
            badgeLate = itemView.findViewById(R.id.badgeLate);
        }

        public void bind(Attendance attendance) {
            if (attendance.getDate() != null) {
                textViewDate.setText(dateFormat.format(attendance.getDate()));
            }
            
            // Use user.fullName if available, otherwise fallback to officer.name
            String officerName = "Unknown";
            if (attendance.getUser() != null && attendance.getUser().getFullName() != null) {
                officerName = attendance.getUser().getFullName();
            } else if (attendance.getOfficer() != null && attendance.getOfficer().getName() != null) {
                officerName = attendance.getOfficer().getName();
            }
            textViewOfficerName.setText(officerName);
            
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
            
            // Update badges
            updateBadges(attendance);
        }
        
        private void updateBadges(Attendance attendance) {
            boolean hasClockIn = attendance.getClockIn() != null;
            boolean hasClockOut = attendance.getClockOut() != null;
            boolean isLate = false;
            
            // Check if clock in is late (only if clock settings are available and clock in is after start time)
            if (hasClockIn && clockSettings != null && clockSettings.getClockInStartTime() != null) {
                try {
                    // Parse clock in time
                    Calendar clockInCal = Calendar.getInstance();
                    clockInCal.setTime(attendance.getClockIn());
                    int clockInHour = clockInCal.get(Calendar.HOUR_OF_DAY);
                    int clockInMinute = clockInCal.get(Calendar.MINUTE);
                    int clockInMinutes = clockInHour * 60 + clockInMinute;
                    
                    // Parse clock in start time from settings (format: HH:mm:ss or HH:mm)
                    String clockInStartTime = clockSettings.getClockInStartTime();
                    String[] startParts = clockInStartTime.split(":");
                    int startHour = Integer.parseInt(startParts[0]);
                    int startMinute = Integer.parseInt(startParts[1]);
                    int startMinutes = startHour * 60 + startMinute;
                    
                    // Consider late only if clock in is after the configured start time
                    isLate = clockInMinutes > startMinutes;
                } catch (Exception e) {
                    // If parsing fails, don't show late badge
                    isLate = false;
                }
            }
            
            // Show/hide badges
            if (hasClockIn || hasClockOut || isLate) {
                layoutBadges.setVisibility(View.VISIBLE);
                
                badgeClockIn.setVisibility(hasClockIn ? View.VISIBLE : View.GONE);
                badgeClockOut.setVisibility(hasClockOut ? View.VISIBLE : View.GONE);
                badgeLate.setVisibility(isLate ? View.VISIBLE : View.GONE);
            } else {
                layoutBadges.setVisibility(View.GONE);
            }
        }
    }
}

