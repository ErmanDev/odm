package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;
import com.example.officerdutymanagement.model.Officer;

import java.util.List;

public class OfficerAdapter extends RecyclerView.Adapter<OfficerAdapter.OfficerViewHolder> {

    private List<Officer> officerList;

    public OfficerAdapter(List<Officer> officerList) {
        this.officerList = officerList;
    }

    @NonNull
    @Override
    public OfficerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_officer, parent, false);
        return new OfficerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficerViewHolder holder, int position) {
        Officer officer = officerList.get(position);
        holder.textViewName.setText(officer.getName());
        holder.textViewDepartment.setText(officer.getDepartment());
    }

    @Override
    public int getItemCount() {
        return officerList != null ? officerList.size() : 0;
    }

    public void updateOfficerList(List<Officer> newList) {
        this.officerList = newList;
        notifyDataSetChanged();
    }

    static class OfficerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDepartment;

        OfficerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewOfficerName);
            textViewDepartment = itemView.findViewById(R.id.textViewOfficerDepartment);
        }
    }
}

