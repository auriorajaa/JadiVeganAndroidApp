package com.example.jadiveganapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.adapter.FavoriteRecipeAdapter;
import com.example.jadiveganapp.databinding.ActivityDashboardUserBinding;
import com.example.jadiveganapp.databinding.ActivityProfileBinding;
import com.example.jadiveganapp.model.FavoriteRecipeModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    // view binding
    private ActivityProfileBinding binding;

    private RecyclerView recipeRv;
    private ArrayList<FavoriteRecipeModel> favoriteRecipeList;
    private FavoriteRecipeAdapter favoriteRecipeAdapter;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeRv = findViewById(R.id.recipeRv);
        recipeRv.setLayoutManager(new LinearLayoutManager(this));
        favoriteRecipeList = new ArrayList<>();
        favoriteRecipeAdapter = new FavoriteRecipeAdapter(this, favoriteRecipeList);
        recipeRv.setAdapter(favoriteRecipeAdapter);

        // setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        loadFavoriteRecipes();

        // handle click edit profile
        binding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });

        // bottom bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), DashboardUserActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadFavoriteRecipes() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Favorites");

        userFavoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteRecipeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String recipeUid = ds.getKey();
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeUid);
                    recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot recipeSnapshot) {
                            if (recipeSnapshot.exists()) {
                                String title = recipeSnapshot.child("RecipeTitle").getValue(String.class);
                                String image = recipeSnapshot.child("RecipeImage").getValue(String.class);
                                String category = recipeSnapshot.child("RecipeCategory").getValue(String.class);
                                String servings = recipeSnapshot.child("RecipeServings").getValue(String.class);

                                FavoriteRecipeModel favoriteRecipe = new FavoriteRecipeModel(recipeUid, title, image, category, servings);
                                favoriteRecipeList.add(favoriteRecipe);

                                Collections.reverse(favoriteRecipeList);

                                favoriteRecipeAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileActivity.this, "Failed to load recipe details", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static final String formatTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        // Format timestamp to "dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = dateFormat.format(calendar.getTime());

        return date;
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user " + firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get all info of user from snapshot
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();

                        // format date
                        String formattedDate = formatTimestamp(Long.parseLong(timestamp));

                        // set data to ui
                        binding.userEmail.setText(email);
                        binding.userName.setText(name);
                        binding.userCreatedAt.setText("Created at: " + formattedDate);

                        // set image only if activity is not destroyed
                        if (!isDestroyed()) {
                            Glide.with(ProfileActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.user)
                                    .into(binding.profilePicture);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load user info", error.toException());
                    }
                });
    }

}