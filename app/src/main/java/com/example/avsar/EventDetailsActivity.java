package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView imageVenue;
    TextView textVenueName, textCost, textAddress, textPhone, textBack;
    TextView textBookingAlert;
    RatingBar ratingBar;
    LinearLayout photoContainer, layoutBookingAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        imageVenue = findViewById(R.id.imageVenue);
        textVenueName = findViewById(R.id.textVenueName);
        textCost = findViewById(R.id.textCost);
        textAddress = findViewById(R.id.textAddress);
        textPhone = findViewById(R.id.textPhone);
        ratingBar = findViewById(R.id.ratingBar);
        photoContainer = findViewById(R.id.photoContainer);
        textBookingAlert = findViewById(R.id.textBookingAlert);
        layoutBookingAlert = findViewById(R.id.layoutBookingAlert);

        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, SeeAllVenuesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        String venueId = getIntent().getStringExtra("venueId");
        if (venueId != null) {
            loadVenueDetails(venueId);
            checkIfVenueBookedToday(venueId);
        } else {
            Toast.makeText(this, "Venue ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadVenueDetails(String venueId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("venues").child(venueId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String cost = snapshot.child("cost").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    Float rating = snapshot.child("ratings").getValue(Float.class);

                    textVenueName.setText(name != null ? name : "No name");
                    textCost.setText(cost != null ? "Starting from " + cost : "Cost not available");
                    textAddress.setText(address != null ? address : "No address");
                    textPhone.setText(phone != null ? phone : "No phone");
                    if (rating != null) ratingBar.setRating(rating);

                    if (imageUrl != null) {
                        Glide.with(EventDetailsActivity.this).load(imageUrl).into(imageVenue);
                    }

                    photoContainer.removeAllViews();
                    for (DataSnapshot photoSnap : snapshot.child("photos").getChildren()) {
                        String photoUrl = photoSnap.getValue(String.class);
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            ImageView img = new ImageView(EventDetailsActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
                            params.setMargins(8, 8, 8, 8);
                            img.setLayoutParams(params);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(EventDetailsActivity.this).load(photoUrl).into(img);
                            photoContainer.addView(img);
                        }
                    }
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Venue not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventDetailsActivity.this, "Error loading venue details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfVenueBookedToday(String venueId) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance()
                .getReference("bookingsByVenue").child(venueId);

        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean booked = false;
                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    String bookedDate = bookingSnap.child("date").getValue(String.class);
                    String duration = bookingSnap.child("duration").getValue(String.class);

                    if (bookedDate == null) continue;
                    if (duration == null || duration.isEmpty()) duration = "1 day";

                    int days = 1;
                    try {
                        days = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                    try {
                        Date start = sdf.parse(bookedDate);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(start);
                        cal.add(Calendar.DATE, days);
                        Date end = cal.getTime();

                        Date today = Calendar.getInstance().getTime();
                        if (today.compareTo(start) >= 0 && today.compareTo(end) < 0) {
                            booked = true;
                            break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                layoutBookingAlert.setVisibility(booked ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventDetailsActivity.this, "Error checking booking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
