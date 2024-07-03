package com.example.jadiveganapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView, favoriteBtn;
    private TextView recipeTitleTextView, recipeCategoryTextView, recipeServingsTextView, recipeIngredients, recipeInstructions, recipeTips;
    private String recipeUid;
    private FirebaseAuth firebaseAuth;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Get the recipe UID from the intent
        recipeUid = getIntent().getStringExtra("recipeUid");

        // Initialize views
        recipeImageView = findViewById(R.id.recipe_image);
        favoriteBtn = findViewById(R.id.favoriteBtn);
        recipeTitleTextView = findViewById(R.id.recipe_title);
        recipeCategoryTextView = findViewById(R.id.recipe_category);
        recipeServingsTextView = findViewById(R.id.recipe_servings);
        recipeIngredients = findViewById(R.id.recipe_ingredient);
        recipeInstructions = findViewById(R.id.recipe_instructions);
        recipeTips = findViewById(R.id.recipe_tips);

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to read recipe details", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if the recipe is already in favorites
        checkIfFavorite();

        // Set click listener on the favorite button
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFavorite();
                } else {
                    addFavorite();
                }
            }
        });
    }

    private void checkIfFavorite() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        favoriteReference.child(recipeUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                if (isFavorite) {
                    favoriteBtn.setImageResource(R.drawable.ic_bookmark_black);
                } else {
                    favoriteBtn.setImageResource(R.drawable.ic_bookmark_border);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to check favorite status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFavorite() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        HashMap<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("timestamp", System.currentTimeMillis());

        favoriteReference.child(recipeUid).setValue(favoriteData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        favoriteBtn.setImageResource(R.drawable.ic_bookmark_black);
                        Toast.makeText(RecipeDetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        isFavorite = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipeDetailActivity.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFavorite() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        favoriteReference.child(recipeUid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        favoriteBtn.setImageResource(R.drawable.ic_bookmark_border);
                        Toast.makeText(RecipeDetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipeDetailActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to handle back button click
    public void onBackButtonClick(View view) {
        onBackPressed(); // Navigate back to previous activity
    }
}
