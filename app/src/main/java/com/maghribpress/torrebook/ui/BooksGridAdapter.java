package com.maghribpress.torrebook.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maghribpress.torrebook.GlideApp;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.db.entity.Book;

import java.util.List;
;

public class BooksGridAdapter extends BaseAdapter {
    private List<Book> mlistBooks;
    private LayoutInflater mlayoutInflater;
    public BooksGridAdapter(List<Book> listBooks) {
        this.mlistBooks = listBooks;
        hasStableIds();
    }
    @Override
    public int getCount() {
        return mlistBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mlistBooks.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            mlayoutInflater = LayoutInflater.from(parent.getContext());
            convertView = mlayoutInflater.inflate(R.layout.grid_item_layout, null);
            holder = new ViewHolder();
            holder.bookCover = (ImageView) convertView.findViewById(R.id.book_cover);
            /*holder.bookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config config = new Config().setThemeColorRes(R.color.colorAccent).setFont(Constants.FONT_RALEWAY);
                    FolioReader folioReader = FolioReader.get();
                    folioReader.setConfig(config,true);
                    folioReader.openBook(mlistBooks.get(position).getEpubPath());
                }
            });*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Book _book = mlistBooks.get(position);
        boolean isBitmap=false;
        if(_book.getCoverURL() != null){
            if(URLUtil.isValidUrl(_book.getCoverURL())) {
                GlideApp.with(holder.bookCover.getContext())
                        .load(_book.getCoverURL())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .fitCenter()
                        .into(holder.bookCover);
            }else{
                isBitmap=true;
            }
        }else{
            isBitmap=true;
        }
        if(isBitmap){
            if(_book.getCover()!=null) {
                GlideApp.with(holder.bookCover.getContext())
                        .load(_book.getCover())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .fitCenter()
                        .into(holder.bookCover);
            }
        }

        return convertView;
    }
    static class ViewHolder {
        ImageView bookCover;
    }
}
