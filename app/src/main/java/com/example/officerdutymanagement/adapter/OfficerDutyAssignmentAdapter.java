package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.DutyAssignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfficerDutyAssignmentAdapter extends RecyclerView.Adapter<OfficerDutyAssignmentAdapter.DutyAssignmentViewHolder> {

    private List<DutyAssignment> dutyAssignmentList;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public OfficerDutyAssignmentAdapter(List<DutyAssignment> dutyAssignmentList) {
        this.dutyAssignmentList = dutyAssignmentList;
    }

    @NonNull
    @Override
    public DutyAssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_officer_duty_assignment, parent, false);
        return new DutyAssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DutyAssignmentViewHolder holder, int position) {
        DutyAssignment assignment = dutyAssignmentList.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return dutyAssignmentList != null ? dutyAssignmentList.size() : 0;
    }

    public void updateDutyAssignmentList(List<DutyAssignment> newList) {
        this.dutyAssignmentList = newList;
        notifyDataSetChanged();
    }

    class DutyAssignmentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewStatus;
        private TextView textViewTaskLocation;

        public DutyAssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTaskLocation = itemView.findViewById(R.id.textViewTaskLocation);
        }

        public void bind(DutyAssignment assignment) {
            // Format date
            if (assignment.getDate() != null) {
                try {
                    Date date = inputFormat.parse(assignment.getDate());
                    if (date != null) {
                        textViewDate.setText(outputFormat.format(date));
                    } else {
                        textViewDate.setText(assignment.getDate());
                    }
                } catch (ParseException e) {
                    // If parsing fails, try simpler format
                    try {
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = simpleFormat.parse(assignment.getDate());
                        if (date != null) {
                            textViewDate.setText(outputFormat.format(date));
                        } else {
                            textViewDate.setText(assignment.getDate());
                        }
                    } catch (ParseException ex) {
                        textViewDate.setText(assignment.getDate());
                    }
                }
            } else {
                textViewDate.setText("--");
            }

            // Set task/location
            if (assignment.getTaskLocation() != null) {
                textViewTaskLocation.setText(assignment.getTaskLocation());
            } else {
                textViewTaskLocation.setText("--");
            }

            // Set status badge
            String status = assignment.getStatus() != null ? assignment.getStatus().toUpperCase() : "PENDING";
            textViewStatus.setText(status);
            
            // Set status badge color
            if ("APPROVED".equals(status) || "ONGOING".equals(status)) {
                textViewStatus.setBackgroundResource(R.drawable.badge_green_background);
            } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                textViewStatus.setBackgroundResource(R.drawable.badge_red_background);
            } else if ("COMPLETED".equals(status)) {
                textViewStatus.setBackgroundResource(R.drawable.badge_green_background);
            } else {
                // Pending status
                textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            }
        }
    }
}

