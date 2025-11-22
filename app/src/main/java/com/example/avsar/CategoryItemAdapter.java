package com.example.avsar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.ViewHolder> {

    private Context context;
    private List<CategoryItem> itemList;
    private List<String> itemKeys;

    private String categoryName;

    public CategoryItemAdapter(Context context, List<CategoryItem> itemList, List<String> itemKeys, String categoryName) {
        this.context = context;
        this.itemList = itemList;
        this.itemKeys = itemKeys;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = itemList.get(position);
        String itemKey = itemKeys.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.title.setText(item.getTitle());
        holder.cost.setText(item.getCost());
        holder.location.setText(item.getLocation());
        holder.ratingBar.setRating((float) item.getRatings());
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);


        // 🔄 Check if already shortlisted
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("shortlistedVendors")
                .child(userId)
                .child(categoryName + "_" + itemKey);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.iconSave.setImageResource(snapshot.exists()
                        ? R.drawable.ic_heart_filled // saved icon
                        : R.drawable.ic_favorite_border); // not saved icon
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 💖 Save / Remove on click
        holder.iconSave.setOnClickListener(v -> {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ref.removeValue();
                        holder.iconSave.setImageResource(R.drawable.ic_favorite_border);
                        Toast.makeText(context, "Removed from shortlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> vendorMap = new HashMap<>();
                        vendorMap.put("title", item.getTitle());
                        vendorMap.put("cost", item.getCost());
                        vendorMap.put("location", item.getLocation());
                        vendorMap.put("imageUrl", item.getImageUrl());
                        vendorMap.put("rating", item.getRatings());

                        ref.setValue(vendorMap);
                        holder.iconSave.setImageResource(R.drawable.ic_heart_filled);
                        Toast.makeText(context, "Added to shortlist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        // 👉 Open details screen
        holder.itemView.setOnClickListener(v -> {
            if (position < itemKeys.size()) {
                Intent intent = new Intent(context, CategoryDetailsActivity.class);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("itemKey", itemKey);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, iconSave;
        TextView title, cost, location;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageVendor);
            title = itemView.findViewById(R.id.textTitle);
            cost = itemView.findViewById(R.id.textCost);
            location = itemView.findViewById(R.id.textLocation);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            iconSave = itemView.findViewById(R.id.iconSave); // ❤️ make sure it's added in XML
        }
    }
}
