package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.AbsenceRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OfficerAbsenceRequestAdapter extends RecyclerView.Adapter<OfficerAbsenceRequestAdapter.AbsenceRequestViewHolder> {

    private List<AbsenceRequest> absenceRequestList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public OfficerAbsenceRequestAdapter(List<AbsenceRequest> absenceRequestList) {
        this.absenceRequestList = absenceRequestList;
    }

    @NonNull
    @Override
    public AbsenceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_officer_absence_request, parent, false);
        return new AbsenceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceRequestViewHolder holder, int position) {
        AbsenceRequest absenceRequest = absenceRequestList.get(position);
        holder.bind(absenceRequest);
    }

    @Override
    public int getItemCount() {
        return absenceRequestList != null ? absenceRequestList.size() : 0;
    }

    public void updateAbsenceRequestList(List<AbsenceRequest> newList) {
        this.absenceRequestList = newList;
        notifyDataSetChanged();
    }

    class AbsenceRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDateRange;
        private TextView textViewStatus;
        private TextView textViewReason;

        public AbsenceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateRange = itemView.findViewById(R.id.textViewDateRange);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewReason = itemView.findViewById(R.id.textViewReason);
        }

        public void bind(AbsenceRequest absenceRequest) {
            // Set date range
            if (absenceRequest.getStartDate() != null && absenceRequest.getEndDate() != null) {
                String dateRange = dateFormat.format(absenceRequest.getStartDate()) + " - " + 
                                  dateFormat.format(absenceRequest.getEndDate());
                textViewDateRange.setText(dateRange);
            } else {
                textViewDateRange.setText("--");
            }

            // Set reason
            if (absenceRequest.getReason() != null) {
                textViewReason.setText(absenceRequest.getReason());
            } else {
                textViewReason.setText("--");
            }

            // Set status badge
            String status = absenceRequest.getStatus() != null ? absenceRequest.getStatus().toUpperCase() : "PENDING";
            textViewStatus.setText(status);
            
            // Set status badge color
            if ("APPROVED".equals(status)) {
                textViewStatus.setBackgroundResource(R.drawable.badge_green_background);
            } else if ("REJECTED".equals(status)) {
                textViewStatus.setBackgroundResource(R.drawable.badge_red_background);
            } else {
                // Pending status
                textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            }
        }
    }
}

