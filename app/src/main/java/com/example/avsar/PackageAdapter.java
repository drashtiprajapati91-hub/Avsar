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

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private Context context;
    private List<Package> packageList;

    public PackageAdapter(Context context, List<Package> packageList) {
        this.context = context;
        this.packageList = packageList;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        Package p = packageList.get(position);

        holder.title.setText(p.getTitle());
        holder.description.setText(p.getDescription());
        holder.cost.setText(p.getCost());
        holder.duration.setText(p.getDuration());

        Glide.with(context).load(p.getImageUrl()).into(holder.image);

        // 🔥 Open details screen on item click
        holder.itemView.setOnClickListener(v -> {
            if (p.getTitle() != null) {
                Intent intent = new Intent(context, PackageDetailsActivity.class);
                intent.putExtra("packageTitle", p.getTitle());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Package title missing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, cost, duration;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.packageImage);
            title = itemView.findViewById(R.id.packageTitle);
            description = itemView.findViewById(R.id.packageDescription);
            cost = itemView.findViewById(R.id.packageCost);
            duration = itemView.findViewById(R.id.packageDuration);
        }
    }
}
