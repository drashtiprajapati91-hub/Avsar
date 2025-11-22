package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    int[] itemIds = {
            R.id.item_personal_info,
            R.id.item_bookings,
            R.id.item_inbox,
            R.id.item_shortlist,
            R.id.item_support,
            R.id.item_about,
    };

    int[] iconIds = {
            R.drawable.ic_user,
            R.drawable.ic_bookings,
            R.drawable.ic_inbox,
            R.drawable.ic_shortlist,
            R.drawable.ic_support,
            R.drawable.ic_about,
    };

    String[] titles = {
            "Personal Info",
            "Bookings",
            "Inbox",
            "Shortlist",
            "Support & FAQ’s",
            "About app"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        logoutBtn = findViewById(R.id.logoutBtn);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        TextView profileNameText = findViewById(R.id.profileName);
        ImageView profileImage = findViewById(R.id.profileImage);

        String uid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String avatar = snapshot.child("avatar").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        if (name != null) profileNameText.setText(name);
                        else profileNameText.setText("User");

                        if (profileImageUrl != null) {
                            Glide.with(AccountActivity.this)
                                    .load(profileImageUrl)
                                    .override(400, 400)
                                    .centerCrop()
                                    .into(profileImage);
                        } else if (avatar != null) {
                            int avatarResId = getResources().getIdentifier(avatar, "drawable", getPackageName());
                            if (avatarResId != 0) {
                                profileImage.setImageResource(avatarResId);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        profileNameText.setText("User");
                    }
                });

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(AccountActivity.this, Login.class));
            finish();
        });

        for (int i = 0; i < itemIds.length; i++) {
            setupMenuItem(itemIds[i], iconIds[i], titles[i]);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_account);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_vendors) {
                startActivity(new Intent(this, AllCategoriesActivity.class));
                return true;
            } else if (itemId == R.id.nav_e_invite) {
                startActivity(new Intent(this, EInviteActivity.class));
                return true;
            } else if (itemId == R.id.nav_events) {
                startActivity(new Intent(this, MyEventsActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                return true;
            }

            return false;
        });
    }

    private void setupMenuItem(int layoutId, int iconRes, String title) {
        View item = findViewById(layoutId);
        if (item != null) {
            ImageView icon = item.findViewById(R.id.icon);
            TextView label = item.findViewById(R.id.title);

            icon.setImageResource(iconRes);
            label.setText(title);

            item.setOnClickListener(v -> handleItemClick(title));
        }
    }

    private void handleItemClick(String title) {
        switch (title) {
            case "Personal Info":
                startActivity(new Intent(this, PersonalInfoActivity.class));
                break;
            case "Bookings":
                startActivity(new Intent(this, BookingActivity.class));
                break;
            case "Inbox":
                break;
            case "Shortlist":
                startActivity(new Intent(this, VendorShortlistActivity.class));
                break;
            case "Support & FAQ’s":
                break;
            case "About app":
                break;
        }
    }
}
