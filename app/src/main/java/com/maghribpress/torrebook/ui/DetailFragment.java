package com.maghribpress.torrebook.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maghribpress.torrebook.GlideApp;
import com.maghribpress.torrebook.R;


public class DetailFragment extends Fragment {
    public static final String BOOK_COVER = "DETAIL_BOOK_COVER";
    public static final String BOOK_TITLE = "DETAIL_BOOK_TITLE";
    public static final String BOOK_AUTHORS = "DETAIL_BOOK_AUTHORS";
    public static final String BOOK_PAGES = "DETAIL_BOOK_PAGES";
    public static final String BOOK_DESCRIPTION = "DETAIL_BOOK_DESCRIPTION";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_detail_layout, container, false);
        showBackButton();
        ImageView bookcover = (ImageView) view.findViewById(R.id.img_book_cover_detail);
        TextView booktitle = (TextView) view.findViewById(R.id.txt_title_detail);
        TextView bookAuthors = (TextView) view.findViewById(R.id.txt_authors_detail);
        TextView bookInfo = (TextView) view.findViewById(R.id.txt_publishinfo_detail);
        TextView bookdescription = (TextView) view.findViewById(R.id.txt_description_detail);
        Button bookDownload = (Button) view.findViewById(R.id.btn_download);
        Bundle bundle = getArguments();
        if(!bundle.isEmpty()) {
            if (bundle.containsKey(DetailFragment.BOOK_TITLE)) {
                booktitle.setText(bundle.getString(DetailFragment.BOOK_TITLE));
            }
            if (bundle.containsKey(BOOK_COVER)) {
                GlideApp.with(bookcover.getContext())
                        .load(bundle.getString(BOOK_COVER))
                        .placeholder(R.drawable.placeholder)
                        .into(bookcover);
            }
            if (bundle.containsKey(BOOK_AUTHORS)) {
                bookAuthors.setText(bundle.getString(BOOK_AUTHORS));
            }
            if (bundle.containsKey(BOOK_PAGES)) {
                String bookPages = String.valueOf(bundle.getInt(BOOK_PAGES)) +  " Pages";
                bookInfo.setText(bookPages);
            }
            if (bundle.containsKey(BOOK_DESCRIPTION)) {
                bookdescription.setText(bundle.getString(BOOK_DESCRIPTION));
            }
        }

        return view;
    }
    public void showBackButton() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
