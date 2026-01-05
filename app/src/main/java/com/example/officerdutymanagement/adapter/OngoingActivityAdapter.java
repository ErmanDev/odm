package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            holder.buttonAction.setText("");
            holder.buttonAction.setVisibility(View.GONE);
        } else {
            // Populated row
            holder.textViewDate.setText(activity.getDate());
            holder.textViewTask.setText(activity.getTask());
            holder.textViewLocation.setText(activity.getLocation());
            holder.textViewStatus.setText(activity.getStatus());
            
            // Set action button text
            if (activity.getAction() != null && !activity.getAction().isEmpty()) {
                holder.buttonAction.setText(activity.getAction());
                holder.buttonAction.setVisibility(View.VISIBLE);
            } else {
                holder.buttonAction.setText("View Details");
                holder.buttonAction.setVisibility(View.VISIBLE);
            }

            // Set status background based on status value
            if ("Not Started".equals(activity.getStatus())) {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
            } else if ("Ongoing".equals(activity.getStatus()) || "Scheduled".equals(activity.getStatus())) {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_ongoing_bg);
            } else {
                holder.textViewStatus.setBackgroundResource(R.drawable.status_pending_bg);
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
        Button buttonAction;

        OngoingActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewActivityDate);
            textViewTask = itemView.findViewById(R.id.textViewActivityTask);
            textViewLocation = itemView.findViewById(R.id.textViewActivityLocation);
            textViewStatus = itemView.findViewById(R.id.textViewActivityStatus);
            buttonAction = itemView.findViewById(R.id.textViewActivityAction);
        }
    }
}

