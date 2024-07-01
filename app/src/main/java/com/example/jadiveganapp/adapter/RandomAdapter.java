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
import com.example.jadiveganapp.model.RandomModel;

import java.util.List;

public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.ViewHolder>{

    private Context context;
    private List<RandomModel> randomModelList;

    public RandomAdapter(Context context, List<RandomModel> randomModelList) {
        this.context = context;
        this.randomModelList = randomModelList;
    }

    @NonNull
    @Override
    public RandomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RandomAdapter.ViewHolder holder, int position) {
        RandomModel randomModel = randomModelList.get(position);

        // Bind data to views
        Glide.with(context).load(randomModel.getRecipeImage()).into(holder.RecipeImage);
        holder.RecipeTitle.setText(randomModel.getRecipeTitle());
        holder.RecipeCategory.setText(randomModel.getRecipeCategory());
        holder.RecipeServings.setText(randomModel.getRecipeServings() + " Servings");

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Navigate to RecipeDetailActivity and pass UID
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipeUid", randomModel.getUID());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return randomModelList.size();
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
