package com.example.avsar;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeeAllVenuesActivity extends AppCompatActivity {

    RecyclerView recyclerViewAllVenues;
    List<Venue> allVenueList;
    VenueAdapter adapter;
    TextView textBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_venues);

        recyclerViewAllVenues = findViewById(R.id.recyclerViewAllVenues);
        recyclerViewAllVenues.setLayoutManager(new LinearLayoutManager(this));

        allVenueList = new ArrayList<>();
        adapter = new VenueAdapter(this, allVenueList);
        recyclerViewAllVenues.setAdapter(adapter);

        // 🟡 Initialize Back TextView
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(SeeAllVenuesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Optional: avoids stacking multiple MainActivity instances
            startActivity(intent);
            finish();
        });

        loadAllVenuesFromFirebase();
    }

    private void loadAllVenuesFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("venues");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allVenueList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Venue venue = ds.getValue(Venue.class);
                    venue.setId(ds.getKey());
                    allVenueList.add(venue);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeeAllVenuesActivity.this, "Failed to load venues", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
