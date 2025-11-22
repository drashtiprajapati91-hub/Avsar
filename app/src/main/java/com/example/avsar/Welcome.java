package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {

    Button loginBtn, registerBtn;
    TextView guestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // Ensure your XML file is named activity_welcome.xml

        // Initialize views
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        guestText = findViewById(R.id.guestText);

        // Login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(Welcome.this, Login.class);
                startActivity(loginIntent);
            }
        });

        // Register button click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Welcome.this, Register.class);
                startActivity(registerIntent);
            }
        });

        // Guest text click
        guestText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guestIntent = new Intent(Welcome.this, MainActivity.class);
                startActivity(guestIntent);
            }
        });
    }
}
