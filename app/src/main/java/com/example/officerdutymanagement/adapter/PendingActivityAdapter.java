package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.PendingActivity;

import java.util.List;

public class PendingActivityAdapter extends RecyclerView.Adapter<PendingActivityAdapter.ActivityViewHolder> {

    private List<PendingActivity> activityList;

    public PendingActivityAdapter(List<PendingActivity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pending_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        PendingActivity activity = activityList.get(position);
        holder.textViewOfficerName.setText(activity.getOfficerName());
        holder.textViewActivity.setText(activity.getActivity());
        
        // Set status with appropriate background
        String status = activity.getStatus();
        holder.textViewStatus.setText(status);
        
        if ("Pending".equals(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
        } else if ("On going".equals(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.status_ongoing_bg);
        } else {
            holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
        }
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOfficerName;
        TextView textViewActivity;
        TextView textViewStatus;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOfficerName = itemView.findViewById(R.id.textViewOfficerName);
            textViewActivity = itemView.findViewById(R.id.textViewActivity);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}

