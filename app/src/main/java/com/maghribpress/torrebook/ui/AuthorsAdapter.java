package com.maghribpress.torrebook.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.maghribpress.torrebook.GlideApp;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.db.entity.Author;

import java.util.List;

public class AuthorsAdapter extends RecyclerView.Adapter<AuthorsAdapter.AuthorsViewHolder>{
    private List<Author> mauthorsList;
    public AuthorsAdapter(List<Author> authorslList) {
        this.mauthorsList = authorslList;
    }
    @NonNull
    @Override
    public AuthorsAdapter.AuthorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AuthorsAdapter.AuthorsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.authors_recycler_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorsAdapter.AuthorsViewHolder holder, int position) {
        holder.txtAuthorName.setText(mauthorsList.get(position).getName());
        GlideApp.with(holder.imgAuthorPicture.getContext())
                .load(mauthorsList.get(position).getPictureURL())
                .placeholder(R.drawable.author_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imgAuthorPicture);
    }

    @Override
    public int getItemCount() {
        return mauthorsList.size();
    }

    public class AuthorsViewHolder extends RecyclerView.ViewHolder {

        TextView txtAuthorName;
        ImageView imgAuthorPicture;

        AuthorsViewHolder(View itemView) {
            super(itemView);
            txtAuthorName = (TextView) itemView.findViewById(R.id.cell_detail_author_name);
            imgAuthorPicture = (ImageView) itemView.findViewById(R.id.cell_detail_img_author);

        }

    }
}
