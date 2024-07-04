package com.example.jadiveganapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.adapter.CommentAdapter;
import com.example.jadiveganapp.databinding.ActivityRecipeDetailBinding;
import com.example.jadiveganapp.databinding.DialogCommentAddBinding;
import com.example.jadiveganapp.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private TextView recipeTitleTextView, recipeCategoryTextView, recipeServingsTextView, recipeIngredients, recipeInstructions, recipeTips, addCommentBtn;
    private String recipeUid;
    private FirebaseAuth firebaseAuth;
    private boolean isFavorite;
    private ProgressDialog progressDialog;

    // ArrayList to hold comments
    private ArrayList<CommentModel> commentModelArrayList;

    // Comment adapter
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Get the recipe UID from the intent
        recipeUid = getIntent().getStringExtra("recipeUid");

        // Initialize views
        recipeTitleTextView = binding.recipeTitle;
        recipeCategoryTextView = binding.recipeCategory;
        recipeServingsTextView = binding.recipeServings;
        recipeIngredients = binding.recipeIngredient;
        recipeInstructions = binding.recipeInstructions;
        recipeTips = binding.recipeTips;
        addCommentBtn = binding.addCommentBtn;

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
                    Glide.with(RecipeDetailActivity.this).load(image).into(binding.recipeImage);
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

        // Load comments
        loadComments();

        // Set click listener on the favorite button
        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFavorite();
                } else {
                    addFavorite();
                }
            }
        });

        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog();
            }
        });
    }

    private void loadComments() {
        commentModelArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
        ref.child(recipeUid)
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentModelArrayList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CommentModel model = ds.getValue(CommentModel.class);
                            commentModelArrayList.add(model);
                        }

                        Collections.reverse(commentModelArrayList);

                        commentAdapter = new CommentAdapter(RecipeDetailActivity.this, commentModelArrayList);
                        binding.commentsRv.setAdapter(commentAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RecipeDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String comment = "";

    private void addCommentDialog() {
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // handle click dismiss dialog
        commentAddBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // handle click add comment
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = commentAddBinding.commentEt.getText().toString().trim();

                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(RecipeDetailActivity.this, "Cannot send empty comment!", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        // Show progress
        progressDialog.setMessage("Sending comment...");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + timestamp);
        hashMap.put("recipeId", "" + recipeUid);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("comment", "" + comment);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        // DB paths
        // Recipes > recipeUID > Comments > timestamp > commentData
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
        ref.child(recipeUid).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RecipeDetailActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RecipeDetailActivity.this, "Failed to send comment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfFavorite() {
        if (firebaseAuth.getCurrentUser() == null) {
            return; // Exit method if user is not logged in
        }

        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        favoriteReference.child(recipeUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                if (isFavorite) {
                    binding.favoriteBtn.setImageResource(R.drawable.ic_bookmark_black);
                } else {
                    binding.favoriteBtn.setImageResource(R.drawable.ic_bookmark_border);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to check favorite status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFavorite() {

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        HashMap<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("timestamp", System.currentTimeMillis());

        favoriteReference.child(recipeUid).setValue(favoriteData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        binding.favoriteBtn.setImageResource(R.drawable.ic_bookmark_black);
                        Toast.makeText(RecipeDetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        isFavorite = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RecipeDetailActivity.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFavorite() {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoriteReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        favoriteReference.child(recipeUid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        binding.favoriteBtn.setImageResource(R.drawable.ic_bookmark_border);
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
