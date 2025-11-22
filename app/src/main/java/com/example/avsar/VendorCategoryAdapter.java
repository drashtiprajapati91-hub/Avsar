package com.example.avsar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorCategoryAdapter extends RecyclerView.Adapter<VendorCategoryAdapter.ViewHolder> {

    private Context context;
    private List<VendorCategory> categoryList;

    public VendorCategoryAdapter(Context context, List<VendorCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vendor_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VendorCategory category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryImage.setImageResource(category.getImageResId());

        // 👉 Click listener for both image and text
        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(context, CategoryItemsActivity.class);
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        };

        holder.categoryName.setOnClickListener(clickListener);
        holder.categoryImage.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.imageViewCategory);
            categoryName = itemView.findViewById(R.id.textViewCategoryName);
        }
    }
}
