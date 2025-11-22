package com.example.avsar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class VendorShortlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textBack, textEmpty;
    private CategoryItemAdapter adapter;
    private List<CategoryItem> vendorList = new ArrayList<>();
    private List<String> vendorKeys = new ArrayList<>();
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_shortlist);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewShortlist);
        textBack = findViewById(R.id.textBack);
        textEmpty = findViewById(R.id.textEmptyShortlist);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Back button handling
        if (textBack != null) {
            textBack.setOnClickListener(v -> {
                Intent intent = new Intent(VendorShortlistActivity.this, MyEventsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            Log.e("VendorShortlist", "❌ textBack not found in layout!");
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryItemAdapter(this, vendorList, vendorKeys, "shortlist");
        recyclerView.setAdapter(adapter);

        loadShortlistedVendors();
    }

    private void loadShortlistedVendors() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("shortlistedVendors").child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vendorList.clear();
                vendorKeys.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        CategoryItem item = snap.getValue(CategoryItem.class);
                        if (item != null) {
                            vendorList.add(item);
                            vendorKeys.add(snap.getKey());
                        }
                    }
                    textEmpty.setVisibility(View.GONE);
                } else {
                    textEmpty.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VendorShortlistActivity.this, "Failed to load vendors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
