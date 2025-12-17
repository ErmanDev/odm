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

import java.util.List;

public class DutyAssignmentAdapter extends RecyclerView.Adapter<DutyAssignmentAdapter.DutyAssignmentViewHolder> {

    private final List<DutyAssignment> dutyAssignments;

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
        holder.textViewDate.setText(assignment.getDate());
        holder.textViewOfficer.setText(assignment.getOfficerName());
        holder.textViewDepartment.setText(assignment.getDepartment());
        holder.textViewTaskLocation.setText(assignment.getTaskLocation());
        holder.textViewStatus.setText(assignment.getStatus());

        holder.buttonAction.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), v.getContext().getString(R.string.editing_assignment, assignment.getOfficerName()), Toast.LENGTH_SHORT).show();
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

