package com.takusemba.cropmesample.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.takusemba.cropmesample.R;
import com.takusemba.cropmesample.models.Photo;
import com.takusemba.cropmesample.ui.OnPhotoClickListener;

import java.util.List;

/**
 * Created by takusemba on 2017/09/10.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<Photo> photos;
    private OnPhotoClickListener listener;
    private int length;

    PhotoAdapter(List<Photo> photos, OnPhotoClickListener listener, int length) {
        this.photos = photos;
        this.listener = listener;
        this.length = length;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false);
        ImageView image = view.findViewById(R.id.image);
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = length;
        params.height = length;
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        final Photo photo = photos.get(position);
        holder.image.setImageBitmap(photo.bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClicked(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        ViewHolder(final View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
        }
    }
}