package com.example.avsar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etName, etMobile;
    private Button btnSave, chooseAvatarBtn;
    private ImageView profileImage;
    private Uri imageUri;

    private final int PICK_IMAGE = 1;

    FirebaseAuth auth;
    DatabaseReference userRef;
    StorageReference storageRef;

    private final int[] avatarIds = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        etName = findViewById(R.id.etEditName);
        etMobile = findViewById(R.id.etEditMobile);
        btnSave = findViewById(R.id.btnSave);
        profileImage = findViewById(R.id.profileImageEdit);
        chooseAvatarBtn = findViewById(R.id.chooseAvatarBtn);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(user.getUid() + ".jpg");

        // Load current data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etName.setText(snapshot.child("name").getValue(String.class));
                etMobile.setText(snapshot.child("mobile").getValue(String.class));

                String avatarName = snapshot.child("avatar").getValue(String.class);
                String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                if (profileImageUrl != null) {
                    Glide.with(PersonalInfoActivity.this)
                            .load(profileImageUrl)
                            .override(400, 400)
                            .centerCrop()
                            .into(profileImage);
                } else if (avatarName != null) {
                    int resId = getResources().getIdentifier(avatarName, "drawable", getPackageName());
                    if (resId != 0) profileImage.setImageResource(resId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalInfoActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Pick from gallery
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        // Choose avatar
        chooseAvatarBtn.setOnClickListener(v -> openAvatarDialog());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newMobile = etMobile.getText().toString().trim();

            if (newName.isEmpty() || newMobile.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            userRef.child("name").setValue(newName);
            userRef.child("mobile").setValue(newMobile);

            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Return to AccountActivity
        });
    }

    private void openAvatarDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.avatar_selection_dialog);
        GridView avatarGrid = dialog.findViewById(R.id.avatarGrid);

        AvatarAdapter adapter = new AvatarAdapter(this, avatarIds);
        avatarGrid.setAdapter(adapter);

        avatarGrid.setOnItemClickListener((parent, view, position, id) -> {
            int selectedAvatar = avatarIds[position];
            profileImage.setImageResource(selectedAvatar);

            String avatarName = "avatar" + (position + 1);
            userRef.child("avatar").setValue(avatarName);
            userRef.child("profileImageUrl").removeValue(); // clear uploaded image if avatar selected

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .override(400, 400)
                    .centerCrop()
                    .into(profileImage);

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    userRef.child("profileImageUrl").setValue(uri.toString());
                    userRef.child("avatar").removeValue(); // clear avatar if uploading new image
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
