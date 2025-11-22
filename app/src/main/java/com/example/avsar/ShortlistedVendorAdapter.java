package com.example.avsar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ShortlistedVendorAdapter extends RecyclerView.Adapter<ShortlistedVendorAdapter.ViewHolder> {

    private Context context;
    private List<ShortlistedVendor> vendorList;

    public ShortlistedVendorAdapter(Context context, List<ShortlistedVendor> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShortlistedVendor vendor = vendorList.get(position);

        holder.textTitle.setText(vendor.getTitle());
        holder.textCost.setText(vendor.getCost());
        holder.textLocation.setText(vendor.getLocation());
        holder.ratingBar.setRating(vendor.getRating());
        holder.textReviewCount.setText("(" + Math.round(vendor.getRating()) + " Reviews)");

        Glide.with(context).load(vendor.getImageUrl()).into(holder.imageVendor);

        holder.buttonContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            context.startActivity(intent);
        });

        holder.buttonEnquiry.setOnClickListener(v -> {
            // Open an enquiry form or send a message
            // This is a placeholder
            android.widget.Toast.makeText(context, "Enquiry sent to " + vendor.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageVendor, iconSave;
        TextView textTitle, textCost, textLocation, textReviewCount;
        RatingBar ratingBar;
        Button buttonContact, buttonEnquiry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageVendor = itemView.findViewById(R.id.imageVendor);
            textTitle = itemView.findViewById(R.id.textTitle);
            textCost = itemView.findViewById(R.id.textCost);
            textLocation = itemView.findViewById(R.id.textLocation);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textReviewCount = itemView.findViewById(R.id.textReviewCount);
            buttonContact = itemView.findViewById(R.id.buttonContact);
            buttonEnquiry = itemView.findViewById(R.id.buttonEnquiry);
        }
    }
}
