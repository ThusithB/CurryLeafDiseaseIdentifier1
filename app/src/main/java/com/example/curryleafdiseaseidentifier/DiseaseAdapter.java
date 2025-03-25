package com.example.curryleafdiseaseidentifier;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder> {

    private Context context;
    private List<DiseaseModel> diseaseList;
    private OnItemClickListener onItemClickListener;  // Added listener

    public DiseaseAdapter(Context context, List<DiseaseModel> diseaseList, OnItemClickListener listener) {
        this.context = context;
        this.diseaseList = diseaseList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disease_card, parent, false);
        return new DiseaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseViewHolder holder, int position) {
        DiseaseModel disease = diseaseList.get(position);
        holder.diseaseName.setText(disease.getName());
        holder.diseaseImage.setImageResource(disease.getImageResourceId()); // Set disease image

        // Set the item click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(disease);  // Pass the clicked disease object
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    // Create an interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(DiseaseModel disease);
    }

    public static class DiseaseViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;
        ImageView diseaseImage;

        public DiseaseViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.diseaseName);
            diseaseImage = itemView.findViewById(R.id.diseaseImage);
        }
    }
}
