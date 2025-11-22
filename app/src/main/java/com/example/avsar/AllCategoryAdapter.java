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

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.AllCategoryViewHolder> {

    private Context context;
    private List<VendorCategory> categoryList;

    public AllCategoryAdapter(Context context, List<VendorCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public AllCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_category, parent, false);
        return new AllCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCategoryViewHolder holder, int position) {
        VendorCategory category = categoryList.get(position);
        holder.imageView.setImageResource(category.getImageResId());
        holder.textView.setText(category.getName());

        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(context, CategoryItemsActivity.class);
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        };

        holder.imageView.setOnClickListener(clickListener);
        holder.textView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class AllCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public AllCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageCategory);
            textView = itemView.findViewById(R.id.textCategory);
        }
    }
}
