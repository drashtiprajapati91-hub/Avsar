package com.example.avsar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeeAllPackagesActivity extends AppCompatActivity {

    RecyclerView recyclerViewAllPackages;
    List<Package> packageList;
    PackageAdapter packageAdapter;
    TextView textBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_packages);

        recyclerViewAllPackages = findViewById(R.id.recyclerViewAllPackages);
        recyclerViewAllPackages.setLayoutManager(new LinearLayoutManager(this));

        packageList = new ArrayList<>();
        packageAdapter = new PackageAdapter(this, packageList);
        recyclerViewAllPackages.setAdapter(packageAdapter);

        // 🟡 Initialize Back TextView
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(SeeAllPackagesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Optional: avoids stacking multiple MainActivity instances
            startActivity(intent);
            finish();
        });

        loadPackagesFromFirebase();
    }

    private void loadPackagesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("packages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        packageList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Package p = ds.getValue(Package.class);
                            if (p != null) {
                                packageList.add(p);
                            }
                        }
                        packageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SeeAllPackagesActivity.this, "Failed to load packages", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
