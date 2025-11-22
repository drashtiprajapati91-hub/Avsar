package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyEventsActivity extends AppCompatActivity {

    ImageView imageEvent;
    TextView itemChecklist, itemGuests, itemEInvites, itemBudget, itemVendors, itemBookings, textBack, textEventName;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Find Views
        textBack = findViewById(R.id.textBack);
        textEventName = findViewById(R.id.textEventName);
        imageEvent = findViewById(R.id.imageEvent);
        itemChecklist = findViewById(R.id.itemChecklist);
        itemGuests = findViewById(R.id.itemGuests);
        itemBudget = findViewById(R.id.itemBudget);
        itemVendors = findViewById(R.id.itemVendors);
        itemBookings = findViewById(R.id.itemBookings);

        // 🔁 Back button logic
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, SelectEventTypeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // ✅ Get selected event from intent
        String selectedEvent = getIntent().getStringExtra("eventType");

        if (selectedEvent != null && !selectedEvent.isEmpty()) {
            textEventName.setText(selectedEvent);

            // 🔐 Save to Firebase under "events" node
            String eventId = databaseReference.push().getKey();
            if (eventId != null) {
                databaseReference.child(eventId).child("name").setValue(selectedEvent)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show());
            }
        } else {
            textEventName.setText("No Event Selected");
        }

        // ➤ Navigation logic (Uncomment when the screens are ready)
        itemChecklist.setOnClickListener(v ->
                startActivity(new Intent(MyEventsActivity.this, ChecklistActivity.class)));

         itemGuests.setOnClickListener(v -> startActivity(new Intent(MyEventsActivity.this, GuestListActivity.class)));
         itemBudget.setOnClickListener(v -> startActivity(new Intent(MyEventsActivity.this, BudgetActivity.class)));
         itemVendors.setOnClickListener(v -> startActivity(new Intent(MyEventsActivity.this, VendorShortlistActivity.class)));
         itemBookings.setOnClickListener(v -> startActivity(new Intent(MyEventsActivity.this, BookingActivity.class)));
    }
}
