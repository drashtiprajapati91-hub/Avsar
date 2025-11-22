package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class CategoryDetailsActivity extends AppCompatActivity {

    ImageView imageCategory, iconSave;
    TextView textCategoryTitle, textCategoryCost, textCategoryAddress, textCategoryLocation, textBack;
    RatingBar ratingBar;
    LinearLayout photoContainer;
    Button buttonEnquiry, buttonBook;

    String categoryName, itemKey;
    boolean isSaved = false;
    DatabaseReference saveRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        // 🔧 Initialize all views
        imageCategory = findViewById(R.id.imageCategory);
        textCategoryTitle = findViewById(R.id.textCategoryTitle);
        textCategoryCost = findViewById(R.id.textCategoryCost);
        textCategoryAddress = findViewById(R.id.textCategoryAddress);
        textCategoryLocation = findViewById(R.id.textCategoryLocation);
        ratingBar = findViewById(R.id.ratingBar);
        photoContainer = findViewById(R.id.photoContainer);
        iconSave = findViewById(R.id.iconSave);
        textBack = findViewById(R.id.textBack);
        buttonEnquiry = findViewById(R.id.buttonEnquiry); // ✅ now initialized
        buttonBook = findViewById(R.id.buttonBook);       // ✅ now initialized

        // 🔙 Back
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryDetailsActivity.this, CategoryItemsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });


        // 🧾 Get data from intent
        categoryName = getIntent().getStringExtra("categoryName");
        itemKey = getIntent().getStringExtra("itemKey");

        if (categoryName != null && itemKey != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            saveRef = FirebaseDatabase.getInstance().getReference("shortlistedVendors").child(userId).child(categoryName + "_" + itemKey);
            checkSavedStatus();
            loadCategoryDetails(categoryName, itemKey);
        } else {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            finish();
        }

        // ❤️ Save toggle
        iconSave.setOnClickListener(v -> {
            if (isSaved) {
                saveRef.removeValue();
                iconSave.setImageResource(R.drawable.ic_favorite_border);
                isSaved = false;
                Toast.makeText(this, "Removed from shortlist", Toast.LENGTH_SHORT).show();
            } else {
                saveVendor();
            }
        });

        // 📞 Enquiry
        buttonEnquiry.setOnClickListener(v ->
                Toast.makeText(this, "Enquiry feature coming soon!", Toast.LENGTH_SHORT).show());

        // ✅ Book
        buttonBook.setOnClickListener(v ->
                Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void checkSavedStatus() {
        saveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSaved = snapshot.exists();
                iconSave.setImageResource(isSaved ? R.drawable.ic_heart_filled : R.drawable.ic_favorite_border);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void saveVendor() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(categoryName).child(itemKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String cost = snapshot.child("cost").getValue(String.class);
                String location = snapshot.child("location").getValue(String.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                Float rating = snapshot.child("ratings").getValue(Float.class);
                if (rating == null) rating = 0f;

                saveRef.setValue(new CategoryItem(title, cost, imageUrl, location, rating));
                iconSave.setImageResource(R.drawable.ic_heart_filled);
                isSaved = true;
                Toast.makeText(CategoryDetailsActivity.this, "Saved to shortlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadCategoryDetails(String category, String itemKey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(category).child(itemKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String cost = snapshot.child("cost").getValue(String.class);
                String address = snapshot.child("about").getValue(String.class);
                String location = snapshot.child("location").getValue(String.class);
                Float rating = snapshot.child("ratings").getValue(Float.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                textCategoryTitle.setText(title);
                textCategoryCost.setText(cost);
                textCategoryAddress.setText(address);
                textCategoryLocation.setText(location);
                if (rating != null) ratingBar.setRating(rating);
                Glide.with(CategoryDetailsActivity.this).load(imageUrl).into(imageCategory);

                // 📸 Load gallery
                photoContainer.removeAllViews();
                for (DataSnapshot photoSnap : snapshot.child("photos").getChildren()) {
                    String url = photoSnap.getValue(String.class);
                    if (url != null && !url.isEmpty()) {
                        ImageView img = new ImageView(CategoryDetailsActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
                        params.setMargins(8, 8, 8, 8);
                        img.setLayoutParams(params);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(CategoryDetailsActivity.this).load(url).into(img);
                        photoContainer.addView(img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryDetailsActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
