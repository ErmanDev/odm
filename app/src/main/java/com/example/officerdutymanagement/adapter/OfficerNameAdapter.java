package com.example.officerdutymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.R;

import java.util.List;

public class OfficerNameAdapter extends RecyclerView.Adapter<OfficerNameAdapter.OfficerNameViewHolder> {

    private List<String> officerNameList;

    public OfficerNameAdapter(List<String> officerNameList) {
        this.officerNameList = officerNameList;
    }

    @NonNull
    @Override
    public OfficerNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_officer_name, parent, false);
        return new OfficerNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficerNameViewHolder holder, int position) {
        String officerName = officerNameList.get(position);
        holder.textViewName.setText(officerName);
    }

    @Override
    public int getItemCount() {
        return officerNameList != null ? officerNameList.size() : 0;
    }

    static class OfficerNameViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        OfficerNameViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewOfficerName);
        }
    }
}

