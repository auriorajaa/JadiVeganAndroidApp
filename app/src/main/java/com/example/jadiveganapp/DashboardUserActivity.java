package com.example.jadiveganapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jadiveganapp.adapter.HomeCategoryAdapter;
import com.example.jadiveganapp.adapter.RandomAdapter;
import com.example.jadiveganapp.adapter.RecentAdapter;
import com.example.jadiveganapp.databinding.ActivityDashboardUserBinding;
import com.example.jadiveganapp.model.HomeCategoryModel;
import com.example.jadiveganapp.model.RandomModel;
import com.example.jadiveganapp.model.RecentModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardUserActivity extends AppCompatActivity {

    private ActivityDashboardUserBinding binding;
    private FirebaseAuth firebaseAuth;

    // Initialize RecyclerView
    private RecyclerView recentRecipeRecyclerView, homeCategoryRecyclerView, randomRecipeRecyclerView;

    // Category
    private List<HomeCategoryModel> homeCategoryModelList;
    private HomeCategoryAdapter homeCategoryAdapter;

    // Recent Recipe
    private List<RecentModel> recentModelList;
    private RecentAdapter recentAdapter;

    // Random Recipe
    private List<RandomModel> randomModelList;
    private RandomAdapter randomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Handle logout button click
        binding.logoutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUser();
        });

        // Initialize RecyclerView for Home Category
        homeCategoryRecyclerView = findViewById(R.id.homeCategoryRecyclerView);
        homeCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        homeCategoryModelList = new ArrayList<>();
        homeCategoryAdapter = new HomeCategoryAdapter(this, homeCategoryModelList);
        homeCategoryRecyclerView.setAdapter(homeCategoryAdapter);

        // Retrieve home category data from Firebase
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("Category");
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                homeCategoryModelList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String name = categorySnapshot.child("Name").getValue(String.class);
                    String image = categorySnapshot.child("Image").getValue(String.class);
                    HomeCategoryModel homeCategoryModel = new HomeCategoryModel(name, image);
                    homeCategoryModelList.add(homeCategoryModel);
                }
                homeCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });

        // Initialize RecyclerView for Recent Recipes
        recentRecipeRecyclerView = findViewById(R.id.recentRecipeRecyclerView);
        recentRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recentModelList = new ArrayList<>();
        recentAdapter = new RecentAdapter(this, recentModelList);
        recentRecipeRecyclerView.setAdapter(recentAdapter);

        // Initialize RecyclerView for Random Recipes
        randomRecipeRecyclerView = findViewById(R.id.randomRecipeRecyclerView);
        randomRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        randomModelList = new ArrayList<>();
        randomAdapter = new RandomAdapter(this, randomModelList);
        randomRecipeRecyclerView.setAdapter(randomAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        databaseReference.orderByChild("CreatedAt").limitToLast(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recentModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey(); // Get the UID
                    String title = snapshot.child("RecipeTitle").getValue(String.class);
                    String category = snapshot.child("RecipeCategory").getValue(String.class);
                    String servings = snapshot.child("RecipeServings").getValue(String.class);
                    String image = snapshot.child("RecipeImage").getValue(String.class);

                    // Create RecentModel object with fetched data
                    RecentModel recipe = new RecentModel(uid, title, category, servings, image);
                    recentModelList.add(recipe);
                }
                Collections.reverse(recentModelList);

                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });


        // RANDOM RECIPES
        // Mengambil data dari Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RandomModel> tempModelList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey(); // Get the UID
                    String title = snapshot.child("RecipeTitle").getValue(String.class);
                    String category = snapshot.child("RecipeCategory").getValue(String.class);
                    String servings = snapshot.child("RecipeServings").getValue(String.class);
                    String image = snapshot.child("RecipeImage").getValue(String.class);

                    // Create RandomModel object with fetched data
                    RandomModel recipe = new RandomModel(uid, title, category, servings, image);
                    tempModelList.add(recipe);
                }

                // Shuffle the list to randomize
                Collections.shuffle(tempModelList);

                // Clear the existing list and add 6 random items
                randomModelList.clear();
                for (int i = 0; i < Math.min(6, tempModelList.size()); i++) {
                    randomModelList.add(tempModelList.get(i));
                }

                // Notify the adapter about the data change
                randomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });


        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else {
                return false;
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String uid = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        binding.nameText.setText(name + ",");
                    } else {
                        binding.nameText.setText("User,");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Failed to read data", databaseError.toException());
                }
            });
        }
    }
}
