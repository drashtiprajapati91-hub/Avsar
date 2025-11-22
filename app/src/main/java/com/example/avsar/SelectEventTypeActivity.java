package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SelectEventTypeActivity extends AppCompatActivity {

    private EditText editFullName, editMobile, editEmail, editGuestCount;
    private RadioGroup radioGroupEvents;
    private Button btnNext;
    private TextView textBack;

    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_type);

        // Firebase Realtime Database Reference
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Initialize Views
        editFullName = findViewById(R.id.editFullName);
        editMobile = findViewById(R.id.editMobile);
        editEmail = findViewById(R.id.editEmail);
        editGuestCount = findViewById(R.id.editGuestCount);
        radioGroupEvents = findViewById(R.id.radioGroupEvents);
        btnNext = findViewById(R.id.btnNext);
        textBack = findViewById(R.id.textBack);

        // Back to MainActivity
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(SelectEventTypeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Save Event and Go to MyEventsActivity
        btnNext.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String mobile = editMobile.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String guestCount = editGuestCount.getText().toString().trim();

            int selectedId = radioGroupEvents.getCheckedRadioButtonId();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(mobile) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(guestCount) || selectedId == -1) {
                Toast.makeText(this, "Please fill all fields and select event type", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String eventType = selectedRadio.getText().toString();

            // Get current Firebase user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            String userEmail = user.getEmail();
            String userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";

            // Create unique event ID
            String eventId = eventsRef.push().getKey();

            // Prepare event data
            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", eventId);
            eventData.put("eventType", eventType);
            eventData.put("fullName", fullName);
            eventData.put("mobile", mobile);
            eventData.put("email", email);
            eventData.put("guestCount", guestCount);
            eventData.put("userId", userId);
            eventData.put("userEmail", userEmail);
            eventData.put("userName", userName);

            // Save event under userId > eventId in Firebase
            assert eventId != null;
            eventsRef.child(userId).child(eventId).setValue(eventData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

                        // Pass event data to MyEventsActivity
                        Intent intent = new Intent(SelectEventTypeActivity.this, MyEventsActivity.class);
                        intent.putExtra("eventId", eventId);
                        intent.putExtra("eventType", eventType);
                        intent.putExtra("fullName", fullName);
                        intent.putExtra("guestCount", guestCount);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
