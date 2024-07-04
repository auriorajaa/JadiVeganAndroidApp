package com.example.jadiveganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.R;
import com.example.jadiveganapp.RecipeDetailActivity;
import com.example.jadiveganapp.model.FavoriteRecipeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavoriteRecipeAdapter extends RecyclerView.Adapter<FavoriteRecipeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FavoriteRecipeModel> favoriteRecipeList;
    private FirebaseAuth firebaseAuth;

    public FavoriteRecipeAdapter(Context context, ArrayList<FavoriteRecipeModel> favoriteRecipeList) {
        this.context = context;
        this.favoriteRecipeList = favoriteRecipeList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recipe_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRecipeModel recipe = favoriteRecipeList.get(position);

        holder.recipeTitle.setText(recipe.getRecipeTitle());
        holder.recipeCategory.setText(recipe.getRecipeCategory());
        holder.recipeServings.setText(recipe.getRecipeServings() + " Servings");

        Glide.with(context)
                .load(recipe.getRecipeImage())
                .into(holder.recipeImage);

        // Handle item click to open RecipeDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipeUid", recipe.getRecipeUid());
            context.startActivity(intent);
        });

        holder.removeFavBtn.setOnClickListener(v -> {
            String uid = firebaseAuth.getCurrentUser().getUid();
            String recipeId = recipe.getRecipeUid();
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

            favoritesRef.child(recipeId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove recipe", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return favoriteRecipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle, recipeCategory, recipeServings;
        ImageButton removeFavBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeCategory = itemView.findViewById(R.id.recipe_category);
            recipeServings = itemView.findViewById(R.id.recipe_servings);
            removeFavBtn = itemView.findViewById(R.id.removeFavBtn);
        }
    }
}
