package com.example.easymap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private List<PlaceResult> results;
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(PlaceResult result);
    }
    
    public ResultsAdapter(List<PlaceResult> results, OnItemClickListener listener) {
        this.results = results;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceResult result = results.get(position);
        holder.text1.setText(result.getName());
        holder.text2.setText(result.getAddress() + " • " + result.getDistance());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(result);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return results.size();
    }
    
    public void updateResults(List<PlaceResult> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        
        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
} 