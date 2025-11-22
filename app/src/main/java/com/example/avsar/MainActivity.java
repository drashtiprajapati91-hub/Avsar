package com.example.avsar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import com.example.avsar.R;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.search.*;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner locationSpinner;
    RecyclerView recyclerViewCategories, recyclerViewVenues, recyclerViewPackages;
    TextView textViewSeeAll, seeAllVenues, seeAllEvents;
    List<VendorCategory> categoryList;
    List<Venue> venueList;
    List<Package> packageList;
    VenueAdapter venueAdapter;
    PackageAdapter packageAdapter;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Initialize Firebase App Check Debug Provider
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        // ✅ Initialize Views
        locationSpinner = findViewById(R.id.locationSpinner);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewVenues = findViewById(R.id.recyclerViewVenues);
        recyclerViewPackages = findViewById(R.id.recyclerViewPackages);
        textViewSeeAll = findViewById(R.id.textViewSeeAll);
        seeAllVenues = findViewById(R.id.seeAllVenues);
        seeAllEvents = findViewById(R.id.seeAllEvents);

        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerViewVenues.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPackages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        categoryList = new ArrayList<>();
        venueList = new ArrayList<>();
        packageList = new ArrayList<>();
        
        //search bar
        SearchView searchView = findViewById(R.id.searchBar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVenues(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVenues(newText);
                return false;
            }
        });

        ImageView iconNotification = findViewById(R.id.notificationIcon);
        iconNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });



        // Sample categories
        categoryList.add(new VendorCategory(R.drawable.m_wedding, "Planners"));
        categoryList.add(new VendorCategory(R.drawable.catering, "Catering"));
        categoryList.add(new VendorCategory(R.drawable.makeup, "Make up"));
        categoryList.add(new VendorCategory(R.drawable.photography, "Photography"));
        categoryList.add(new VendorCategory(R.drawable.mehandi, "Mehendi"));
        categoryList.add(new VendorCategory(R.drawable.djsound, "DJ & Sound"));
        categoryList.add(new VendorCategory(R.drawable.w_cake, "Cake"));
        categoryList.add(new VendorCategory(R.drawable.ic_event, "Others"));





        VendorCategoryAdapter adapter = new VendorCategoryAdapter(this, categoryList);
        recyclerViewCategories.setAdapter(adapter);

        venueAdapter = new VenueAdapter(this, venueList);
        recyclerViewVenues.setAdapter(venueAdapter);

        packageAdapter = new PackageAdapter(this, packageList);
        recyclerViewPackages.setAdapter(packageAdapter);

        // Navigation to other activities
        textViewSeeAll.setOnClickListener(v -> startActivity(new Intent(this, AllCategoriesActivity.class)));
        seeAllVenues.setOnClickListener(v -> startActivity(new Intent(this, SeeAllVenuesActivity.class)));
        seeAllEvents.setOnClickListener(v -> startActivity(new Intent(this, SeeAllPackagesActivity.class)));

        // Location Spinner
        List<String> locations = new ArrayList<>();
        locations.add("All");
        locations.add("Ahmedabad");
        locations.add("Gandhinagar");
        locations.add("Surat");
        locations.add("Rajkot");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(spinnerAdapter);

        loadVenuesFromFirebase("All");
        loadPackagesFromFirebase();

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                loadVenuesFromFirebase(selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ✅ Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Already on Home
                return true;
            } else if (itemId == R.id.nav_vendors) {
                startActivity(new Intent(this, AllCategoriesActivity.class));
                return true;
            } else if (itemId == R.id.nav_e_invite) {
                startActivity(new Intent(this, EInviteActivity.class));
                return true;
            } else if (itemId == R.id.nav_events) {
                startActivity(new Intent(this, SelectEventTypeActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }

            return false;
        });

    }

    private void filterVenues(String query) {
    }

    private void loadVenuesFromFirebase(String locationFilter) {
        DatabaseReference venueRef = FirebaseDatabase.getInstance().getReference("venues");
        venueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                venueList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Venue venue = ds.getValue(Venue.class);
                    if (venue != null && venue.getLocation() != null &&
                            (locationFilter.equals("All") || venue.getLocation().equalsIgnoreCase(locationFilter))) {
                        // 🔥 Set the Firebase key as ID
                        venue.setId(ds.getKey());

                        venueList.add(venue);
                        Log.d("VENUE_LOCATION", "Venue: " + venue.getName() + " Location: " + venue.getLocation());
                    }
                }
                venueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load venues.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPackagesFromFirebase() {
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("packages");
        packageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packageList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Package p = ds.getValue(Package.class);
                    if (p != null) {
                        packageList.add(p);
                        Log.d("PACKAGE", "Loaded: " + p.getTitle());
                    }
                }
                packageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load packages.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
