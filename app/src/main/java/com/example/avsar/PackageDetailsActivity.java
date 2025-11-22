package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

public class PackageDetailsActivity extends AppCompatActivity {

    ImageView imagePackage;
    TextView textPackageTitle, textPackageCost, textPackageDescription, textPackageDuration, textBack;
    RatingBar ratingBarPackage;
    LinearLayout photoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        // Initialize views
        imagePackage = findViewById(R.id.imagePackage);
        textPackageTitle = findViewById(R.id.textPackageTitle);
        textPackageCost = findViewById(R.id.textPackageCost);
        textPackageDescription = findViewById(R.id.textPackageDescription);
        textPackageDuration = findViewById(R.id.textPackageDuration);
        ratingBarPackage = findViewById(R.id.ratingBarPackage);
        photoContainer = findViewById(R.id.photoContainer);

        // 🟡 Initialize Back TextView
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(PackageDetailsActivity.this, SeeAllPackagesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Optional: avoids stacking multiple MainActivity instances
            startActivity(intent);
            finish();
        });

        // Get package title from Intent
        String packageTitle = getIntent().getStringExtra("packageTitle");
        if (packageTitle != null) {
            loadPackageDetails(packageTitle);
        } else {
            Toast.makeText(this, "Package title not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPackageDetails(String title) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("packages");
        reference.orderByChild("title").equalTo(title)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String imageUrl = ds.child("imageUrl").getValue(String.class);
                                String cost = ds.child("cost").getValue(String.class);
                                String description = ds.child("description").getValue(String.class);
                                String duration = ds.child("duration").getValue(String.class);
                                Float rating = ds.child("ratings").getValue(Float.class);

                                textPackageTitle.setText(title);
                                textPackageCost.setText(cost);
                                textPackageDescription.setText(description);
                                textPackageDuration.setText(duration);
                                if (rating != null) ratingBarPackage.setRating(rating);
                                Glide.with(PackageDetailsActivity.this).load(imageUrl).into(imagePackage);

                                // Load photos
                                photoContainer.removeAllViews(); // Clear previous
                                for (DataSnapshot photoSnap : ds.child("photos").getChildren()) {
                                    String photoUrl = photoSnap.getValue(String.class);
                                    if (photoUrl != null && !photoUrl.isEmpty()) {
                                        ImageView img = new ImageView(PackageDetailsActivity.this);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
                                        params.setMargins(8, 8, 8, 8);
                                        img.setLayoutParams(params);
                                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        Glide.with(PackageDetailsActivity.this).load(photoUrl).into(img);
                                        photoContainer.addView(img);
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(PackageDetailsActivity.this, "Package not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PackageDetailsActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
