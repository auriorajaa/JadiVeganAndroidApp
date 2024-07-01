package com.example.jadiveganapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.R;
import com.example.jadiveganapp.ViewAllActivity;
import com.example.jadiveganapp.model.HomeCategoryModel;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    Context context;
    List<HomeCategoryModel> homeCategoryModelList;

    public HomeCategoryAdapter(Context context, List<HomeCategoryModel> homeCategoryModelList) {
        this.context = context;
        this.homeCategoryModelList = homeCategoryModelList;
    }

    @NonNull
    @Override
    public HomeCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(homeCategoryModelList.get(position).getImage()).into(holder.Image);
        holder.Name.setText(homeCategoryModelList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("Category", homeCategoryModelList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeCategoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView Name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.home_cat_img);
            Name = itemView.findViewById(R.id.cat_home_name);
        }
    }
}
