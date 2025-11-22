package com.example.avsar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;

public class BookingActivity extends AppCompatActivity {

    Spinner spinnerEventType, spinnerVenue;
    EditText inputDate, inputTime, inputCost, inputDuration;
    Button buttonSubmit;

    DatabaseReference bookingRef, venuesRef, bookingByVenueRef;
    FirebaseUser user;

    String userId, userName = "Anonymous";

    ArrayList<String> venueNames = new ArrayList<>();
    ArrayList<String> venueIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        spinnerEventType = findViewById(R.id.spinnerEventType);
        spinnerVenue = findViewById(R.id.spinnerVenues);
        inputDate = findViewById(R.id.inputDate);
        inputTime = findViewById(R.id.inputTime);
        inputCost = findViewById(R.id.inputCost);
        inputDuration = findViewById(R.id.inputDuration);
        buttonSubmit = findViewById(R.id.buttonSubmitBooking);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        userId = user.getUid();
        userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";

        bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(userId);
        venuesRef = FirebaseDatabase.getInstance().getReference("venues");
        bookingByVenueRef = FirebaseDatabase.getInstance().getReference("bookingsByVenue");

        String[] eventTypes = {"Wedding", "Birthday", "Corporate", "Engagement", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);

        loadVenues();

        inputDate.setOnClickListener(v -> showDatePicker());
        inputTime.setOnClickListener(v -> showTimePicker());

        buttonSubmit.setOnClickListener(v -> saveBooking());
    }

    private void loadVenues() {
        venuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                venueNames.clear();
                venueIds.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String id = snap.getKey();
                    String name = snap.child("name").getValue(String.class);
                    if (id != null && name != null) {
                        venueIds.add(id);
                        venueNames.add(name);
                    }
                }

                ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(BookingActivity.this,
                        android.R.layout.simple_spinner_item, venueNames);
                venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVenue.setAdapter(venueAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Failed to load venues", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) ->
                        inputDate.setText(day + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    if (selectedHour == 0) selectedHour = 12;
                    else if (selectedHour > 12) selectedHour -= 12;

                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                            selectedHour, selectedMinute, amPm);
                    inputTime.setText(formattedTime);
                },
                hour, minute, false);
        dialog.show();
    }

    private void saveBooking() {
        String eventType = spinnerEventType.getSelectedItem().toString();
        String date = inputDate.getText().toString().trim();
        String time = inputTime.getText().toString().trim();
        String costStr = inputCost.getText().toString().trim();
        String duration = inputDuration.getText().toString().trim();
        int venueIndex = spinnerVenue.getSelectedItemPosition();

        if (date.isEmpty() || time.isEmpty() || costStr.isEmpty() || duration.isEmpty() || venueIndex == -1) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String venueId = venueIds.get(venueIndex);
        String venueName = venueNames.get(venueIndex);
        int totalCost = Integer.parseInt(costStr);
        String bookingId = UUID.randomUUID().toString();

        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("eventType", eventType);
        bookingData.put("venueId", venueId);
        bookingData.put("venueName", venueName);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("duration", duration);
        bookingData.put("totalCost", totalCost);
        bookingData.put("userId", userId);
        bookingData.put("userName", userName);

        bookingRef.child(bookingId).setValue(bookingData);

        bookingByVenueRef.child(venueId).child(bookingId).setValue(bookingData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BookingActivity.this, MyEventsActivity.class);
                    intent.putExtra("venueId", venueId);
                    intent.putExtra("fromDate", date);
                    intent.putExtra("toDate", date);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save booking", Toast.LENGTH_SHORT).show());
    }
}
