package com.takusemba.cropmesample.ui.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.takusemba.cropmesample.R;
import com.takusemba.cropmesample.models.Album;

import java.util.List;

/**
 * Created by takusemba on 2017/09/10.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static final int COLUMNS = 4;

    private List<Album> albums;
    private Context context;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album, viewGroup, false);
        return new AlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.title.setText(album.name);

        PhotoAdapter adapter = new PhotoAdapter(album.photos);
        GridLayoutManager layoutManager = new GridLayoutManager(context, COLUMNS);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setHasFixedSize(true);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView recyclerView;

        ViewHolder(final View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }
}