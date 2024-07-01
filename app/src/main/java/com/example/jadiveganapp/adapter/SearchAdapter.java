package com.example.jadiveganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.R;
import com.example.jadiveganapp.RecipeDetailActivity;
import com.example.jadiveganapp.model.RandomModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<RandomModel> searchResults;
    private Context context;

    public SearchAdapter(Context context, List<RandomModel> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RandomModel recipe = searchResults.get(position);
        holder.title.setText(recipe.getRecipeTitle());
        holder.category.setText(recipe.getRecipeCategory());
        holder.servings.setText(recipe.getRecipeServings() + " Servings");
        // Load image into ImageView using Glide
        Glide.with(context).load(recipe.getRecipeImage()).into(holder.image);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            String uid = recipe.getUID();
            if (uid != null) {
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra("recipeUid", uid);
                context.startActivity(intent);
            } else {
                // Handle null UID scenario
                // Optionally show a Toast or handle the case gracefully
                Toast.makeText(context, "Recipe UID is missing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateList(List<RandomModel> newList) {
        searchResults = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, servings;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.view_all_name);
            category = itemView.findViewById(R.id.view_all_category);
            servings = itemView.findViewById(R.id.view_all_servings);
            image = itemView.findViewById(R.id.view_all_img);
        }
    }
}
