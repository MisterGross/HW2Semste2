package com.tnc.crackertracker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tnc.crackertracker.Model.FoodItem;
import com.tnc.crackertracker.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<FoodItem> itemList;

    public MyAdapter(List<FoodItem> itemList) {
        this.itemList = itemList;
    }

    public void addData(FoodItem newItem) {
        itemList.add(newItem);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvFoodName);
            quantityTextView = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
