package com.example.jadiveganapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeTitleTextView, recipeCategoryTextView, recipeServingsTextView, recipeIngredients, recipeInstructions, recipeTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize views
        recipeImageView = findViewById(R.id.recipe_image);
        recipeTitleTextView = findViewById(R.id.recipe_title);
        recipeCategoryTextView = findViewById(R.id.recipe_category);
        recipeServingsTextView = findViewById(R.id.recipe_servings);
        recipeIngredients = findViewById(R.id.recipe_ingredient);
        recipeInstructions = findViewById(R.id.recipe_instructions);
        recipeTips = findViewById(R.id.recipe_tips);

        // Get UID from intent
        String recipeUid = getIntent().getStringExtra("recipeUid");

        if (recipeUid == null) {
            Toast.makeText(this, "Recipe UID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Tutup aktivitas
            return;
        }

        // Retrieve recipe details from Firebase using UID
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeUid);
        recipeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("RecipeTitle").getValue(String.class);
                    String category = dataSnapshot.child("RecipeCategory").getValue(String.class);
                    String servings = dataSnapshot.child("RecipeServings").getValue(String.class);
                    String image = dataSnapshot.child("RecipeImage").getValue(String.class);
                    String ingredients = dataSnapshot.child("RecipeIngredients").getValue(String.class);
                    String instructions = dataSnapshot.child("RecipeInstructions").getValue(String.class);
                    String tips = dataSnapshot.child("RecipeTips").getValue(String.class);

                    // Set retrieved data to views
                    Glide.with(RecipeDetailActivity.this).load(image).into(recipeImageView);
                    recipeTitleTextView.setText(title);
                    recipeCategoryTextView.setText(category);
                    recipeServingsTextView.setText(servings + " Servings");
                    recipeIngredients.setText(ingredients);
                    recipeInstructions.setText(instructions);
                    recipeTips.setText(tips);
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    Log.e("RecipeDetailActivity", "Recipe not found with UID: " + recipeUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecipeDetailActivity", "Failed to read recipe details", databaseError.toException());
            }
        });
    }

    // Method to handle back button click
    public void onBackButtonClick(View view) {
        onBackPressed(); // Navigate back to previous activity
    }
}
