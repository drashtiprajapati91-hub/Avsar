package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class GuestListActivity extends AppCompatActivity {

    EditText inputFamily, inputRelatives, inputCloseFriends, inputColleagues,
            inputFamilyFriends, inputStaff;
    Button buttonAddGuestList;
    TextView textBack;

    DatabaseReference guestListRef;
    FirebaseUser user;

    String eventId, fullName, mobile, email, guestCount, userName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);


        // Get user and Firebase ref
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = user.getUid();
        userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";
        guestListRef = FirebaseDatabase.getInstance().getReference("guest_lists").child(userId);

        // Get data from SelectEventTypeActivity
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        fullName = intent.getStringExtra("fullName");
        mobile = intent.getStringExtra("mobile");
        email = intent.getStringExtra("email");
        guestCount = intent.getStringExtra("guestCount");

        // Views
        inputFamily = findViewById(R.id.inputFamily);
        inputRelatives = findViewById(R.id.inputRelatives);
        inputCloseFriends = findViewById(R.id.inputCloseFriends);
        inputColleagues = findViewById(R.id.inputColleagues);
        inputFamilyFriends = findViewById(R.id.inputFamilyFriends);
        inputStaff = findViewById(R.id.inputStaff);
        buttonAddGuestList = findViewById(R.id.buttonAddGuestList);
        textBack = findViewById(R.id.textBack);

        textBack.setOnClickListener(v -> {
            startActivity(new Intent(GuestListActivity.this, MyEventsActivity.class));
            finish();
        });

        buttonAddGuestList.setOnClickListener(v -> {
            HashMap<String, Object> guestData = new HashMap<>();
            guestData.put("eventId", eventId);
            guestData.put("fullName", fullName);
            guestData.put("email", email);
            guestData.put("mobile", mobile);
            guestData.put("guestCount", guestCount);
            guestData.put("userId", userId);
            guestData.put("userName", userName);

            guestData.put("Family", inputFamily.getText().toString().trim());
            guestData.put("Relatives", inputRelatives.getText().toString().trim());
            guestData.put("CloseFriends", inputCloseFriends.getText().toString().trim());
            guestData.put("Colleagues", inputColleagues.getText().toString().trim());
            guestData.put("FamilyFriends", inputFamilyFriends.getText().toString().trim());
            guestData.put("WeddingStaff", inputStaff.getText().toString().trim());

            guestListRef.child(eventId).setValue(guestData)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Guest list saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save guest list", Toast.LENGTH_SHORT).show());
        });
    }
}
