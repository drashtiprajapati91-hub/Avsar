package com.example.avsar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textBack;
    CategoryItemAdapter adapter;
    List<CategoryItem> itemList;
    List<String> itemKeys;
    String categoryName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        recyclerView = findViewById(R.id.recyclerViewCategoryItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemKeys = new ArrayList<>();

        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryItemsActivity.this, AllCategoriesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });


        categoryName = getIntent().getStringExtra("categoryName");

        adapter = new CategoryItemAdapter(this, itemList, itemKeys, categoryName);
        recyclerView.setAdapter(adapter);

        if (categoryName != null && !categoryName.isEmpty()) {
            loadCategoryItemsFromFirebase(categoryName);
        } else {
            Toast.makeText(this, "Category name is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategoryItemsFromFirebase(String categoryName) {
        FirebaseDatabase.getInstance().getReference("categories").child(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        itemKeys.clear(); // Important to avoid IndexOutOfBounds
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CategoryItem item = ds.getValue(CategoryItem.class);
                            if (item != null) {
                                itemList.add(item);
                                itemKeys.add(ds.getKey()); // Add key for click handling
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CategoryItemsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
