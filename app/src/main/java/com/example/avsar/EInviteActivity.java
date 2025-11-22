package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EInviteActivity extends AppCompatActivity {

    SearchView searchViewTemplates;
    TextView textBack;
    TextView textSeeAllWedding, textSeeAllBirthday, textSeeAllBabyShower, textSeeAllNaming,
            textSeeAllEngagement, textSeeAllHousewarming, textSeeAllAnniversary, textSeeAllFestival;

    RecyclerView recyclerViewWedding, recyclerViewBirthday, recyclerViewBabyShower, recyclerViewNaming,
            recyclerViewEngagement, recyclerViewHousewarming, recyclerViewAnniversary, recyclerViewFestival;

    HashMap<String, TemplateAdapter> adapterMap = new HashMap<>();
    HashMap<String, List<TemplateModel>> templateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einvite);

        searchViewTemplates = findViewById(R.id.searchViewTemplates);
        textBack = findViewById(R.id.textBack);

        // ✅ Initialize all 'See all' TextViews
        textSeeAllWedding = findViewById(R.id.textSeeAllWedding);
        textSeeAllBirthday = findViewById(R.id.textSeeAllBirthday);
        textSeeAllBabyShower = findViewById(R.id.textSeeAllBabyShower);
        textSeeAllNaming = findViewById(R.id.textSeeAllNaming);
        textSeeAllEngagement = findViewById(R.id.textSeeAllEngagement);
        textSeeAllHousewarming = findViewById(R.id.textSeeAllHousewarming);
        textSeeAllAnniversary = findViewById(R.id.textSeeAllAnniversary);
        textSeeAllFestival = findViewById(R.id.textSeeAllFestival);

        // Setup RecyclerViews
        recyclerViewWedding = initRecycler(R.id.recyclerViewWedding, "Wedding Templates");
        recyclerViewBirthday = initRecycler(R.id.recyclerViewBirthday, "Birthday Templates");
        recyclerViewBabyShower = initRecycler(R.id.recyclerViewBabyShower, "Baby Shower Templates");
        recyclerViewNaming = initRecycler(R.id.recyclerViewNaming, "Naming Ceremony");
        recyclerViewEngagement = initRecycler(R.id.recyclerViewEngagement, "Engagement Templates");
        recyclerViewHousewarming = initRecycler(R.id.recyclerViewHousewarming, "Housewarming Templates");
        recyclerViewAnniversary = initRecycler(R.id.recyclerViewAnniversary, "Anniversary Templates");
        recyclerViewFestival = initRecycler(R.id.recyclerViewFestival, "Festival Templates");

        // Setup click listeners
        textSeeAllWedding.setOnClickListener(v -> openSeeAll("Wedding Templates"));
        textSeeAllBirthday.setOnClickListener(v -> openSeeAll("Birthday Templates"));
        textSeeAllBabyShower.setOnClickListener(v -> openSeeAll("Baby Shower Templates"));
        textSeeAllNaming.setOnClickListener(v -> openSeeAll("Naming Ceremony"));
        textSeeAllAnniversary.setOnClickListener(v -> openSeeAll("Anniversary Templates"));
        textSeeAllEngagement.setOnClickListener(v -> openSeeAll("Engagement Templates"));
        textSeeAllHousewarming.setOnClickListener(v -> openSeeAll("Housewarming Templates"));
        textSeeAllFestival.setOnClickListener(v -> openSeeAll("Festival Templates"));

        // Back button click
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(EInviteActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadTemplatesFromFirebase();
        setupSearchFunctionality();
    }

    private void openSeeAll(String category) {
        Intent intent = new Intent(EInviteActivity.this, SeeAllTemplatesActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private RecyclerView initRecycler(int id, String category) {
        RecyclerView recyclerView = findViewById(id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<TemplateModel> list = new ArrayList<>();
        TemplateAdapter adapter = new TemplateAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapterMap.put(category, adapter);
        templateMap.put(category, list);
        return recyclerView;
    }

    private void loadTemplatesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("e_invite_templates")
                .get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot categorySnap : snapshot.getChildren()) {
                        String category = categorySnap.getKey();
                        if (category == null || !adapterMap.containsKey(category)) continue;

                        List<TemplateModel> list = new ArrayList<>();
                        for (DataSnapshot templateSnap : categorySnap.getChildren()) {
                            TemplateModel template = templateSnap.getValue(TemplateModel.class);
                            list.add(template);
                        }
                        templateMap.put(category, list);
                        adapterMap.get(category).updateList(list);
                    }
                });
    }

    private void setupSearchFunctionality() {
        searchViewTemplates.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTemplates(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTemplates(newText);
                return true;
            }
        });
    }

    private void filterTemplates(String query) {
        String lowerQuery = query.toLowerCase();
        for (String category : templateMap.keySet()) {
            List<TemplateModel> allTemplates = templateMap.get(category);
            List<TemplateModel> filtered = new ArrayList<>();
            for (TemplateModel t : allTemplates) {
                if (t.getTitle().toLowerCase().contains(lowerQuery)) {
                    filtered.add(t);
                }
            }
            adapterMap.get(category).updateList(filtered);
        }
    }
}
