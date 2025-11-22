package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeeAllTemplatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TemplateAdapter adapter;
    private List<TemplateModel> templateList;
    private TextView textBack, textTitle;
    private String templateCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_templates);

        recyclerView = findViewById(R.id.recyclerViewAllTemplates);
        textBack = findViewById(R.id.textBack);
        textTitle = findViewById(R.id.textTitle);

        templateList = new ArrayList<>();
        adapter = new TemplateAdapter(this, templateList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Get category from intent
        templateCategory = getIntent().getStringExtra("category");
        if (templateCategory == null || templateCategory.isEmpty()) {
            Toast.makeText(this, "No template category provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textTitle.setText(templateCategory);

        // Handle back
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(SeeAllTemplatesActivity.this, EInviteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadTemplatesFromFirebase(templateCategory);
    }

    private void loadTemplatesFromFirebase(String category) {
        FirebaseDatabase.getInstance().getReference("e_invite_templates")
                .child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        templateList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            TemplateModel template = ds.getValue(TemplateModel.class);
                            if (template != null) {
                                templateList.add(template);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SeeAllTemplatesActivity.this, "Failed to load templates", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
