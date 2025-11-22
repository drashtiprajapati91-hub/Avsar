package com.example.avsar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private Context context;
    private List<TemplateModel> templateList;

    public TemplateAdapter(Context context, List<TemplateModel> templateList) {
        this.context = context;
        this.templateList = templateList;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        TemplateModel template = templateList.get(position);
        holder.title.setText(template.getTitle());

        Glide.with(context)
                .load(template.getImageUrl())
                .placeholder(R.drawable.image_rounded_bg)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    public void updateList(List<TemplateModel> newList) {
        this.templateList = newList;
        notifyDataSetChanged();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageTemplate);
            title = itemView.findViewById(R.id.textTemplateTitle);
        }
    }
}
