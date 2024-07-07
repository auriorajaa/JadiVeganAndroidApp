package com.example.jadiveganapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jadiveganapp.adapter.SearchAdapter;
import com.example.jadiveganapp.model.RandomModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private List<RandomModel> searchResults;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, searchResults);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        // Menampilkan semua data saat activity pertama kali dibuka
        loadAllRecipes();

        // Handle onClick untuk memperluas SearchView saat diklik
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded(); // Memastikan SearchView diperluas saat diklik
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Jika teks pencarian kosong, tampilkan semua data lagi
                    loadAllRecipes();
                } else {
                    // Lakukan pencarian berdasarkan teks baru
                    searchRecipes(newText);
                }
                return false;
            }
        });

        // Bottom bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), DashboardUserActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
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

    private void loadAllRecipes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchResults.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String title = snapshot.child("RecipeTitle").getValue(String.class);
                    String category = snapshot.child("RecipeCategory").getValue(String.class);
                    String servings = snapshot.child("RecipeServings").getValue(String.class);
                    String image = snapshot.child("RecipeImage").getValue(String.class);

                    RandomModel recipe = new RandomModel(uid, title, category, servings, image);
                    searchResults.add(recipe);
                }
                searchAdapter.updateList(searchResults);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });
    }

    private void searchRecipes(String query) {
        // Convert query to lowercase
        String lowerCaseQuery = query.toLowerCase();

        // Get a reference to the database and read all recipes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchResults.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String title = snapshot.child("RecipeTitle").getValue(String.class);
                    String category = snapshot.child("RecipeCategory").getValue(String.class);
                    String servings = snapshot.child("RecipeServings").getValue(String.class);
                    String image = snapshot.child("RecipeImage").getValue(String.class);

                    if (title != null && title.toLowerCase().contains(lowerCaseQuery)) {
                        RandomModel recipe = new RandomModel(uid, title, category, servings, image);
                        searchResults.add(recipe);
                    }
                }
                searchAdapter.updateList(searchResults);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });
    }


    // Method to handle back button click
    public void onBackButtonClick(View view) {
        onBackPressed(); // Navigate back to previous activity
    }
}
