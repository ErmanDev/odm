package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DutyAssignmentAdapter extends RecyclerView.Adapter<DutyAssignmentAdapter.DutyAssignmentViewHolder> {

    private final List<DutyAssignment> dutyAssignments;
    private OnActionClickListener onActionClickListener;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnActionClickListener {
        void onEdit(int position, DutyAssignment assignment);
        void onDelete(int position, DutyAssignment assignment);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.onActionClickListener = listener;
    }

    public DutyAssignmentAdapter(List<DutyAssignment> dutyAssignments) {
        this.dutyAssignments = dutyAssignments;
    }

    @NonNull
    @Override
    public DutyAssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duty_assignment, parent, false);
        return new DutyAssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DutyAssignmentViewHolder holder, int position) {
        DutyAssignment assignment = dutyAssignments.get(position);
        
        // Format date
        if (assignment.getDate() != null) {
            try {
                Date date = inputFormat.parse(assignment.getDate());
                if (date != null) {
                    holder.textViewDate.setText(outputFormat.format(date));
                } else {
                    holder.textViewDate.setText(assignment.getDate());
                }
            } catch (ParseException e) {
                // If parsing fails, try simpler format
                try {
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = simpleFormat.parse(assignment.getDate());
                    if (date != null) {
                        holder.textViewDate.setText(outputFormat.format(date));
                    } else {
                        holder.textViewDate.setText(assignment.getDate());
                    }
                } catch (ParseException ex) {
                    holder.textViewDate.setText(assignment.getDate());
                }
            }
        } else {
            holder.textViewDate.setText("--");
        }
        
        // Display user.fullName if available, fallback to officerName
        String officerName = "Unknown";
        if (assignment.getUser() != null && assignment.getUser().getFullName() != null) {
            officerName = assignment.getUser().getFullName();
        } else if (assignment.getOfficerName() != null) {
            officerName = assignment.getOfficerName();
        }
        holder.textViewOfficer.setText(officerName);
        
        holder.textViewDepartment.setText(assignment.getDepartment() != null ? assignment.getDepartment() : "--");
        holder.textViewTaskLocation.setText(assignment.getTaskLocation() != null ? assignment.getTaskLocation() : "--");
        
        // Set status badge
        String status = assignment.getStatus() != null ? assignment.getStatus().toUpperCase() : "PENDING";
        holder.textViewStatus.setText(status);
        
        // Set status badge color
        if ("APPROVED".equals(status) || "ONGOING".equals(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.badge_green_background);
        } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.badge_red_background);
        } else if ("COMPLETED".equals(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.badge_green_background);
        } else {
            // Pending status
            holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
        }

        holder.buttonAction.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                // Show options dialog
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
                builder.setTitle("Options");
                builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        // Edit
                        onActionClickListener.onEdit(position, assignment);
                    } else if (which == 1) {
                        // Delete
                        onActionClickListener.onDelete(position, assignment);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dutyAssignments != null ? dutyAssignments.size() : 0;
    }

    static class DutyAssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewOfficer;
        TextView textViewDepartment;
        TextView textViewTaskLocation;
        TextView textViewStatus;
        MaterialButton buttonAction;

        public DutyAssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewOfficer = itemView.findViewById(R.id.textViewOfficer);
            textViewDepartment = itemView.findViewById(R.id.textViewDepartment);
            textViewTaskLocation = itemView.findViewById(R.id.textViewTaskLocation);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonAction = itemView.findViewById(R.id.buttonAction);
        }
    }
}

