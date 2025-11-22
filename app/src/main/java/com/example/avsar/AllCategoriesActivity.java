package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textBack;
    private AllCategoryAdapter adapter;
    private List<VendorCategory> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        recyclerView = findViewById(R.id.recyclerViewAllCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        adapter = new AllCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        // 🟡 Initialize Back TextView
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(AllCategoriesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Optional: avoids stacking multiple MainActivity instances
            startActivity(intent);
            finish(); // Optional: closes AllCategoriesActivity
        });


        loadCategoriesFromFirebase();
    }

    private void loadCategoriesFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String categoryName = ds.getKey(); // "Planners", "Catering", etc.
                    String imageUrl = ds.child("imageUrl").getValue(String.class); // Optional if using Firebase URLs
                    int localImageResId = getCategoryImageResId(categoryName);

                    categoryList.add(new VendorCategory(localImageResId, categoryName));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllCategoriesActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "onCancelled: " + error.getMessage());
            }
        });
    }

    // Map Firebase key names to local drawable resource IDs
    private int getCategoryImageResId(String categoryName) {
        switch (categoryName) {
            case "Planners":
                return R.drawable.m_wedding;
            case "Catering":
                return R.drawable.catering;
            case "Make up":
                return R.drawable.makeup;
            case "Photography":
                return R.drawable.photography;
            case "Mehendi":
                return R.drawable.mehandi;
            case "DJ & Sound":
                return R.drawable.djsound;
            case "Cake":
                return R.drawable.w_cake;
            case "Decoration":
                return R.drawable.decoration;
            case "Jewellery":
                return R.drawable.jewellery;
            case "Lighting":
                return R.drawable.lighting;
            case "Wedding wear":
                return R.drawable.wedding_wear;
            case "Entertainment":
                return R.drawable.event_host;
            case "Invitation Cards":
                return R.drawable.invitation_card;
            case "Transport Services":
                return R.drawable.transport;
            default:
                return R.drawable.ic_event; // fallback image
        }
    }
}
