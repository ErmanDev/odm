package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.OngoingActivity;

import java.util.List;

public class OngoingActivityAdapter extends RecyclerView.Adapter<OngoingActivityAdapter.OngoingActivityViewHolder> {

    private List<OngoingActivity> activityList;

    public OngoingActivityAdapter(List<OngoingActivity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public OngoingActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_ongoing_activity, parent, false);
        return new OngoingActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingActivityViewHolder holder, int position) {
        OngoingActivity activity = activityList.get(position);
        
        // Check if this is an empty row
        boolean isEmpty = (activity.getDate() == null || activity.getDate().isEmpty()) &&
                         (activity.getTask() == null || activity.getTask().isEmpty());
        
        if (isEmpty) {
            // Empty row - show empty text
            holder.textViewDate.setText("");
            holder.textViewTask.setText("");
            holder.textViewLocation.setText("");
            holder.textViewStatus.setText("");
            holder.textViewStatus.setBackgroundResource(android.R.color.transparent);
            holder.textViewAction.setText("");
            holder.textViewAction.setCompoundDrawables(null, null, null, null);
        } else {
            // Populated row
            holder.textViewDate.setText(activity.getDate());
            holder.textViewTask.setText(activity.getTask());
            holder.textViewLocation.setText(activity.getLocation());
            holder.textViewStatus.setText(activity.getStatus());
            holder.textViewAction.setText(activity.getAction());

            // Set status background based on status value
            if ("Not Started".equals(activity.getStatus())) {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            } else if ("Ongoing".equals(activity.getStatus()) || "Scheduled".equals(activity.getStatus())) {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_ongoing_bg);
            } else {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            }

            // Add dropdown icon to Action if not empty
            if (activity.getAction() != null && !activity.getAction().isEmpty()) {
                android.graphics.drawable.Drawable dropdownIcon = ContextCompat.getDrawable(
                    holder.itemView.getContext(), android.R.drawable.arrow_down_float);
                if (dropdownIcon != null) {
                    dropdownIcon.setTint(ContextCompat.getColor(
                        holder.itemView.getContext(), R.color.dashboard_header_bg));
                    holder.textViewAction.setCompoundDrawablesWithIntrinsicBounds(null, null, dropdownIcon, null);
                    holder.textViewAction.setCompoundDrawablePadding(8);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }

    static class OngoingActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewTask;
        TextView textViewLocation;
        TextView textViewStatus;
        TextView textViewAction;

        OngoingActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewActivityDate);
            textViewTask = itemView.findViewById(R.id.textViewActivityTask);
            textViewLocation = itemView.findViewById(R.id.textViewActivityLocation);
            textViewStatus = itemView.findViewById(R.id.textViewActivityStatus);
            textViewAction = itemView.findViewById(R.id.textViewActivityAction);
        }
    }
}

