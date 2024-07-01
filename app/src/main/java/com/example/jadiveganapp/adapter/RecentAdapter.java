package com.example.jadiveganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.R;
import com.example.jadiveganapp.RecipeDetailActivity;
import com.example.jadiveganapp.model.RecentModel;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private Context context;
    private List<RecentModel> recentModelList;

    public RecentAdapter(Context context, List<RecentModel> recentModelList) {
        this.context = context;
        this.recentModelList = recentModelList;
    }

    @NonNull
    @Override
    public RecentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentAdapter.ViewHolder holder, int position) {
        RecentModel recentModel = recentModelList.get(position);

        // Bind data to views
        Glide.with(context).load(recentModel.getRecipeImage()).into(holder.RecipeImage);
        holder.RecipeTitle.setText(recentModel.getRecipeTitle());
        holder.RecipeCategory.setText(recentModel.getRecipeCategory());
        holder.RecipeServings.setText(recentModel.getRecipeServings() + " Servings");

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Navigate to RecipeDetailActivity and pass UID
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipeUid", recentModel.getUID());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return recentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView RecipeImage;
        TextView RecipeTitle, RecipeCategory, RecipeServings, UID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            RecipeImage = itemView.findViewById(R.id.recent_img);
            RecipeTitle = itemView.findViewById(R.id.recent_title);
            RecipeCategory = itemView.findViewById(R.id.recent_category);
            RecipeServings = itemView.findViewById(R.id.recent_servings);
            UID = itemView.findViewById(R.id.UID); // Initialize UID TextView
        }
    }
}
