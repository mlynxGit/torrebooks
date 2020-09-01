package com.maghribpress.torrebook.ui;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.ramotion.foldingcell.FoldingCell;
import com.maghribpress.torrebook.GlideApp;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.Functions;
import com.maghribpress.torrebook.db.entity.Book;

import android.graphics.Color;
import android.graphics.PorterDuff;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder> {
    private List<Book> mbooksList;

    public BooksAdapter(List<Book> bookslList) {
        this.mbooksList = bookslList;
        setHasStableIds(true);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BooksViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {
        final Book book = mbooksList.get(position);

        holder.mainFoldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mainFoldingCell.toggle(false);
            }
        });
        holder.txtTitle.setText(book.getTitle());
        holder.txtDetailTitle.setText(book.getTitle());
        GlideApp.with(holder.imgBookCover.getContext())
                .load(book.getCoverURL())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgBookCover);
        holder.txtAuthors.setText(book.getAuthorsString());
        String rankingInfo = String.valueOf(book.getRating()) + "(" + book.getRatingCount() +")";
        holder.txtRatingInfo.setText(rankingInfo);
        holder.ratingbar.setRating(book.getRating());
        String publishTextasHtml = "Published "  + book.getReleaseDate() + " - <font color='#444444'>" + book.getPages() + " Pages </font>";
        //holder.txtPublishInfo.setText(Html.fromHtml(publishTextasHtml));
        holder.txtDetailLanguage.setText(book.getLanguage());
        holder.txtDetailDescription.setText(book.getDescription());
        String pagesCount = String.valueOf(book.getPages()) + " Pages";
        holder.txtDetailPages.setText(pagesCount);
        holder.txtDetailPublishDate.setText(book.getReleaseDate());
        holder.txtDetailPublisher.setText(book.getPublisher());
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(book.getCategories());
        holder.categoriesView.setAdapter(categoriesAdapter);
        AuthorsAdapter authorsAdapter = new AuthorsAdapter(book.getAuthors());
        holder.authorsView.setAdapter(authorsAdapter);
        holder.btnoptionsOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.btnoptionsOverflow);
                //inflating menu from xml resource
                popup.inflate(R.menu.book_row_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_download:
                                setupforDownload(book,view);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
        View.OnClickListener onlickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailFragment detailFragment = new DetailFragment();
                Bundle fragBundle = new Bundle();
                fragBundle.putString(DetailFragment.BOOK_TITLE,book.getTitle());
                fragBundle.putString(DetailFragment.BOOK_AUTHORS,book.getAuthorsString());
                fragBundle.putString(DetailFragment.BOOK_COVER,book.getCoverURL());
                fragBundle.putString(DetailFragment.BOOK_DESCRIPTION,book.getDescription());
                fragBundle.putInt(DetailFragment.BOOK_PAGES,book.getPages());
                detailFragment.setArguments(fragBundle);
                MainActivity mact = (MainActivity) v.getContext();
                FragmentManager manager = mact.getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.add(R.id.fragment_container, detailFragment);
                ft.addToBackStack(detailFragment.getClass().getSimpleName());
                ft.commit();
            }
        };
        View.OnClickListener downloadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupforDownload(book,v);
            }
        };
        holder.btnDetailDownload.setOnClickListener(downloadClickListener);
    }
    private void setupforDownload(Book book,View v) {
        TorrentFragment torrentFragment = new TorrentFragment();
        Bundle fragBundle = new Bundle();
        fragBundle.putString(TorrentFragment.TORRENT_TITLE,book.getTitle());
        fragBundle.putString(TorrentFragment.TORRENT_GOOGLE_ID,book.getGoogleid());
        fragBundle.putString(TorrentFragment.TORRENT_COVERURL,book.getCoverURL());
        fragBundle.putString(TorrentFragment.TORRENT_LANGUAGE,book.getLanguage());
        fragBundle.putInt(TorrentFragment.TORRENT_PAGES,book.getPages());
        fragBundle.putFloat(TorrentFragment.TORRENT_RATING,book.getRating());
        fragBundle.putInt(TorrentFragment.TORRENT_RATING_COUNT,book.getRatingCount());
        fragBundle.putString(TorrentFragment.TORRENT_DESCRIPTION,book.getDescription());
        fragBundle.putString(TorrentFragment.TORRENT_ISBN10,book.getIsbn10());
        fragBundle.putString(TorrentFragment.TORRENT_ISBN13,book.getIsbn13());
        fragBundle.putString(TorrentFragment.TORRENT_PUBLISHER,book.getPublisher());
        fragBundle.putString(TorrentFragment.TORRENT_RELEASE_DATE,book.getReleaseDate());
        fragBundle.putStringArrayList(TorrentFragment.TORRENT_AUTHORS_NAMES,book.getFieldListFromAuthors());
        fragBundle.putStringArrayList(TorrentFragment.TORRENT_CATEGORIES_NAMES,book.getFieldListFromCategories());
        fragBundle.putParcelableArrayList(TorrentFragment.TORRENT_AUTHORS,book.getAuthors());
        fragBundle.putParcelableArrayList(TorrentFragment.TORRENT_CATEGORIES,book.getCategories());
        Log.d("SEARCHQUERY","Before Size");
        if(book.getAuthors().size() > 0) {
            Log.d("SEARCHQUERY","in Size");
            if(book.getAuthors().get(0).getName().contains(" ")) {
                Log.d("SEARCHQUERY","in Contains");
                fragBundle.putString(TorrentFragment.TORRENT_AUTHOR_LASTNAME,book.getAuthors().get(0).getName().split(" ")[1]);
            }
        }
        torrentFragment.setArguments(fragBundle);
        MainActivity mact = (MainActivity) Functions.getActivity(v);
        FragmentManager manager = mact.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(R.id.fragment_container, torrentFragment);
        ft.addToBackStack(torrentFragment.getClass().getSimpleName());
        ft.commit();
    }
    @Override
    public int getItemCount() {
        return mbooksList.size();
    }
    public void addItems(List<Book> booklList) {
        this.mbooksList = booklList;
        notifyDataSetChanged();
    }
    public class BooksViewHolder extends RecyclerView.ViewHolder {
        FoldingCell mainFoldingCell;
        TextView txtTitle, txtAuthors, txtRatingInfo, txtDetailTitle, txtDetailPages, txtDetailPublishDate, txtDetailPublisher, txtDetailDescription, txtDetailLanguage;
        ImageView imgBookCover;
        Button btnDetailDownload, btnoptionsOverflow;
        RelativeLayout mainLayout;
        RatingBar ratingbar;
        RecyclerView categoriesView, authorsView;

        BooksViewHolder(View itemView) {
            super(itemView);
            mainFoldingCell = (FoldingCell) itemView.findViewById(R.id.folding_cell);
            mainLayout = (RelativeLayout)  itemView.findViewById(R.id.main_layout_holder);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtAuthors = (TextView) itemView.findViewById(R.id.txt_authors);
            txtRatingInfo = (TextView) itemView.findViewById(R.id.txt_ratinginfo);
            //txtPublishInfo = (TextView) itemView.findViewById(R.id.txt_publishinfo);
            imgBookCover = (ImageView) itemView.findViewById(R.id.img_book_cover);
            txtDetailTitle = (TextView) itemView.findViewById(R.id.cell_detail_title);
            txtDetailPages = (TextView) itemView.findViewById(R.id.cell_detail_pages);
            txtDetailPublishDate = (TextView) itemView.findViewById(R.id.cell_detail_publishdate);
            txtDetailPublisher = (TextView) itemView.findViewById(R.id.cell_detail_publisher);
            txtDetailDescription = (TextView) itemView.findViewById(R.id.cell_detail_description);
            txtDetailLanguage = (TextView) itemView.findViewById(R.id.cell_detail_language);
            btnDetailDownload = (Button) itemView.findViewById(R.id.cell_detail_download_button);
            btnoptionsOverflow = (Button) itemView.findViewById(R.id.buttonOptions);
            authorsView = (RecyclerView) itemView.findViewById(R.id.cell_detail_authors_recycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            authorsView.setLayoutManager(layoutManager);
            ratingbar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            LayerDrawable stars = (LayerDrawable) ratingbar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(Color.parseColor("#c5c5c5"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.parseColor("#FA634F"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(Color.parseColor("#FA634F"), PorterDuff.Mode.SRC_ATOP);
            categoriesView = (RecyclerView) itemView.findViewById(R.id.categories_view);
            FlexboxLayoutManager _layoutManager = new FlexboxLayoutManager(itemView.getContext());
            _layoutManager.setFlexDirection(FlexDirection.ROW);
            _layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            categoriesView.setLayoutManager(_layoutManager);

        }

    }
}
