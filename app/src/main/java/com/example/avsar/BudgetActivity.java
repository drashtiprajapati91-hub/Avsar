package com.example.avsar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetActivity extends AppCompatActivity {

    EditText inputTotalBudget;
    SeekBar seekVenue, seekDecoration, seekPhotography, seekCatering;
    TextView valueVenue, valueDecoration, valuePhotography, valueCatering, textBack;
    Button buttonSaveBudget, saveBudgetBtn;
    PieChart budgetChart;

    DatabaseReference budgetRef;
    FirebaseUser user;

    String userId, userName = "Anonymous", eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        inputTotalBudget = findViewById(R.id.inputTotalBudget);
        seekVenue = findViewById(R.id.seekVenue);
        seekDecoration = findViewById(R.id.seekDecoration);
        seekPhotography = findViewById(R.id.seekPhotography);
        seekCatering = findViewById(R.id.seekCatering);

        valueVenue = findViewById(R.id.valueVenue);
        valueDecoration = findViewById(R.id.valueDecoration);
        valuePhotography = findViewById(R.id.valuePhotography);
        valueCatering = findViewById(R.id.valueCatering);
        textBack = findViewById(R.id.textBack);
        buttonSaveBudget = findViewById(R.id.buttonSaveBudget);
        budgetChart = findViewById(R.id.budgetChart);
        saveBudgetBtn = findViewById(R.id.saveBudgetBtn);

        saveBudgetBtn.setOnClickListener(v -> {
            String input = inputTotalBudget.getText().toString().trim();
            int maxBudget;

            if (!TextUtils.isEmpty(input)) {
                try {
                    int inputTotal = Integer.parseInt(input);

                    if (inputTotal > 0) {
                        maxBudget = inputTotal / 4;
                        Toast.makeText(this, "✅ Budget Max Set: " + maxBudget, Toast.LENGTH_SHORT).show();
                    } else {
                        maxBudget = 200000;
                        Toast.makeText(this, "⚠️ Budget must be more than 0. Defaulting to 200000", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    maxBudget = 200000;
                    Toast.makeText(this, "❌ Invalid input. Using default max budget: 200000", Toast.LENGTH_SHORT).show();
                }
            } else {
                maxBudget = 200000;
                Toast.makeText(this, "⚠️ Input empty. Defaulting to 200000", Toast.LENGTH_SHORT).show();
            }

            // Apply the calculated maxBudget to SeekBars
            seekVenue.setMax(maxBudget);
            seekDecoration.setMax(maxBudget);
            seekPhotography.setMax(maxBudget);
            seekCatering.setMax(maxBudget);
        });



        seekVenue.setProgress(80000);
        seekDecoration.setProgress(60000);
        seekPhotography.setProgress(40000);
        seekCatering.setProgress(20000);
        inputTotalBudget.setText("200000");

        valueVenue.setText("Rs.80000");
        valueDecoration.setText("Rs.60000");
        valuePhotography.setText("Rs.40000");
        valueCatering.setText("Rs.20000");


        textBack.setOnClickListener(v -> {
            Intent intent = new Intent(BudgetActivity.this, MyEventsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        userId = user.getUid();
        userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";
        budgetRef = FirebaseDatabase.getInstance().getReference("budgets").child(userId);

        eventId = getIntent().getStringExtra("eventId");

        if (TextUtils.isEmpty(eventId)) {
            // fallback: generate new eventId
            eventId = budgetRef.push().getKey(); // Firebase auto ID
            Toast.makeText(this, "⚠️ No event ID found, using a default one.", Toast.LENGTH_SHORT).show();
        }

        setupSeekBar(seekVenue, valueVenue);
        setupSeekBar(seekDecoration, valueDecoration);
        setupSeekBar(seekPhotography, valuePhotography);
        setupSeekBar(seekCatering, valueCatering);

        drawChart(seekVenue.getProgress(), seekDecoration.getProgress(), seekPhotography.getProgress(), seekCatering.getProgress());

        buttonSaveBudget.setOnClickListener(v -> saveBudgetToFirebase());
    }

    private void setupSeekBar(SeekBar seekBar, TextView valueText) {
        valueText.setText("Rs." + seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int i, boolean b) {
                valueText.setText("Rs." + i);
            }

            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    private void saveBudgetToFirebase() {
        String totalStr = inputTotalBudget.getText().toString().trim();
        if (TextUtils.isEmpty(totalStr)) {
            Toast.makeText(this, "Enter total budget", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalBudget = Integer.parseInt(totalStr);
        int venue = seekVenue.getProgress();
        int decor = seekDecoration.getProgress();
        int photo = seekPhotography.getProgress();
        int cater = seekCatering.getProgress();

        int totalUsed = venue + decor + photo + cater;
        int over = Math.max(0, totalUsed - totalBudget);
        int under = Math.max(0, totalBudget - totalUsed);

        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("totalBudget", totalBudget);
        map.put("venue", venue);
        map.put("decoration", decor);
        map.put("photography", photo);
        map.put("catering", cater);
        map.put("totalUsed", totalUsed);
        map.put("over", over);
        map.put("under", under);

        budgetRef.child(eventId).setValue(map)
                .addOnSuccessListener(unused -> Toast.makeText(this, "✅ Budget saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "❌ Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        drawChart(venue, decor, photo, cater);
    }

    private void drawChart(int venue, int decor, int photo, int cater) {
        int total = venue + decor + photo + cater;
        if (total == 0) {
            budgetChart.clear();
            budgetChart.setNoDataText("Please assign budget to at least one section.");
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (venue > 0) entries.add(new PieEntry(venue, "Venue"));
        if (decor > 0) entries.add(new PieEntry(decor, "Decoration"));
        if (photo > 0) entries.add(new PieEntry(photo, "Photography"));
        if (cater > 0) entries.add(new PieEntry(cater, "Catering"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData pieData = new PieData(dataSet);
        budgetChart.setData(pieData);

        Description desc = new Description();
        desc.setText("");
        budgetChart.setDescription(desc);

        budgetChart.setDrawHoleEnabled(true);
        budgetChart.setHoleRadius(45f);
        budgetChart.setTransparentCircleRadius(50f);
        budgetChart.setCenterText("Your Budget");
        budgetChart.setCenterTextSize(16f);
        budgetChart.getLegend().setEnabled(false);

        budgetChart.setVisibility(PieChart.VISIBLE);
        budgetChart.invalidate(); // refresh
    }
}
