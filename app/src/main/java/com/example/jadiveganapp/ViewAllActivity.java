package com.example.jadiveganapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jadiveganapp.adapter.ViewAllAdapter;
import com.example.jadiveganapp.model.RecentModel;
import com.example.jadiveganapp.model.ViewAllModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewAllAdapter viewAllAdapter;
    private List<ViewAllModel> viewAllModelList;

    private TextView categoryName;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        categoryName = findViewById(R.id.category_name);

        // Dapatkan kategori yang dipilih dari intent
        selectedCategory = getIntent().getStringExtra("Category");
        categoryName.setText(selectedCategory);

        String selectedCategory = getIntent().getStringExtra("Category");

        recyclerView = findViewById(R.id.view_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(this, viewAllModelList);
        recyclerView.setAdapter(viewAllAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewAllModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey(); // Dapatkan UID

                    String title = snapshot.child("RecipeTitle").getValue(String.class);
                    String category = snapshot.child("RecipeCategory").getValue(String.class);
                    String servings = snapshot.child("RecipeServings").getValue(String.class);
                    String image = snapshot.child("RecipeImage").getValue(String.class);

                    // Buat objek ViewAllModel dengan data yang didapatkan
                    ViewAllModel recipe = new ViewAllModel(uid, image, title, category, servings);

                    // Tambahkan resep jika kategori sesuai
                    if (selectedCategory.equals(category)) {
                        viewAllModelList.add(recipe);
                    }
                }
                viewAllAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewAllActivity", "Error: " + databaseError.getMessage());
            }
        });


    }

    private void loadRecipes(String category) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewAllModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ViewAllModel recipe = snapshot.getValue(ViewAllModel.class);
                    if (recipe != null && recipe.getRecipeCategory().equals(category)) {
                        viewAllModelList.add(recipe);
                    }
                }
                viewAllAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewAllActivity", "Error: " + databaseError.getMessage());
            }
        });
    }

    // Method to handle back button click
    public void onBackButtonClick(View view) {
        onBackPressed(); // Navigate back to previous activity
    }
}
