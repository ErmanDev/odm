package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.AbsenceRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AbsenceRequestAdapter extends RecyclerView.Adapter<AbsenceRequestAdapter.AbsenceRequestViewHolder> {

    private List<AbsenceRequest> absenceRequestList;
    private OnActionClickListener onActionClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnActionClickListener {
        void onApprove(int position, AbsenceRequest absenceRequest);
        void onReject(int position, AbsenceRequest absenceRequest);
    }

    public AbsenceRequestAdapter(List<AbsenceRequest> absenceRequestList) {
        this.absenceRequestList = absenceRequestList;
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.onActionClickListener = listener;
    }

    @NonNull
    @Override
    public AbsenceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absence_request, parent, false);
        return new AbsenceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceRequestViewHolder holder, int position) {
        AbsenceRequest absenceRequest = absenceRequestList.get(position);
        holder.bind(absenceRequest, position);
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
        private TextView textViewOfficerName;
        private TextView textViewStatus;
        private TextView textViewDateRange;
        private TextView textViewReason;
        private LinearLayout layoutActions;
        private Button buttonApprove;
        private Button buttonReject;

        public AbsenceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOfficerName = itemView.findViewById(R.id.textViewOfficerName);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDateRange = itemView.findViewById(R.id.textViewDateRange);
            textViewReason = itemView.findViewById(R.id.textViewReason);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }

        public void bind(AbsenceRequest absenceRequest, int position) {
            // Set officer name
            String officerName = "Unknown";
            if (absenceRequest.getUser() != null && absenceRequest.getUser().getFullName() != null) {
                officerName = absenceRequest.getUser().getFullName();
            }
            textViewOfficerName.setText(officerName);

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
                // Pending status - use orange/yellow background
                textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            }

            // Show/hide action buttons based on status
            if ("pending".equalsIgnoreCase(absenceRequest.getStatus())) {
                layoutActions.setVisibility(View.VISIBLE);
                buttonApprove.setOnClickListener(v -> {
                    if (onActionClickListener != null) {
                        onActionClickListener.onApprove(position, absenceRequest);
                    }
                });
                buttonReject.setOnClickListener(v -> {
                    if (onActionClickListener != null) {
                        onActionClickListener.onReject(position, absenceRequest);
                    }
                });
            } else {
                layoutActions.setVisibility(View.GONE);
            }
        }
    }
}

