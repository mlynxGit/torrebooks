package com.maghribpress.torrebook.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.TorrentDownloadListener;
import com.maghribpress.torrebook.classes.TorrentFile;
import com.maghribpress.torrebook.db.entity.Book;

import java.util.List;

import mbanje.kurt.fabbutton.FabButton;

public class TorrentAdapter extends RecyclerView.Adapter<TorrentAdapter.TorrentsViewHolder> {
    private List<TorrentFile> mtorrentsList;
    private TorrentDownloadListener mTDL;
    Book mBook;
    public TorrentAdapter(List<TorrentFile> mtorrentsList, TorrentDownloadListener tdl,Book book) {
        this.mtorrentsList = mtorrentsList;
        mTDL =tdl;
        mBook = book;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TorrentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TorrentAdapter.TorrentsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.torrent_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TorrentsViewHolder holder, int position) {
        holder.txtTitle.setText(mtorrentsList.get(position).getTitle());
        holder.txtFileSize.setText(mtorrentsList.get(position).getTorrentsize());
        holder.txtAdded.setText(mtorrentsList.get(position).getAdded());
        if (mtorrentsList.get(position).getSeeders() > 0) {
            holder.txtSeeders.setTextColor(holder.txtSeeders.getContext().getResources().getColor(R.color.colorGreen));
            Drawable img = holder.txtSeeders.getContext().getResources().getDrawable(R.drawable.ic_users);
            holder.txtSeeders.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.txtSeeders.setText(String.valueOf(mtorrentsList.get(position).getSeeders()));
        } else {
            if (mtorrentsList.get(position).getPopularity() > 0) {
                holder.txtSeeders.setTextColor(holder.txtSeeders.getContext().getResources().getColor(R.color.colorGreen));
                Drawable img = holder.txtSeeders.getContext().getResources().getDrawable(R.drawable.ic_arrow_up_green);
                holder.txtSeeders.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                holder.txtSeeders.setText(String.valueOf(mtorrentsList.get(position).getPopularity()));
            }
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.txtDownloadLabel.setText(v.getContext().getString(R.string.starting));
                mTDL.StartDownload(mBook,mtorrentsList.get(position).getMagnet(),v);
            }
        };
        holder.fileCard.setOnClickListener(clickListener);
        holder.progressDownload.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return mtorrentsList.size();
    }
    public void setItems(List<TorrentFile> torrents) {
           this.mtorrentsList = torrents;
           notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class TorrentsViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtFileSize, txtSeeders, txtAdded, txtDownloadLabel;
        CardView fileCard;
        FabButton progressDownload;

        TorrentsViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.torrentFileTitle);
            txtFileSize = (TextView) itemView.findViewById(R.id.filesize);
            txtDownloadLabel = (TextView) itemView.findViewById(R.id.txt_download_details);
            txtSeeders = (TextView) itemView.findViewById(R.id.seeders);
            txtAdded = (TextView) itemView.findViewById(R.id.added);
            fileCard = (CardView) itemView.findViewById(R.id.file_card);
            progressDownload = (FabButton) itemView.findViewById(R.id.btnDownload);
            progressDownload.resetIcon();
        }

    }
}
