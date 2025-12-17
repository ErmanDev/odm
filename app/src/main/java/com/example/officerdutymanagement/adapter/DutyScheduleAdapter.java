package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.DutySchedule;

import java.util.List;

public class DutyScheduleAdapter extends RecyclerView.Adapter<DutyScheduleAdapter.DutyScheduleViewHolder> {

    private List<DutySchedule> dutyScheduleList;

    public DutyScheduleAdapter(List<DutySchedule> dutyScheduleList) {
        this.dutyScheduleList = dutyScheduleList;
    }

    @NonNull
    @Override
    public DutyScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_duty_schedule, parent, false);
        return new DutyScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DutyScheduleViewHolder holder, int position) {
        DutySchedule schedule = dutyScheduleList.get(position);
        holder.textViewDate.setText(schedule.getDate());
        holder.textViewDuty.setText(schedule.getDuty());
    }

    @Override
    public int getItemCount() {
        return dutyScheduleList != null ? dutyScheduleList.size() : 0;
    }

    static class DutyScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewDuty;

        DutyScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDuty = itemView.findViewById(R.id.textViewDuty);
        }
    }
}

