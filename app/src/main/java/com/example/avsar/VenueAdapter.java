package com.example.avsar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueViewHolder> {

    private Context context;
    private List<Venue> venueList;
    private String selectedLocation = "";

    public VenueAdapter(Context context, List<Venue> venueList) {
        this.context = context;
        this.venueList = venueList;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_venue, parent, false);
        return new VenueViewHolder(view);
    }

    public void updateList(List<Venue> newList) {
        venueList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venueList.get(position);

        Glide.with(context).load(venue.getImageUrl()).into(holder.imageVenue);
        holder.textVenueName.setText(venue.getName());
        holder.textVenueCost.setText(venue.getCostPerPlate());
        holder.textVenueCapacity.setText(venue.getCapacity());
        holder.textLocation.setText(venue.getLocation());

        holder.iconSave.setImageResource(venue.isSaved() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        // ✅ Click to open EventDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            if (venue.getId() != null) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("venueId", venue.getId());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Venue ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Save toggle
        holder.iconSave.setOnClickListener(v -> {
            venue.setSaved(!venue.isSaved());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {
        ImageView imageVenue, iconSave;
        TextView textVenueName, textVenueCost, textVenueCapacity, textLocation;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            textLocation = itemView.findViewById(R.id.textLocation);
            imageVenue = itemView.findViewById(R.id.imageVenue);
            iconSave = itemView.findViewById(R.id.iconSave);
            textVenueName = itemView.findViewById(R.id.textVenueName);
            textVenueCost = itemView.findViewById(R.id.textVenueCost);
            textVenueCapacity = itemView.findViewById(R.id.textVenueCapacity);
        }
    }
}
