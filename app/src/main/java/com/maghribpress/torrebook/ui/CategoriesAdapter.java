package com.maghribpress.torrebook.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.db.entity.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{
    private List<Category> mcategoriesList;
    public CategoriesAdapter(List<Category> categorieslList) {
        this.mcategoriesList = categorieslList;
    }
    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesAdapter.CategoriesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        holder.txtCategory.setText(mcategoriesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mcategoriesList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategory;

        CategoriesViewHolder(View itemView) {
            super(itemView);
            txtCategory = (TextView) itemView.findViewById(R.id.txt_category_name);
        }

    }
}

