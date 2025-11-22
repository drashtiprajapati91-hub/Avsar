package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class ChecklistActivity extends AppCompatActivity {

    CheckBox checkVenue, checkCaterer, checkPhotographer, checkMenu,
            checkDecoration, checkInvitation, checkMakeup, checkBudget, checkCake;
    Button buttonAddList;

    DatabaseReference checklistRef, userRef;
    TextView textBack;
    FirebaseUser currentUser;
    String userName = "Unknown";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        // 🟡 Initialize Back TextView
        textBack = findViewById(R.id.textBack);
        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChecklistActivity.this, MyEventsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Optional: avoids stacking multiple MainActivity instances
            startActivity(intent);
            finish();
        });

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid();
        checklistRef = FirebaseDatabase.getInstance().getReference("checklists");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);


        // Load user name
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if (name != null) userName = name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChecklistActivity.this, "Failed to load user name", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize views
        checkVenue = findViewById(R.id.checkVenue);
        checkCaterer = findViewById(R.id.checkCaterer);
        checkPhotographer = findViewById(R.id.checkPhotographer);
        checkMenu = findViewById(R.id.checkMenu);
        checkDecoration = findViewById(R.id.checkDecoration);
        checkInvitation = findViewById(R.id.checkInvitation);
        checkMakeup = findViewById(R.id.checkMakeup);
        checkBudget = findViewById(R.id.checkBudget);
        checkCake = findViewById(R.id.checkCake);
        buttonAddList = findViewById(R.id.buttonAddList);

        buttonAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a unique event ID
                String eventId = checklistRef.push().getKey();

                if (eventId == null) {
                    Toast.makeText(ChecklistActivity.this, "Failed to generate event ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> checklistMap = new HashMap<>();
                checklistMap.put("BookVenue", checkVenue.isChecked());
                checklistMap.put("BookCaterer", checkCaterer.isChecked());
                checklistMap.put("BookPhotographer", checkPhotographer.isChecked());
                checklistMap.put("DecideFoodMenu", checkMenu.isChecked());
                checklistMap.put("BookDecoration", checkDecoration.isChecked());
                checklistMap.put("SendInvitation", checkInvitation.isChecked());
                checklistMap.put("CallMakeupArtist", checkMakeup.isChecked());
                checklistMap.put("CheckBudget", checkBudget.isChecked());
                checklistMap.put("OrderCake", checkCake.isChecked());
                checklistMap.put("userId", userId);
                checklistMap.put("userName", userName);
                checklistMap.put("eventId", eventId);

                checklistRef.child(eventId).setValue(checklistMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ChecklistActivity.this,
                                    "Checklist saved!\nEvent ID: " + eventId,
                                    Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(ChecklistActivity.this,
                                        "Failed to save checklist", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
